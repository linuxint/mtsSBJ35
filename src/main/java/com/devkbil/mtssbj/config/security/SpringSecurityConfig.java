package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.common.JwtRequestFilter;
import com.devkbil.mtssbj.config.ConfigConstant;
import com.devkbil.mtssbj.config.CorsConfig;
import com.devkbil.mtssbj.error.ErrorCode;
import com.devkbil.mtssbj.error.ErrorResponse;
import com.devkbil.mtssbj.member.MemberService;
import tools.jackson.databind.ObjectMapper;

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
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;

import javax.sql.DataSource;
import java.io.PrintWriter;
import java.util.Arrays;

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
    private final JwtRequestFilter jwtRequestFilter;
    private final CorsConfig corsConfig;
    private final MemberService memberService;

    /**
     * 로그인 페이지 URL 경로
     */
    public static final String URL_LOGIN = "/memberLogin";

    // === 1. Password Encoder ===
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // === 2. Session Registry & Event Publisher ===
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public static ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher());
    }

    // === 3. WebSecurityCustomizer: 정적 리소스 무시 ===
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**", "/favicon.ico");
    }

    // === 4. SecurityFilterChain ===
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // --- 4.1 세션 관리 ---
        http.sessionManagement(session -> session
                .sessionFixation().changeSessionId()
                .maximumSessions(1)
                .expiredSessionStrategy(customSessionExpiredStrategy)
                .maxSessionsPreventsLogin(false)
                .sessionRegistry(sessionRegistry())
                .expiredUrl(URL_LOGIN + "?expired")
        );

        // --- 4.2 JWT 필터 ---
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // --- 4.3 CSRF / CORS ---
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.configurationSource(request -> {
            var config = new org.springframework.web.cors.CorsConfiguration();
            config.setAllowedOrigins(Arrays.asList(corsConfig.corsAllowedOrigins.split(",")));
            config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(Arrays.asList("*"));
            config.setAllowCredentials(true);
            return config;
        }));

        // --- 4.4 Headers / CSP ---
        http.headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                .contentSecurityPolicy(csp -> csp
                        .policyDirectives(
                                "default-src 'self'; " +
                                        "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                                        "style-src 'self' 'unsafe-inline'; " +
                                        "img-src 'self' data:; " +
                                        "font-src 'self' data:; " +
                                        "object-src 'none';"
                        )
                )
        );

        // --- 4.5 URL 권한 설정 ---
        http.authorizeHttpRequests(authorize -> authorize
                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                .requestMatchers(ConfigConstant.allAllowList.toArray(new String[0])).permitAll()
                .anyRequest().authenticated()
        );

        // --- 4.6 예외 처리 ---
        http.exceptionHandling(exceptionConfig -> exceptionConfig
                .authenticationEntryPoint(unauthorizedEntryPoint)
                .accessDeniedHandler(accessDeniedHandler)
        );

        // --- 4.7 Form Login ---
        http.formLogin(login -> login
                .loginPage(URL_LOGIN)
                .loginProcessingUrl(ConfigConstant.URL_LOGIN_PROCESS)
                .usernameParameter(ConfigConstant.PARAMETER_LOGIN_ID)
                .passwordParameter(ConfigConstant.PARAMETER_LOGIN_PWD)
                .successHandler(new MyAuthenticationSuccessHandler(memberService))
                .failureHandler(userLoginFailHandler)
                .permitAll()
        );

        // --- 4.8 Remember Me ---
        http.rememberMe(rememberMe -> rememberMe
                .key(ConfigConstant.REMEMBER_ME_KEY)
                .rememberMeCookieName(ConfigConstant.REMEMBER_ME_COOKIE_NAME)
                //.alwaysRemember(true).key("SomeUniqueKeyForRememberMe")
                .userDetailsService(userDetailsService)
                .tokenRepository(tokenRepository())
        );

        // --- 4.9 Logout ---
        http.logout(logout -> logout
                .logoutUrl(ConfigConstant.URL_LOGOUT)
                .invalidateHttpSession(true)
                .deleteCookies(
                        ConfigConstant.SID_COOKIE_NAME,
                        ConfigConstant.JSESSIONID,
                        ConfigConstant.REMEMBER_ME_COOKIE_NAME
                )
                .permitAll()
        );

        return http.build();
    }

    // === 5. Authentication Manager ===
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // === 6. Persistent Token Repository ===
    @Bean
    public PersistentTokenRepository tokenRepository() {
        JdbcTokenRepositoryImpl repo = new JdbcTokenRepositoryImpl();
        repo.setDataSource(dataSource);
        return repo;
    }

    // === 7. Unauthorized / AccessDenied Handlers ===
    public final AuthenticationEntryPoint unauthorizedEntryPoint =
            (request, response, authException) -> {
                HttpSession session = request.getSession(false);
                if (session == null || request.getParameterMap().isEmpty()) {
                    log.info("미인증 접근: 세션 없음 또는 입력 없음");
                    response.sendRedirect(URL_LOGIN);
                    return;
                }
                if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.getWriter().write("{\"error\": \"Access Denied\", \"message\": \"권한이 부족합니다.\"}");
                } else {
                    response.sendRedirect(ConfigConstant.URL_ACCESS_DENIED);
                }
            };

    public final AccessDeniedHandler accessDeniedHandler =
            (request, response, ex) -> {
                ErrorResponse fail = ErrorResponse.of(ErrorCode.FORBIDDEN_ERROR, "스프링 시큐리티 접근 거부...");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                PrintWriter writer = response.getWriter();
                writer.write(new ObjectMapper().writeValueAsString(fail));
                writer.flush();
            };

    // === 8. SimpleUrlAuthenticationSuccessHandler ===
    @Bean
    public SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler handler = new SimpleUrlAuthenticationSuccessHandler();
        handler.setDefaultTargetUrl(URL_LOGIN);
        handler.setAlwaysUseDefaultTargetUrl(true);
        return handler;
    }

}