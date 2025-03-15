# React.js 전환을 위한 서버 프로그램 공통화 설계 문서

## 1. 개요

이 문서는 기존 프로젝트를 React.js로 전환할 때 서버 프로그램의 공통화 관점에서의 설계를 정의합니다. REST API 기반의 백엔드 아키텍처를 통해 React.js 프론트엔드와 효율적으로 통신하고, 재사용 가능한 컴포넌트와 일관된 패턴을 적용하여 유지보수성과 확장성을 높이는 것을 목표로 합니다.

## 2. 서버 아키텍처 개요

### 2.1 계층 구조

서버 프로그램은 다음과 같은 계층 구조로 설계합니다:

1. **API 계층**: 클라이언트 요청을 처리하는 REST 컨트롤러
2. **서비스 계층**: 비즈니스 로직을 구현하는 서비스 컴포넌트
3. **데이터 접근 계층**: 데이터베이스와의 상호작용을 담당하는 리포지토리
4. **도메인 계층**: 비즈니스 엔티티와 규칙을 정의하는 도메인 모델
5. **공통 계층**: 여러 계층에서 사용되는 공통 컴포넌트, 유틸리티, 설정

### 2.2 패키지 구조

```
com.devkbil.mtssbj
├── api                  # API 계층 (컨트롤러)
│   ├── v1               # API 버전 1
│   │   ├── auth         # 인증 관련 API
│   │   ├── board        # 게시판 관련 API
│   │   ├── member       # 회원 관련 API
│   │   ├── schedule     # 일정 관련 API
│   │   └── admin        # 관리자 관련 API
│   └── common           # API 공통 컴포넌트
│       ├── advice       # 컨트롤러 어드바이스
│       ├── dto          # 공통 DTO
│       └── response     # 응답 래퍼
├── service              # 서비스 계층
│   ├── auth             # 인증 관련 서비스
│   ├── board            # 게시판 관련 서비스
│   ├── member           # 회원 관련 서비스
│   ├── schedule         # 일정 관련 서비스
│   └── admin            # 관리자 관련 서비스
├── domain               # 도메인 계층
│   ├── entity           # 엔티티 클래스
│   ├── repository       # 리포지토리 인터페이스
│   ├── event            # 도메인 이벤트
│   └── vo               # 값 객체
├── common               # 공통 계층
│   ├── config           # 설정 클래스
│   ├── security         # 보안 관련 클래스
│   ├── exception        # 예외 클래스
│   ├── util             # 유틸리티 클래스
│   └── aop              # AOP 컴포넌트
└── infrastructure       # 인프라스트럭처 계층
    ├── cache            # 캐싱 관련 구현
    ├── file             # 파일 처리 관련 구현
    ├── email            # 이메일 관련 구현
    └── sms              # SMS 관련 구현
```

## 3. REST API 표준화

### 3.1 URL 설계 원칙

1. **리소스 중심 설계**: URL은 명사 형태의 리소스를 표현
2. **계층 구조 반영**: 리소스 간의 관계를 URL 구조에 반영
3. **버전 관리**: API 버전을 URL에 포함 (`/api/v1/*`)
4. **일관된 복수형 사용**: 리소스 컬렉션은 복수형으로 표현
5. **소문자와 하이픈 사용**: URL은 소문자와 하이픈으로 구성

### 3.2 HTTP 메서드 활용

| 메서드 | 용도 | 예시 |
|--------|------|------|
| GET | 리소스 조회 | `/api/v1/boards` (목록 조회)<br>`/api/v1/boards/{id}` (상세 조회) |
| POST | 리소스 생성 | `/api/v1/boards` (게시글 생성) |
| PUT | 리소스 전체 수정 | `/api/v1/boards/{id}` (게시글 전체 수정) |
| PATCH | 리소스 부분 수정 | `/api/v1/boards/{id}` (게시글 일부 수정) |
| DELETE | 리소스 삭제 | `/api/v1/boards/{id}` (게시글 삭제) |

### 3.3 표준 API 엔드포인트 패턴

#### 3.3.1 인증 및 사용자 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 로그인 | `/api/v1/auth/login` | POST | 사용자 로그인 | PUBLIC |
| 로그아웃 | `/api/v1/auth/logout` | POST | 사용자 로그아웃 | USER |
| 토큰 갱신 | `/api/v1/auth/refresh` | POST | 액세스 토큰 갱신 | PUBLIC |
| 회원 | `/api/v1/members` | GET | 회원 목록 조회 | ADMIN |
| | `/api/v1/members/{id}` | GET | 회원 상세 조회 | USER |
| | `/api/v1/members` | POST | 회원 가입 | PUBLIC |
| | `/api/v1/members/{id}` | PUT | 회원 정보 전체 수정 | USER |
| | `/api/v1/members/{id}` | PATCH | 회원 정보 부분 수정 | USER |
| | `/api/v1/members/{id}` | DELETE | 회원 삭제 | ADMIN |
| | `/api/v1/members/me` | GET | 내 정보 조회 | USER |
| | `/api/v1/members/me` | PUT | 내 정보 수정 | USER |
| | `/api/v1/members/password` | PATCH | 비밀번호 변경 | USER |

