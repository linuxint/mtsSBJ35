# 서비스 계층 변경사항

## 공통 사항
1. 트랜잭션 관리 방식 개선
2. 비즈니스 로직과 데이터 접근 로직 분리
3. VO와 DTO 간 변환 로직 추가
4. 예외 처리 강화
5. 비동기 처리 도입

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