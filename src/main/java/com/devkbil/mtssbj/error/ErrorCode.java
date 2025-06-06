package com.devkbil.mtssbj.error;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [공통 코드] API 통신에 대한 '에러 코드'를 Enum 형태로 관리를 한다.
 * Global Error CodeList : 전역으로 발생하는 에러코드를 관리한다.
 * Custom Error CodeList : 업무 페이지에서 발생하는 에러코드를 관리한다
 * Error Code Constructor : 에러코드를 직접적으로 사용하기 위한 생성자를 구성한다.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public enum ErrorCode {

    /**
     * ******************************* Global Error CodeList ***************************************
     * HTTP Status Code
     * 400 : Bad Request
     * 401 : Unauthorized
     * 403 : Forbidden
     * 404 : Not Found
     * 500 : Internal Server Error
     * *********************************************************************************************
     */
    BAD_REQUEST_ERROR(400, "G001", "Bad Request Exception"),                    // 잘못된 서버 요청
    REQUEST_BODY_MISSING_ERROR(400, "G002", "Required request body is missing"),    // @ModelAttribute 데이터 미 존재
    INVALID_TYPE_VALUE(400, "G003", " Invalid Type Value"),                     // 유효하지 않은 타입
    MISSING_REQUEST_PARAMETER_ERROR(400, "G004", "Missing Servlet RequestParameter Exception"), // Request Parameter 로 데이터가 전달되지 않을 경우
    IO_ERROR(400, "G005", "I/O Exception"),                                     // 입력/출력 값이 유효하지 않음
    JSON_PARSE_ERROR(400, "G006", "JsonParseException"),                        // com.google.gson JSON 파싱 실패
    JACKSON_PROCESS_ERROR(400, "G007", "com.fasterxml.jackson.core Exception"),// com.fasterxml.jackson.core Processing Error
    UNAUTHORIZED_ERROR(401, "G401", "Unauthorized Exception"),
    FORBIDDEN_ERROR(403, "G008", "Forbidden Exception"),                        // 권한이 없음
    NOT_FOUND_ERROR(404, "G009", "Not Found Exception"),                        // 서버로 요청한 리소스가 존재하지 않음
    NULL_POINT_ERROR(404, "G010", "Null Point Exception"),                      // NULL Point Exception 발생
    NOT_VALID_ERROR(404, "G011", "handle Validation Exception"),                // @ModelAttribute 및 @RequestParam, @PathVariable 값이 유효하지 않음
    NOT_VALID_HEADER_ERROR(404, "G012", "Header에 데이터가 존재하지 않는 경우 "),      // @ModelAttribute 및 @RequestParam, @PathVariable 값이 유효하지 않음
    INTERNAL_SERVER_ERROR(500, "G999", "Internal Server Error Exception"),      // 서버가 처리 할 방법을 모르는 경우 발생

    /**
     * ******************************* Custom Error CodeList ***************************************
     */
    INSERT_ERROR(200, "9999", "Insert Transaction Error Exception"),    // Transaction Insert Error
    UPDATE_ERROR(200, "9999", "Update Transaction Error Exception"),    // Transaction Update Error
    DELETE_ERROR(200, "9999", "Delete Transaction Error Exception"),    // Transaction Delete Error
    BUSINESS_EXCEPTION_ERROR(200, "B999", "Business Exception Error");  // 비즈니스 로직 처리 중 예외가 발생한 경우

    /**
     * ******************************* Error Code Constructor ***************************************
     */
    private int status;             // 에러 코드의 '코드 상태'을 반환한다.
    private String divisionCode;    // 에러 코드의 '코드간 구분 값'을 반환한다.
    private String message;         // 에러 코드의 '코드 메시지'을 반환한다.

    // 생성자 구성
    ErrorCode(int status, String divisionCode, String message) {
        this.status = status;
        this.divisionCode = divisionCode;
        this.message = message;
    }
}