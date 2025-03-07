package com.devkbil.mtssbj.error;

import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 전역 예외 처리기에서 발생한 에러에 대한 응답 처리를 관리하는 클래스.
 * 이 클래스는 다양한 에러 상황에 대한 일관된 응답 형식을 제공합니다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private int status;                 // HTTP 상태 코드
    private String divisionCode;        // 에러의 구분을 위한 내부 코드
    private String resultMsg;           // 사용자에게 표시할 에러 메시지
    private List<FieldError> errors;    // 폼 검증 등에서 발생한 필드별 상세 에러 목록
    private String reason;              // 에러의 상세 이유나 추가 설명

    /**
     * 기본 에러 응답 객체를 생성하는 생성자.
     *
     * @param code 에러 코드 정보를 담고 있는 ErrorCode 객체
     */
    @Builder
    protected ErrorResponse(final ErrorCode code) {
        this.resultMsg = code.getMessage();
        this.status = code.getStatus();
        this.divisionCode = code.getDivisionCode();
        this.errors = new ArrayList<>();
    }

    /**
     * 추가 설명이 포함된 에러 응답 객체를 생성하는 생성자.
     *
     * @param code   에러 코드 정보를 담고 있는 ErrorCode 객체
     * @param reason 에러에 대한 추가 설명이나 이유
     */
    @Builder
    protected ErrorResponse(final ErrorCode code, final String reason) {
        this.resultMsg = code.getMessage();
        this.status = code.getStatus();
        this.divisionCode = code.getDivisionCode();
        this.reason = reason;
    }

    /**
     * 필드 에러 목록이 포함된 에러 응답 객체를 생성하는 생성자.
     *
     * @param code   에러 코드 정보를 담고 있는 ErrorCode 객체
     * @param errors 필드별 상세 에러 정보 목록
     */
    @Builder
    protected ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.resultMsg = code.getMessage();
        this.status = code.getStatus();
        this.errors = errors;
        this.divisionCode = code.getDivisionCode();
    }

    /**
     * Spring의 BindingResult로부터 에러 응답 객체를 생성하는 팩토리 메소드.
     * 주로 폼 검증 실패 시 사용됩니다.
     *
     * @param code          에러 코드 정보를 담고 있는 ErrorCode 객체
     * @param bindingResult Spring의 폼 검증 결과 객체
     * @return 생성된 ErrorResponse 객체
     */
    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    /**
     * 기본 에러 응답 객체를 생성하는 팩토리 메소드.
     *
     * @param code 에러 코드 정보를 담고 있는 ErrorCode 객체
     * @return 생성된 ErrorResponse 객체
     */
    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    /**
     * 추가 설명이 포함된 에러 응답 객체를 생성하는 팩토리 메소드.
     *
     * @param code   에러 코드 정보를 담고 있는 ErrorCode 객체
     * @param reason 에러에 대한 추가 설명이나 이유
     * @return 생성된 ErrorResponse 객체
     */
    public static ErrorResponse of(final ErrorCode code, final String reason) {
        return new ErrorResponse(code, reason);
    }

    /**
     * 필드 단위의 상세 에러 정보를 담는 내부 클래스.
     * Spring의 BindingResult나 개별 필드 에러를 처리하는데 사용됩니다.
     */
    @Getter
    public static class FieldError {
        private final String field;     // 에러가 발생한 필드의 이름
        private final String value;     // 유효성 검사에서 거절된 값
        private final String reason;    // 에러의 구체적인 이유나 메시지

        /**
         * FieldError 객체를 생성합니다.
         *
         * @param field  에러가 발생한 필드 이름
         * @param value  거절된 값
         * @param reason 에러 이유
         */
        @Builder
        FieldError(String field, String value, String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        /**
         * 단일 필드 에러에 대한 FieldError 목록을 생성합니다.
         *
         * @param field  에러가 발생한 필드 이름
         * @param value  거절된 값
         * @param reason 에러 이유
         * @return 생성된 FieldError를 포함하는 리스트
         */
        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        /**
         * Spring의 BindingResult로부터 FieldError 목록을 생성합니다.
         *
         * @param bindingResult 폼 검증 결과를 담고 있는 BindingResult 객체
         * @return 변환된 FieldError 목록
         */
        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                .map(error -> new FieldError(
                    error.getField(),
                    error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                    error.getDefaultMessage()))
                .collect(Collectors.toList());
        }
    }
}