package com.devkbil.mtssbj.develop.logview;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

/**
 * Logback 설정 클래스
 * - 개발용 로그 뷰어 연동을 위한 로그 설정을 초기화합니다.
 * - Logback Appender를 추가하여 로그를 처리하는 커스텀 Appender를 설정합니다.
 */
@Configuration
public class DevLogbackConfig {
    @Bean
    public DevLogbackAppenderService<ILoggingEvent> devLogbackAppenderService() {
        return new DevLogbackAppenderService<ILoggingEvent>();
    }

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void registerLogbackAppender() {
        DevLogbackAppenderService<ILoggingEvent> devLogbackAppenderService =
            applicationContext.getBean(DevLogbackAppenderService.class);
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        DevLogbackAppender<ILoggingEvent> devLogbackAppender = new DevLogbackAppender<>(devLogbackAppenderService);
        devLogbackAppender.setContext(loggerContext);
        devLogbackAppender.setName("devLogbackAppender");
        RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>) loggerContext.getLogger("ROOT").getAppender("FILE_APPENDER");
        devLogbackAppender.setEncoder(appender == null ? new PatternLayoutEncoder() : appender.getEncoder());
        devLogbackAppender.start();
        loggerContext.getLogger("ROOT").addAppender(devLogbackAppender);
    }
}