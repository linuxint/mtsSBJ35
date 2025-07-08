package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.common.JwtRequestFilter;
import com.devkbil.mtssbj.config.ConfigConstant;
import com.devkbil.mtssbj.config.CorsConfig;
import com.devkbil.mtssbj.error.ErrorCode;
import com.devkbil.mtssbj.error.ErrorResponse;
import com.devkbil.mtssbj.member.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpSession;

import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.sql.DataSource;

import java.io.PrintWriter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final DataSource dataSource;
    private final CustomSessionExpiredStrategy customSessionExpiredStrategy;
    private final UserDetailsService userDetailsService;
    private final AuthenticationFailureHandler userLoginFailHandler;
    public final JwtRequestFilter jwtRequestFilter;
    private final CorsConfig corsConfig;

    /**
     * 로그인 페이지 URL 경로
     */
    public static final String URL_LOGIN = "/memberLogin";

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, MemberService memberService) throws Exception {

        http.sessionManagement(session -> session
                .sessionFixation().changeSessionId()
                .maximumSessions(1) // 사용자 별로 1개 세션만 허용
                .expiredSessionStrategy(customSessionExpiredStrategy)
                .maxSessionsPreventsLogin(false) // 이미 로그인한 사용자를 새로 로그인 허용
                .sessionRegistry(sessionRegistry())
                .expiredUrl("/memberLogin?expired")
        );
        // JWT 필터 추가: 필요한 경우 주석을 해제하고 실제 구현체를 추가하세요.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.csrf(CsrfConfigurer::disable);
        http.cors(cors -> cors.configurationSource(request -> {
            var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
            corsConfiguration.setAllowedOrigins(java.util.Arrays.asList(corsConfig.corsAllowedOrigins.split(",")));
            corsConfiguration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            corsConfiguration.setAllowedHeaders(java.util.Collections.singletonList("*"));
            corsConfiguration.setAllowCredentials(true);
            return corsConfiguration;
        }));
        http
            .headers(headerConfig -> headerConfig
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .contentSecurityPolicy(csp -> csp
//                    .policyDirectives("default-src 'self'; script-src 'self'; object-src 'none';")
                    .policyDirectives(
                        "default-src 'self'; " +
                            "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                            "style-src 'self' 'unsafe-inline'; " +
                            "img-src 'self' data:; " + // ← 이 줄 추가!
                            "font-src 'self' data:; " +
                            "object-src 'none';"
                    )
                )
            );
        http.authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(ConfigConstant.allAllowList.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
        );
        http.exceptionHandling(
            exceptionConfig -> exceptionConfig
            .authenticationEntryPoint(unauthorizedEntryPoint)
            .accessDeniedHandler(accessDeniedHandler)
        );
        http.formLogin(login -> login
            .loginPage(URL_LOGIN)
                .loginProcessingUrl(ConfigConstant.URL_LOGIN_PROCESS) // 로그인 고정
                .usernameParameter(ConfigConstant.PARAMETER_LOGIN_ID)
                .passwordParameter(ConfigConstant.PARAMETER_LOGIN_PWD)
                .successHandler(new MyAuthenticationSuccessHandler(memberService))
                //.defaultSuccessUrl("/memberLoginChk", true)
                .failureHandler(userLoginFailHandler)
                //.failureUrl("/memberLoginError")
                .permitAll()
        );
        http.rememberMe(rememberMe -> rememberMe
                .key(ConfigConstant.REMEMBER_ME_KEY)
                .rememberMeCookieName(ConfigConstant.REMEMBER_ME_COOKIE_NAME)
                //.alwaysRemember(true).key("SomeUniqueKeyForRememberMe")
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository())
        );
        http.logout(logout -> logout
                .logoutUrl(ConfigConstant.URL_LOGOUT)
                .invalidateHttpSession(true)
                .deleteCookies(ConfigConstant.SID_COOKIE_NAME, ConfigConstant.JSESSIONID, ConfigConstant.REMEMBER_ME_COOKIE_NAME)
                .permitAll()
        );
        return http.build();
    }

    public final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                if (request.getParameterMap().isEmpty()) { // 입력값이 비어 있는 경우
                    log.info("요청에 입력값이 없습니다.");
                    response.sendRedirect(ConfigConstant.URL_LOGIN); // 로그인 페이지로 리다이렉트
                    return;
                }
                // 2. 세션이 없는 경우
                HttpSession session = request.getSession(false); // 세션 유효성 확인
                if (session == null) { // 세션이 없는 경우
                    log.info("세션이 존재하지 않습니다.");
                    response.sendRedirect(ConfigConstant.URL_LOGIN); // 로그인 페이지로 리다이렉트
                    return;
                }
                if (!request.getParameterMap().isEmpty()) {
                    if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                        // REST 요청일 경우 JSON 응답
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write(
                                "{\"error\": \"Access Denied\", \"message\": \"권한이 부족합니다.\"}"
                        );
                    } else {
                        // 브라우저 요청일 경우 권한 부족 페이지로 리다이렉트
                        response.sendRedirect(ConfigConstant.URL_ACCESS_DENIED); // 권한 부족 페이지로 리다이렉트
                    }
                }
                ErrorResponse fail = ErrorResponse.of(ErrorCode.UNAUTHORIZED_ERROR, "스프링 시큐리티 인증 실패...");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    public final AccessDeniedHandler accessDeniedHandler =
            (request, response, accessDeniedException) -> {
                // 1. 입력이 없는 경우

                ErrorResponse fail = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR, "스프링 시큐리티 접근 거부...");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                String json = new ObjectMapper().writeValueAsString(fail);
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(json);
                writer.flush();
            };

    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }

    /**
     * Spring Security 설정 파일에서 AuthenticationManager를 빈으로 정의해야 합니다.
     * Spring Boot 2.x에서는 AuthenticationManager를 직접 정의하는 것이 필요했지만,
     * Spring Boot 3.x에서는 이를 자동으로 구성하지 않으므로 명시적으로 설정해야 합니다.
     *
     * @param authenticationConfiguration Spring Security의 인증 설정을 포함하는 구성 객체
     * @return AuthenticationManager 인스턴스. 사용자 인증을 처리하고 관리하는 Spring Security의 핵심 인터페이스
     * @throws Exception 인증 관리자 생성 중 오류 발생 시
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(ConfigConstant.URL_LOGIN);
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

}
