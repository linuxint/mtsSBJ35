package com.devkbil.mtssbj.config;

import com.devkbil.common.CustomXssFilter;
import com.devkbil.common.HtmlCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * XssConfig는 WebMvcConfigurer 인터페이스를 구현한 구성 클래스로,
 * 메시지와 필터를 사용하여 Spring MVC 동작을 사용자 정의하여
 * Cross-Site Scripting (XSS) 공격을 방지하는 데 중점을 둡니다.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class XssConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    /**
     * (1) HTML 특수 문자를 이스케이프 처리하도록 수정된 ObjectMapper 기반의
     * Jackson Message Converter 등록
     */
    @Bean
    public MappingJackson2HttpMessageConverter createJsonEscapeConverter() {
        ObjectMapper escapedObjectMapper = objectMapper.copy();
        escapedObjectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(escapedObjectMapper);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createJsonEscapeConverter());
    }

    /**₩₩
     * (2) 커스텀 XSS 필터 등록 - 파라미터 기반 XSS 방지
     */
    @Bean
    public FilterRegistrationBean<Filter> xssFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CustomXssFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

}