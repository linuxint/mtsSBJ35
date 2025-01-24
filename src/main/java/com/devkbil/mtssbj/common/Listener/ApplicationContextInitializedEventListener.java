package com.devkbil.mtssbj.common.Listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

/**
 * Spring Framework에서 애플리케이션 컨텍스트가 초기화 단계에 도달했을 때 발생하는 {@link ApplicationContextInitializedEvent}를 처리하는 리스너 클래스입니다.
 *
 * {@link ApplicationContextInitializedEvent}는 {@code ApplicationContext}가 모든 설정 및 초기화 작업을 완료하고
 * 준비 완료 상태임을 나타냅니다. 이는 특히 애플리케이션 컨텍스트와 관련 있는 리소스가 초기화되었음을 의미합니다.
 *
 * 이 구현은 이벤트 발생 시간을 비롯한 컨텍스트 초기화 정보를 기록하거나 적절한 초기 작업을 수행할 수 있도록 설계되었습니다.
 */
public class ApplicationContextInitializedEventListener implements ApplicationListener<ApplicationContextInitializedEvent> {

    private final Logger logSystem = LoggerFactory.getLogger("SYSTEM");

    /**
     * 애플리케이션 컨텍스트 초기화 이벤트를 처리합니다.
     * 이 메서드는 초기화된 컨텍스트와 이벤트 관련 세부정보를 처리하거나 기록하기 위해 호출됩니다.
     *
     * @param event 애플리케이션 컨텍스트 초기화에 대한 세부 정보를 포함하는 {@link ApplicationContextInitializedEvent}
     */
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        logSystem.debug("***********************************************************");
        logSystem.debug("*                                                         *");
        logSystem.debug("* ApplicationContextInitializedEvent " + new Date(event.getTimestamp()));
        logSystem.debug("*                                                         *");
        logSystem.debug("***********************************************************");
    }
}
