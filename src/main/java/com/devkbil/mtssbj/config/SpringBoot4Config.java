package com.devkbil.mtssbj.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Boot 4.0 전용 설정 클래스
 * Spring Boot 4.0에서 새로 추가된 기능들을 구성합니다.
 */
@Configuration
@EnableWebMvc
@EnableAsync
@EnableScheduling
@Slf4j
public class SpringBoot4Config {

    /**
     * Spring Boot 4.0의 새로운 기능들을 활성화합니다.
     */
    @Bean
    @ConditionalOnProperty(name = "spring.main.web-application-type", havingValue = "servlet")
    public SpringBoot4Features springBoot4Features() {
        log.info("Spring Boot 4.0 기능들이 활성화되었습니다.");
        return new SpringBoot4Features();
    }

    /**
     * 개발 환경에서만 활성화되는 Spring Boot 4.0 개발 도구들
     */
    @Bean
    @Profile({"local", "dev"})
    public SpringBoot4DevTools springBoot4DevTools() {
        log.info("Spring Boot 4.0 개발 도구들이 활성화되었습니다.");
        return new SpringBoot4DevTools();
    }

    /**
     * Spring Boot 4.0 기능들을 관리하는 내부 클래스
     */
    public static class SpringBoot4Features {
        // Spring Boot 4.0의 새로운 기능들을 여기에 추가할 수 있습니다.
    }

    /**
     * Spring Boot 4.0 개발 도구들을 관리하는 내부 클래스
     */
    public static class SpringBoot4DevTools {
        // Spring Boot 4.0의 개발 도구들을 여기에 추가할 수 있습니다.
    }
} 