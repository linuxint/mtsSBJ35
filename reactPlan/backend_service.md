# 서비스 계층 변경사항

## 공통 사항
1. 트랜잭션 관리 방식 개선
2. 비즈니스 로직과 데이터 접근 로직 분리
3. VO와 DTO 간 변환 로직 추가
4. 예외 처리 강화
5. 비동기 처리 도입

## 메인 서비스
### IndexService
- 세션 기반 상태 관리 제거
- JWT 토큰 처리 로직 추가
- 캐시 처리 최적화

### SampleService
- 비즈니스 로직 분리
- 트랜잭션 처리 개선
- 응답 DTO 변환 처리

## CRUD 서비스
### CrudService
- MyBatis 동적 쿼리 최적화
- 페이징 처리 개선
- 검색 조건 처리 강화

### EtcService
- 유틸리티 메서드 분리
- 공통 기능 모듈화
- 캐시 적용

## 개발 도구 서비스
### DbtoolService
- DB 연결 풀 관리 개선
- 쿼리 실행 보안 강화
- 결과 캐싱 적용

### NaverMapService
- API 호출 최적화
- 에러 처리 개선
- 캐시 적용

## 서버 관리 서비스
### SvcService, HWService, SWService
- 비동기 처리 적용
- 모니터링 통합
- 알림 처리 개선

## 공통 서비스
### CodeCacheService
- 캐시 갱신 전략 개선
- 메모리 사용 최적화
- 동시성 처리 강화

## Code Service
### 변경 전
```java
@Service
public class CodeService {
    @Autowired
    private CodeMapper codeMapper;
    
    public List<CodeVO> getCodeList() {
        return codeMapper.selectCodeList();
    }
    
    public CodeVO getCode(String codeId) {
        return codeMapper.selectCode(codeId);
    }
    
    public void saveCode(CodeVO code) {
        if (code.getCodeId() == null) {
            codeMapper.insertCode(code);
        } else {
            codeMapper.updateCode(code);
        }
    }
}
```

### 변경 후
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CodeService {
    private final CodeMapper codeMapper;
    private final ModelMapper modelMapper;
    
    public List<CodeResponse> getCodeList(String groupCode) {
        List<CodeVO> codes = codeMapper.selectCodeList(groupCode);
        return codes.stream()
            .map(code -> modelMapper.map(code, CodeResponse.class))
            .collect(Collectors.toList());
    }
    
    public CodeResponse getCode(String codeId) {
        CodeVO code = Optional.ofNullable(codeMapper.selectCode(codeId))
            .orElseThrow(() -> new NotFoundException("코드를 찾을 수 없습니다."));
        return modelMapper.map(code, CodeResponse.class);
    }
    
    @Transactional
    public CodeResponse createCode(CodeRequest request) {
        if (codeMapper.existsByGroupCodeAndCode(request.getGroupCode(), request.getCode())) {
            throw new DuplicateKeyException("이미 존재하는 코드입니다.");
        }
        
        CodeVO code = modelMapper.map(request, CodeVO.class);
        codeMapper.insertCode(code);
        return modelMapper.map(code, CodeResponse.class);
    }
    
    @Transactional
    public CodeResponse updateCode(String codeId, CodeRequest request) {
        CodeVO code = Optional.ofNullable(codeMapper.selectCode(codeId))
            .orElseThrow(() -> new NotFoundException("코드를 찾을 수 없습니다."));
        
        modelMapper.map(request, code);
        codeMapper.updateCode(code);
        return modelMapper.map(code, CodeResponse.class);
    }
    
    @Transactional
    public void deleteCode(String codeId) {
        if (codeMapper.selectCode(codeId) == null) {
            throw new NotFoundException("코드를 찾을 수 없습니다.");
        }
        codeMapper.deleteCode(codeId);
    }
}
```

## Board Service
### 변경 전
```java
@Service
public class BoardService {
    @Autowired
    private BoardMapper boardMapper;
    @Autowired
    private FileService fileService;
    
    public List<BoardVO> getBoardList(BoardSearchVO search) {
        return boardMapper.selectBoardList(search);
    }
    
    public BoardVO getBoard(Long boardId) {
        BoardVO board = boardMapper.selectBoard(boardId);
        board.setFiles(fileService.getFileList(boardId));
        return board;
    }
    
