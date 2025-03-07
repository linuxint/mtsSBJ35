package com.devkbil.mtssbj.common.listener;

import com.devkbil.mtssbj.common.events.CustomApplicationEvent;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * CustomApplicationEventListener는 {@link CustomApplicationEvent}를 수신하는 Spring 컴포넌트입니다.
 * 이 리스너는 사용자 정의 애플리케이션 이벤트를 처리하며, 이러한 이벤트가 Spring ApplicationContext에서
 * 게시될 때 지정된 작업을 수행합니다.
 *
 * {@link CustomApplicationEvent}는 사용자 정의 메시지를 포함하며, 이 리스너에서 접근하여 로그로 남길 수 있습니다.
 * 이를 통해 애플리케이션 실행 중 모니터링 또는 애플리케이션 관련 이벤트를 처리할 수 있습니다.
 *
 * 특징:
 * - {@link CustomApplicationEvent}로부터 수신한 메시지를 로그로 기록합니다.
 *
 * 이벤트 처리:
 * - {@link #handleCustomEvent(CustomApplicationEvent)} 메서드는 {@link EventListener}로 주석 처리되어 있습니다.
 * 이는 ApplicationContext 내에서 {@link CustomApplicationEvent} 유형의 이벤트를 자동으로 수신합니다.
 *
 * 의존성:
 * - 로깅을 위한 `Slf4j`가 필요합니다.
 * - Spring 컴포넌트로 자동 스캐닝하기 위한 `@Component`가 필요합니다.
 */
@Slf4j
@Component
public class CustomApplicationEventListener {

    /**
     * <p>{@link CustomApplicationEvent} 유형의 사용자 정의 애플리케이션 이벤트를 처리합니다.
     * 이 메서드는 {@link CustomApplicationEvent}가 Spring ApplicationContext에 게시되었을 때 자동으로 실행됩니다.</p>
     *
     * @param event 사용자 정의 애플리케이션 이벤트로, 메시지와 같은 이벤트 세부 정보를 포함합니다.
     */
    @EventListener
    public void handleCustomEvent(CustomApplicationEvent event) {
        log.info("CustomApplicationEvent received. Message: {}", event.getMessage());
    }
}
