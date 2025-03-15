# 컨트롤러 변경사항

## 공통 사항
1. Spring MVC 컨트롤러에서 REST API 컨트롤러로 변경
2. JSP 뷰 반환에서 JSON 응답으로 변경
3. 세션 기반 인증에서 JWT 기반 인증으로 변경
4. 예외 처리 방식 통일화
5. API 문서화를 위한 SpringDoc 어노테이션 추가

## Code Controller
### 변경 전
```java
@Controller
@RequestMapping("/admin/code")
public class CodeController {
    @Autowired
    private CodeService codeService;
    
    @RequestMapping("/list")
    public String list(Model model) {
        List<CodeVO> list = codeService.getCodeList();
        model.addAttribute("list", list);
        return "admin/code/list";
    }
    
    @RequestMapping("/form")
    public String form(Model model, String codeId) {
        if (codeId != null) {
            CodeVO code = codeService.getCode(codeId);
            model.addAttribute("code", code);
        }
        return "admin/code/form";
    }
    
    @RequestMapping("/save")
    public String save(CodeVO code) {
        codeService.saveCode(code);
        return "redirect:/admin/code/list";
    }
}
```

### 변경 후
```java
@RestController
@RequestMapping("/api/admin/codes")
@Tag(name = "코드 관리", description = "코드 관리 API")
@RequiredArgsConstructor
public class CodeController {
    private final CodeService codeService;
    
    @GetMapping
    @Operation(summary = "코드 목록 조회")
    public ResponseEntity<List<CodeResponse>> getCodeList(
            @Parameter(description = "그룹 코드") @RequestParam(required = false) String groupCode) {
        List<CodeResponse> codes = codeService.getCodeList(groupCode);
        return ResponseEntity.ok(codes);
    }
    
    @GetMapping("/{codeId}")
    @Operation(summary = "코드 상세 조회")
    public ResponseEntity<CodeResponse> getCode(
            @Parameter(description = "코드 ID") @PathVariable String codeId) {
        CodeResponse code = codeService.getCode(codeId);
        return ResponseEntity.ok(code);
    }
    
    @PostMapping
    @Operation(summary = "코드 등록")
    public ResponseEntity<CodeResponse> createCode(
            @Parameter(description = "코드 정보") @RequestBody @Valid CodeRequest request) {
        CodeResponse code = codeService.createCode(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(code);
    }
    
    @PutMapping("/{codeId}")
    @Operation(summary = "코드 수정")
    public ResponseEntity<CodeResponse> updateCode(
            @Parameter(description = "코드 ID") @PathVariable String codeId,
            @Parameter(description = "코드 정보") @RequestBody @Valid CodeRequest request) {
        CodeResponse code = codeService.updateCode(codeId, request);
        return ResponseEntity.ok(code);
    }
    
    @DeleteMapping("/{codeId}")
    @Operation(summary = "코드 삭제")
    public ResponseEntity<Void> deleteCode(
            @Parameter(description = "코드 ID") @PathVariable String codeId) {
        codeService.deleteCode(codeId);
        return ResponseEntity.noContent().build();
    }
}
```

## Menu Controller
### 변경 전
```java
@Controller
@RequestMapping("/admin/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;
    
    @RequestMapping("/list")
    public String list(Model model) {
        List<MenuVO> list = menuService.getMenuList();
        model.addAttribute("list", list);
        return "admin/menu/list";
    }
    
    @RequestMapping("/save")
    public String save(MenuVO menu) {
        menuService.saveMenu(menu);
        return "redirect:/admin/menu/list";
    }
    
    @RequestMapping("/delete")
    public String delete(String menuId) {
        menuService.deleteMenu(menuId);
        return "redirect:/admin/menu/list";
    }
}
```

### 변경 후
```java
@RestController
@RequestMapping("/api/admin/menus")
@Tag(name = "메뉴 관리", description = "메뉴 관리 API")
@RequiredArgsConstructor
public class MenuController {
    private final MenuService menuService;
    
    @GetMapping
    @Operation(summary = "메뉴 목록 조회")
    public ResponseEntity<List<MenuResponse>> getMenuList() {
        List<MenuResponse> menus = menuService.getMenuList();
        return ResponseEntity.ok(menus);
    }
    
    @GetMapping("/tree")
    @Operation(summary = "메뉴 트리 조회")
    public ResponseEntity<List<MenuTreeResponse>> getMenuTree() {
        List<MenuTreeResponse> menuTree = menuService.getMenuTree();
        return ResponseEntity.ok(menuTree);
    }
    
    @PostMapping
    @Operation(summary = "메뉴 등록")
    public ResponseEntity<MenuResponse> createMenu(
            @Parameter(description = "메뉴 정보") @RequestBody @Valid MenuRequest request) {
        MenuResponse menu = menuService.createMenu(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(menu);
    }
    
    @PutMapping("/{menuId}")
    @Operation(summary = "메뉴 수정")
    public ResponseEntity<MenuResponse> updateMenu(
            @Parameter(description = "메뉴 ID") @PathVariable String menuId,
            @Parameter(description = "메뉴 정보") @RequestBody @Valid MenuRequest request) {
        MenuResponse menu = menuService.updateMenu(menuId, request);
        return ResponseEntity.ok(menu);
    }
    
    @DeleteMapping("/{menuId}")
    @Operation(summary = "메뉴 삭제")
    public ResponseEntity<Void> deleteMenu(
            @Parameter(description = "메뉴 ID") @PathVariable String menuId) {
        menuService.deleteMenu(menuId);
        return ResponseEntity.noContent().build();
    }
    
    @PutMapping("/order")
    @Operation(summary = "메뉴 순서 변경")
    public ResponseEntity<Void> updateMenuOrder(
            @Parameter(description = "메뉴 순서 정보") @RequestBody List<MenuOrderRequest> request) {
        menuService.updateMenuOrder(request);
        return ResponseEntity.ok().build();
    }
}
```

