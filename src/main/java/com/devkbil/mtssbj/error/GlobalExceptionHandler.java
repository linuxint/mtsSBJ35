package com.devkbil.mtssbj.error;

import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.json.JsonParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;

//@RestControllerAdvice
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final HttpStatus httpStatusOk = HttpStatus.OK;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        log.error("handleMethodArgumentNotValidException", ex);
        BindingResult bindingResult = ex.getBindingResult();
        StringBuilder stringBuilder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            stringBuilder.append(fieldError.getField()).append(":");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append(", ");
        }
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_VALID_ERROR, String.valueOf(stringBuilder));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * {@link MissingRequestHeaderException} 유형의 예외를 처리합니다.
     * 오류를 로깅하고 적절한 {@link ErrorResponse} 객체를 준비한 후 ResponseEntity로 반환합니다.
     *
     * @param ex 처리할 {@link MissingRequestHeaderException}, 누락된 요청 헤더 오류에 대한 세부 정보를 포함합니다.
     * @return 오류 세부 정보와 적합한 HTTP 상태 코드를 포함하는 {@link ErrorResponse} 객체를 가진 {@link ResponseEntity}.
     */
    @ExceptionHandler(MissingRequestHeaderException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        log.error("MissingRequestHeaderException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * HttpMessageNotReadableException을 처리합니다. 이 예외는 요청 본문이 누락되었거나
     * 형식 또는 구문 오류로 인해 원하는 객체로 역직렬화할 수 없을 때 발생합니다.
     *
     * @param ex 발생한 HttpMessageNotReadableException 인스턴스
     * @return 적절한 오류 세부 정보를 가진 ErrorResponse 객체를 포함하는 ResponseEntity
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        log.error("HttpMessageNotReadableException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * {@link MissingServletRequestParameterException} 유형의 예외를 처리합니다.
     * 이 메서드는 필수 요청 매개변수가 누락된 경우 호출됩니다.
     * 오류를 로깅하고 적절한 {@link ErrorResponse} 객체를 생성하여 400 Bad Request HTTP 상태와 함께 반환합니다.
     *
     * @param ex 필수 요청 매개변수가 누락되어 발생한 {@link MissingServletRequestParameterException}
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ErrorResponse> handleMissingRequestHeaderExceptionException(MissingServletRequestParameterException ex) {
        log.error("handleMissingServletRequestParameterException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.MISSING_REQUEST_PARAMETER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    /**
     * HttpClientErrorException.BadRequest 예외를 처리하고 적절한 ResponseEntity를 생성합니다.
     *
     * @param ex Bad Request 예외를 나타내는 HttpClientErrorException
     * @return 오류 코드와 메시지가 채워진 ErrorResponse 객체와 성공을 나타내는 HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<ErrorResponse> handleBadRequestException(HttpClientErrorException ex) {
        log.error("HttpClientErrorException.BadRequest", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.BAD_REQUEST_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * 요청된 처리기를 찾을 수 없을 때 발생하는 예외를 처리합니다. 
     * 예를 들어, 요청된 URI가 정의된 경로나 처리기와 일치하지 않을 때 {@link NoHandlerFoundException}에 의해 트리거됩니다.
     *
     * @param ex 처리기를 찾을 수 없음을 나타내는 예외
     * @return 오류 응답의 세부 정보를 포함하는 {@link ResponseEntity}
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    protected ResponseEntity<ErrorResponse> handleNoHandlerFoundExceptionException(NoHandlerFoundException ex) {
        log.error("handleNoHandlerFoundExceptionException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * 애플리케이션 실행 중에 발생한 NullPointerException을 처리합니다.
     * 이 메서드는 예외를 로깅하고 오류에 대한 세부 정보를 가진 적절한 응답을 반환합니다.
     *
     * @param ex 던져진 NullPointerException
     * @return ErrorResponse 객체와 연결된 HTTP 상태를 포함한 ResponseEntity
     */
    @ExceptionHandler(NullPointerException.class)
    protected ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex) {
        log.error("handleNullPointerException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.NULL_POINT_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * IOException을 처리하고 표준화된 오류 응답을 구성합니다.
     *
     * @param ex 발생한 IOException
     * @return 오류 세부 정보와 HTTP 상태를 가진 ErrorResponse를 포함하는 ResponseEntity
     */
    @ExceptionHandler(IOException.class)
    protected ResponseEntity<ErrorResponse> handleIOException(IOException ex) {
        log.error("handleIOException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.IO_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * {@link JsonParseException} 유형의 예외를 처리합니다. 
     * 이 메서드는 JSON 구문 분석 중 오류가 발생했을 때 호출됩니다.
     * 오류를 로깅하고 적절한 오류 응답을 생성합니다.
     *
     * @param ex JSON 파싱 중 발생한 {@link JsonParseException} 인스턴스
     * @return 적절한 HTTP 상태와 오류 세부 정보를 포함하는 {@link ResponseEntity}
     */
    @ExceptionHandler(JsonParseException.class)
    protected ResponseEntity<ErrorResponse> handleJsonParseExceptionException(JsonParseException ex) {
        log.error("handleJsonParseExceptionException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.JSON_PARSE_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * JsonProcessingException 유형의 예외를 처리하고 적절한 오류 응답을 반환합니다.
     *
     * @param ex 발생한 예외
     * @return 오류 응답과 HTTP 상태 코드를 포함하는 ResponseEntity
     */
    @ExceptionHandler(JsonProcessingException.class)
    protected ResponseEntity<ErrorResponse> handleJsonProcessingException(JsonProcessingException ex) {
        log.error("handleJsonProcessingException", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.REQUEST_BODY_MISSING_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    // ==================================================================================================================

    /**
     * 애플리케이션 내에서 발생하는 모든 예외를 처리합니다.
     * 이 메서드는 적절한 오류 응답을 생성하고 ResponseEntity로 반환합니다.
     *
     * @param ex 던져진 예외
     * @return 오류 응답과 해당 오류 코드 및 메시지가 포함된 ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    protected final ResponseEntity<ErrorResponse> handleAllExceptions(Exception ex) {
        log.error("Exception", ex);
        final ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, httpStatusOk);
    }

    /**
     * BusinessException에서 발생한 에러를 처리합니다.
     *
     * @param ex BusinessException
     * @return ResponseEntity
     */
    @ExceptionHandler(BusinessExceptionHandler.class)
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessExceptionHandler ex) {
        log.debug("===========================================================");
        log.debug("여기로 오는가?!");
        log.debug("===========================================================");

        final ErrorResponse response = ErrorResponse.of(ErrorCode.BUSINESS_EXCEPTION_ERROR, ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}