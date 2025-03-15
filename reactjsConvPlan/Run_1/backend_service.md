# 백엔드 서비스 변경 계획

## 현재 상태
- 대부분의 비즈니스 로직이 컨트롤러 또는 DAO 클래스에 직접 구현됨
- 서비스 레이어가 존재하지만 단순한 위임(delegation) 패턴으로 구현됨
- 트랜잭션 관리가 일관되지 않음
- 캐싱 전략 부재
- 비즈니스 규칙이 여러 레이어에 분산되어 있음
- 비동기 처리가 제한적이거나 없음
- 도메인 간 결합도가 높음

## 변경 계획 개요
서비스 레이어를 도메인 중심으로 재구성하고, 책임을 명확히 분리하며, 비즈니스 로직을 응집력 있게 구현합니다. 또한 트랜잭션, 캐싱, 비동기 처리 등 기술적 개선을 통해 성능과 확장성을 높이면서 유지보수성을 개선합니다.

## 상세 변경 내용

### 서비스 계층 구조 개선
현재 상태: 평면적인 서비스 구조 또는 단순 위임 패턴

변경 계획:
- 도메인 중심의 서비스 패키지 구조 구성
- 애플리케이션 서비스와 도메인 서비스 분리
- 비즈니스 로직의 서비스 계층 집중화
- 인터페이스와 구현체 분리를 통한 유연성 확보

구현 예시:
```java
// 패키지 구조
// com.devkbil.mtssbj.member.service (인터페이스)
// com.devkbil.mtssbj.member.service.impl (구현체)

// 애플리케이션 서비스
@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final MemberValidator memberValidator;
    private final EventPublisher eventPublisher;
    
    @Override
    public MemberResponseDTO createMember(MemberCreateRequestDTO requestDTO) {
        // 밸리데이션
        memberValidator.validateCreateRequest(requestDTO);
        
        // 중복 확인
        if (memberRepository.existsByUsername(requestDTO.getUsername())) {
            throw new DuplicateUsernameException(requestDTO.getUsername());
        }
        
        // 비즈니스 로직 수행
        Member member = new Member();
        member.setUsername(requestDTO.getUsername());
        member.setEmail(requestDTO.getEmail());
        member.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        member.setName(requestDTO.getName());
        member.setStatus(MemberStatus.ACTIVE);
        
        Member savedMember = memberRepository.save(member);
        
        // 이벤트 발행
        eventPublisher.publishEvent(new MemberCreatedEvent(savedMember.getId()));
        
        // 응답 반환
        return MemberResponseDTO.from(savedMember);
    }
    
    // 다른 멤버 관련 메서드들...
}

// 도메인 서비스
@Service
@RequiredArgsConstructor
class MemberValidator {
    
    private final EmailValidator emailValidator;
    
    public void validateCreateRequest(MemberCreateRequestDTO request) {
        if (StringUtils.isBlank(request.getUsername())) {
            throw new InvalidRequestException("사용자 이름은 필수 항목입니다.");
        }
        
        if (StringUtils.isBlank(request.getPassword())) {
            throw new InvalidRequestException("비밀번호는 필수 항목입니다.");
        }
        
        if (request.getPassword().length() < 8) {
            throw new InvalidRequestException("비밀번호는 최소 8자 이상이어야 합니다.");
        }
        
        if (StringUtils.isBlank(request.getEmail())) {
            throw new InvalidRequestException("이메일은 필수 항목입니다.");
        }
        
        if (!emailValidator.isValid(request.getEmail())) {
            throw new InvalidRequestException("유효하지 않은 이메일 형식입니다.");
        }
    }
}
```

### 트랜잭션 관리 개선
현재 상태: 일관되지 않은 트랜잭션 경계, 중첩된 트랜잭션 처리 미흡

변경 계획:
- 선언적 트랜잭션 관리 표준화
- 트랜잭션 전파 설정 최적화
- 비즈니스 요구사항에 맞는 격리 수준 설정
- 읽기 전용 트랜잭션 최적화

