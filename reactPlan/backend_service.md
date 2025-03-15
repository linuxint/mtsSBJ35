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

## 메일 모듈 확장

### 메일 통합 설정
#### 변경 전
레거시 시스템에서는 JavaMail API를 직접 사용했습니다.

#### 변경 후
```java
@Configuration
@EnableIntegration
public class MailIntegrationConfig {

    @Value("${mail.imap.host}")
    private String imapHost;
    
    @Value("${mail.imap.port}")
    private int imapPort;
    
    @Value("${mail.smtp.host}")
    private String smtpHost;
    
    @Value("${mail.smtp.port}")
    private int smtpPort;
    
    @Bean
    public ImapIdleChannelAdapter imapIdleChannelAdapter() {
        ImapMailReceiver mailReceiver = new ImapMailReceiver("imaps://" + imapHost + ":" + imapPort + "/INBOX");
        mailReceiver.setShouldMarkMessagesAsRead(true);
        mailReceiver.setShouldDeleteMessages(false);
        
        ImapIdleChannelAdapter adapter = new ImapIdleChannelAdapter(mailReceiver);
        adapter.setAutoStartup(true);
        adapter.setOutputChannelName("imapChannel");
        
        return adapter;
    }
    
    @Bean
    public MessageChannel imapChannel() {
        return new DirectChannel();
    }
    
    @Bean
    @ServiceActivator(inputChannel = "imapChannel")
    public MessageHandler handleIncomingMail() {
        return message -> {
            Object payload = message.getPayload();
            if (payload instanceof MimeMessage) {
                // 메일 처리 로직
            }
        };
    }
    
    @Bean
    public JavaMailSender mailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        
        return mailSender;
    }
}
```

### Spring Integration 메일 송신
#### 변경 전
```java
public void sendMail(String to, String subject, String text) {
    Properties props = new Properties();
    props.put("mail.smtp.host", "smtp.example.com");
    props.put("mail.smtp.port", "587");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    
    Session session = Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
        }
    });
    
    try {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("from@example.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(text);
        
        Transport.send(message);
    } catch (MessagingException e) {
        throw new RuntimeException(e);
    }
}
```

#### 변경 후
```java
@Service
public class SpringIntegrationSendMail {
    
    private final JavaMailSender mailSender;
    private final MessageChannel mailChannel;
    
    @Autowired
    public SpringIntegrationSendMail(JavaMailSender mailSender, 
                                    @Qualifier("mailChannel") MessageChannel mailChannel) {
        this.mailSender = mailSender;
        this.mailChannel = mailChannel;
    }
    
    @Bean
    public MessageChannel mailChannel() {
        return new DirectChannel();
    }
    
    @Bean
    @ServiceActivator(inputChannel = "mailChannel")
    public MessageHandler mailMessageHandler() {
        MailSendingMessageHandler handler = new MailSendingMessageHandler(mailSender);
        handler.setAsync(true);
        return handler;
    }
    
    public void sendEmail(String to, String subject, String text) {
        Map<String, Object> headers = new HashMap<>();
        headers.put(MailHeaders.SUBJECT, subject);
        headers.put(MailHeaders.TO, to);
        
        Message<String> message = MessageBuilder
                .withPayload(text)
                .copyHeaders(headers)
                .build();
        
        mailChannel.send(message);
    }
}
```

### Spring Integration IMAP
#### 변경 전
```java
public List<MailVO> readMail(String username, String password) {
    Properties props = new Properties();
    props.put("mail.store.protocol", "imaps");
    props.put("mail.imaps.host", "imap.example.com");
    props.put("mail.imaps.port", "993");
    
    List<MailVO> mails = new ArrayList<>();
    
    try {
        Session session = Session.getDefaultInstance(props, null);
        Store store = session.getStore("imaps");
        store.connect("imap.example.com", username, password);
        
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        
        Message[] messages = inbox.getMessages();
        for (Message message : messages) {
            MailVO mail = new MailVO();
            mail.setSubject(message.getSubject());
            mail.setFrom(message.getFrom()[0].toString());
            mail.setSentDate(message.getSentDate());
            mail.setContent(message.getContent().toString());
            mails.add(mail);
        }
        
        inbox.close(false);
        store.close();
    } catch (Exception e) {
        e.printStackTrace();
    }
    
    return mails;
}
```

