package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.common.ExtendReloadableResourceBundleMessageSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import java.time.Duration;
import java.util.Locale;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring 애플리케이션에서 메시지 소스 속성과 로케일 및 로케일 변경 인터셉터를 설정하는 구성 클래스입니다.
 * 메시지, 로케일 관리 및 로케일 변경 메커니즘을 위한 Bean을 정의합니다.
 */
@Configuration
@EnableConfigurationProperties
@ConditionalOnMissingBean(name = AbstractApplicationContext.MESSAGE_SOURCE_BEAN_NAME, search = SearchStrategy.CURRENT)
@Slf4j
public class MessageSourceConfig {

    /**
     * MessageSource 속성을 관리하기 위한 MessageSourceProperties 인스턴스를 구성하여 반환합니다.
     *
     * @return Spring 메시지 속성을 관리하기 위한 새로운 MessageSourceProperties 인스턴스
     */
    @Bean
    @ConfigurationProperties(prefix = "spring.messages")
    public MessageSourceProperties properties() {
        return new MessageSourceProperties();
    }

    /**
     * LocaleResolver Bean을 구성하여 기본 로케일 및 타임존을 세션에 설정합니다.
     * 기본 로케일은 Locale.KOREA로 설정되며, 커스텀 속성 이름을 사용하여 로케일 및 타임존을 설정합니다.
     *
     * @return 세션에서 로케일 및 타임존 관리를 위한 구성된 LocaleResolver 인스턴스
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.KOREA); // 기본 로케일 설정
        slr.setLocaleAttributeName("current.locale"); // 로케일 속성 이름 설정
        slr.setTimeZoneAttributeName("current.timezone"); // 타임존 속성 이름 설정
        return slr;
    }

    /**
     * 요청 파라미터를 사용하여 로케일 변경을 처리하기 위한 LocaleChangeInterceptor Bean을 생성 및 구성합니다.
     *
     * @return 파라미터 이름이 "language"로 설정된 구성된 LocaleChangeInterceptor 인스턴스
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("language"); // 파라미터 이름 설정
        return localeChangeInterceptor;
    }

    /**
     * ExtendReloadableResourceBundleMessageSource Bean을 MessageSourceProperties 객체를 사용하여 구성합니다.
     *
     * @param properties 메시지 소스의 기반 이름, 인코딩, 캐시 지속 시간 및 기타 설정을 포함하는 속성 객체
     * @return 구성된 MessageSource 인스턴스
     */
    @Bean
    public MessageSource messageSource(MessageSourceProperties properties) {

        ExtendReloadableResourceBundleMessageSource messageSource = new ExtendReloadableResourceBundleMessageSource();

        // BaseName이 있는 경우 처리
        if (StringUtils.hasText(String.valueOf(properties.getBasename()))) {
            String baseName = "";
            if (properties.getBasename().contains(",")) { // BaseName이 여러 개인 경우 처리
                baseName = String.valueOf(properties.getBasename());
            } else {
                baseName = String.valueOf(properties.getBasename().get(0));
            }
            baseName = StringUtils.trimAllWhitespace(baseName); // 공백 제거
            String[] baseNames = StringUtils.commaDelimitedListToStringArray(baseName); // 콤마로 구분된 BaseName 배열 생성
            messageSource.setBasenames(baseNames);
            messageSource.setCacheSeconds(3); // 캐시 시간 설정
        }

        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name()); // 기본 인코딩 설정
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale()); // 시스템 로케일로의 폴백 설정
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis()); // 캐시 지속 시간 설정
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat()); // 항상 메시지 형식 사용 여부 설정
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage()); // 메시지 코드 기본 메시지 사용 여부 설정

        return messageSource;
    }

}