    @Transactional
    public void saveBoard(BoardVO board, MultipartFile[] files) {
        if (board.getBoardId() == null) {
            boardMapper.insertBoard(board);
        } else {
            boardMapper.updateBoard(board);
        }
        
        if (files != null) {
            for (MultipartFile file : files) {
                fileService.saveFile(board.getBoardId(), file);
            }
        }
    }
}
```

### 변경 후
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BoardService {
    private final BoardMapper boardMapper;
    private final FileMapper fileMapper;
    private final FileStorageService fileStorageService;
    private final ModelMapper modelMapper;
    
    public Page<BoardResponse> getBoardList(BoardSearchRequest search, Pageable pageable) {
        int total = boardMapper.selectBoardListCount(search);
        List<BoardVO> boards = boardMapper.selectBoardList(search, 
            new RowBounds(pageable.getPageNumber() * pageable.getPageSize(), 
                         pageable.getPageSize()));
                         
        List<BoardResponse> content = boards.stream()
            .map(board -> modelMapper.map(board, BoardResponse.class))
            .collect(Collectors.toList());
            
        return new PageImpl<>(content, pageable, total);
    }
    
    public BoardResponse getBoard(Long boardId) {
        BoardVO board = Optional.ofNullable(boardMapper.selectBoardWithFiles(boardId))
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
            
        boardMapper.incrementViewCount(boardId);
        return modelMapper.map(board, BoardResponse.class);
    }
    
    @Transactional
    public BoardResponse createBoard(BoardRequest request, List<MultipartFile> files) {
        BoardVO board = modelMapper.map(request, BoardVO.class);
        boardMapper.insertBoard(board);
        
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                FileInfo fileInfo = fileStorageService.store(file);
                FileVO fileVO = FileVO.builder()
                    .boardId(board.getBoardId())
                    .fileName(fileInfo.getFileName())
                    .filePath(fileInfo.getFilePath())
                    .fileSize(fileInfo.getFileSize())
                    .build();
                fileMapper.insertFile(fileVO);
            }
        }
        
        return modelMapper.map(board, BoardResponse.class);
    }
    
    @Transactional
    public BoardResponse updateBoard(Long boardId, BoardRequest request, List<MultipartFile> files) {
        BoardVO board = Optional.ofNullable(boardMapper.selectBoard(boardId))
            .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        
        modelMapper.map(request, board);
        boardMapper.updateBoard(board);
        
        if (files != null && !files.isEmpty()) {
            // 기존 파일 삭제
            List<String> oldFilePaths = fileMapper.selectFilePathsByBoardId(boardId);
            oldFilePaths.forEach(fileStorageService::delete);
            fileMapper.deleteFilesByBoardId(boardId);
            
            // 새 파일 저장
            for (MultipartFile file : files) {
                FileInfo fileInfo = fileStorageService.store(file);
                FileVO fileVO = FileVO.builder()
                    .boardId(board.getBoardId())
                    .fileName(fileInfo.getFileName())
                    .filePath(fileInfo.getFilePath())
                    .fileSize(fileInfo.getFileSize())
                    .build();
                fileMapper.insertFile(fileVO);
            }
        }
        
        return modelMapper.map(board, BoardResponse.class);
    }
    
    @Transactional
    public void deleteBoard(Long boardId) {
        if (boardMapper.selectBoard(boardId) == null) {
            throw new NotFoundException("게시글을 찾을 수 없습니다.");
        }
        
        // 파일 삭제
        List<String> filePaths = fileMapper.selectFilePathsByBoardId(boardId);
        filePaths.forEach(fileStorageService::delete);
        fileMapper.deleteFilesByBoardId(boardId);
        
        boardMapper.deleteBoard(boardId);
    }
}
```

## Member Service
### 변경 전
```java
@Service
public class MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public MemberVO login(String id, String password) {
        MemberVO member = memberMapper.selectMemberById(id);
        if (member != null && passwordEncoder.matches(password, member.getPassword())) {
            return member;
        }
        return null;
    }
    
    public MemberVO getMember(String id) {
        return memberMapper.selectMemberById(id);
    }
    
    public void saveMember(MemberVO member) {
        if (member.getId() == null) {
            member.setPassword(passwordEncoder.encode(member.getPassword()));
            memberMapper.insertMember(member);
        } else {
            memberMapper.updateMember(member);
        }
    }
}
```

