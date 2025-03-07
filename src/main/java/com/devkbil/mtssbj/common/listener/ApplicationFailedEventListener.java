package com.devkbil.mtssbj.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션 시작 도중 오류가 발생했을 때 발생하는 {@link ApplicationFailedEvent}를 처리하는 리스너 클래스입니다.
 * <p>
 * {@link ApplicationFailedEvent}는 애플리케이션이 부팅 과정에서 예외가 발생하고 정상적으로 시작되지 못했을 때 트리거됩니다.
 * 이는 디버깅 및 에러 처리 목적으로 유용하며, 추가적인 복구 작업이나 기록을 수행할 수 있습니다.
 * <p>
 * 이 클래스는 실패 관련 정보를 로그에 기록하거나 후속 작업을 실행하기 위해 설계되었습니다.
 */
public class ApplicationFailedEventListener implements ApplicationListener<ApplicationFailedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 시작 중 실패 이벤트를 처리합니다.
     * 이 메서드는 디버깅 목적으로 실패 이벤트 발생 시간과 관련 정보를 로그로 기록합니다.
     *
     * @param event 애플리케이션 실패에 대한 세부 정보를 포함하는 {@link ApplicationFailedEvent} 객체
     */
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ApplicationFailedEvent 발생 시간: {}", new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}