구현 예시:
```java
@Service
public class BoardServiceImpl implements BoardService {
    
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    
    // 기본 트랜잭션 설정 - 읽기/쓰기
    @Override
    @Transactional
    public BoardResponseDTO createBoard(BoardCreateRequestDTO requestDTO) {
        // 게시글 생성 로직
    }
    
    // 읽기 전용 트랜잭션 설정
    @Override
    @Transactional(readOnly = true)
    public List<BoardSummaryDTO> getBoardList(BoardSearchCriteria criteria) {
        // 게시글 목록 조회 로직
    }
    
    // 타임아웃이 설정된 트랜잭션
    @Override
    @Transactional(timeout = 10)
    public void importBoardsFromExcel(MultipartFile file) {
        // 엑셀 파일로부터 게시글 일괄 등록 로직
    }
    
    // 특정 예외 롤백 설정
    @Override
    @Transactional(rollbackFor = {BusinessException.class}, noRollbackFor = {NotFoundException.class})
    public void processBoardAction(Long boardId, String action) {
        // 게시글 액션 처리 로직
    }
    
    // 중첩 트랜잭션 처리
    @Override
    @Transactional
    public void deleteBoardWithComments(Long boardId) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new NotFoundException("게시글을 찾을 수 없습니다."));
        
        // 새로운 트랜잭션으로 댓글 삭제 (REQUIRES_NEW)
        deleteCommentsInNewTransaction(boardId);
        
        // 게시글 삭제
        boardRepository.delete(board);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteCommentsInNewTransaction(Long boardId) {
        commentRepository.deleteByBoardId(boardId);
    }
}
```

### 비즈니스 로직 캡슐화 개선
현재 상태: 비즈니스 로직이 여러 레이어에 분산되어 있음

변경 계획:
- 중요 비즈니스 로직을 서비스 계층에 집중
- 단순한 CRUD 외의.복잡한 비즈니스 규칙 구현
- 도메인 이벤트를 통한 부수 효과 처리
- 유스케이스별 서비스 메서드 설계

구현 예시:
```java
@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final EventPublisher eventPublisher;
    
    @Override
    public ScheduleResponseDTO createSchedule(ScheduleCreateRequestDTO requestDTO) {
        // 생성자 확인
        Member creator = memberRepository.findById(requestDTO.getCreatorId())
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        
        // 일정 생성
        Schedule schedule = new Schedule();
        schedule.setTitle(requestDTO.getTitle());
        schedule.setDescription(requestDTO.getDescription());
        schedule.setStartDate(requestDTO.getStartDate());
        schedule.setEndDate(requestDTO.getEndDate());
        schedule.setCreator(creator);
        schedule.setStatus(ScheduleStatus.PENDING);
        
        // 참석자 추가
        if (requestDTO.getAttendeeIds() != null && !requestDTO.getAttendeeIds().isEmpty()) {
            List<Member> attendees = memberRepository.findAllById(requestDTO.getAttendeeIds());
            schedule.addAttendees(attendees);
        }
        
        // 반복 일정 설정
        if (requestDTO.getRecurrencePattern() != null) {
            schedule.configureRecurrence(
                    requestDTO.getRecurrencePattern(),
                    requestDTO.getRecurrenceEndDate()
            );
        }
        
        // 저장
        Schedule savedSchedule = scheduleRepository.save(schedule);
        
        // 알림 발송 (비동기 처리)
        notificationService.sendScheduleCreationNotifications(savedSchedule);
        
        // 이벤트 발행
        eventPublisher.publishEvent(new ScheduleCreatedEvent(savedSchedule.getId()));
        
        return ScheduleResponseDTO.from(savedSchedule);
    }
    
    @Override
    public ScheduleResponseDTO approveSchedule(Long scheduleId, Long approverId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));
        
        Member approver = memberRepository.findById(approverId)
                .orElseThrow(() -> new NotFoundException("승인자를 찾을 수 없습니다."));
        
        // 권한 확인
        if (!schedule.canBeApprovedBy(approver)) {
            throw new AccessDeniedException("일정을 승인할 권한이 없습니다.");
        }
        
        // 비즈니스 로직 수행
        schedule.approve(approver);
        
        // 저장
        Schedule updatedSchedule = scheduleRepository.save(schedule);
        
        // 알림 발송
        notificationService.sendScheduleApprovalNotifications(updatedSchedule);
        
        // 이벤트 발행
        eventPublisher.publishEvent(new ScheduleApprovedEvent(updatedSchedule.getId()));
        
        return ScheduleResponseDTO.from(updatedSchedule);
    }
}
```

### 캐싱 전략 개선
현재 상태: 캐싱 전략 부재 또는 비효율적인 캐싱

변경 계획:
- Spring Cache 추상화 도입
- 자주 조회되는 데이터에 대한 캐싱 전략 구현
- 캐시 무효화 매커니즘 최적화
- 분산 캐싱 고려 (필요 시)

