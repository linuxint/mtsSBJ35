package com.devkbil.mtssbj.common.util;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * Java 8+ 시간 API를 사용하는 시간 측정 및 계산 유틸리티 클래스입니다.
 * 
 * 주요 기능:
 * - 시작/종료 시간 측정
 * - 경과 시간 계산
 * - 시간 형식 변환
 * - 두 시각 사이의 차이 계산
 * 
 * 이 클래스는 스레드 안전하며, Java 8의 시간 API를 사용하여 더 정확한 시간 계산을 제공합니다.
 */
@Slf4j
public class TimeUtil {
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // ThreadLocal을 사용하여 스레드별로 독립적인 시간 측정을 가능하게 함
    private static final ThreadLocal<Instant> startTime = new ThreadLocal<>();
    private static final ThreadLocal<Instant> endTime = new ThreadLocal<>();

    /**
     * ThreadLocal 리소스를 정리합니다.
     * 스레드 풀 환경에서는 스레드 재사용 전에 이 메소드를 호출해야 합니다.
     */
    public static void cleanup() {
        startTime.remove();
        endTime.remove();
    }

    private TimeUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 시작 시간을 현재 시각으로 설정합니다.
     */
    public static void setStartTime() {
        startTime.set(Instant.now());
    }

    /**
     * 종료 시간을 현재 시각으로 설정합니다.
     */
    public static void setEndTime() {
        endTime.set(Instant.now());
    }

    /**
     * 시작 시간과 종료 시간 사이의 경과 시간을 밀리초 단위로 반환합니다.
     *
     * @return 경과 시간 (밀리초), 시작/종료 시간이 설정되지 않은 경우 0 반환
     */
    public static long getDiffTime() {
        Instant start = startTime.get();
        Instant end = endTime.get();

        if (start == null || end == null) {
            return 0L;
        }

        return Duration.between(start, end).toMillis();
    }

    /**
     * 밀리초 단위의 시간을 "HH:mm:ss.SSS" 형식의 문자열로 변환합니다.
     *
     * @param millis 변환할 시간 (밀리초)
     * @return 형식화된 시간 문자열 (예: "01:30:45.123")
     */
    public static String formatTime(long millis) {
        Duration duration = Duration.ofMillis(millis);
        return String.format("%02d:%02d:%02d.%03d",
            duration.toHours() % 24,
            duration.toMinutesPart(),
            duration.toSecondsPart(),
            duration.toMillisPart());
    }

    /**
     * 두 시각 사이의 차이를 초 단위로 계산합니다.
     * 
     * 입력된 시각 문자열은 "yyyy-MM-dd" 또는 "yyyy-MM-dd HH:mm:ss" 형식이어야 합니다.
     *
     * @param fromTime 시작 시각 (문자열)
     * @param toTime 종료 시각 (문자열)
     * @return 두 시각 사이의 차이 (초 단위), 오류 발생시 0 반환
     */
    public static long diffTime(String fromTime, String toTime) {
        try {
            LocalDateTime from = parseDateTime(fromTime);
            LocalDateTime to = parseDateTime(toTime);
            return ChronoUnit.SECONDS.between(from, to);
        } catch (Exception e) {
            log.error("시간 차이 계산 중 오류 발생: fromTime={}, toTime={}", fromTime, toTime, e);
            return 0;
        }
    }

    /**
     * 문자열을 LocalDateTime으로 파싱합니다.
     * "yyyy-MM-dd HH:mm:ss" 또는 "yyyy-MM-dd" 형식을 지원합니다.
     * 날짜만 입력된 경우 시간은 00:00:00으로 설정됩니다.
     *
     * @param dateTimeStr 파싱할 날짜/시간 문자열
     * @return 파싱된 LocalDateTime
     * @throws Exception 파싱 실패시 발생
     */
    private static LocalDateTime parseDateTime(String dateTimeStr) throws Exception {
        try {
            return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
        } catch (Exception e) {
            // 날짜만 있는 경우 시도
            try {
                return LocalDate.parse(dateTimeStr, DATE_FORMATTER).atStartOfDay();
            } catch (Exception e2) {
                log.error("날짜 파싱 실패: {}", dateTimeStr, e2);
                throw e2;
            }
        }
    }

}
