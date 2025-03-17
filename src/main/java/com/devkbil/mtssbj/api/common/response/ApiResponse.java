package com.devkbil.mtssbj.api.common.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.Builder;
import lombok.Getter;

/**
 * API 응답을 위한 공통 래퍼 클래스입니다.
 * 모든 REST API 응답은 이 클래스의 형식을 따릅니다.
 *
 * @param <T> 응답 데이터의 타입
 */
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorDetails error;
    private final LocalDateTime timestamp;

    /**
     * API 오류 상세 정보를 담는 내부 클래스입니다.
     */
    @Builder
    @Getter
    public static class ErrorDetails {
        private final String code;
        private final String message;
        private final Map<String, Object> details;
    }

    /**
     * 성공 응답을 생성합니다.
     *
     * @param data 응답 데이터
     * @param <T>  데이터 타입
     * @return 성공 응답 객체
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 오류 응답을 생성합니다.
     *
     * @param code    오류 코드
     * @param message 오류 메시지
     * @param <T>     데이터 타입
     * @return 오류 응답 객체
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(code)
                .message(message)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 상세 정보가 포함된 오류 응답을 생성합니다.
     *
     * @param code    오류 코드
     * @param message 오류 메시지
     * @param details 오류 상세 정보
     * @param <T>     데이터 타입
     * @return 오류 응답 객체
     */
    public static <T> ApiResponse<T> error(String code, String message, Map<String, Object> details) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code(code)
                .message(message)
                .details(details)
                .build();

        return ApiResponse.<T>builder()
                .success(false)
                .error(errorDetails)
                .timestamp(LocalDateTime.now())
                .build();
    }
}