#### 변경 후
```java
@Service
public class SpringIntegrationImap {
    
    private final ImapIdleChannelAdapter imapAdapter;
    private final MailRepository mailRepository;
    
    @Autowired
    public SpringIntegrationImap(ImapIdleChannelAdapter imapAdapter, MailRepository mailRepository) {
        this.imapAdapter = imapAdapter;
        this.mailRepository = mailRepository;
    }
    
    @ServiceActivator(inputChannel = "imapChannel")
    public void processNewEmail(Message<?> message) {
        if (message.getPayload() instanceof MimeMessage) {
            MimeMessage email = (MimeMessage) message.getPayload();
            try {
                MailVO mail = new MailVO();
                mail.setSubject(email.getSubject());
                mail.setFrom(email.getFrom()[0].toString());
                mail.setSentDate(email.getSentDate());
                
                // 메일 내용 추출
                Object content = email.getContent();
                if (content instanceof String) {
                    mail.setContent((String) content);
                } else if (content instanceof Multipart) {
                    // 멀티파트 메일 처리
                    processMultipart((Multipart) content, mail);
                }
                
                // 메일 저장
                mailRepository.save(mail);
            } catch (Exception e) {
                throw new RuntimeException("메일 처리 중 오류 발생", e);
            }
        }
    }
    
    private void processMultipart(Multipart multipart, MailVO mail) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (bodyPart.getContentType().toLowerCase().contains("text/plain")) {
                mail.setContent((String) bodyPart.getContent());
            } else if (bodyPart.getContentType().toLowerCase().contains("text/html")) {
                mail.setHtmlContent((String) bodyPart.getContent());
            } else if (bodyPart.getDisposition() != null && 
                      bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                saveAttachment(bodyPart, mail);
            }
        }
    }
    
    private void saveAttachment(BodyPart bodyPart, MailVO mail) throws Exception {
        String fileName = bodyPart.getFileName();
        InputStream is = bodyPart.getInputStream();
        
        // 첨부 파일 저장 로직
        byte[] bytes = IOUtils.toByteArray(is);
        
        MailAttachmentVO attachment = new MailAttachmentVO();
        attachment.setFileName(fileName);
        attachment.setFileSize(bytes.length);
        attachment.setFilePath(saveFile(fileName, bytes));
        attachment.setMailId(mail.getId());
        
        mail.getAttachments().add(attachment);
    }
    
    private String saveFile(String fileName, byte[] data) {
        // 파일 저장 처리
        String path = "mail_attachments/" + UUID.randomUUID() + "_" + fileName;
        try {
            FileUtils.writeByteArrayToFile(new File(path), data);
        } catch (IOException e) {
            throw new FileStorageException("첨부 파일 저장 실패", e);
        }
        return path;
    }
}
```

### ImportMail
#### 변경 전
레거시 시스템에서는 메일 가져오기가 동기식으로 처리되었습니다.

