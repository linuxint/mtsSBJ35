# 백엔드 컨트롤러 변경 계획

## 현재 상태
- Spring MVC 기반의 전통적인 컨트롤러 구조
- JSP 뷰를 직접 반환하는 형태 (Model에 데이터 추가)
- URL 매핑이 `/컨트롤러명/메소드명` 형태로 일관성 부족
- 메서드당 하나의 엔드포인트로 설계
- 비즈니스 로직이 컨트롤러에 일부 포함되어 있음
- 하드코딩된 응답 형식
- HTTP 메서드 사용이 제한적 (주로 GET, POST만 사용)
- 페이징, 정렬 등이 일관되지 않게 구현되어 있음
- 요청 밸리데이션이 컨트롤러에 구현되어 있거나 부족함

## 변경 계획 개요
Spring MVC의 컨트롤러를 RESTful 방식으로 재설계하여 React 프론트엔드와 효과적으로 통신할 수 있게 합니다. REST API 모범 사례를 적용하여 일관성 있는 API 설계, 효율적인 자원 표현, 적절한 HTTP 상태 코드 사용, 그리고 견고한 오류 처리를 구현합니다.

## 상세 변경 내용

### 컨트롤러 구조 및 패키지 개선
현재 상태: 일관성 없는 컨트롤러 구조, 패키지 구성이 기능별로 되어 있지 않음

변경 계획:
- 도메인 기반 패키지 구조로 재조집
- 각 도메인에 대한 전용 컨트롤러 클래스 구현
- `@RestController` 어노테이션 사용으로 일관된 JSON 응답
- 핸들러 메서드의 명확한 책임 분리

구현 예시:
```java
// 기존 컨트롤러
@Controller
public class MemberController {
    @RequestMapping(value = "/memList", method = RequestMethod.GET)
    public String memList(Model model) {
        // 회원 목록을 Model에 추가하고 JSP 뷰 반환
        model.addAttribute("list", memberService.getMemList());
        return "member/memList";
    }
    
    @RequestMapping(value = "/memDetail", method = RequestMethod.GET)
    public String memDetail(Model model, @RequestParam("uid") String uid) {
        // 회원 상세정보를 Model에 추가하고 JSP 뷰 반환
        model.addAttribute("mem", memberService.getMember(uid));
        return "member/memDetail";
    }
}

// 변경된 컨트롤러
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    
    @GetMapping
    public ResponseEntity<List<MemberResponseDTO>> getAllMembers() {
        List<MemberResponseDTO> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }
    
    @GetMapping("/{memberId}")
    public ResponseEntity<MemberResponseDTO> getMemberById(@PathVariable Long memberId) {
        MemberResponseDTO member = memberService.getMemberById(memberId);
        return ResponseEntity.ok(member);
    }
    
    @PostMapping
    public ResponseEntity<MemberResponseDTO> createMember(@Valid @RequestBody MemberCreateRequestDTO requestDTO) {
        MemberResponseDTO createdMember = memberService.createMember(requestDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/members/" + createdMember.getId()))
                .body(createdMember);
    }
    
    @PutMapping("/{memberId}")
    public ResponseEntity<MemberResponseDTO> updateMember(
            @PathVariable Long memberId,
            @Valid @RequestBody MemberUpdateRequestDTO requestDTO) {
        MemberResponseDTO updatedMember = memberService.updateMember(memberId, requestDTO);
        return ResponseEntity.ok(updatedMember);
    }
    
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {
        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
```

### RESTful URL 및 HTTP 메서드 설계
현재 상태: 일관성 없는 URL 구조와 제한적인 HTTP 메서드 사용

변경 계획:
- 리소스 중심 URL 설계 (명사 사용)
- 적절한 HTTP 메서드 사용 (GET, POST, PUT, DELETE, PATCH)
- 중첩된 리소스 관계 표현
- 일관된 복수형 엔드포인트 명명 규칙
- URL 버전 관리 도입

