package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.common.HtmlCharacterEscapes;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * XssConfig는 WebMvcConfigurer 인터페이스를 구현한 구성 클래스로,
 * 메시지와 필터를 사용하여 Spring MVC 동작을 사용자 정의하여
 * Cross-Site Scripting (XSS) 공격을 방지하는 데 중점을 둡니다.
 * <p>
 * 이 구성은 HTML 문자 이스케이프를 적용하기 위한 메시지 컨버터 설정과,
 * 모든 들어오는 HTTP 요청을 가로채어 XSS 보호를 보장하는 XSS 이스케이프 필터 등록을 포함합니다.
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class XssConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    /**
     * 이 메서드는 XSS 공격 방지를 위해 HTML 문자를 이스케이프 처리하는
     * 커스텀 JSON 이스케이프 컨버터를 추가합니다.
     *
     * @param converters 설정하고 확장할 {@link HttpMessageConverter} 목록
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(createJsonEscapeConverter());
    }

    /**
     * XSS 공격 방지를 위해 HTML 특수 문자를 이스케이프 처리하도록 수정된
     * {@link ObjectMapper}를 사용하는 커스텀 {@link MappingJackson2HttpMessageConverter}를 생성합니다.
     *
     * @return HTML 문자 이스케이프를 적용한 {@link ObjectMapper}를 포함한 {@link MappingJackson2HttpMessageConverter}.
     */
    @Bean
    public MappingJackson2HttpMessageConverter createJsonEscapeConverter() {
        ObjectMapper escapedObjectMapper = objectMapper.copy(); // 기존 ObjectMapper 복사 및 재사용.
        escapedObjectMapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(escapedObjectMapper);
    }

    /**
     * XSS 공격을 방지하기 위해 모든 HTTP 요청을 가로채고 정제하는 XssEscapeServletFilter를 등록합니다.
     *
     * @return XssEscapeServletFilter를 구성한 FilterRegistrationBean으로, 모든 URL 패턴에 필터를 최고 우선순위로 적용합니다.
     */
    @Bean
    public FilterRegistrationBean<XssEscapeServletFilter> registerXssEscapeFilter() {
        FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>(new XssEscapeServletFilter());
        filterRegistration.setOrder(1); // 필터 우선순위 설정.
        filterRegistration.addUrlPatterns("/*"); // 필터를 모든 요청에 적용.
        return filterRegistration;
    }
}