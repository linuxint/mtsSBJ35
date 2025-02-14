package com.devkbil.mtssbj.develop.logview;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Logback 설정 클래스
 * - 개발용 로그 뷰어 연동을 위한 로그 설정을 초기화합니다.
 * - Logback Appender를 추가하여 로그를 처리하는 커스텀 Appender를 설정합니다.
 */
@Configuration
public class DevelopLogbackConfig implements InitializingBean {

    /**
     * Logback Appender 서비스 Bean 등록
     * - 로그 큐를 처리하기 위한 서비스입니다.
     */
    @Bean
    public DevelopLogbackAppenderService<ILoggingEvent> developLogbackAppenderService() {
        return new DevelopLogbackAppenderService<ILoggingEvent>();
    }

    /**
     * Logback Appender 초기화
     * - Logback의 `ROOT` Logger에 새로운 Appender를 추가하여 로그를 처리합니다.
     */
    @Override
    public void afterPropertiesSet() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        DevelopLogbackAppender<ILoggingEvent> developLogbackAppender = new DevelopLogbackAppender<>(developLogbackAppenderService());

        developLogbackAppender.setContext(loggerContext);
        developLogbackAppender.setName("developLogbackAppender");
        RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>) loggerContext.getLogger("ROOT").getAppender("FILE_APPENDER");
        developLogbackAppender.setEncoder(appender == null ? new PatternLayoutEncoder() : appender.getEncoder());

        developLogbackAppender.start();
        loggerContext.getLogger("ROOT").addAppender(developLogbackAppender);

    }

}