구현 예시:
```
// 기존 URL 구조
/getBoard?idx=123        // 게시글 조회
/boardInsert             // 게시글 등록
/boardUpdate?idx=123     // 게시글 수정
/boardDelete?idx=123     // 게시글 삭제
/boardcommentsList?idx=123  // 게시글 댓글 목록

// 변경된 RESTful URL 구조
GET     /api/v1/boards                // 게시글 목록 조회
GET     /api/v1/boards/{boardId}      // 특정 게시글 조회
POST    /api/v1/boards                // 게시글 등록
PUT     /api/v1/boards/{boardId}      // 게시글 전체 수정
PATCH   /api/v1/boards/{boardId}      // 게시글 부분 수정
DELETE  /api/v1/boards/{boardId}      // 게시글 삭제
GET     /api/v1/boards/{boardId}/comments  // 게시글의 댓글 목록 조회
POST    /api/v1/boards/{boardId}/comments  // 게시글에 댓글 등록
```

구현 예시 (게시판 컨트롤러):
```java
@RestController
@RequestMapping("/api/v1/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    
    @GetMapping
    public ResponseEntity<Page<BoardSummaryDTO>> getAllBoards(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdDate,desc") String[] sort) {
        
        Page<BoardSummaryDTO> boards = boardService.getAllBoards(
                BoardSearchCriteria.builder()
                        .keyword(keyword)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build());
        
        return ResponseEntity.ok(boards);
    }
    
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> getBoardById(@PathVariable Long boardId) {
        BoardDetailDTO board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(board);
    }
    
    @PostMapping
    public ResponseEntity<BoardDetailDTO> createBoard(
            @Valid @RequestBody BoardCreateRequestDTO requestDTO) {
        BoardDetailDTO createdBoard = boardService.createBoard(requestDTO);
        return ResponseEntity
                .created(URI.create("/api/v1/boards/" + createdBoard.getId()))
                .body(createdBoard);
    }
    
    @PutMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> updateBoard(
            @PathVariable Long boardId,
            @Valid @RequestBody BoardUpdateRequestDTO requestDTO) {
        BoardDetailDTO updatedBoard = boardService.updateBoard(boardId, requestDTO);
        return ResponseEntity.ok(updatedBoard);
    }
    
    @DeleteMapping("/{boardId}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<CommentDTO>> getBoardComments(@PathVariable Long boardId) {
        List<CommentDTO> comments = boardService.getBoardComments(boardId);
        return ResponseEntity.ok(comments);
    }
    
    @PostMapping("/{boardId}/comments")
    public ResponseEntity<CommentDTO> addBoardComment(
            @PathVariable Long boardId,
            @Valid @RequestBody CommentCreateRequestDTO requestDTO) {
        CommentDTO createdComment = boardService.addBoardComment(boardId, requestDTO);
        return ResponseEntity
                .created(URI.create(
                        "/api/v1/boards/" + boardId + "/comments/" + createdComment.getId()))
                .body(createdComment);
    }
}
```

### 요청 및 응답 형식 표준화
현재 상태: 일관성 없는 응답 형식, 모델 객체가 직접 전달, 페이징 처리 불일치

변경 계획:
- 일관된 응답 포맷 정의 (성공, 오류)
- DTO를 통한 요청/응답 데이터 캡슐화
- 페이징, 정렬, 필터링의 표준화된 처리
- HTTP 상태 코드의 적절한 사용
- HAL 또는 다른 하이퍼미디어 형식 고려

