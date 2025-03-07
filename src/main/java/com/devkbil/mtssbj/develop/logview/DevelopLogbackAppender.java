package com.devkbil.mtssbj.develop.logview;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * 로그 이벤트를 처리하는 Custom Logback Appender
 * <p>
 * 로그 이벤트를 로컬 큐에 저장하여 추가 처리를 위해
 * {@code DevelopLogbackAppenderService}와 통합합니다.
 * 로그 이벤트를 문자열 형식으로 변환하기 위해 인코더를 사용합니다.
 *
 * @param <E> 이 Appender가 처리하는 로그 이벤트의 타입
 */
@RequiredArgsConstructor
public class DevelopLogbackAppender<E> extends UnsynchronizedAppenderBase<E> {

    private final DevelopLogbackAppenderService<E> developLogbackAppenderService;

    @Setter
    private Encoder<E> encoder;

    /**
     * 로그 이벤트 객체를 로컬 큐에 추가하여 추가 처리합니다.
     * 이 메서드는 구성된 인코더를 사용하여 제공된 이벤트 객체를 문자열 형식으로 인코딩하고
     * 인코딩된 메시지와 이벤트 객체를 모두 {@code DevelopLogbackAppenderService}에 저장을 위해 전달합니다.
     * Appender가 시작되지 않은 경우 아무 작업도 수행하지 않고 종료됩니다.
     *
     * @param eventObject 처리 및 저장될 로그 이벤트 객체
     */
    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        developLogbackAppenderService.addLog(eventObject, new String(encoder.encode(eventObject)));
    }

}