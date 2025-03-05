package com.devkbil.mtssbj.error;

import lombok.Builder;
import lombok.Getter;

/**
 * [공통] API Response 결과의 반환 값을 관리
 *
 * @param <T> API 응답 결과의 데이터 타입
 */
@Getter
public class ApiResponse<T> {

    // API 응답 결과 Response
    private T result;

    // API 응답 코드 Response
    private int resultCode;

    // API 응답 코드 Message
    private String resultMsg;

    /**
     * ApiResponse 생성자
     *
     * @param result     API 응답 결과 데이터
     * @param resultCode API 응답 코드
     * @param resultMsg  API 응답 메시지
     */
    @Builder
    public ApiResponse(final T result, final int resultCode, final String resultMsg) {
        this.result = result;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
    }

}