구현 예시:
```java
// 표준 API 응답 클래스
@Getter
@Builder
public class ApiResponse<T> {
    private final boolean success;
    private final T data;
    private final ErrorDetails error;
    
    @Builder
    @Getter
    public static class ErrorDetails {
        private final String code;
        private final String message;
        private final Map<String, String> validationErrors;
    }
    
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
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
                .build();
    }
    
    public static <T> ApiResponse<T> validationError(String message, Map<String, String> validationErrors) {
        ErrorDetails errorDetails = ErrorDetails.builder()
                .code("VALIDATION_ERROR")
                .message(message)
                .validationErrors(validationErrors)
                .build();
        
        return ApiResponse.<T>builder()
                .success(false)
                .error(errorDetails)
                .build();
    }
}

// 공통 응답 래퍼 컨트롤러 예시
@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {
    private final ScheduleService scheduleService;
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScheduleSummaryDTO>>> getAllSchedules(
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Page<ScheduleSummaryDTO> schedules = scheduleService.getAllSchedules(
                ScheduleSearchCriteria.builder()
                        .fromDate(fromDate)
                        .toDate(toDate)
                        .page(page)
                        .size(size)
                        .build());
        
        return ResponseEntity.ok(ApiResponse.success(schedules));
    }
    
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse<ScheduleDetailDTO>> getScheduleById(@PathVariable Long scheduleId) {
        try {
            ScheduleDetailDTO schedule = scheduleService.getScheduleById(scheduleId);
            return ResponseEntity.ok(ApiResponse.success(schedule));
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("SCHEDULE_NOT_FOUND", e.getMessage()));
        }
    }
}
```

페이징 응답 예시:
```java
@Getter
public class PagedResponse<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean last;
    
    public PagedResponse(Page<T> page) {
        this.content = page.getContent();
        this.page = page.getNumber();
        this.size = page.getSize();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.last = page.isLast();
    }
}
```

### 오류 처리 메커니즘 개선
현재 상태: 비일관적인 오류 처리, 포괄적인 예외 처리, 유의미한 오류 메시지 부족

변경 계획:
- 전역 예외 처리기 구현 (@ControllerAdvice, @ExceptionHandler)
- 일관된 오류 응답 형식
- 유의미한 HTTP 상태 코드 매핑
- 세부적인 예외 처리 및 메시지 제공
- 유효성 검사 오류 처리 개선

구현 예시:
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("RESOURCE_NOT_FOUND", ex.getMessage()));
    }
    
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error("ACCESS_DENIED", ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> validationErrors = new HashMap<>();
        
        ex.getBindingResult().getFieldErrors().forEach(error -> 
                validationErrors.put(error.getField(), error.getDefaultMessage()));
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.validationError("입력값 검증에 실패했습니다.", validationErrors));
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(ex.getErrorCode(), ex.getMessage()));
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

### 입력 데이터 유효성 검사 개선
현재 상태: 컨트롤러 내 유효성 검증 또는 체계적이지 않은 검증

변경 계획:
- 빈 검증(Bean Validation) 어노테이션 활용
- 복잡한 유효성 검증을 위한 사용자 정의 어노테이션 개발
- 요청 DTO에 대한 철저한 유효성 검증
- 서비스 계층과의 책임 분리

구현 예시:
```java
// 요청 DTO에 유효성 검증 적용
@Getter
@Setter
public class MemberCreateRequestDTO {
    
    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    @Size(min = 4, max = 20, message = "사용자 이름은 4-20자 사이여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "사용자 이름은 영문자, 숫자, 특수문자(._-)만 포함할 수 있습니다.")
    private String username;
    
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
    
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 50, message = "이름은 최대.50자까지 입력 가능합니다.")
    private String name;
    
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;
    
    @PhoneNumber(message = "유효한 전화번호 형식이 아닙니다.")
    private String phone;
}

// 사용자 정의 유효성 검증 어노테이션
@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {
    String message() default "유효하지 않은 전화번호입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 유효성 검증기 구현
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {
    
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{2,3}-\\d{3,4}-\\d{4}$");
    
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // null 값은 @NotNull 또는 @NotEmpty로 검증
        }
        
        return PHONE_PATTERN.matcher(value).matches();
    }
}
```

### 컨트롤러 보안 강화
현재 상태: 보안 로직이 컨트롤러에 혼재, 일관되지 않은 인증/인가 처리

변경 계획:
- Spring Security의 메서드 수준 보안 활용
- `@PreAuthorize`, `@PostAuthorize` 어노테이션 적용
- CSRF 방어 설정
- 권한 기반 엔드포인트 접근 제어
- 민감한 데이터 마스킹