#### 변경 후
```java
@Service
public class ImportMail {
    
    private final MailRepository mailRepository;
    private final JavaMailSender mailSender;
    
    @Autowired
    public ImportMail(MailRepository mailRepository, JavaMailSender mailSender) {
        this.mailRepository = mailRepository;
        this.mailSender = mailSender;
    }
    
    @Async
    public CompletableFuture<List<MailVO>> importMailsAsync(String username, String password) {
        return CompletableFuture.supplyAsync(() -> {
            return importMails(username, password);
        });
    }
    
    public List<MailVO> importMails(String username, String password) {
        List<MailVO> importedMails = new ArrayList<>();
        
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        
        try {
            Session session = Session.getInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.example.com", username, password);
            
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            
            // 최근 30일 이내의 메일만 가져오기
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -30);
            Date thirtyDaysAgo = cal.getTime();
            
            SearchTerm newerThan = new ReceivedDateTerm(ComparisonTerm.GT, thirtyDaysAgo);
            Message[] messages = inbox.search(newerThan);
            
            for (Message message : messages) {
                MailVO mail = convertMessageToMailVO(message);
                importedMails.add(mail);
                mailRepository.save(mail);
            }
            
            inbox.close(false);
            store.close();
        } catch (Exception e) {
            throw new MailProcessingException("메일 가져오기 실패", e);
        }
        
        return importedMails;
    }
    
    private MailVO convertMessageToMailVO(Message message) throws Exception {
        MailVO mail = new MailVO();
        mail.setSubject(message.getSubject());
        mail.setFrom(Arrays.toString(message.getFrom()));
        mail.setTo(Arrays.toString(message.getRecipients(Message.RecipientType.TO)));
        mail.setSentDate(message.getSentDate());
        
        // 메일 내용 추출
        Object content = message.getContent();
        if (content instanceof String) {
            mail.setContent((String) content);
        } else if (content instanceof Multipart) {
            extractMultipartContent((Multipart) content, mail);
        }
        
        return mail;
    }
    
    private void extractMultipartContent(Multipart multipart, MailVO mail) throws Exception {
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (bodyPart.getContentType().toLowerCase().contains("text/plain")) {
                mail.setContent((String) bodyPart.getContent());
            } else if (bodyPart.getContentType().toLowerCase().contains("text/html")) {
                mail.setHtmlContent((String) bodyPart.getContent());
            } else if (bodyPart.getDisposition() != null && 
                      bodyPart.getDisposition().equalsIgnoreCase(Part.ATTACHMENT)) {
                saveAttachment(bodyPart, mail);
            }
        }
    }
    
    private void saveAttachment(BodyPart bodyPart, MailVO mail) throws Exception {
        String fileName = bodyPart.getFileName();
        InputStream is = bodyPart.getInputStream();
        
        // 첨부 파일 저장 로직
        byte[] bytes = IOUtils.toByteArray(is);
        
        MailAttachmentVO attachment = new MailAttachmentVO();
        attachment.setFileName(fileName);
        attachment.setFileSize(bytes.length);
        attachment.setFilePath(saveFile(fileName, bytes));
        attachment.setMailId(mail.getId());
        
        mail.getAttachments().add(attachment);
    }
    
    private String saveFile(String fileName, byte[] data) {
        // 파일 저장 처리
        String path = "mail_attachments/" + UUID.randomUUID() + "_" + fileName;
        try {
            FileUtils.writeByteArrayToFile(new File(path), data);
        } catch (IOException e) {
            throw new FileStorageException("첨부 파일 저장 실패", e);
        }
        return path;
    }
}
```

## QR코드 기능

### QRCodeGenerator
#### 변경 전
레거시 시스템에서는 단순한 QR코드 생성 방식이 사용되었습니다.

#### 변경 후
```java
@Component
public class QRCodeGenerator {
    
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    private static final String DEFAULT_FORMAT = "PNG";
    
    public byte[] generateQRCode(String content) throws Exception {
        return generateQRCode(content, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
    
    public byte[] generateQRCode(String content, int width, int height) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, DEFAULT_FORMAT, outputStream);
        
        return outputStream.toByteArray();
    }
    
    public String generateQRCodeBase64(String content) throws Exception {
        byte[] qrCode = generateQRCode(content);
        return Base64.getEncoder().encodeToString(qrCode);
    }
    
    public void saveQRCodeToFile(String content, String filePath) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 
                                DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        Path path = FileSystems.getDefault().getPath(filePath);
        MatrixToImageWriter.writeToPath(bitMatrix, DEFAULT_FORMAT, path);
    }
    
    public BufferedImage generateQRCodeImage(String content) throws Exception {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, 
                                DEFAULT_WIDTH, DEFAULT_HEIGHT);
        
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
```

