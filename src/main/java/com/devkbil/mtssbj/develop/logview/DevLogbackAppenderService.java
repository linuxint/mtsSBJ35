package com.devkbil.mtssbj.develop.logview;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Logback Appender 서비스 클래스
 * - 로컬 큐를 활용하여 로그 메시지를 저장합니다.
 * - 큐의 크기를 제한하며, 초과 시 가장 오래된 로그를 제거합니다.
 *
 * @param <E> 이벤트 객체의 타입
 */
@Slf4j
@RequiredArgsConstructor
public class DevLogbackAppenderService<E> {

    private static final int queueSize = 1000; // 저장할 로그 최대 개수

    @Getter
    private final Queue<LogObject<E>> logQueue = new LinkedBlockingQueue<>(queueSize);

    /**
     * 로그 추가
     * - 로그 메시지를 큐에 추가하며, 큐가 가득 찬 경우 가장 오래된 로그를 제거합니다.
     *
     * @param eventObject 로그 이벤트 객체
     * @param logMessage  로그 메시지
     */
    public void addLog(E eventObject, String logMessage) {
        // 로그뷰를 초과하면 가장 오래된 로그를 비우고 추가하는 식으로 동작
        if (logQueue.size() >= queueSize) {
            logQueue.remove();
        }
        logQueue.offer(new LogObject<E>(eventObject, logMessage));
    }

    /**
     * 로그 객체 클래스
     * - 이벤트와 메시지를 함께 저장합니다.
     *
     * @param <E> 이벤트 객체의 타입
     */
    @Data
    @AllArgsConstructor
    public static class LogObject<E> {
        E eventObject;
        String logMessage;
    }
}