구현 예시:
```java
@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final AdminService adminService;
    
    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDTO>> getAllUsers() {
        List<UserAdminDTO> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    
    @GetMapping("/system/settings")
    @PreAuthorize("hasAuthority('SYSTEM_SETTINGS_READ')")
    public ResponseEntity<SystemSettingsDTO> getSystemSettings() {
        SystemSettingsDTO settings = adminService.getSystemSettings();
        return ResponseEntity.ok(settings);
    }
    
    @PutMapping("/system/settings")
    @PreAuthorize("hasAuthority('SYSTEM_SETTINGS_WRITE')")
    public ResponseEntity<SystemSettingsDTO> updateSystemSettings(
            @Valid @RequestBody SystemSettingsUpdateDTO settings) {
        SystemSettingsDTO updatedSettings = adminService.updateSystemSettings(settings);
        return ResponseEntity.ok(updatedSettings);
    }
    
    @GetMapping("/audit-logs")
    @PreAuthorize("hasAuthority('AUDIT_LOGS_READ')")
    public ResponseEntity<List<AuditLogDTO>> getAuditLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        List<AuditLogDTO> logs = adminService.getAuditLogs(fromDate, toDate);
        return ResponseEntity.ok(logs);
    }
}

@RestController
@RequestMapping("/api/v1/member/profile")
public class MemberProfileController {
    
    private final MemberProfileService profileService;
    
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberProfileDTO> getMyProfile() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        MemberProfileDTO profile = profileService.getProfileByUsername(username);
        return ResponseEntity.ok(profile);
    }
    
    @PutMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MemberProfileDTO> updateMyProfile(
            @Valid @RequestBody ProfileUpdateRequestDTO requestDTO) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        MemberProfileDTO updatedProfile = profileService.updateProfile(username, requestDTO);
        return ResponseEntity.ok(updatedProfile);
    }
    
    @GetMapping("/{memberId}")
    @PreAuthorize("hasRole('ADMIN') or #memberId == principal.memberId")
    public ResponseEntity<MemberProfileDTO> getMemberProfile(@PathVariable Long memberId) {
        MemberProfileDTO profile = profileService.getProfileById(memberId);
        return ResponseEntity.ok(profile);
    }
}
```

### 비동기 컨트롤러 적용 (필요 시)
현재 상태: 동기 처리 방식으로 인한 응답 시간 지연

변경 계획:
- 장시간 실행 작업에 비동기 메서드 적용
- `CompletableFuture` 또는 `Callable` 반환 타입 활용
- 작업 진행 상황 모니터링 메커니즘 구현
- WebFlux 도입 검토 (필요 시)

구현 예시:
```java
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {
    
    private final ReportService reportService;
    
    @PostMapping
    public CompletableFuture<ResponseEntity<ReportResponseDTO>> generateReport(
            @Valid @RequestBody ReportRequestDTO requestDTO) {
        return reportService.generateReportAsync(requestDTO)
                .thenApply(report -> ResponseEntity
                        .created(URI.create("/api/v1/reports/" + report.getId()))
                        .body(report));
    }
    
    @GetMapping("/{reportId}/status")
    public ResponseEntity<ReportStatusDTO> getReportStatus(@PathVariable String reportId) {
        ReportStatusDTO status = reportService.getReportStatus(reportId);
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/{reportId}/download")
    public ResponseEntity<Resource> downloadReport(@PathVariable String reportId) {
        ReportDownloadDTO downloadInfo = reportService.prepareReportDownload(reportId);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(downloadInfo.getContentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                        "attachment; filename=\"" + downloadInfo.getFileName() + "\"")
                .body(downloadInfo.getResource());
    }
}
```

### API 문서화 개선
현재 상태: API 문서화 부족 또는 최신 상태가 아님

변경 계획:
- SpringDoc (OpenAPI 3) 적용
- 컨트롤러 및 DTO에 주석 및 어노테이션 추가
- 예제 요청/응답 제공
- 자동화된 API 문서 생성 및 배포
- Swagger UI 또는 ReDoc 통합