### QrCodeService
#### 변경 전
레거시 시스템에서는 QR코드의 통합 서비스가 제공되지 않았습니다.

#### 변경 후
```java
@Service
public class QrCodeService {
    
    private final QRCodeGenerator qrCodeGenerator;
    private final QrCodeRepository qrCodeRepository;
    
    @Autowired
    public QrCodeService(QRCodeGenerator qrCodeGenerator, QrCodeRepository qrCodeRepository) {
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrCodeRepository = qrCodeRepository;
    }
    
    @Transactional
    public QrCodeDTO generateAndSaveQrCode(String content, String description) throws Exception {
        // QR 코드 생성
        byte[] qrCodeImage = qrCodeGenerator.generateQRCode(content);
        String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
        
        // QR 코드 정보 저장
        QrCodeEntity qrCode = new QrCodeEntity();
        qrCode.setContent(content);
        qrCode.setDescription(description);
        qrCode.setCreatedDate(new Date());
        qrCode.setBase64Image(base64Image);
        
        qrCodeRepository.save(qrCode);
        
        // DTO 변환하여 반환
        QrCodeDTO dto = new QrCodeDTO();
        dto.setId(qrCode.getId());
        dto.setContent(qrCode.getContent());
        dto.setDescription(qrCode.getDescription());
        dto.setCreatedDate(qrCode.getCreatedDate());
        dto.setBase64Image(qrCode.getBase64Image());
        
        return dto;
    }
    
    public QrCodeDTO getQrCodeById(Long id) {
        Optional<QrCodeEntity> qrCodeOptional = qrCodeRepository.findById(id);
        
        if (qrCodeOptional.isPresent()) {
            QrCodeEntity qrCode = qrCodeOptional.get();
            
            QrCodeDTO dto = new QrCodeDTO();
            dto.setId(qrCode.getId());
            dto.setContent(qrCode.getContent());
            dto.setDescription(qrCode.getDescription());
            dto.setCreatedDate(qrCode.getCreatedDate());
            dto.setBase64Image(qrCode.getBase64Image());
            
            return dto;
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    public List<QrCodeDTO> getAllQrCodes() {
        List<QrCodeEntity> qrCodes = qrCodeRepository.findAll();
        
        return qrCodes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ResponseEntity<byte[]> getQrCodeImageById(Long id) throws Exception {
        Optional<QrCodeEntity> qrCodeOptional = qrCodeRepository.findById(id);
        
        if (qrCodeOptional.isPresent()) {
            QrCodeEntity qrCode = qrCodeOptional.get();
            byte[] imageBytes = Base64.getDecoder().decode(qrCode.getBase64Image());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    @Transactional
    public void deleteQrCode(Long id) {
        if (qrCodeRepository.existsById(id)) {
            qrCodeRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    private QrCodeDTO convertToDTO(QrCodeEntity entity) {
        QrCodeDTO dto = new QrCodeDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setBase64Image(entity.getBase64Image());
        return dto;
    }
}
```

### QrCodeRepository
#### 변경 전
레거시 시스템에서는 QR코드 저장소가 구현되지 않았습니다.

#### 변경 후
```java
@Repository
public interface QrCodeRepository extends JpaRepository<QrCodeEntity, Long> {
    
    List<QrCodeEntity> findByContentContaining(String content);
    
    List<QrCodeEntity> findByCreatedDateBetween(Date startDate, Date endDate);
    
    @Query("SELECT q FROM QrCodeEntity q WHERE q.description LIKE %:keyword%")
    List<QrCodeEntity> searchByDescription(@Param("keyword") String keyword);
    
    @Query(value = "SELECT * FROM qr_code WHERE created_date > :date", nativeQuery = true)
    List<QrCodeEntity> findRecentQrCodes(@Param("date") Date date);
    
    Optional<QrCodeEntity> findByContent(String content);
}
```

### QrCodeServiceImpl
#### 변경 전
레거시 시스템에서는 서비스 구현체가 분리되지 않았습니다.