## Board Controller
### 변경 전
```java
@Controller
@RequestMapping("/board")
public class BoardController {
    @Autowired
    private BoardService boardService;
    
    @RequestMapping("/list")
    public String list(Model model, BoardSearchVO search) {
        List<BoardVO> list = boardService.getBoardList(search);
        model.addAttribute("list", list);
        return "board/list";
    }
    
    @RequestMapping("/view")
    public String view(Model model, Long boardId) {
        BoardVO board = boardService.getBoard(boardId);
        model.addAttribute("board", board);
        return "board/view";
    }
    
    @RequestMapping("/form")
    public String form(Model model, Long boardId) {
        if (boardId != null) {
            BoardVO board = boardService.getBoard(boardId);
            model.addAttribute("board", board);
        }
        return "board/form";
    }
    
    @RequestMapping("/save")
    public String save(BoardVO board, MultipartFile[] files) {
        boardService.saveBoard(board, files);
        return "redirect:/board/list";
    }
}
```

### 변경 후
```java
@RestController
@RequestMapping("/api/boards")
@Tag(name = "게시판", description = "게시판 API")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
    
    @GetMapping
    @Operation(summary = "게시글 목록 조회")
    public ResponseEntity<Page<BoardResponse>> getBoardList(
            @Parameter(description = "검색 조건") BoardSearchRequest search,
            @Parameter(description = "페이지 정보") Pageable pageable) {
        Page<BoardResponse> boards = boardService.getBoardList(search, pageable);
        return ResponseEntity.ok(boards);
    }
    
    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 상세 조회")
    public ResponseEntity<BoardResponse> getBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId) {
        BoardResponse board = boardService.getBoard(boardId);
        return ResponseEntity.ok(board);
    }
    
    @PostMapping
    @Operation(summary = "게시글 등록")
    public ResponseEntity<BoardResponse> createBoard(
            @Parameter(description = "게시글 정보") @RequestPart @Valid BoardRequest request,
            @Parameter(description = "첨부 파일") @RequestPart(required = false) List<MultipartFile> files) {
        BoardResponse board = boardService.createBoard(request, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(board);
    }
    
    @PutMapping("/{boardId}")
    @Operation(summary = "게시글 수정")
    public ResponseEntity<BoardResponse> updateBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId,
            @Parameter(description = "게시글 정보") @RequestPart @Valid BoardRequest request,
            @Parameter(description = "첨부 파일") @RequestPart(required = false) List<MultipartFile> files) {
        BoardResponse board = boardService.updateBoard(boardId, request, files);
        return ResponseEntity.ok(board);
    }
    
    @DeleteMapping("/{boardId}")
    @Operation(summary = "게시글 삭제")
    public ResponseEntity<Void> deleteBoard(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{boardId}/replies")
    @Operation(summary = "댓글 등록")
    public ResponseEntity<ReplyResponse> createReply(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId,
            @Parameter(description = "댓글 정보") @RequestBody @Valid ReplyRequest request) {
        ReplyResponse reply = boardService.createReply(boardId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }
    
    @GetMapping("/{boardId}/replies")
    @Operation(summary = "댓글 목록 조회")
    public ResponseEntity<List<ReplyResponse>> getReplies(
            @Parameter(description = "게시글 ID") @PathVariable Long boardId) {
        List<ReplyResponse> replies = boardService.getReplies(boardId);
        return ResponseEntity.ok(replies);
    }
}
```

## Member Controller
### 변경 전
```java
@Controller
@RequestMapping("/member")
public class MemberController {
    @Autowired
    private MemberService memberService;
    
    @RequestMapping("/login")
    public String login() {
        return "member/login";
    }
    
    @RequestMapping("/loginProc")
    public String loginProc(String id, String password, HttpSession session) {
        MemberVO member = memberService.login(id, password);
        if (member != null) {
            session.setAttribute("member", member);
            return "redirect:/";
        }
        return "redirect:/member/login";
    }
    
    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/member/login";
    }
    
    @RequestMapping("/info")
    public String info(Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("member");
        model.addAttribute("member", member);
        return "member/info";
    }
}
```

### 변경 후
```java
@RestController
@RequestMapping("/api/members")
@Tag(name = "회원", description = "회원 API")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "로그인")
    public ResponseEntity<TokenResponse> login(
            @Parameter(description = "로그인 정보") @RequestBody @Valid LoginRequest request) {
        TokenResponse token = authService.login(request);
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신")
    public ResponseEntity<TokenResponse> refresh(
            @Parameter(description = "리프레시 토큰") @RequestBody @Valid TokenRefreshRequest request) {
        TokenResponse token = authService.refresh(request);
        return ResponseEntity.ok(token);
    }
    
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MemberResponse> getMyInfo() {
        MemberResponse member = memberService.getMyInfo();
        return ResponseEntity.ok(member);
    }
    
    @PutMapping("/me")
    @Operation(summary = "내 정보 수정")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<MemberResponse> updateMyInfo(
            @Parameter(description = "회원 정보") @RequestBody @Valid MemberUpdateRequest request) {
        MemberResponse member = memberService.updateMyInfo(request);
        return ResponseEntity.ok(member);
    }
    
    @PutMapping("/me/password")
    @Operation(summary = "비밀번호 변경")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<Void> changePassword(
            @Parameter(description = "비밀번호 정보") @RequestBody @Valid PasswordChangeRequest request) {
        memberService.changePassword(request);
        return ResponseEntity.ok().build();
    }
} 