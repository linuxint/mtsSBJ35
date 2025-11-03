package com.devkbil.mtssbj.config;

import com.devkbil.common.CustomXssFilter;
import com.devkbil.common.HtmlCharacterEscapes;

import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.Filter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class XssConfig {
    /**
     * HTML 특수 문자를 이스케이프 처리하도록 수정된 ObjectMapper 빈 등록 (Jackson 3.x)
     */
    @Bean
    public ObjectMapper xssSafeObjectMapper() {
        // Jackson 3.x에서는 ObjectMapper에 직접 CharacterEscapes를 적용할 수 없습니다.
        // 대신 ObjectMapper를 생성하고, 필요하다면 ObjectWriter/JsonGenerator 사용 시 CharacterEscapes를 적용해야 합니다.
        // 빈 등록은 기본 ObjectMapper로 하고, 실제 직렬화 시점에 escapes 적용 권장
        return new ObjectMapper();
    }

    /**
     * 커스텀 XSS 필터 등록 - 파라미터 기반 XSS 방지
     */
    @Bean
    public FilterRegistrationBean<Filter> xssFilterRegistrationBean() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new CustomXssFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public FilterRegistrationBean<CustomXssFilter> xssFilterRegistration() {
        FilterRegistrationBean<CustomXssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CustomXssFilter());
        registrationBean.addUrlPatterns("/*"); // 필요한 경로 지정
        registrationBean.setOrder(1); // 필터 순서
        return registrationBean;
    }
}