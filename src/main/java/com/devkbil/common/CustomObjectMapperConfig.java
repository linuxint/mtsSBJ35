package com.devkbil.common;

import com.devkbil.mtssbj.common.security.XssSanitizer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CustomObjectMapperConfig {

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // XSS 필터 적용
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new XssSanitizer());
        mapper.registerModule(module);

        return mapper;
    }
}