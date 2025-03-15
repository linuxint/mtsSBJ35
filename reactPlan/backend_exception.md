# 예외 처리 변경사항

## 공통 사항
1. 전역 예외 처리 도입
2. 비즈니스 예외 정의
3. 예외 응답 포맷 표준화
4. 유효성 검사 예외 처리
5. 보안 관련 예외 처리

## 예외 응답 클래스
### 변경 전
```java
// 별도의 예외 응답 클래스 없이 에러 페이지로 리다이렉트
```

### 변경 후
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<FieldError> fieldErrors;
    
    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FieldError {
        private String field;
        private String message;
    }
    
    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .build();
    }
    
    public static ErrorResponse of(HttpStatus status, BindingResult bindingResult, String path) {
        List<FieldError> fieldErrors = bindingResult.getFieldErrors()
            .stream()
            .map(error -> FieldError.builder()
                .field(error.getField())
                .message(error.getDefaultMessage())
                .build())
            .collect(Collectors.toList());
        
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message("입력값이 올바르지 않습니다.")
            .path(path)
            .fieldErrors(fieldErrors)
            .build();
    }
}
```

## 비즈니스 예외 클래스
### 변경 전
```java
// 별도의 비즈니스 예외 클래스 없이 RuntimeException 사용
```

### 변경 후
```java
public abstract class BusinessException extends RuntimeException {
    private final HttpStatus status;
    
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
}

public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}

public class DuplicateKeyException extends BusinessException {
    public DuplicateKeyException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}

public class InvalidPasswordException extends BusinessException {
    public InvalidPasswordException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}

public class AuthenticationException extends BusinessException {
    public AuthenticationException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}

public class AuthorizationException extends BusinessException {
    public AuthorizationException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
```

## 전역 예외 처리
### 변경 전
```java
// 별도의 전역 예외 처리 없이 각 컨트롤러에서 try-catch로 처리
```

### 변경 후
```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException e, HttpServletRequest request) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getStatus(), e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(e.getStatus()).body(response);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.BAD_REQUEST, e.getBindingResult(), request.getRequestURI());
        return ResponseEntity.badRequest().body(response);
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        log.error("AccessDeniedException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }
    
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        log.error("AuthenticationException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED, e.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception e, HttpServletRequest request) {
        log.error("Exception: ", e);
        ErrorResponse response = ErrorResponse.of(
            HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.", request.getRequestURI());
        return ResponseEntity.internalServerError().body(response);
    }
}
```

## 보안 예외 처리
### 변경 전
```java
// Spring Security의 기본 예외 처리 사용
```

### 변경 후
```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.UNAUTHORIZED, "인증에 실패했습니다.", request.getRequestURI());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
}

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        ErrorResponse errorResponse = ErrorResponse.of(
            HttpStatus.FORBIDDEN, "접근 권한이 없습니다.", request.getRequestURI());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), errorResponse);
    }
} 