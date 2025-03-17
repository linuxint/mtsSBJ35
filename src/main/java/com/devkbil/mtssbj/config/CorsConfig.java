package com.devkbil.mtssbj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 어플리케이션의 전역 CORS 설정을 정의하는 Configuration 클래스입니다.
 * WebMvcConfigurer bean을 사용하여 CORS 매핑을 커스터마이징합니다.
 * <p>
 * 이 설정은 지정된 출처(Origin)로부터의 교차 출처 요청을
 * 지정된 HTTP 메서드 및 헤더와 함께 처리할 수 있도록 합니다.
 */
@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    public String corsAllowedOrigins;
    /**
     * 애플리케이션의 CORS 설정을 구성합니다.
     * 이 메서드는 지정된 출처, HTTP 메서드 및 헤더에 대한
     * 교차 출처 요청을 허용하는 전역 CORS 구성을 정의합니다.
     *
     * @return CORS 매핑을 사용자 정의하는 WebMvcConfigurer 빈
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 요청 경로에 대해 CORS 허용
                        .allowedOrigins(corsAllowedOrigins) // 허용할 Origin 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
                        .allowedHeaders("*") // 모든 요청 헤더 허용
                        .allowCredentials(true); // 인증 정보 허용 (쿠키, Authorization 헤더 등)
            }
        };
    }
}
