package com.devkbil.mtssbj.common.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션의 환경 구성이 완료되었지만 {@link org.springframework.context.ApplicationContext}가
 * 완전히 초기화되기 전에 발생하는 {@link ApplicationEnvironmentPreparedEvent}를 처리하는 리스너 클래스입니다.
 *
 * {@link ApplicationEnvironmentPreparedEvent}는 `Environment`가 준비되고, 프로파일, 속성 및 기타 환경 정보가 초기화된 후 트리거됩니다.
 * 그러나 이 이벤트는 ApplicationContext가 생성되기 이전에 발생하므로 Bean 정의 등에 접근할 수는 없습니다.
 *
 * 이 클래스는 초기 환경과 관련된 세부 정보를 기록하거나 특정 동작을 실행하기 위해 설계되었습니다.
 */
public class ApplicationEnvironmentPreparedEventListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 환경 준비 이벤트를 처리합니다.
     * 이 메서드는 환경 준비 단계에서 발생하는 이벤트의 타임스탬프 및 관련 정보를 디버깅 목적으로 로그로 기록합니다.
     *
     * @param event 애플리케이션 환경 준비 이벤트에 대한 세부 정보를 포함하는 {@link ApplicationEnvironmentPreparedEvent} 객체
     */
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ApplicationEnvironmentPreparedEvent 발생 시간: " + new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}