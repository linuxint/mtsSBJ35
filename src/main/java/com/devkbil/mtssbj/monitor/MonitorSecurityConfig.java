package com.devkbil.mtssbj.monitor;

/**
 * 모니터링 엔드포인트에 대한 보안 설정
 * 모니터링 API에 대한 액세스를 관리자 사용자로 제한
 */
// @Configuration
// @EnableWebSecurity
// @Order(1)
public class MonitorSecurityConfig {

    /*
     * 모니터링 엔드포인트를 보호하기 위한 보안 필터 체인을 구성합니다.
     * "/api/monitor/**" 경로 패턴에 대해 "ADMIN" 역할을 가진 사용자만 액세스할 수 있도록 보장합니다.
     * 이러한 특정 엔드포인트에 대해 CSRF 보호를 비활성화합니다.
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