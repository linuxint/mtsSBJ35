package com.devkbil.mtssbj.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션 컨텍스트가 새로 고침될 때 발생하는 {@link ContextRefreshedEvent}를 처리하는 리스너 클래스입니다.
 * <p>
 * Spring Framework에서 {@link ContextRefreshedEvent}는 애플리케이션 컨텍스트(ApplicationContext)가 초기화되거나
 * 새로 고침될(refresh) 때 트리거됩니다. 이는 컨텍스트가 설정된 후 준비 상태를 갖췄음을 나타냅니다.
 * <p>
 * 이 클래스는 해당 이벤트를 수신하여 디버깅 목적으로 이벤트 발생 시간과 관련 정보를 로깅하거나 필요한 초기화 작업을 수행할 수 있도록 설계되었습니다.
 */
public class ApplicationContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 컨텍스트 새로 고침 이벤트를 처리합니다.
     * 이벤트 발생 시간과 세부 정보를 디버깅 목적으로 로그로 기록합니다.
     *
     * @param event {@link ContextRefreshedEvent} 애플리케이션 컨텍스트 새로 고침의 세부 정보를 포함하는 객체
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ContextRefreshedEvent 발생 시간: {} ", new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}