package com.devkbil.mtssbj.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;

/**
 * IOException 처리를 위한 유틸리티 클래스
 * <p>
 * 이 클래스는 IOException 발생 시 적절한 로깅과 사용자 피드백을 제공하는 메서드를 제공합니다.
 * 또한 전역 예외 처리를 위한 ExceptionHandler도 포함하고 있습니다.
 */
@Slf4j
@RestControllerAdvice
public class IOExceptionHandler {

    /**
     * IOException을 처리하는 전역 예외 핸들러
     * <p>
     * 이 메서드는 애플리케이션에서 발생하는 모든 IOException을 캐치하여
     * 로깅하고 사용자에게 적절한 오류 메시지를 반환합니다.
     *
     * @param ex 발생한 IOException
     * @return 오류 메시지와 상태 코드를 포함한 ResponseEntity
     */
    @ExceptionHandler(IOException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleIOException(IOException ex) {
        log.error("IOException 발생: {}", ex.getMessage(), ex);
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "파일 또는 네트워크 작업 중 오류가 발생했습니다.");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * IOException이 발생할 수 있는 작업을 안전하게 실행합니다.
     * <p>
     * 이 메서드는 IOException이 발생할 수 있는 작업을 실행하고,
     * 예외 발생 시 로깅 후 기본값을 반환합니다.
     *
     * @param supplier 실행할 작업
     * @param defaultValue 예외 발생 시 반환할 기본값
     * @param <T> 반환 타입
     * @return 작업 결과 또는 기본값
     */
    public static <T> T executeWithIOExceptionHandling(IOSupplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (IOException e) {
            log.error("IOException 발생: {}", e.getMessage(), e);
            return defaultValue;
        }
    }

    /**
     * IOException이 발생할 수 있는 작업을 안전하게 실행합니다.
     * <p>
     * 이 메서드는 IOException이 발생할 수 있는 작업을 실행하고,
     * 예외 발생 시 로깅 후 기본값 공급자를 통해 기본값을 반환합니다.
     *
     * @param supplier 실행할 작업
     * @param defaultValueSupplier 예외 발생 시 기본값을 제공할 공급자
     * @param <T> 반환 타입
     * @return 작업 결과 또는 기본값
     */
    public static <T> T executeWithIOExceptionHandling(IOSupplier<T> supplier, Supplier<T> defaultValueSupplier) {
        try {
            return supplier.get();
        } catch (IOException e) {
            log.error("IOException 발생: {}", e.getMessage(), e);
            return defaultValueSupplier.get();
        }
    }

    /**
     * IOException이 발생할 수 있는 작업을 안전하게 실행합니다 (반환값 없음).
     * <p>
     * 이 메서드는 IOException이 발생할 수 있는 작업을 실행하고,
     * 예외 발생 시 로깅만 수행합니다.
     *
     * @param runnable 실행할 작업
     */
    public static void executeWithIOExceptionHandling(IORunnable runnable) {
        try {
            runnable.run();
        } catch (IOException e) {
            log.error("IOException 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * IOException을 던질 수 있는 Supplier 인터페이스
     *
     * @param <T> 반환 타입
     */
    @FunctionalInterface
    public interface IOSupplier<T> {
        T get() throws IOException;
    }

    /**
     * IOException을 던질 수 있는 Runnable 인터페이스
     */
    @FunctionalInterface
    public interface IORunnable {
        void run() throws IOException;
    }
}