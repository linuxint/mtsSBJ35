package com.devkbil.mtssbj.monitor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 모니터링 엔드포인트에 대한 보안 설정
 * 모니터링 API에 대한 액세스를 관리자 사용자로 제한
 */
// @Configuration
// @EnableWebSecurity
// @Order(1)
public class MonitorSecurityConfig {

    /**
     * 모니터링 엔드포인트를 보호하기 위한 보안 필터 체인을 구성합니다.
     * "/api/monitor/**" 경로 패턴에 대해 "ADMIN" 역할을 가진 사용자만 액세스할 수 있도록 보장합니다.
     * 이러한 특정 엔드포인트에 대해 CSRF 보호를 비활성화합니다.
     *
     * @param http HTTP 요청에 대한 보안 세부 정보를 구성하기 위한 {@link HttpSecurity}
     * @return 정의된 보안 규칙을 적용하는 구성된 {@link SecurityFilterChain}
     * @throws Exception 보안 구성을 구성하는 동안 오류가 발생한 경우
     */
    // @Bean
    // public SecurityFilterChain monitorFilterChain(HttpSecurity http) throws Exception {
    //     http
    //         .securityMatcher("/api/monitor/**")
    //         .authorizeHttpRequests(authorize -> authorize
    //             .requestMatchers("/api/monitor/**").hasRole("ADMIN")
    //         )
    //         .csrf(csrf -> csrf.ignoringRequestMatchers("/api/monitor/**"));
    //
    //     return http.build();
    // }
}