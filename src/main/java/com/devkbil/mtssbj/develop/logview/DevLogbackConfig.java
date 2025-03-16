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
public class DevLogbackConfig implements InitializingBean {

    /**
     * {@code DevLogbackAppenderService}에 대한 Spring Bean을 생성하고 등록합니다.
     * 이 서비스는 로그 메시지를 저장하기 위해 로그 큐를 관리하며,
     * 큐의 크기를 제한하고 가득 찼을 경우 가장 오래된 항목을 제거합니다.
     *
     * @return 로그 이벤트를 처리하도록 구성된 {@code DevLogbackAppenderService}의 새 인스턴스
     */
    @Bean
    public DevLogbackAppenderService<ILoggingEvent> devLogbackAppenderService() {
        return new DevLogbackAppenderService<ILoggingEvent>();
    }

    /**
     * Logback Appender 초기화
     * - Logback의 `ROOT` Logger에 새로운 Appender를 추가하여 로그를 처리합니다.
     */
    @Override
    public void afterPropertiesSet() {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        DevLogbackAppender<ILoggingEvent> devLogbackAppender = new DevLogbackAppender<>(devLogbackAppenderService());

        devLogbackAppender.setContext(loggerContext);
        devLogbackAppender.setName("devLogbackAppender");
        RollingFileAppender<ILoggingEvent> appender = (RollingFileAppender<ILoggingEvent>) loggerContext.getLogger("ROOT").getAppender("FILE_APPENDER");
        devLogbackAppender.setEncoder(appender == null ? new PatternLayoutEncoder() : appender.getEncoder());

        devLogbackAppender.start();
        loggerContext.getLogger("ROOT").addAppender(devLogbackAppender);

    }

}