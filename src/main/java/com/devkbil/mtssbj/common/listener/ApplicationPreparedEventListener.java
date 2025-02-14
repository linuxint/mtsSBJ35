package com.devkbil.mtssbj.common.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션 컨텍스트 초기화가 완료된 후,
 * 하지만 아직 활성화되기 전 단계에서 발생하는 {@link ApplicationPreparedEvent}를 처리하는 리스너 클래스입니다.
 *
 * {@link ApplicationPreparedEvent}는 컨텍스트 초기화가 완료되고 애플리케이션이 활성화되기 전에
 * 추가적인 설정 작업을 수행할 수 있는 마지막 단계입니다.
 *
 * 이 클래스는 초기화 정보를 로깅하거나 초기 설정 작업을 추가적으로 실행하기 위해 설계되었습니다.
 */
public class ApplicationPreparedEventListener implements ApplicationListener<ApplicationPreparedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 컨텍스트 초기화 이벤트를 처리합니다.
     * 이 메서드는 초기화 완료 후 컨텍스트 관련 정보를 로깅하거나 부가적인 초기화 작업을 수행할 수 있습니다.
     *
     * @param event 애플리케이션 컨텍스트 초기화에 대한 세부 정보를 포함하는 이벤트 객체
     */
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ApplicationPreparedEvent " + new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}