#### 변경 후
```java
@Service
public class QrCodeServiceImpl implements QrCodeService {
    
    private final QRCodeGenerator qrCodeGenerator;
    private final QrCodeRepository qrCodeRepository;
    private final QrCodeMapper qrCodeMapper;
    
    @Autowired
    public QrCodeServiceImpl(QRCodeGenerator qrCodeGenerator, 
                            QrCodeRepository qrCodeRepository,
                            QrCodeMapper qrCodeMapper) {
        this.qrCodeGenerator = qrCodeGenerator;
        this.qrCodeRepository = qrCodeRepository;
        this.qrCodeMapper = qrCodeMapper;
    }
    
    @Override
    @Transactional
    public QrCodeDTO generateAndSaveQrCode(String content, String description) throws Exception {
        // QR 코드 생성
        byte[] qrCodeImage = qrCodeGenerator.generateQRCode(content);
        String base64Image = Base64.getEncoder().encodeToString(qrCodeImage);
        
        // QR 코드 정보 저장
        QrCodeEntity qrCode = new QrCodeEntity();
        qrCode.setContent(content);
        qrCode.setDescription(description);
        qrCode.setCreatedDate(new Date());
        qrCode.setBase64Image(base64Image);
        
        qrCodeRepository.save(qrCode);
        
        // DTO 변환하여 반환
        return qrCodeMapper.entityToDto(qrCode);
    }
    
    @Override
    public QrCodeDTO getQrCodeById(Long id) {
        Optional<QrCodeEntity> qrCodeOptional = qrCodeRepository.findById(id);
        
        if (qrCodeOptional.isPresent()) {
            QrCodeEntity qrCode = qrCodeOptional.get();
            return qrCodeMapper.entityToDto(qrCode);
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    @Override
    public List<QrCodeDTO> getAllQrCodes() {
        List<QrCodeEntity> qrCodes = qrCodeRepository.findAll();
        
        return qrCodes.stream()
                .map(qrCodeMapper::entityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResponseEntity<byte[]> getQrCodeImageById(Long id) throws Exception {
        Optional<QrCodeEntity> qrCodeOptional = qrCodeRepository.findById(id);
        
        if (qrCodeOptional.isPresent()) {
            QrCodeEntity qrCode = qrCodeOptional.get();
            byte[] imageBytes = Base64.getDecoder().decode(qrCode.getBase64Image());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    @Override
    @Transactional
    public void deleteQrCode(Long id) {
        if (qrCodeRepository.existsById(id)) {
            qrCodeRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("QR 코드를 찾을 수 없습니다: " + id);
        }
    }
    
    @Override
    public List<QrCodeDTO> searchQrCodes(String keyword) {
        List<QrCodeEntity> results = qrCodeRepository.searchByDescription(keyword);
        
        return results.stream()
                .map(qrCodeMapper::entityToDto)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<QrCodeDTO> getRecentQrCodes(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        Date date = calendar.getTime();
        
        List<QrCodeEntity> recentQrCodes = qrCodeRepository.findRecentQrCodes(date);
        
        return recentQrCodes.stream()
                .map(qrCodeMapper::entityToDto)
                .collect(Collectors.toList());
    }
}
```

### QrCodeMapper
#### 변경 전
레거시 시스템에서는 매퍼 클래스가 사용되지 않았습니다.

#### 변경 후
```java
@Component
public class QrCodeMapper {
    
    public QrCodeDTO entityToDto(QrCodeEntity entity) {
        QrCodeDTO dto = new QrCodeDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setDescription(entity.getDescription());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setBase64Image(entity.getBase64Image());
        return dto;
    }
    
    public QrCodeEntity dtoToEntity(QrCodeDTO dto) {
        QrCodeEntity entity = new QrCodeEntity();
        entity.setId(dto.getId());
        entity.setContent(dto.getContent());
        entity.setDescription(dto.getDescription());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setBase64Image(dto.getBase64Image());
        return entity;
    }
    
    public List<QrCodeDTO> entitiesToDtos(List<QrCodeEntity> entities) {
        return entities.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }
} 