구현 예시:
```java
@RestController
@RequestMapping("/api/v1/boards")
@Tag(name = "게시판 관리", description = "게시판 CRUD 및 댓글 관리 API")
@RequiredArgsConstructor
public class BoardController {
    
    private final BoardService boardService;
    
    @Operation(
        summary = "게시글 목록 조회",
        description = "페이징, 정렬, 검색 조건을 적용하여 게시글 목록을 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        )
    })
    @GetMapping
    public ResponseEntity<Page<BoardSummaryDTO>> getAllBoards(
            @Parameter(description = "검색 키워드 (제목, 내용에서 검색)")
            @RequestParam(required = false) String keyword,
            
            @Parameter(description = "페이지 번호 (0부터 시작)")
            @RequestParam(defaultValue = "0") int page,
            
            @Parameter(description = "페이지 크기")
            @RequestParam(defaultValue = "10") int size,
            
            @Parameter(description = "정렬 (형식: 필드명,정렬방향 - 예: createdDate,desc)")
            @RequestParam(defaultValue = "createdDate,desc") String[] sort) {
        
        Page<BoardSummaryDTO> boards = boardService.getAllBoards(
                BoardSearchCriteria.builder()
                        .keyword(keyword)
                        .page(page)
                        .size(size)
                        .sort(sort)
                        .build());
        
        return ResponseEntity.ok(boards);
    }
    
    @Operation(
        summary = "게시글 상세 조회",
        description = "게시글 ID를 기준으로 상세 정보를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = BoardDetailDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "게시글 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ApiResponse.class)
            )
        )
    })
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDetailDTO> getBoardById(
            @Parameter(description = "게시글 ID", required = true)
            @PathVariable Long boardId) {
        BoardDetailDTO board = boardService.getBoardById(boardId);
        return ResponseEntity.ok(board);
    }
    
    // ... 다른 메서드들
}
```

### 도메인별 구체적인 변경 계획

#### 회원 관련 컨트롤러
- `MemberController`: 회원 등록, 조회, 수정, 삭제 API
- `MemberProfileController`: 회원 프로필 관련 API
- `MemberPermissionController`: 회원 권한 관련 API

#### 게시판 관련 컨트롤러
- `BoardController`: 게시글 CRUD API
- `CommentController`: 댓글 CRUD API
- `BoardAttachmentController`: 첨부 파일 관련 API
- `BoardCategoryController`: 게시판 분류 관리 API

#### 일정 관련 컨트롤러
- `ScheduleController`: 일정 CRUD API
- `CalendarViewController`: 캘린더 뷰 데이터 API
- `ScheduleReminderController`: 일정 알림 관련 API

#### 메일 관련 컨트롤러
- `MailController`: 메일 송수신 관련 API
- `MailAttachmentController`: 메일 첨부 파일 관련 API
- `MailTemplateController`: 메일 템플릿 관리 API

#### 관리자 관련 컨트롤러
- `AdminMemberController`: 관리자용 회원 관리 API
- `SystemSettingsController`: 시스템 설정 관리 API
- `AuditLogController`: 감사 로그 관리 API
- `StatisticsController`: 통계 데이터 제공 API

### 마이그레이션 단계
1. 전체 컨트롤러 매핑 분석 및 RESTful 엔드포인트 설계
2. 요청/응답 DTO 클래스 설계 및 구현
3. 표준 응답 포맷 및 오류 처리 구현
4. 도메인별 컨트롤러 재구현
5. 인증/인가 및 보안 설정 적용
6. API 문서화 및 테스트
7. 프론트엔드와의 통합 테스트
8. 성능 테스트 및 최적화

### 테스트 전략
1. 단위 테스트: 각 컨트롤러 메서드의 독립적인 테스트
2. 통합 테스트: 서비스 계층과 연동된 테스트
3. API 테스트: 엔드포인트의 기능 및 응답 검증
4. 보안 테스트: 권한 기반 접근 제어 확인
5. 성능 테스트: 응답 시간 및 처리량 측정