#### 3.3.2 게시판 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 게시판 설정 | `/api/v1/boards/config` | GET | 게시판 설정 목록 조회 | USER |
| | `/api/v1/boards/config/{id}` | GET | 게시판 설정 상세 조회 | USER |
| | `/api/v1/boards/config` | POST | 게시판 설정 생성 | ADMIN |
| | `/api/v1/boards/config/{id}` | PUT | 게시판 설정 수정 | ADMIN |
| | `/api/v1/boards/config/{id}` | DELETE | 게시판 설정 삭제 | ADMIN |
| 게시글 | `/api/v1/boards/{boardId}/posts` | GET | 게시글 목록 조회 | USER |
| | `/api/v1/boards/{boardId}/posts/{id}` | GET | 게시글 상세 조회 | USER |
| | `/api/v1/boards/{boardId}/posts` | POST | 게시글 생성 | USER |
| | `/api/v1/boards/{boardId}/posts/{id}` | PUT | 게시글 수정 | USER |
| | `/api/v1/boards/{boardId}/posts/{id}` | DELETE | 게시글 삭제 | USER |
| 댓글 | `/api/v1/boards/{boardId}/posts/{postId}/comments` | GET | 댓글 목록 조회 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/comments` | POST | 댓글 작성 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/comments/{id}` | PUT | 댓글 수정 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/comments/{id}` | DELETE | 댓글 삭제 | USER |
| 첨부파일 | `/api/v1/boards/{boardId}/posts/{postId}/attachments` | GET | 첨부파일 목록 조회 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/attachments` | POST | 첨부파일 업로드 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/attachments/{id}` | GET | 첨부파일 다운로드 | USER |
| | `/api/v1/boards/{boardId}/posts/{postId}/attachments/{id}` | DELETE | 첨부파일 삭제 | USER |

#### 3.3.3 공통 코드 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 코드 그룹 | `/api/v1/codes/groups` | GET | 코드 그룹 목록 조회 | USER |
| | `/api/v1/codes/groups/{id}` | GET | 코드 그룹 상세 조회 | USER |
| | `/api/v1/codes/groups` | POST | 코드 그룹 생성 | ADMIN |
| | `/api/v1/codes/groups/{id}` | PUT | 코드 그룹 수정 | ADMIN |
| | `/api/v1/codes/groups/{id}` | DELETE | 코드 그룹 삭제 | ADMIN |
| 코드 | `/api/v1/codes/groups/{groupId}/codes` | GET | 코드 목록 조회 | USER |
| | `/api/v1/codes/groups/{groupId}/codes/{id}` | GET | 코드 상세 조회 | USER |
| | `/api/v1/codes/groups/{groupId}/codes` | POST | 코드 생성 | ADMIN |
| | `/api/v1/codes/groups/{groupId}/codes/{id}` | PUT | 코드 수정 | ADMIN |
| | `/api/v1/codes/groups/{groupId}/codes/{id}` | DELETE | 코드 삭제 | ADMIN |

#### 3.3.4 메일 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 메일 | `/api/v1/mails` | GET | 메일 목록 조회 | USER |
| | `/api/v1/mails/{id}` | GET | 메일 상세 조회 | USER |
| | `/api/v1/mails` | POST | 메일 발송 | USER |
| | `/api/v1/mails/{id}` | DELETE | 메일 삭제 | USER |
| | `/api/v1/mails/drafts` | GET | 임시보관함 목록 조회 | USER |
| | `/api/v1/mails/drafts` | POST | 임시보관함 저장 | USER |
| 메일 첨부파일 | `/api/v1/mails/{mailId}/attachments` | GET | 첨부파일 목록 조회 | USER |
| | `/api/v1/mails/{mailId}/attachments` | POST | 첨부파일 업로드 | USER |
| | `/api/v1/mails/{mailId}/attachments/{id}` | GET | 첨부파일 다운로드 | USER |
| | `/api/v1/mails/{mailId}/attachments/{id}` | DELETE | 첨부파일 삭제 | USER |

#### 3.3.5 일정 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 일정 | `/api/v1/schedules` | GET | 일정 목록 조회 | USER |
| | `/api/v1/schedules/{id}` | GET | 일정 상세 조회 | USER |
| | `/api/v1/schedules` | POST | 일정 생성 | USER |
| | `/api/v1/schedules/{id}` | PUT | 일정 수정 | USER |
| | `/api/v1/schedules/{id}` | DELETE | 일정 삭제 | USER |
| | `/api/v1/schedules/calendar` | GET | 캘린더 뷰 일정 조회 | USER |
| 일정 참석자 | `/api/v1/schedules/{scheduleId}/attendees` | GET | 참석자 목록 조회 | USER |
| | `/api/v1/schedules/{scheduleId}/attendees` | POST | 참석자 추가 | USER |
| | `/api/v1/schedules/{scheduleId}/attendees/{id}` | DELETE | 참석자 삭제 | USER |

#### 3.3.6 프로젝트 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 프로젝트 | `/api/v1/projects` | GET | 프로젝트 목록 조회 | USER |
| | `/api/v1/projects/{id}` | GET | 프로젝트 상세 조회 | USER |
| | `/api/v1/projects` | POST | 프로젝트 생성 | MANAGER |
| | `/api/v1/projects/{id}` | PUT | 프로젝트 수정 | MANAGER |
| | `/api/v1/projects/{id}` | DELETE | 프로젝트 삭제 | MANAGER |
| 업무 | `/api/v1/projects/{projectId}/tasks` | GET | 업무 목록 조회 | USER |
| | `/api/v1/projects/{projectId}/tasks/{id}` | GET | 업무 상세 조회 | USER |
| | `/api/v1/projects/{projectId}/tasks` | POST | 업무 생성 | USER |
| | `/api/v1/projects/{projectId}/tasks/{id}` | PUT | 업무 수정 | USER |
| | `/api/v1/projects/{projectId}/tasks/{id}` | DELETE | 업무 삭제 | USER |
| 업무 담당자 | `/api/v1/projects/{projectId}/tasks/{taskId}/assignees` | GET | 담당자 목록 조회 | USER |
| | `/api/v1/projects/{projectId}/tasks/{taskId}/assignees` | POST | 담당자 추가 | USER |
| | `/api/v1/projects/{projectId}/tasks/{taskId}/assignees/{id}` | DELETE | 담당자 삭제 | USER |

#### 3.3.7 파일 관리 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 파일 | `/api/v1/files` | POST | 파일 업로드 | USER |
| | `/api/v1/files/{id}` | GET | 파일 다운로드 | USER |
| | `/api/v1/files/{id}` | DELETE | 파일 삭제 | USER |
| | `/api/v1/files/temp` | POST | 임시 파일 업로드 | USER |
| | `/api/v1/files/temp/{token}` | GET | 임시 파일 다운로드 | USER |

#### 3.3.8 관리자 API

| 리소스 | 엔드포인트 | 메서드 | 설명 | 권한 |
|--------|------------|--------|------|------|
| 시스템 설정 | `/api/v1/admin/settings` | GET | 시스템 설정 조회 | ADMIN |
| | `/api/v1/admin/settings` | PUT | 시스템 설정 수정 | ADMIN |
| 사용자 관리 | `/api/v1/admin/users` | GET | 사용자 목록 조회 | ADMIN |
| | `/api/v1/admin/users/{id}` | GET | 사용자 상세 조회 | ADMIN |
| | `/api/v1/admin/users/{id}` | PUT | 사용자 정보 수정 | ADMIN |
| | `/api/v1/admin/users/{id}/status` | PATCH | 사용자 상태 변경 | ADMIN |
| 권한 관리 | `/api/v1/admin/roles` | GET | 권한 목록 조회 | ADMIN |
| | `/api/v1/admin/roles/{id}` | GET | 권한 상세 조회 | ADMIN |
| | `/api/v1/admin/roles` | POST | 권한 생성 | ADMIN |
| | `/api/v1/admin/roles/{id}` | PUT | 권한 수정 | ADMIN |
| | `/api/v1/admin/roles/{id}` | DELETE | 권한 삭제 | ADMIN |
| 감사 로그 | `/api/v1/admin/audit-logs` | GET | 감사 로그 조회 | ADMIN |
| | `/api/v1/admin/audit-logs/{id}` | GET | 감사 로그 상세 조회 | ADMIN |

## 4. 공통 응답 형식

### 4.1 기본 응답 구조

모든 API 응답은 일관된 형식을 따릅니다:

```json
{
  "success": true,
  "data": {
    "field1": "value1",
    "field2": "value2"
  },
  "error": null,
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

<!-- 실제 응답 데이터는 API에 따라 다양한 형태로 제공됩니다 -->

오류 발생 시:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "오류 메시지",
    "details": {
      "field1": "오류 상세 정보",
      "validationErrors": ["입력값이 유효하지 않습니다"]
    }
  },
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

