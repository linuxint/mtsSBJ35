# Controller 변경 사항

## 1. 공통 변경 사항
1. REST API 형식으로 전환
```java
// AS-IS
@Controller
@RequestMapping("/admin/code")
public class CodeController {
    @RequestMapping("/list")
    public String list(Model model) {
        // ...
    }
}

// TO-BE
@RestController
@RequestMapping("/api/admin/code")
public class CodeController {
    @GetMapping
    public ApiResponse<List<CodeDTO>> list() {
        // ...
    }
}
```

2. 응답 형식 표준화
```java
// AS-IS
return "jsonView";  // ModelAndView 사용

// TO-BE
return ApiResponse.success(data);  // 표준 응답 객체 사용
```

3. 요청/응답 DTO 사용
```java
// AS-IS
public String save(HttpServletRequest request) {
    String codeId = request.getParameter("codeId");
    // ...
}

// TO-BE
public ApiResponse<CodeDTO> save(@RequestBody CodeSaveRequest request) {
    // ...
}
```

## 2. 기능별 변경 사항
### 2.1 관리자 기능
#### CodeController
```java
@RestController
@RequestMapping("/api/admin/code")
public class CodeController {
    @GetMapping
    @Operation(summary = "코드 목록 조회")
    public ApiResponse<List<CodeDTO>> list();

    @GetMapping("/{codeId}")
    @Operation(summary = "코드 상세 조회")
    public ApiResponse<CodeDTO> detail(@PathVariable String codeId);

    @PostMapping
    @Operation(summary = "코드 등록")
    public ApiResponse<CodeDTO> save(@RequestBody CodeSaveRequest request);

    @PutMapping("/{codeId}")
    @Operation(summary = "코드 수정")
    public ApiResponse<CodeDTO> update(@PathVariable String codeId, @RequestBody CodeUpdateRequest request);

    @DeleteMapping("/{codeId}")
    @Operation(summary = "코드 삭제")
    public ApiResponse<Void> delete(@PathVariable String codeId);
}
```

#### MenuController
```java
@RestController
@RequestMapping("/api/admin/menu")
public class MenuController {
    @GetMapping
    @Operation(summary = "메뉴 목록 조회")
    public ApiResponse<List<MenuDTO>> list();

    @GetMapping("/{menuId}")
    @Operation(summary = "메뉴 상세 조회")
    public ApiResponse<MenuDTO> detail(@PathVariable String menuId);

    @PostMapping
    @Operation(summary = "메뉴 등록")
    public ApiResponse<MenuDTO> save(@RequestBody MenuSaveRequest request);

    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정")
    public ApiResponse<MenuDTO> update(@PathVariable String menuId, @RequestBody MenuUpdateRequest request);

    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제")
    public ApiResponse<Void> delete(@PathVariable String menuId);
}
```

### 2.2 인증 기능
#### AuthController
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ApiResponse<TokenDTO> login(@RequestBody LoginRequest request);

    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신")
    public ApiResponse<TokenDTO> refresh(@RequestBody RefreshTokenRequest request);

    @PostMapping("/logout")
    @Operation(summary = "로그아웃")
    public ApiResponse<Void> logout();
}
```

### 2.3 게시판 기능
#### BoardController
```java
@RestController
@RequestMapping("/api/board")
public class BoardController {
    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public ApiResponse<PageResponse<BoardDTO>> list(@ModelAttribute BoardSearchRequest request);

    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 상세 조회")
    public ApiResponse<BoardDTO> detail(@PathVariable Long boardId);

    @PostMapping
    @Operation(summary = "게시글 등록")
    public ApiResponse<BoardDTO> save(@RequestBody BoardSaveRequest request);

    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정")
    public ApiResponse<BoardDTO> update(@PathVariable Long boardId, @RequestBody BoardUpdateRequest request);

    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제")
    public ApiResponse<Void> delete(@PathVariable Long boardId);
}
```

### 2.4 일정 관리
#### ScheduleController
```java
@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {
    @GetMapping
    @Operation(summary = "일정 목록 조회")
    public ApiResponse<List<ScheduleDTO>> list(@ModelAttribute ScheduleSearchRequest request);

    @GetMapping("/{scheduleId}")
    @Operation(summary = "일정 상세 조회")
    public ApiResponse<ScheduleDTO> detail(@PathVariable Long scheduleId);

    @PostMapping
    @Operation(summary = "일정 등록")
    public ApiResponse<ScheduleDTO> save(@RequestBody ScheduleSaveRequest request);

    @PutMapping("/{scheduleId}")
    @Operation(summary = "일정 수정")
    public ApiResponse<ScheduleDTO> update(@PathVariable Long scheduleId, @RequestBody ScheduleUpdateRequest request);

    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "일정 삭제")
    public ApiResponse<Void> delete(@PathVariable Long scheduleId);
}
```

### 2.5 메일 기능
#### MailController
```java
@RestController
@RequestMapping("/api/mail")
public class MailController {
    @GetMapping
    @Operation(summary = "메일 목록 조회")
    public ApiResponse<PageResponse<MailDTO>> list(@ModelAttribute MailSearchRequest request);

    @GetMapping("/{mailId}")
    @Operation(summary = "메일 상세 조회")
    public ApiResponse<MailDTO> detail(@PathVariable Long mailId);

    @PostMapping
    @Operation(summary = "메일 발송")
    public ApiResponse<MailDTO> send(@RequestBody MailSendRequest request);

    @DeleteMapping("/{mailId}")
    @Operation(summary = "메일 삭제")
    public ApiResponse<Void> delete(@PathVariable Long mailId);
}
```

## 3. 주요 변경 포인트
1. URL 패턴
   - `/api` 접두어 추가
   - REST 리소스 명명 규칙 적용
   - 복수형 사용 (예: `/codes`, `/menus`)

2. HTTP 메서드
   - GET: 조회
   - POST: 생성
   - PUT: 수정
   - DELETE: 삭제

3. 응답 형식
   - 성공: `ApiResponse.success(data)`
   - 실패: `ApiResponse.error(errorCode, message)`
   - 페이징: `PageResponse<T>` 사용

4. API 문서화
   - Swagger/OpenAPI 어노테이션 추가
   - API 설명 및 예제 추가

5. 검증
   - `@Valid` 사용
   - 요청 DTO에서 검증 규칙 정의

6. 예외 처리
   - `@ExceptionHandler` 사용
   - 글로벌 예외 처리기 구현 