구현 예시:
```java
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("members"),
                new ConcurrentMapCache("boards"),
                new ConcurrentMapCache("schedules"),
                new ConcurrentMapCache("codeDetails")
        ));
        return cacheManager;
    }
}

@Service
@RequiredArgsConstructor
@Transactional
public class CodeServiceImpl implements CodeService {
    
    private final CodeRepository codeRepository;
    private final CodeDetailRepository codeDetailRepository;
    
    @Override
    @Cacheable(value = "codeDetails", key = "#codeId")
    @Transactional(readOnly = true)
    public List<CodeDetailDTO> getCodeDetails(String codeId) {
        return codeDetailRepository.findByCodeId(codeId).stream()
                .map(CodeDetailDTO::from)
                .collect(Collectors.toList());
    }
    
    @Override
    @CachePut(value = "codeDetails", key = "#result.codeId")
    public CodeDetailDTO createCodeDetail(CodeDetailCreateDTO createDTO) {
        CodeDetail codeDetail = new CodeDetail();
        codeDetail.setCodeId(createDTO.getCodeId());
        codeDetail.setDetailName(createDTO.getDetailName());
        codeDetail.setDetailValue(createDTO.getDetailValue());
        codeDetail.setSortOrder(createDTO.getSortOrder());
        codeDetail.setUseYn(createDTO.getUseYn());
        
        CodeDetail savedCodeDetail = codeDetailRepository.save(codeDetail);
        return CodeDetailDTO.from(savedCodeDetail);
    }
    
    @Override
    @CacheEvict(value = "codeDetails", key = "#codeId")
    public void deleteCodeDetail(String codeId, String detailValue) {
        codeDetailRepository.deleteByCodeIdAndDetailValue(codeId, detailValue);
    }
    
    @Override
    @CacheEvict(value = "codeDetails", allEntries = true)
    public void refreshAllCodes() {
        // 모든 코드 캐시를 갱신하는 로직
    }
}
```

### 비동기 처리 도입
현재 상태: 동기식 처리 위주, 사용자 응답 지연 가능성

변경 계획:
- Spring의 @Async 기능 활용
- 비동기 이벤트 처리 메커니즘 구현
- 장시간 실행 작업의 비동기 실행
- 스레드 풀 관리 및 최적화

구현 예시:
```java
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        executor.setThreadNamePrefix("AsyncTask-");
        executor.initialize();
        return executor;
    }
}

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final PushNotificationSender pushSender;
    private final NotificationRepository notificationRepository;
    
    @Override
    @Async
    public void sendScheduleCreationNotifications(Schedule schedule) {
        // 알림 기록 생성
        Notification notification = createNotification(
                schedule.getTitle(),
                "새로운 일정이 생성되었습니다.",
                NotificationType.SCHEDULE_CREATED,
                schedule.getId()
        );
        
        // 참석자에게 알림 발송
        for (Member attendee : schedule.getAttendees()) {
            if (attendee.isEmailNotificationEnabled()) {
                emailSender.sendEmail(
                        attendee.getEmail(),
                        "새로운 일정 알림",
                        "새로운 일정 '" + schedule.getTitle() + "'이 생성되었습니다."
                );
            }
            
            if (attendee.isSmsNotificationEnabled()) {
                smsSender.sendSms(
                        attendee.getPhone(),
                        "새로운 일정 '" + schedule.getTitle() + "'이 생성되었습니다."
                );
            }
            
            if (attendee.isPushNotificationEnabled()) {
                pushSender.sendPushNotification(
                        attendee.getDeviceToken(),
                        "새로운 일정 알림",
                        "새로운 일정 '" + schedule.getTitle() + "'이 생성되었습니다."
                );
            }
        }
        
        notificationRepository.save(notification);
    }
    
    @Override
    @Async
    public CompletableFuture<Boolean> sendBulkNotifications(List<NotificationRequestDTO> notifications) {
        try {
            int successCount = 0;
            for (NotificationRequestDTO notification : notifications) {
                boolean success = sendNotification(notification);
                if (success) {
                    successCount++;
                }
            }
            
            return CompletableFuture.completedFuture(successCount == notifications.size());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
    
    private boolean sendNotification(NotificationRequestDTO notification) {
        // 알림 발송 로직
        return true;
    }
}
```

### 이벤트 기반 아키텍처 도입
현재 상태: 직접적인 서비스 호출, 강한 결합

변경 계획:
- Spring의 ApplicationEvent 활용
- 이벤트 발행/구독 메커니즘 구현
- 도메인 이벤트와 시스템 이벤트 구분
- 비동기 이벤트 처리

구현 예시:
```java
// 이벤트 클래스
public class MemberCreatedEvent extends ApplicationEvent {
    private final Long memberId;
    
    public MemberCreatedEvent(Long memberId) {
        super(memberId);
        this.memberId = memberId;
    }
    
    public Long getMemberId() {
        return memberId;
    }
}

// 이벤트 발행자
@Component
@RequiredArgsConstructor
public class EventPublisherImpl implements EventPublisher {
    
    private final ApplicationEventPublisher applicationEventPublisher;
    
    @Override
    public void publishEvent(ApplicationEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

// 이벤트 리스너
@Component
@RequiredArgsConstructor
public class MemberEventListener {
    
    private final WelcomeEmailService welcomeEmailService;
    private final AuditService auditService;
    
    @EventListener
    @Async
    public void handleMemberCreatedEvent(MemberCreatedEvent event) {
        // 환영 이메일 발송
        welcomeEmailService.sendWelcomeEmail(event.getMemberId());
        
        // 감사 로그 기록
        auditService.logMemberCreation(event.getMemberId());
    }
}
```

