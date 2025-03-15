# Service 변경 사항

## 1. 공통 변경 사항
1. 트랜잭션 처리
```java
// AS-IS
@Transactional
public void save(CodeVO vo) {
    mapper.insert(vo);
}

// TO-BE
@Transactional
public CodeDTO save(CodeSaveRequest request) {
    CodeVO vo = CodeMapper.INSTANCE.toVO(request);
    mapper.insert(vo);
    return CodeMapper.INSTANCE.toDTO(vo);
}
```

2. 예외 처리
```java
// AS-IS
try {
    // 비즈니스 로직
} catch (Exception e) {
    e.printStackTrace();
    return null;
}

// TO-BE
try {
    // 비즈니스 로직
} catch (Exception e) {
    log.error("에러 발생", e);
    throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
}
```

3. 매퍼 사용
```java
// AS-IS
CodeVO vo = new CodeVO();
vo.setCodeId(request.getCodeId());
// ...

// TO-BE
@Mapper
public interface CodeMapper {
    CodeVO toVO(CodeSaveRequest request);
    CodeDTO toDTO(CodeVO vo);
}
```

## 2. 기능별 변경 사항
### 2.1 관리자 기능
#### CodeService
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class CodeService {
    private final CodeMapper mapper;
    private final CodeEntityMapper entityMapper;

    @Transactional(readOnly = true)
    public List<CodeDTO> list(CodeSearchRequest request) {
        List<CodeVO> codes = mapper.selectList(request);
        return codes.stream()
                   .map(entityMapper::toDTO)
                   .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CodeDTO detail(String codeId) {
        CodeVO code = mapper.selectOne(codeId)
                           .orElseThrow(() -> new NotFoundException("코드를 찾을 수 없습니다."));
        return entityMapper.toDTO(code);
    }

    @Transactional
    public CodeDTO save(CodeSaveRequest request) {
        validateCode(request);
        CodeVO code = entityMapper.toVO(request);
        mapper.insert(code);
        return entityMapper.toDTO(code);
    }

    @Transactional
    public CodeDTO update(String codeId, CodeUpdateRequest request) {
        CodeVO code = mapper.selectOne(codeId)
                           .orElseThrow(() -> new NotFoundException("코드를 찾을 수 없습니다."));
        entityMapper.updateVO(request, code);
        mapper.update(code);
        return entityMapper.toDTO(code);
    }

    @Transactional
    public void delete(String codeId) {
        if (!mapper.delete(codeId)) {
            throw new NotFoundException("코드를 찾을 수 없습니다.");
        }
    }
}
```

### 2.2 인증 기능
#### AuthService
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final UserMapper userMapper;
    private final JwtTokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    public TokenDTO login(LoginRequest request) {
        UserVO user = userMapper.selectByUsername(request.getUsername())
                               .orElseThrow(() -> new AuthenticationException("사용자를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("비밀번호가 일치하지 않습니다.");
        }

        return tokenProvider.createToken(user.getUserId());
    }

    public TokenDTO refresh(RefreshTokenRequest request) {
        if (!tokenProvider.validateToken(request.getRefreshToken())) {
            throw new AuthenticationException("유효하지 않은 토큰입니다.");
        }

        String userId = tokenProvider.getUserId(request.getRefreshToken());
        return tokenProvider.createToken(userId);
    }
}
```

### 2.3 게시판 기능
#### BoardService
```java
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardMapper mapper;
    private final BoardEntityMapper entityMapper;
    private final FileService fileService;

    @Transactional(readOnly = true)
    public PageResponse<BoardDTO> list(BoardSearchRequest request) {
        int total = mapper.selectCount(request);
        List<BoardVO> boards = mapper.selectList(request);
        
        List<BoardDTO> content = boards.stream()
                                     .map(entityMapper::toDTO)
                                     .collect(Collectors.toList());
                                     
        return new PageResponse<>(content, total, request.getPage(), request.getSize());
    }

    @Transactional
    public BoardDTO save(BoardSaveRequest request) {
        BoardVO board = entityMapper.toVO(request);
        mapper.insert(board);
        
        if (!CollectionUtils.isEmpty(request.getFiles())) {
            fileService.saveFiles(board.getBoardId(), request.getFiles());
        }
        
        return entityMapper.toDTO(board);
    }
}
```

## 3. 주요 변경 포인트
1. 비즈니스 로직 분리
   - 컨트롤러에서 비즈니스 로직 이동
   - 도메인 중심 설계
   - 책임 분리

2. 트랜잭션 관리
   - `@Transactional` 적절한 사용
   - 읽기 전용 트랜잭션 구분
   - 트랜잭션 전파 레벨 설정

3. 매핑 전략
   - MapStruct 사용
   - Request/Response DTO 분리
   - Entity Mapper 구현

4. 유효성 검증
   - 비즈니스 규칙 검증
   - 중복 체크
   - 권한 체크

5. 예외 처리
   - 커스텀 예외 정의
   - 예외 변환
   - 로깅 전략

6. 성능 최적화
   - N+1 문제 해결
   - 캐시 적용
   - 페이징 처리 