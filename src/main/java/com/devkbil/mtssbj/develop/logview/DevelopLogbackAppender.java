package com.devkbil.mtssbj.develop.logview;

import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.encoder.Encoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Logback 커스텀 Appender
 * - Logback 로그를 큐에 저장하는 기능을 제공합니다.
 *
 * @param <E> 이벤트 타입
 */
@RequiredArgsConstructor
public class DevelopLogbackAppender<E> extends UnsynchronizedAppenderBase<E> {

    private final DevelopLogbackAppenderService<E> developLogbackAppenderService;

    @Setter
    private Encoder<E> encoder;

    /**
     * 로그 이벤트를 큐에 추가
     *
     * @param eventObject 로그 이벤트 객체
     */
    @Override
    protected void append(E eventObject) {
        if (!isStarted()) {
            return;
        }
        developLogbackAppenderService.addLog(eventObject, new String(encoder.encode(eventObject)));
    }

}