### 변경 후
```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final ModelMapper modelMapper;
    
    public TokenResponse login(LoginRequest request) {
        MemberVO member = Optional.ofNullable(memberMapper.selectMemberById(request.getId()))
            .orElseThrow(() -> new AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다."));
        
        if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
            throw new AuthenticationException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }
        
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getRoles());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());
        
        member.setRefreshToken(refreshToken);
        memberMapper.updateMember(member);
        
        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
    
    public TokenResponse refresh(TokenRefreshRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }
        
        String memberId = tokenProvider.getMemberId(request.getRefreshToken());
        MemberVO member = Optional.ofNullable(memberMapper.selectMemberByRefreshToken(request.getRefreshToken()))
            .orElseThrow(() -> new AuthenticationException("토큰이 일치하지 않습니다."));
        
        String accessToken = tokenProvider.createAccessToken(member.getId(), member.getRoles());
        String refreshToken = tokenProvider.createRefreshToken(member.getId());
        
        member.setRefreshToken(refreshToken);
        memberMapper.updateMember(member);
        
        return TokenResponse.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .build();
    }
    
    public MemberResponse getMyInfo() {
        String memberId = SecurityUtil.getCurrentMemberId();
        MemberVO member = Optional.ofNullable(memberMapper.selectMemberWithRoles(memberId))
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        return modelMapper.map(member, MemberResponse.class);
    }
    
    @Transactional
    public MemberResponse updateMyInfo(MemberUpdateRequest request) {
        String memberId = SecurityUtil.getCurrentMemberId();
        MemberVO member = Optional.ofNullable(memberMapper.selectMemberById(memberId))
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        
        modelMapper.map(request, member);
        memberMapper.updateMember(member);
        return modelMapper.map(member, MemberResponse.class);
    }
    
    @Transactional
    public void changePassword(PasswordChangeRequest request) {
        String memberId = SecurityUtil.getCurrentMemberId();
        MemberVO member = Optional.ofNullable(memberMapper.selectMemberById(memberId))
            .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        
        if (!passwordEncoder.matches(request.getCurrentPassword(), member.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }
        
        member.setPassword(passwordEncoder.encode(request.getNewPassword()));
        memberMapper.updateMember(member);
    }
}
```

## Department Service
### 변경 전
```java
@Service
@Transactional
public class DepartmentServiceImpl implements DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Override
    public List<DepartmentVO> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    @Override
    public DepartmentVO getDepartment(String id) {
        return departmentRepository.findById(id);
    }
    
    @Override
    public void saveDepartment(DepartmentVO department) {
        departmentRepository.save(department);
    }
    
    @Override
    public void deleteDepartment(String id) {
        departmentRepository.delete(id);
    }
}
```

### 변경 후
```java
@Service
@Transactional
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {
    
    private final DepartmentMapper departmentMapper;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getAllDepartments() {
        List<DepartmentVO> departments = departmentMapper.selectDepartmentList();
        return departments.stream()
            .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public DepartmentDTO getDepartment(String departmentId) {
        DepartmentVO department = Optional.ofNullable(departmentMapper.selectDepartment(departmentId))
            .orElseThrow(() -> new NotFoundException("부서를 찾을 수 없습니다: " + departmentId));
        return modelMapper.map(department, DepartmentDTO.class);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentDTO> getDepartmentsByParent(String parentId) {
        List<DepartmentVO> departments = departmentMapper.selectDepartmentsByParentId(parentId);
        return departments.stream()
            .map(dept -> modelMapper.map(dept, DepartmentDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    public DepartmentDTO createDepartment(DepartmentDTO departmentDTO) {
        DepartmentVO department = modelMapper.map(departmentDTO, DepartmentVO.class);
        int result = departmentMapper.insertDepartment(department);
        if (result != 1) {
            throw new RuntimeException("부서 생성에 실패했습니다");
        }
        return modelMapper.map(department, DepartmentDTO.class);
    }
    
    @Override
    public DepartmentDTO updateDepartment(String departmentId, DepartmentDTO departmentDTO) {
        if (!departmentId.equals(departmentDTO.getDepartmentId())) {
            throw new IllegalArgumentException("부서 ID가 일치하지 않습니다");
        }
        
        DepartmentVO existingDept = Optional.ofNullable(departmentMapper.selectDepartment(departmentId))
            .orElseThrow(() -> new NotFoundException("부서를 찾을 수 없습니다: " + departmentId));
            
        DepartmentVO department = modelMapper.map(departmentDTO, DepartmentVO.class);
        int result = departmentMapper.updateDepartment(department);
        if (result != 1) {
            throw new RuntimeException("부서 수정에 실패했습니다");
        }
        return modelMapper.map(department, DepartmentDTO.class);
    }
    
    @Override
    public void deleteDepartment(String departmentId) {
        // 하위 부서 확인
        int childCount = departmentMapper.countChildDepartments(departmentId);
        if (childCount > 0) {
            throw new IllegalStateException("하위 부서가 있는 부서는 삭제할 수 없습니다");
        }
        
        // 소속 직원 확인
        int memberCount = departmentMapper.countDepartmentMembers(departmentId);
        if (memberCount > 0) {
            throw new IllegalStateException("소속 직원이 있는 부서는 삭제할 수 없습니다");
        }
        
        int result = departmentMapper.deleteDepartment(departmentId);
        if (result != 1) {
            throw new RuntimeException("부서 삭제에 실패했습니다");
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<DepartmentTreeDTO> getDepartmentTree() {
        List<DepartmentVO> departments = departmentMapper.selectDepartmentTree();
        return buildDepartmentTree(departments);
    }
    
    @Override
    public void updateDepartmentOrder(List<DepartmentOrderDTO> orderDTOs) {
        List<DepartmentOrderVO> orders = orderDTOs.stream()
            .map(dto -> modelMapper.map(dto, DepartmentOrderVO.class))
            .collect(Collectors.toList());
            
        int result = departmentMapper.updateDepartmentOrder(orders);
        if (result != orders.size()) {
            throw new RuntimeException("부서 순서 변경에 실패했습니다");
        }
    }
    
    private List<DepartmentTreeDTO> buildDepartmentTree(List<DepartmentVO> departments) {
        // 트리 구조 생성 로직 구현
        // ...
    }
}
```

