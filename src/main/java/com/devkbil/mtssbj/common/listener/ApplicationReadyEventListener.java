package com.devkbil.mtssbj.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션이 완전히 시작되어 요청을 받을 준비가 되었을 때 발생하는 {@link ApplicationReadyEvent}를 처리하는 리스너 클래스입니다.
 * <p>
 * {@link ApplicationReadyEvent}는 애플리케이션이 성공적으로 초기화된 후 마지막 단계에서 발생합니다.
 * 이는 모든 Bean이 생성되고 초기화된 후 애플리케이션이 클라이언트 요청을 수락할 준비가 되었음을 나타냅니다.
 * <p>
 * 이 클래스는 이벤트 발생 시간 및 관련 로그 정보를 기록하며, 애플리케이션 시작 후 초기화 작업을 수행하거나 로깅 목적으로 설계되었습니다.
 */
public class ApplicationReadyEventListener implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 요청을 받을 준비가 완료된 애플리케이션 상태를 로그로 기록합니다.
     * 이벤트 발생 시간을 포함한 실행 세부 정보를 로깅합니다.
     *
     * @param event {@link ApplicationReadyEvent} 애플리케이션 준비 상태 이벤트 객체
     */
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ApplicationReadyEvent 발생 시간: {}", new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}
