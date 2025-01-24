package com.devkbil.mtssbj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    /**
     * CORS 처리 전역 설정
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") // 모든 요청 경로에 대해 CORS 허용
                        .allowedOrigins("http://localhost:18080", "http://localhost:9090") // 허용할 Origin 설정
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드 설정
                        .allowedHeaders("*") // 모든 요청 헤더 허용
                        .allowCredentials(true); // 인증 정보 허용 (쿠키, Authorization 헤더 등)
            }
        };
    }
}

