package com.devkbil.mtssbj.common.listener;

import org.springframework.boot.context.event.ApplicationStartingEvent;
import org.springframework.context.ApplicationListener;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Framework에서 애플리케이션이 부팅 초기 단계에서 실행되는 {@link ApplicationStartingEvent}를 처리하는 리스너 클래스입니다.
 * <p>
 * {@link ApplicationStartingEvent}는 애플리케이션 실행의 가장 초기에 발생하는 이벤트로, 이때는 대부분의 주요 구성이 완료되지 않았습니다.
 * 로깅 설정 또는 환경 초기화 등 초기 상태에서 실행해야 할 작업을 수행하기 적합합니다.
 * <p>
 * 이 클래스는 이벤트 발생 시간과 초기 애플리케이션 상태 정보를 출력하기 위해 설계되었습니다.
 */
@Slf4j
public class ApplicationStartingEventListener implements ApplicationListener<ApplicationStartingEvent> {

    /**
     * 애플리케이션 시작 단계에서 트리거되는 {@link ApplicationStartingEvent}를 처리합니다.
     * 이벤트 발생 시간을 포함한 애플리케이션 시작 정보를 콘솔에 출력합니다.
     *
     * @param event 애플리케이션 시작 이벤트에 대한 세부 정보를 포함하는 {@link ApplicationStartingEvent}
     */
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        System.out.println("***********************************************************");
        System.out.println("*                                                         *");
        System.out.println("* ApplicationStartingEvent " + new Date(event.getTimestamp()));
        System.out.println("*                                                         *");
        System.out.println("***********************************************************");
    }
}