### 서비스 계층 테스트 강화
현재 상태: 테스트 부족 또는 비효율적인 테스트

변경 계획:
- 단위 테스트 및 통합 테스트 구현
- Mockito를 활용한 의존성 모킹
- 테스트 가독성 및 유지보수성 향상
- 테스트 커버리지 확대

구현 예시:
```java
@ExtendWith(MockitoExtension.class)
class BoardServiceImplTest {
    
    @Mock
    private BoardRepository boardRepository;
    
    @Mock
    private MemberRepository memberRepository;
    
    @Mock
    private EventPublisher eventPublisher;
    
    @InjectMocks
    private BoardServiceImpl boardService;
    
    @Test
    @DisplayName("게시글 생성 성공 테스트")
    void createBoard_Success() {
        // Given
        BoardCreateRequestDTO requestDTO = new BoardCreateRequestDTO();
        requestDTO.setTitle("테스트 제목");
        requestDTO.setContent("테스트 내용");
        requestDTO.setWriterId(1L);
        
        Member writer = new Member();
        writer.setId(1L);
        writer.setUsername("testuser");
        
        Board savedBoard = new Board();
        savedBoard.setId(1L);
        savedBoard.setTitle("테스트 제목");
        savedBoard.setContent("테스트 내용");
        savedBoard.setWriter(writer);
        
        // When
        when(memberRepository.findById(1L)).thenReturn(Optional.of(writer));
        when(boardRepository.save(any(Board.class))).thenReturn(savedBoard);
        
        // Then
        BoardResponseDTO responseDTO = boardService.createBoard(requestDTO);
        
        assertNotNull(responseDTO);
        assertEquals(1L, responseDTO.getId());
        assertEquals("테스트 제목", responseDTO.getTitle());
        assertEquals("테스트 내용", responseDTO.getContent());
        assertEquals("testuser", responseDTO.getWriterName());
        
        verify(boardRepository).save(any(Board.class));
        verify(eventPublisher).publishEvent(any(BoardCreatedEvent.class));
    }
    
    @Test
    @DisplayName("존재하지 않는 작성자로 게시글 생성 시 예외 발생")
    void createBoard_WithNonExistingWriter_ThrowsException() {
        // Given
        BoardCreateRequestDTO requestDTO = new BoardCreateRequestDTO();
        requestDTO.setTitle("테스트 제목");
        requestDTO.setContent("테스트 내용");
        requestDTO.setWriterId(999L);
        
        // When
        when(memberRepository.findById(999L)).thenReturn(Optional.empty());
        
        // Then
        assertThrows(NotFoundException.class, () -> boardService.createBoard(requestDTO));
        
        verify(memberRepository).findById(999L);
        verify(boardRepository, never()).save(any(Board.class));
        verify(eventPublisher, never()).publishEvent(any(BoardCreatedEvent.class));
    }
}
```

### 도메인별 구체적인 변경 계획

#### 회원 관련 서비스
- 회원 등록, 수정, 탈퇴 프로세스 구현
- 인증 및 권한 관련 비즈니스 로직 분리
- 프로필 관리 기능 개선
- 회원 통계 및 보고서 기능 추가

#### 게시판 관련 서비스
- 게시글 CRUD 및 댓글 관리 로직 구현
- 첨부 파일 처리 기능 개선
- 게시판 분류 및 태그 관리 기능 추가
- 게시글 검색 및 필터링 최적화

#### 일정 관련 서비스
- 일정 등록, 수정, 삭제 처리
- 반복 일정 관리 로직 구현
- 일정 알림 및 공유 기능 개선
- 캘린더 뷰 데이터 제공 최적화

#### 메일 관련 서비스
- 메일 송수신 기능 구현
- 첨부 파일 처리 로직 개선
- 메일 템플릿 관리 기능 추가
- 대량 메일 발송 기능 최적화

#### 관리자 관련 서비스
- 시스템 설정 관리 기능 구현
- 사용자 및 권한 관리 로직 개선
- 로그 및 감사 기록 관리 기능 추가
- 통계 및 보고서 생성 기능 최적화

### 마이그레이션 단계
1. 서비스 인터페이스 및 구현체 정의
2. 비즈니스 로직 추출 및 서비스 계층으로 이동
3. 트랜잭션 경계 및 속성 최적화
4. 캐싱 전략 적용
5. 비동기 처리 구현
6. 이벤트 기반 아키텍처 적용
7. 단위 테스트 및 통합 테스트 구현
8. 성능 테스트 및 최적화 