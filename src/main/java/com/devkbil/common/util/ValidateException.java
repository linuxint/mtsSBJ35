package com.devkbil.common.util;


import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Spring Boot ResponseStatusException 기반의 유효성 검증 예외
 */
public class ValidateException extends ResponseStatusException {

    public ValidateException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    public ValidateException(String message, Throwable cause) {
        super(HttpStatus.BAD_REQUEST, message, cause);
    }
}