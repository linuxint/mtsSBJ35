package com.devkbil.mtssbj.config.environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * "local" 프로필 특정 구성 클래스.
 * {@link EnvConfiguration}을 구현하여 "local" 환경 프로필에 대한 특정 동작을 제공합니다.
 * 이 구성은 "local" 프로필이 활성화될 때만 활성화됩니다.
 */
@Profile("local")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SpecificConfigurationLocal implements EnvConfiguration {

    private final Environment environment;

    @Override
    @Bean
    public String getMessage() {
        log.info("[" + environment.getProperty("spring.config.activate.on-profile") + "] environment is running");
        return environment.getProperty("spring.config.activate.on-profile");
    }

    /**
     * Local 캐시 구성
     *
     * @return
     */
    @Override
    @Bean
    public String cache() {
        return "Local Cache Configuration";
    }
}
