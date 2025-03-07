package com.devkbil.mtssbj.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션 컨텍스트가 종료될 때 발생하는 {@link ContextClosedEvent}를 처리하는 리스너 클래스입니다.
 * <p>
 * Spring Framework에서 {@link ContextClosedEvent}는 애플리케이션이 종료되는 시점에 발생하며, 애플리케이션 컨텍스트(ApplicationContext)가
 * 클린업 작업을 수행한 뒤 종료됨을 알립니다.
 * <p>
 * 이 클래스는 해당 이벤트를 수신하여 디버깅 목적으로 이벤트 발생 시간과 관련 정보를 로깅하거나 종료 시 필요한 작업을 수행하도록 설계되었습니다.
 */
public class ApplicationContextClosedEventListener implements ApplicationListener<ContextClosedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 컨텍스트 종료 이벤트를 처리합니다.
     * 이벤트 발생 시간과 세부 정보를 디버깅 목적으로 로그로 기록합니다.
     *
     * @param event {@link ContextClosedEvent} 애플리케이션 컨텍스트 종료 이벤트의 세부 정보를 포함하는 객체
     */
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ContextClosedEvent 발생 시간: {} ", new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}