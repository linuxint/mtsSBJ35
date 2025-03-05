package com.devkbil.mtssbj.error;

import lombok.Builder;
import lombok.Getter;

/**
 * 비즈니스 로직 관련 예외를 관리하기 위해 설계된 사용자 정의 예외 처리 클래스입니다.
 * RuntimeException을 확장하여 애플리케이션에서 체크되지 않은 예외를 던질 수 있습니다.
 * 이 클래스는 세부 메시지와 관련된 에러 코드를 포함한 예외를 생성할 수 있는 생성자를 제공합니다.
 */
public class BusinessExceptionHandler extends RuntimeException {

    @Getter
    private final ErrorCode errorCode;

    /**
     * 지정된 에러 메시지와 에러 코드로 새로운 BusinessExceptionHandler를 생성합니다.
     *
     * @param message   상세 에러 메시지.
     * @param errorCode 특정 에러 세부 정보를 나타내는 ErrorCode 인스턴스.
     */
    @Builder
    public BusinessExceptionHandler(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * 지정된 에러 코드로 새로운 BusinessExceptionHandler를 생성합니다.
     * 에러 메시지는 제공된 에러 코드에서 파생됩니다.
     *
     * @param errorCode 특정 에러 세부 정보를 나타내는 ErrorCode 인스턴스.
     */
    @Builder
    public BusinessExceptionHandler(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}