package com.devkbil.mtssbj.config.environment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

/**
 * "dev" 프로필을 위한 구성 클래스.
 * {@link EnvConfiguration} 인터페이스에 정의된 메서드에 대해
 * 특정 구현을 제공하며, "dev" 프로필이 활성화될 때 활성화됩니다.
 */
@Profile("dev")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SpecificConfigurationDev implements EnvConfiguration {

    private final Environment environment;

    @Override
    @Bean
    public String getMessage() {
        log.info("[" + environment.getProperty("spring.config.activate.on-profile") + "] environment is running");
        return environment.getProperty("spring.config.activate.on-profile");
    }

    /**
     * Dev 캐시 구성
     *
     * @return
     */
    @Override
    @Bean
    public String cache() {
        return "Dev Cache Configuration";
    }
}