<!-- 오류 정보는 상황에 따라 다양한 형태로 제공됩니다 -->

### 4.2 페이징 응답 구조

페이징된 데이터 조회 시:

```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "첫 번째 항목",
        "description": "항목 설명"
      },
      {
        "id": 2,
        "title": "두 번째 항목",
        "description": "항목 설명"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10,
      "sort": {
        "sorted": true,
        "unsorted": false,
        "empty": false
      },
      "offset": 0,
      "paged": true,
      "unpaged": false
    },
    "totalPages": 5,
    "totalElements": 42,
    "last": false,
    "size": 10,
    "number": 0,
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "numberOfElements": 10,
    "first": true,
    "empty": false
  },
  "error": null,
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

### 4.3 응답 래퍼 클래스

```java
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorDetails error;
    private final LocalDateTime timestamp;

    @Builder
    @Getter
    public static class ErrorDetails {
        private final String code;
        private final String message;
        private final Map<String, Object> details;
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

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
```

## 5. 인증 및 권한 부여 시스템

### 5.1 JWT 기반 인증

#### 5.1.1 토큰 구조

- **액세스 토큰**: 짧은 수명(30분~2시간)의 인증 토큰
- **리프레시 토큰**: 긴 수명(1~2주)의 토큰 갱신용 토큰

#### 5.1.2 인증 흐름

1. 클라이언트가 사용자 자격 증명으로 로그인 요청
2. 서버가 자격 증명 검증 후 액세스 토큰과 리프레시 토큰 발급
3. 클라이언트는 모든 요청에 액세스 토큰을 포함하여 전송
4. 액세스 토큰 만료 시 리프레시 토큰으로 새 액세스 토큰 요청
5. 리프레시 토큰 만료 시 재로그인 필요

#### 5.1.3 토큰 저장 및 전송

- 클라이언트: 액세스 토큰은 메모리에, 리프레시 토큰은 HttpOnly 쿠키에 저장
- 서버: 리프레시 토큰은 데이터베이스에 저장하여 무효화 가능하게 함
- 전송: Authorization 헤더에 Bearer 스키마로 전송

### 5.2 권한 부여 시스템

#### 5.2.1 역할 기반 접근 제어 (RBAC)

기본 역할 구조:

- **ROLE_USER**: 일반 사용자 권한
- **ROLE_ADMIN**: 관리자 권한
- **ROLE_MANAGER**: 중간 관리자 권한

#### 5.2.2 권한 기반 접근 제어 (PBAC)

세부 권한 구조:

- **BOARD_READ**: 게시판 읽기 권한
- **BOARD_WRITE**: 게시판 쓰기 권한
- **BOARD_EDIT**: 게시판 수정 권한
- **BOARD_DELETE**: 게시판 삭제 권한
- **MEMBER_READ**: 회원 정보 읽기 권한
- **MEMBER_EDIT**: 회원 정보 수정 권한
- **SCHEDULE_READ**: 일정 읽기 권한
- **SCHEDULE_WRITE**: 일정 생성 권한
- **SCHEDULE_EDIT**: 일정 수정 권한
- **SCHEDULE_DELETE**: 일정 삭제 권한

#### 5.2.3 메서드 수준 보안

Spring Security의 메서드 수준 보안을 활용하여 세밀한 권한 제어:

```java
@PreAuthorize("hasRole('ADMIN')")
public List<MemberDTO> getAllMembers() {
    // 관리자만 모든 회원 정보 조회 가능
}

@PreAuthorize("hasRole('USER') and @boardPermissionEvaluator.canEdit(#boardId, principal)")
public BoardDTO updateBoard(Long boardId, BoardUpdateDTO boardUpdateDTO) {
    // 자신이 작성한 게시글만 수정 가능
}

@PostAuthorize("returnObject.createdBy == principal.username")
public ScheduleDTO getScheduleById(Long scheduleId) {
    // 자신이 생성한 일정만 조회 결과 반환
}
```

#### 5.2.4 커스텀 권한 평가기

복잡한 권한 로직을 위한 커스텀 권한 평가기:

```java
@Component
public class BoardPermissionEvaluator {

    private final BoardRepository boardRepository;

    public BoardPermissionEvaluator(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    public boolean canEdit(Long boardId, Authentication authentication) {
        if (authentication == null) {
            return false;
        }

        // 관리자는 모든 게시글 수정 가능
        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        // 작성자만 수정 가능
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board == null) {
            return false;
        }

        return board.getCreatedBy().equals(authentication.getName());
    }
}
```

## 6. 오류 처리 및 예외 관리

### 6.1 예외 계층 구조

```
RuntimeException
└── BaseException
    ├── ResourceNotFoundException
    │   ├── MemberNotFoundException
    │   ├── BoardNotFoundException
    │   └── ScheduleNotFoundException
    ├── InvalidRequestException
    │   ├── InvalidCredentialsException
    │   └── InvalidParameterException
    ├── BusinessException
    │   ├── DuplicateResourceException
    │   └── ResourceStateException
    ├── SecurityException
    │   ├── AuthenticationException
    │   └── AuthorizationException
    └── SystemException
        ├── ExternalSystemException
        └── InternalSystemException
```

### 6.2 전역 예외 처리기

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage()));
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRequestException(InvalidRequestException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("INVALID_REQUEST", ex.getMessage()));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ApiResponse<Void>> handleSecurityException(SecurityException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("SECURITY_ERROR", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> validationErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> 
                validationErrors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("VALIDATION_ERROR", "입력값 검증에 실패했습니다.", validationErrors));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        log.error("Unhandled exception occurred", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."));
    }
}
```

### 6.3 예외 발생 및 처리 전략

1. **명시적 예외 발생**: 비즈니스 규칙 위반 시 적절한 예외 발생
2. **예외 변환**: 저수준 예외를 의미 있는 비즈니스 예외로 변환
3. **예외 로깅**: 중요 예외는 적절한 로그 레벨로 기록
4. **사용자 친화적 메시지**: 최종 사용자에게는 기술적 상세 정보 없이 이해하기 쉬운 메시지 제공

## 7. 데이터 검증 전략

### 7.1 계층별 검증 책임

1. **API 계층**: 기본적인 입력값 형식 및 필수 필드 검증
2. **서비스 계층**: 비즈니스 규칙 및 도메인 로직 검증
3. **데이터 접근 계층**: 데이터 무결성 및 제약 조건 검증

### 7.2 Bean Validation 활용

```java
public class MemberCreateRequestDTO {

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "사용자 이름은 4-20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "사용자 이름은 영문자, 숫자, 특수문자(._-)만 포함할 수 있습니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 50, message = "이름은 최대 50자까지 입력 가능합니다.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;
}
```

### 7.3 커스텀 유효성 검증기

```java
@Documented
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUsername {
    String message() default "이미 사용 중인 사용자 이름입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {

    private final MemberRepository memberRepository;

    public UniqueUsernameValidator(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true; // null 값은 @NotNull로 검증
        }

        return !memberRepository.existsByUsername(username);
    }
}
```

## 8. 공통 컴포넌트 및 유틸리티

### 8.1 공통 유틸리티 클래스

#### 8.1.1 문자열 유틸리티

```java
@Component
public class StringUtils {

    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$")
                .matcher(email)
                .matches();
    }

    public String maskPersonalInfo(String info, MaskingType type) {
        if (isEmpty(info)) return info;

        switch (type) {
            case EMAIL:
                return maskEmail(info);
            case PHONE:
                return maskPhone(info);
            case NAME:
                return maskName(info);
            default:
                return info;
        }
    }

    // 구현 메서드들...
}
```

#### 8.1.2 날짜 유틸리티

```java
@Component
public class DateUtils {

    public LocalDate parseLocalDate(String dateStr, String format) {
        if (StringUtils.isEmpty(dateStr)) return null;
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format));
    }

    public String formatLocalDate(LocalDate date, String format) {
        if (date == null) return null;
        return date.format(DateTimeFormatter.ofPattern(format));
    }

    public boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        if (date == null) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    // 구현 메서드들...
}
```

### 8.2 AOP 컴포넌트

#### 8.2.1 로깅 AOP

```java
@Aspect
@Component
public class LoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(LoggingAspect.class);

    @Around("execution(* com.devkbil.mtssbj.api..*Controller.*(..))")
    public Object logControllerMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.info("Starting {} in {}", methodName, className);

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long endTime = System.currentTimeMillis();

        log.info("Completed {} in {} ({}ms)", methodName, className, (endTime - startTime));

        return result;
    }
}
```

#### 8.2.2 성능 모니터링 AOP

```java
@Aspect
@Component
public class PerformanceMonitoringAspect {

    private static final Logger log = LoggerFactory.getLogger(PerformanceMonitoringAspect.class);

    @Around("@annotation(com.devkbil.mtssbj.common.annotation.MonitorPerformance)")
    public Object monitorPerformance(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;

        if (executionTime > 1000) {
            log.warn("Performance issue: {}#{} took {}ms", className, methodName, executionTime);
        } else {
            log.debug("{}#{} executed in {}ms", className, methodName, executionTime);
        }

        return result;
    }
}
```

### 8.3 이벤트 기반 아키텍처

#### 8.3.1 도메인 이벤트

```java
public abstract class DomainEvent {
    private final LocalDateTime timestamp;
    private final String source;

    protected DomainEvent(String source) {
        this.timestamp = LocalDateTime.now();
        this.source = source;
    }

    // Getters...
}

public class MemberCreatedEvent extends DomainEvent {
    private final Long memberId;
    private final String username;

    public MemberCreatedEvent(Long memberId, String username) {
        super("MemberService");
        this.memberId = memberId;
        this.username = username;
    }

    // Getters...
}
```

#### 8.3.2 이벤트 발행자 및 구독자

```java
@Component
public class EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

@Component
public class MemberEventListener {

    private static final Logger log = LoggerFactory.getLogger(MemberEventListener.class);
    private final EmailService emailService;

    public MemberEventListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void handleMemberCreatedEvent(MemberCreatedEvent event) {
        log.info("Handling MemberCreatedEvent for member: {}", event.getUsername());

        // 환영 이메일 발송
        emailService.sendWelcomeEmail(event.getMemberId(), event.getUsername());

        // 기타 후속 처리...
    }
}
```

## 9. 서버 컴포넌트 재사용 패턴

### 9.1 추상 클래스 및 인터페이스 활용

#### 9.1.1 기본 서비스 인터페이스

```java
public interface CrudService<T, ID> {
    T findById(ID id);
    List<T> findAll();
    T create(T entity);
    T update(ID id, T entity);
    void delete(ID id);
}

public abstract class AbstractCrudService<T, ID> implements CrudService<T, ID> {

    protected final JpaRepository<T, ID> repository;

    protected AbstractCrudService(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entity not found with id: " + id));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T create(T entity) {
        return repository.save(entity);
    }

    @Override
    public void delete(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Entity not found with id: " + id);
        }
        repository.deleteById(id);
    }
}
```

#### 9.1.2 기본 컨트롤러 인터페이스

```java
public interface CrudController<T, ID, C, U> {
    ResponseEntity<ApiResponse<T>> getById(ID id);
    ResponseEntity<ApiResponse<List<T>>> getAll();
    ResponseEntity<ApiResponse<T>> create(C createDto);
    ResponseEntity<ApiResponse<T>> update(ID id, U updateDto);
    ResponseEntity<ApiResponse<Void>> delete(ID id);
}

public abstract class AbstractCrudController<T, ID, C, U> implements CrudController<T, ID, C, U> {

    protected final CrudService<T, ID> service;

    protected AbstractCrudController(CrudService<T, ID> service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<ApiResponse<T>> getById(ID id) {
        T entity = service.findById(id);
        return ResponseEntity.ok(ApiResponse.success(entity));
    }

    @Override
    public ResponseEntity<ApiResponse<List<T>>> getAll() {
        List<T> entities = service.findAll();
        return ResponseEntity.ok(ApiResponse.success(entities));
    }

    @Override
    public ResponseEntity<ApiResponse<Void>> delete(ID id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
```

### 9.2 컴포지션 패턴

```java
@Service
public class BoardService {

    private final BoardRepository boardRepository;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public BoardService(BoardRepository boardRepository,
                        FileStorageService fileStorageService,
                        NotificationService notificationService) {
        this.boardRepository = boardRepository;
        this.fileStorageService = fileStorageService;
        this.notificationService = notificationService;
    }

    @Transactional
    public BoardDTO createBoard(BoardCreateDTO createDTO) {
        // 게시글 생성
        Board board = new Board();
        board.setTitle(createDTO.getTitle());
        board.setContent(createDTO.getContent());
        // ...

        // 첨부 파일 처리
        if (createDTO.getAttachments() != null && !createDTO.getAttachments().isEmpty()) {
            List<Attachment> attachments = createDTO.getAttachments().stream()
                    .map(fileStorageService::storeFile)
                    .map(fileInfo -> {
                        Attachment attachment = new Attachment();
                        attachment.setFileName(fileInfo.getFileName());
                        attachment.setFilePath(fileInfo.getFilePath());
                        attachment.setFileSize(fileInfo.getFileSize());
                        attachment.setBoard(board);
                        return attachment;
                    })
                    .collect(Collectors.toList());

            board.setAttachments(attachments);
        }

        Board savedBoard = boardRepository.save(board);

        // 알림 발송
        notificationService.notifyNewBoard(savedBoard);

        return BoardDTO.from(savedBoard);
    }
}
```

### 9.3 전략 패턴

```java
public interface FileStorageStrategy {
    FileInfo storeFile(MultipartFile file);
    Resource loadFileAsResource(String fileName);
    void deleteFile(String fileName);
}

@Component
public class LocalFileStorageStrategy implements FileStorageStrategy {

    private final Path fileStorageLocation;

    @Autowired
    public LocalFileStorageStrategy(@Value("${app.file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Override
    public FileInfo storeFile(MultipartFile file) {
        // 파일 저장 구현
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        // 파일 로드 구현
    }

    @Override
    public void deleteFile(String fileName) {
        // 파일 삭제 구현
    }
}

@Component
public class S3FileStorageStrategy implements FileStorageStrategy {

    private final AmazonS3 s3Client;
    private final String bucketName;

    @Autowired
    public S3FileStorageStrategy(AmazonS3 s3Client, 
                                @Value("${app.aws.s3.bucket-name}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    @Override
    public FileInfo storeFile(MultipartFile file) {
        // S3에 파일 업로드 구현
    }

    @Override
    public Resource loadFileAsResource(String fileName) {
        // S3에서 파일 다운로드 구현
    }

    @Override
    public void deleteFile(String fileName) {
        // S3에서 파일 삭제 구현
    }
}

@Service
public class FileStorageService {

    private final FileStorageStrategy storageStrategy;

    @Autowired
    public FileStorageService(@Qualifier("localFileStorageStrategy") FileStorageStrategy storageStrategy) {
        this.storageStrategy = storageStrategy;
    }

    public FileInfo storeFile(MultipartFile file) {
        return storageStrategy.storeFile(file);
    }

    public Resource loadFileAsResource(String fileName) {
        return storageStrategy.loadFileAsResource(fileName);
    }

    public void deleteFile(String fileName) {
        storageStrategy.deleteFile(fileName);
    }
}
```

## 10. 구현 가이드라인

### 10.1 코드 컨벤션

1. **명명 규칙**
   - 클래스: PascalCase (예: `BoardService`)
   - 메서드 및 변수: camelCase (예: `findById`, `boardRepository`)
   - 상수: UPPER_SNAKE_CASE (예: `MAX_RETRY_COUNT`)
   - 패키지: 소문자 (예: `com.devkbil.mtssbj.api.board`)

2. **코드 포맷팅**
   - 들여쓰기: 4 스페이스
   - 최대 줄 길이: 120자
   - 메서드 사이 공백: 1줄
   - 클래스 사이 공백: 2줄

3. **주석 작성**
   - 클래스, 인터페이스, 메서드에 Javadoc 주석 작성
   - 복잡한 로직에 인라인 주석 추가
   - TODO, FIXME 등의 태그 활용

### 10.2 아키텍처 원칙

1. **단일 책임 원칙 (SRP)**
   - 각 클래스는 하나의 책임만 가짐
   - 컨트롤러는 요청 처리, 서비스는 비즈니스 로직, 리포지토리는 데이터 접근에 집중

2. **의존성 주입 (DI)**
   - 생성자 주입 방식 사용
   - 인터페이스 기반 의존성 정의
   - 순환 의존성 방지

3. **관심사 분리 (SoC)**
   - 계층 간 명확한 경계 유지
   - 비즈니스 로직과 인프라스트럭처 코드 분리
   - 도메인 로직과 기술적 구현 분리

4. **인터페이스 분리 원칙 (ISP)**
   - 클라이언트에 필요한 메서드만 포함하는 작은 인터페이스 설계
   - 범용 인터페이스보다 목적에 특화된 인터페이스 선호

5. **의존성 역전 원칙 (DIP)**
   - 고수준 모듈은 저수준 모듈에 의존하지 않고, 둘 다 추상화에 의존
   - 구체적인 구현보다 인터페이스에 의존

### 10.3 마이그레이션 전략

1. **점진적 접근**
   - 도메인별로 순차적 마이그레이션
   - 핵심 기능부터 시작하여 점진적으로 확장
   - 기존 코드와 새 코드의 병행 운영 기간 설정

2. **우선순위 설정**
   - 인증 및 보안 관련 기능 우선 마이그레이션
   - 공통 컴포넌트 및 유틸리티 먼저 구현
   - 사용 빈도가 높은 기능 우선 처리

3. **테스트 전략**
   - 마이그레이션 전 테스트 케이스 작성
   - 단위 테스트 및 통합 테스트 병행
   - 기존 기능과 동일한 결과 보장

4. **롤백 계획**
   - 문제 발생 시 롤백 절차 마련
   - 단계별 검증 포인트 설정
   - 성능 및 안정성 모니터링 체계 구축

## 11. 프론트엔드 연동 전략

### 11.1 API 클라이언트 라이브러리

```typescript
// api/client.ts
import axios, { AxiosInstance, AxiosRequestConfig, AxiosResponse } from 'axios';
import { getAccessToken, refreshToken } from '@/utils/auth';

class ApiClient {
  private client: AxiosInstance;
  private isRefreshing = false;
  private refreshSubscribers: Array<(token: string) => void> = [];

  constructor(baseURL: string) {
    this.client = axios.create({
      baseURL,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors(): void {
    // 요청 인터셉터
    this.client.interceptors.request.use(
      (config) => {
        const token = getAccessToken();
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 응답 인터셉터
    this.client.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;

        // 액세스 토큰 만료 시 리프레시 토큰으로 갱신
        if (error.response?.status === 401 && !originalRequest._retry) {
          if (this.isRefreshing) {
            return new Promise((resolve) => {
              this.refreshSubscribers.push((token) => {
                originalRequest.headers.Authorization = `Bearer ${token}`;
                resolve(this.client(originalRequest));
              });
            });
          }

          originalRequest._retry = true;
          this.isRefreshing = true;

          try {
            const newToken = await refreshToken();
            this.refreshSubscribers.forEach((callback) => callback(newToken));
            this.refreshSubscribers = [];
            originalRequest.headers.Authorization = `Bearer ${newToken}`;
            return this.client(originalRequest);
          } catch (refreshError) {
            // 리프레시 토큰도 만료된 경우 로그인 페이지로 리디렉션
            window.location.href = '/login';
            return Promise.reject(refreshError);
          } finally {
            this.isRefreshing = false;
          }
        }

        return Promise.reject(error);
      }
    );
  }

  public async get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.get<ApiResponse<T>>(url, config);
    return this.handleResponse(response);
  }

  public async post<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.post<ApiResponse<T>>(url, data, config);
    return this.handleResponse(response);
  }

  public async put<T>(url: string, data?: any, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.put<ApiResponse<T>>(url, data, config);
    return this.handleResponse(response);
  }

  public async delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    const response = await this.client.delete<ApiResponse<T>>(url, config);
    return this.handleResponse(response);
  }

  private handleResponse<T>(response: AxiosResponse<ApiResponse<T>>): T {
    const { data } = response;
    if (!data.success) {
      throw new Error(data.error?.message || '알 수 없는 오류가 발생했습니다.');
    }
    return data.data;
  }
}

export const apiClient = new ApiClient(process.env.REACT_APP_API_BASE_URL || '/api/v1');
```

### 11.2 도메인별 API 모듈

```typescript
// api/board.ts
import { apiClient } from './client';
import { Board, BoardCreateDTO, BoardUpdateDTO, PagedResponse } from '@/types';

export const boardApi = {
  getBoards: async (params?: { page?: number; size?: number; keyword?: string }) => {
    return apiClient.get<PagedResponse<Board>>('/boards', { params });
  },

  getBoardById: async (id: number) => {
    return apiClient.get<Board>(`/boards/${id}`);
  },

  createBoard: async (data: BoardCreateDTO) => {
    return apiClient.post<Board>('/boards', data);
  },

  updateBoard: async (id: number, data: BoardUpdateDTO) => {
    return apiClient.put<Board>(`/boards/${id}`, data);
  },

  deleteBoard: async (id: number) => {
    return apiClient.delete<void>(`/boards/${id}`);
  },

  getBoardComments: async (boardId: number) => {
    return apiClient.get<Comment[]>(`/boards/${boardId}/comments`);
  },

  addBoardComment: async (boardId: number, content: string) => {
    return apiClient.post<Comment>(`/boards/${boardId}/comments`, { content });
  }
};
```

### 11.3 React 훅 통합

```typescript
// hooks/useBoard.ts
import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from 'react-query';
import { boardApi } from '@/api/board';
import { Board, BoardCreateDTO, BoardUpdateDTO } from '@/types';

export const useBoardList = (page = 0, size = 10, keyword?: string) => {
  const { data, isLoading, error } = useQuery(
    ['boards', page, size, keyword],
    () => boardApi.getBoards({ page, size, keyword }),
    { keepPreviousData: true }
  );

  return {
    boards: data?.content || [],
    totalPages: data?.totalPages || 0,
    totalElements: data?.totalElements || 0,
    isLoading,
    error
  };
};

export const useBoardDetail = (id: number) => {
  const { data, isLoading, error } = useQuery(
    ['board', id],
    () => boardApi.getBoardById(id),
    { enabled: !!id }
  );

  return {
    board: data,
    isLoading,
    error
  };
};

export const useCreateBoard = () => {
  const queryClient = useQueryClient();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<Error | null>(null);

  const mutation = useMutation(
    (data: BoardCreateDTO) => boardApi.createBoard(data),
    {
      onSuccess: () => {
        queryClient.invalidateQueries('boards');
      }
    }
  );

  const createBoard = async (data: BoardCreateDTO) => {
    setIsSubmitting(true);
    setError(null);
    try {
      const result = await mutation.mutateAsync(data);
      return result;
    } catch (err) {
      setError(err as Error);
      throw err;
    } finally {
      setIsSubmitting(false);
    }
  };

  return {
    createBoard,
    isSubmitting,
    error
  };
};
```

## 12. 결론

이 문서에서 정의한 서버 프로그램 공통화 설계는 React.js 프론트엔드와 효율적으로 통신할 수 있는 REST API 기반의 백엔드 아키텍처를 제공합니다. 주요 이점은 다음과 같습니다:

1. **표준화된 API 설계**: 일관된 URL 구조, HTTP 메서드 활용, 응답 형식을 통해 프론트엔드 개발 효율성 향상
2. **강력한 보안 메커니즘**: JWT 기반 인증과 세밀한 권한 제어로 안전한 애플리케이션 구현
3. **체계적인 오류 처리**: 일관된 예외 처리와 응답 형식으로 프론트엔드에서의 오류 처리 용이
4. **재사용 가능한 컴포넌트**: 추상 클래스, 인터페이스, 디자인 패턴을 활용한 재사용 가능한 서버 컴포넌트
5. **확장성 있는 아키텍처**: 계층 분리와 관심사 분리를 통한 유지보수성과 확장성 확보
6. **프론트엔드 연동 전략**: API 클라이언트 라이브러리와 React 훅을 통한 효율적인 프론트엔드 연동

이 설계를 바탕으로 React.js로의 전환을 진행하면, 프론트엔드와 백엔드가 명확히 분리된 현대적인 웹 애플리케이션 아키텍처를 구축할 수 있습니다. 점진적인 마이그레이션 전략을 통해 리스크를 최소화하면서 기존 시스템을 안정적으로 개선해 나갈 수 있을 것입니다.

서버 프로그램 공통화는 단순히 기술적인 전환을 넘어, 애플리케이션의 품질, 개발 생산성, 유지보수성을 향상시키는 중요한 과정입니다. 이 문서에서 제시한 설계 원칙과 패턴을 적용함으로써, 장기적으로 지속 가능하고 확장 가능한 시스템을 구축할 수 있을 것입니다.