## Connection Service
### 변경 전
```java
// 별도의 서비스 클래스 없이 컨트롤러에서 직접 처리
```

### 변경 후
```java
@Service
@Transactional
@RequiredArgsConstructor
public class ConnectionServiceImpl implements ConnectionService {
    
    private final ConnectionMapper connectionMapper;
    private final ModelMapper modelMapper;
    
    @Override
    @Transactional(readOnly = true)
    public List<ConnectionDTO> getConnectionsByServer(String serverId) {
        List<ConnectionVO> connections = connectionMapper.selectConnectionsByServerId(serverId);
        return connections.stream()
            .map(conn -> modelMapper.map(conn, ConnectionDTO.class))
            .collect(Collectors.toList());
    }
    
    @Override
    @Transactional(readOnly = true)
    public ConnectionDTO getConnection(String connectionId) {
        ConnectionVO connection = Optional.ofNullable(connectionMapper.selectConnection(connectionId))
            .orElseThrow(() -> new NotFoundException("연결을 찾을 수 없습니다: " + connectionId));
        return modelMapper.map(connection, ConnectionDTO.class);
    }
    
    @Override
    public ConnectionDTO createConnection(ConnectionDTO connectionDTO) {
        ConnectionVO connection = modelMapper.map(connectionDTO, ConnectionVO.class);
        connection.setStatus("INACTIVE");
        
        int result = connectionMapper.insertConnection(connection);
        if (result != 1) {
            throw new RuntimeException("연결 생성에 실패했습니다");
        }
        return modelMapper.map(connection, ConnectionDTO.class);
    }
    
    @Override
    public ConnectionDTO updateConnection(String connectionId, ConnectionDTO connectionDTO) {
        if (!connectionId.equals(connectionDTO.getConnectionId())) {
            throw new IllegalArgumentException("연결 ID가 일치하지 않습니다");
        }
        
        ConnectionVO existingConn = Optional.ofNullable(connectionMapper.selectConnection(connectionId))
            .orElseThrow(() -> new NotFoundException("연결을 찾을 수 없습니다: " + connectionId));
            
        ConnectionVO connection = modelMapper.map(connectionDTO, ConnectionVO.class);
        int result = connectionMapper.updateConnection(connection);
        if (result != 1) {
            throw new RuntimeException("연결 수정에 실패했습니다");
        }
        return modelMapper.map(connection, ConnectionDTO.class);
    }
    
    @Override
    public void deleteConnection(String connectionId) {
        ConnectionVO connection = Optional.ofNullable(connectionMapper.selectConnection(connectionId))
            .orElseThrow(() -> new NotFoundException("연결을 찾을 수 없습니다: " + connectionId));
            
        if ("ACTIVE".equals(connection.getStatus())) {
            throw new IllegalStateException("활성 상태의 연결은 삭제할 수 없습니다");
        }
        
        int result = connectionMapper.deleteConnection(connectionId);
        if (result != 1) {
            throw new RuntimeException("연결 삭제에 실패했습니다");
        }
    }
    
    @Override
    public void updateConnectionStatus(String connectionId, String status) {
        ConnectionVO connection = Optional.ofNullable(connectionMapper.selectConnection(connectionId))
            .orElseThrow(() -> new NotFoundException("연결을 찾을 수 없습니다: " + connectionId));
            
        if ("ACTIVE".equals(status)) {
            int activeCount = connectionMapper.countActiveConnections(connection.getServerId());
            if (activeCount >= 5) {
                throw new IllegalStateException("서버당 최대 5개의 활성 연결만 허용됩니다");
            }
        }
        
        int result = connectionMapper.updateConnectionStatus(connectionId, status);
        if (result != 1) {
            throw new RuntimeException("연결 상태 변경에 실패했습니다");
        }
    }
} 