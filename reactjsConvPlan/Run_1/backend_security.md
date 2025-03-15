# 백엔드 보안 변경 계획

## 현재 상태
- 전통적인 Spring Security 구성 사용 중
- 세션 기반 인증 메커니즘
- JSP 기반 폼 로그인
- 권한 검사가 컨트롤러 로직 내 또는 XML 설정에 하드코딩
- CSRF 토큰을 사용한 기본적인 보안
- 단순한 접근 제어 메커니즘
- 비밀번호 암호화에 기본 알고리즘 사용

## 변경 계획 개요
세션 기반 인증에서 JWT 기반의 토큰 인증으로 전환하고, 더 세밀한 권한 제어와 강화된 보안 메커니즘을 구현하여, 현대적인 React 프론트엔드와 원활하게 통합될 수 있는 보안 아키텍처를 구축합니다.

## 상세 변경 내용

### Spring Security 설정 현대화
현재 상태: XML 기반 또는 WebSecurityConfigurerAdapter 상속 방식의 설정

변경 계획:
- Spring Security 최신 버전으로 업그레이드 (5.7 이상 또는 6.0 이상)
- WebSecurityConfigurerAdapter 대신 SecurityFilterChain 빈 사용
- 컴포넌트 기반 구성으로 모듈성 향상
- 보안 헤더 설정 강화

구현 예시:
```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                           JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                           JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // REST API이므로 CSRF 보호 불필요
            .formLogin(formLogin -> formLogin.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/v1/auth/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/v1/boards/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/v1/boards/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/v1/boards/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/boards/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
            .requestMatchers("/v3/api-docs/**")
            .requestMatchers("/swagger-ui/**")
            .requestMatchers("/swagger-resources/**")
            .requestMatchers("/webjars/**")
            .requestMatchers("/error")
            .requestMatchers("/favicon.ico");
    }
}
```

### JWT 인증 시스템 구현
현재 상태: 세션 기반 인증

변경 계획:
- JWT 토큰 생성 및 검증 유틸리티 구현
- 토큰 기반 인증 필터 구현
- 액세스 토큰과 리프레시 토큰 메커니즘 도입
- 토큰 만료 및 갱신 처리 로직 구현
- 토큰 블랙리스트 관리 (필요 시)

구현 예시:
```java
// JWT 유틸리티 클래스
@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    private String jwtSecret;
    
    @Value("${app.jwt.access-token-expiration}")
    private long accessTokenExpiration;
    
    @Value("${app.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;
    
    private final UserDetailsService userDetailsService;
    
    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    
    public String generateAccessToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String generateRefreshToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpiration);
        
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()), SignatureAlgorithm.HS512)
                .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }
    
    public List<String> getRolesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        return claims.get("roles", List.class);
    }
}

// JWT 인증 필터
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                // 권한 정보 추출
                List<String> roles = jwtTokenProvider.getRolesFromToken(jwt);
                List<GrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                
                // Spring Security 컨텍스트에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication = 
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

### 인증 컨트롤러 구현
현재 상태: 폼 로그인 및 세션 관리

변경 계획:
- REST API 기반의 인증 컨트롤러 구현
- 로그인, 회원가입, 토큰 갱신 엔드포인트 제공
- 비밀번호 재설정 워크플로우 구현
- OAuth2 소셜 로그인 지원 (필요 시)

구현 예시:
```java
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    
    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtTokenProvider jwtTokenProvider,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);
        
        // 리프레시 토큰 저장
        refreshTokenService.createRefreshToken(loginRequest.getUsername(), refreshToken);
        
        return ResponseEntity.ok(new JwtAuthResponse(accessToken, refreshToken));
    }
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody SignUpRequest signUpRequest) {
        // 사용자 이름 중복 확인
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "이미 사용 중인 사용자 이름입니다."));
        }
        
        // 이메일 중복 확인
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "이미 사용 중인 이메일입니다."));
        }
        
        // 사용자 생성
        User user = new User();
        user.setName(signUpRequest.getName());
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        
        Role userRole = new Role();
        userRole.setName(RoleName.ROLE_USER);
        user.setRoles(Collections.singleton(userRole));
        
        userRepository.save(user);
        
        return ResponseEntity.ok(new ApiResponse(true, "사용자 등록이 완료되었습니다."));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        // 리프레시 토큰 검증
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshToken -> {
                    String username = refreshToken.getUsername();
                    
                    // 리프레시 토큰 검증
                    if (!jwtTokenProvider.validateToken(refreshToken.getToken())) {
                        refreshTokenService.deleteByToken(refreshToken.getToken());
                        throw new TokenRefreshException(refreshToken.getToken(), "유효하지 않은 리프레시 토큰입니다.");
                    }
                    
                    // 새 액세스 토큰 생성
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    
                    String accessToken = jwtTokenProvider.generateAccessToken(authentication);
                    
                    return ResponseEntity.ok(new JwtAuthResponse(accessToken, refreshToken.getToken()));
                })
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(), "리프레시 토큰을 찾을 수 없습니다."));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@Valid @RequestBody LogoutRequest logoutRequest) {
        refreshTokenService.deleteByToken(logoutRequest.getRefreshToken());
        return ResponseEntity.ok(new ApiResponse(true, "로그아웃 되었습니다."));
    }
}
```

### 권한 부여 시스템 개선
현재 상태: 단순한 역할 기반 권한 체계

변경 계획:
- 세밀한 권한 체계 설계
- 메서드 수준 보안 적용 (@PreAuthorize 활용)
- 커스텀 권한 평가기 구현
- 동적 권한 검사 기능 추가

구현 예시:
```java
// 권한 평가기
@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    private final BoardRepository boardRepository;
    private final ScheduleRepository scheduleRepository;
    
    public CustomPermissionEvaluator(BoardRepository boardRepository,
                                    ScheduleRepository scheduleRepository) {
        this.boardRepository = boardRepository;
        this.scheduleRepository = scheduleRepository;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if (authentication == null || targetDomainObject == null || !(permission instanceof String)) {
            return false;
        }
        
        String permissionString = (String) permission;
        
        if (targetDomainObject instanceof Board) {
            return hasPermissionOnBoard(authentication, (Board) targetDomainObject, permissionString);
        } else if (targetDomainObject instanceof Schedule) {
            return hasPermissionOnSchedule(authentication, (Schedule) targetDomainObject, permissionString);
        }
        
        return false;
    }
    
    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if (authentication == null || targetId == null || targetType == null || !(permission instanceof String)) {
            return false;
        }
        
        String permissionString = (String) permission;
        
        if (targetType.equals("Board")) {
            return hasPermissionOnBoard(authentication, (Long) targetId, permissionString);
        } else if (targetType.equals("Schedule")) {
            return hasPermissionOnSchedule(authentication, (Long) targetId, permissionString);
        }
        
        return false;
    }
    
    private boolean hasPermissionOnBoard(Authentication authentication, Board board, String permission) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = ((CustomUserDetails) userDetails).getUser();
        
        switch (permission) {
            case "READ":
                return true; // 모든 사용자가 게시글 읽기 가능
            case "WRITE":
                return true; // 모든 인증된 사용자가 게시글 작성 가능
            case "UPDATE":
                return board.getCreatedBy().equals(user.getUsername()) || hasAdminRole(authentication);
            case "DELETE":
                return board.getCreatedBy().equals(user.getUsername()) || hasAdminRole(authentication);
            default:
                return false;
        }
    }
    
    private boolean hasPermissionOnBoard(Authentication authentication, Long boardId, String permission) {
        Optional<Board> board = boardRepository.findById(boardId);
        return board.map(b -> hasPermissionOnBoard(authentication, b, permission))
                .orElse(false);
    }
    
    private boolean hasPermissionOnSchedule(Authentication authentication, Schedule schedule, String permission) {
        // 일정 권한 검사 로직 구현
        // ...
        return false;
    }
    
    private boolean hasPermissionOnSchedule(Authentication authentication, Long scheduleId, String permission) {
        Optional<Schedule> schedule = scheduleRepository.findById(scheduleId);
        return schedule.map(s -> hasPermissionOnSchedule(authentication, s, permission))
                .orElse(false);
    }
    
    private boolean hasAdminRole(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}

// 메서드 보안 설정
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {

    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(CustomPermissionEvaluator permissionEvaluator) {
        DefaultMethodSecurityExpressionHandler expressionHandler = new DefaultMethodSecurityExpressionHandler();
        expressionHandler.setPermissionEvaluator(permissionEvaluator);
        return expressionHandler;
    }
}

// 메서드 레벨 보안 사용 예시
@Service
public class BoardService {

    @PreAuthorize("hasRole('USER')")
    public BoardResponseDTO createBoard(BoardCreateRequestDTO requestDTO) {
        // ...
    }
    
    @PreAuthorize("hasPermission(#boardId, 'Board', 'READ')")
    public BoardResponseDTO getBoardDetail(Long boardId) {
        // ...
    }
    
    @PreAuthorize("hasPermission(#boardId, 'Board', 'UPDATE')")
    public BoardResponseDTO updateBoard(Long boardId, BoardUpdateRequestDTO requestDTO) {
        // ...
    }
    
    @PreAuthorize("hasPermission(#boardId, 'Board', 'DELETE')")
    public void deleteBoard(Long boardId) {
        // ...
    }
}
```

### 비밀번호 보안 강화
현재 상태: 기본 암호화 방식

변경 계획:
- 강력한 비밀번호 정책 구현
- 최신 암호화 알고리즘 적용
- 비밀번호 이력 관리 구현
- 비밀번호 만료 정책 도입
- 다단계 인증(MFA) 옵션 검토

구현 예시:
```java
// 비밀번호 유효성 검사기
@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 100;
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile("[A-Z]");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile("[a-z]");
    private static final Pattern DIGIT_PATTERN = Pattern.compile("\\d");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile("[^A-Za-z0-9]");
    
    public List<String> validate(String password) {
        List<String> errors = new ArrayList<>();
        
        if (password == null) {
            errors.add("비밀번호는 필수 입력 항목입니다.");
            return errors;
        }
        
        if (password.length() < MIN_LENGTH) {
            errors.add("비밀번호는 최소 " + MIN_LENGTH + "자 이상이어야 합니다.");
        }
        
        if (password.length() > MAX_LENGTH) {
            errors.add("비밀번호는 최대 " + MAX_LENGTH + "자까지 가능합니다.");
        }
        
        if (!UPPERCASE_PATTERN.matcher(password).find()) {
            errors.add("비밀번호는 적어도 하나의 대문자를 포함해야 합니다.");
        }
        
        if (!LOWERCASE_PATTERN.matcher(password).find()) {
            errors.add("비밀번호는 적어도 하나의 소문자를 포함해야 합니다.");
        }
        
        if (!DIGIT_PATTERN.matcher(password).find()) {
            errors.add("비밀번호는 적어도 하나의 숫자를 포함해야 합니다.");
        }
        
        if (!SPECIAL_CHAR_PATTERN.matcher(password).find()) {
            errors.add("비밀번호는 적어도 하나의 특수문자를 포함해야 합니다.");
        }
        
        return errors;
    }
}

// 비밀번호 이력 관리
@Entity
@Table(name = "password_history")
public class PasswordHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "password_hash", nullable = false)
    private String passwordHash;
    
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;
}

@Service
public class PasswordHistoryService {

    private static final int PASSWORD_HISTORY_LIMIT = 5;
    
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    
    public PasswordHistoryService(PasswordHistoryRepository passwordHistoryRepository,
                                 PasswordEncoder passwordEncoder) {
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public void savePassword(Long userId, String password) {
        PasswordHistory history = new PasswordHistory();
        history.setUserId(userId);
        history.setPasswordHash(passwordEncoder.encode(password));
        history.setCreatedDate(LocalDateTime.now());
        
        passwordHistoryRepository.save(history);
        
        // 이력 제한 초과 시 가장 오래된 기록 삭제
        long count = passwordHistoryRepository.countByUserId(userId);
        if (count > PASSWORD_HISTORY_LIMIT) {
            passwordHistoryRepository.deleteOldestByUserId(userId, count - PASSWORD_HISTORY_LIMIT);
        }
    }
    
    public boolean isPasswordReused(Long userId, String newPassword) {
        List<PasswordHistory> history = passwordHistoryRepository.findByUserIdOrderByCreatedDateDesc(userId);
        
        for (PasswordHistory record : history) {
            if (passwordEncoder.matches(newPassword, record.getPasswordHash())) {
                return true;
            }
        }
        
        return false;
    }
}
```

### 보안 헤더 및 방어 적용
현재 상태: 기본적인 보안 헤더 설정

변경 계획:
- HTTP 보안 헤더 강화 (X-Content-Type-Options, X-Frame-Options, X-XSS-Protection 등)
- CORS 설정 최적화
- XSS 방어 메커니즘 개선
- SQL 인젝션 방어 강화
- CSRF 방어 적절히 구성 (API 토큰 방식에 맞게)

구현 예시:
```java
// 보안 헤더 설정
@Component
public class SecurityHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // X-Content-Type-Options: nosniff - MIME 스니핑 방지
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        // X-Frame-Options: DENY - 프레임 내 렌더링 금지(클릭재킹 방지)
        response.setHeader("X-Frame-Options", "DENY");
        
        // X-XSS-Protection: 1; mode=block - XSS 필터링 활성화
        response.setHeader("X-XSS-Protection", "1; mode=block");
        
        // Strict-Transport-Security - HTTPS 사용 강제
        response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
        
        // Content-Security-Policy - 콘텐츠 보안 정책 설정
        response.setHeader("Content-Security-Policy", "default-src 'self'; script-src 'self' https://trusted-cdn.com; style-src 'self' https://trusted-cdn.com; img-src 'self' data: https://trusted-cdn.com; font-src 'self' https://trusted-cdn.com; connect-src 'self'");
        
        // 캐시 제어 (인증 관련 페이지)
        if (request.getRequestURI().startsWith("/api/v1/auth/")) {
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Expires", "0");
        }
        
        filterChain.doFilter(request, response);
    }
}

// CORS 설정
@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 허용할 출처
        configuration.setAllowedOrigins(Arrays.asList("https://example.com", "http://localhost:3000"));
        
        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 허용할 헤더
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        
        // 인증 정보 포함 여부
        configuration.setAllowCredentials(true);
        
        // 캐시 시간(초)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}
```

### 보안 감사 및 로깅
현재 상태: 기본적인 로깅 또는 로깅 부재

변경 계획:
- 보안 이벤트 로깅 구현
- 인증 및 권한 관련 감사 추적
- 주요 보안 이벤트 알림 메커니즘 구축
- 로그인 시도 및 실패 모니터링
- 비정상 접근 패턴 감지

구현 예시:
```java
// 보안 감사 이벤트
public abstract class SecurityAuditEvent {
    private final String username;
    private final String remoteAddress;
    private final LocalDateTime timestamp;
    
    public SecurityAuditEvent(String username, String remoteAddress) {
        this.username = username;
        this.remoteAddress = remoteAddress;
        this.timestamp = LocalDateTime.now();
    }
    
    // Getters...
}

public class LoginSuccessEvent extends SecurityAuditEvent {
    public LoginSuccessEvent(String username, String remoteAddress) {
        super(username, remoteAddress);
    }
}

public class LoginFailureEvent extends SecurityAuditEvent {
    private final String reason;
    
    public LoginFailureEvent(String username, String remoteAddress, String reason) {
        super(username, remoteAddress);
        this.reason = reason;
    }
    
    // Getters...
}

public class AccessDeniedEvent extends SecurityAuditEvent {
    private final String resource;
    
    public AccessDeniedEvent(String username, String remoteAddress, String resource) {
        super(username, remoteAddress);
        this.resource = resource;
    }
    
    // Getters...
}

// 보안 감사 리스너
@Component
@Slf4j
public class SecurityAuditEventListener {

    private final SecurityAuditRepository securityAuditRepository;
    private final AlertService alertService;
    
    public SecurityAuditEventListener(SecurityAuditRepository securityAuditRepository,
                                     AlertService alertService) {
        this.securityAuditRepository = securityAuditRepository;
        this.alertService = alertService;
    }
    
    @EventListener
    public void handleLoginSuccessEvent(LoginSuccessEvent event) {
        log.info("Login success: user={}, remoteAddress={}", event.getUsername(), event.getRemoteAddress());
        
        SecurityAudit audit = new SecurityAudit();
        audit.setType("LOGIN_SUCCESS");
        audit.setUsername(event.getUsername());
        audit.setRemoteAddress(event.getRemoteAddress());
        audit.setTimestamp(event.getTimestamp());
        
        securityAuditRepository.save(audit);
    }
    
    @EventListener
    public void handleLoginFailureEvent(LoginFailureEvent event) {
        log.warn("Login failure: user={}, remoteAddress={}, reason={}", 
                event.getUsername(), event.getRemoteAddress(), event.getReason());
        
        SecurityAudit audit = new SecurityAudit();
        audit.setType("LOGIN_FAILURE");
        audit.setUsername(event.getUsername());
        audit.setRemoteAddress(event.getRemoteAddress());
        audit.setTimestamp(event.getTimestamp());
        audit.setDetails("Reason: " + event.getReason());
        
        securityAuditRepository.save(audit);
        
        // 연속 로그인 실패 확인 및 알림
        int failureCount = securityAuditRepository.countRecentLoginFailures(
                event.getUsername(), LocalDateTime.now().minusMinutes(10));
                
        if (failureCount >= 5) {
            alertService.sendAlert(
                    "Security Alert: Multiple Login Failures",
                    String.format("User %s has failed to login %d times in the last 10 minutes from %s", 
                            event.getUsername(), failureCount, event.getRemoteAddress())
            );
        }
    }
    
    @EventListener
    public void handleAccessDeniedEvent(AccessDeniedEvent event) {
        log.warn("Access denied: user={}, remoteAddress={}, resource={}", 
                event.getUsername(), event.getRemoteAddress(), event.getResource());
        
        SecurityAudit audit = new SecurityAudit();
        audit.setType("ACCESS_DENIED");
        audit.setUsername(event.getUsername());
        audit.setRemoteAddress(event.getRemoteAddress());
        audit.setTimestamp(event.getTimestamp());
        audit.setDetails("Resource: " + event.getResource());
        
        securityAuditRepository.save(audit);
    }
}
```

### 보안 테스트 전략
현재 상태: 보안 테스트 부족 또는 부재

변경 계획:
- 인증 및 권한 관련 단위 테스트 구현
- 보안 취약점 테스트 자동화
- 모의 해킹 시나리오 작성 및 테스트
- CI/CD 파이프라인에 보안 테스트 통합
- 정기적인 보안 감사 및 점검

구현 예시:
```java
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private UserService userService;
    
    @MockBean
    private JwtTokenProvider jwtTokenProvider;
    
    @Test
    @DisplayName("로그인 엔드포인트는 인증 없이 접근 가능해야 함")
    void shouldAllowUnauthenticatedAccessToLoginEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"user\",\"password\":\"password\"}"))
                .andExpect(status().isOk());
    }
    
    @Test
    @DisplayName("JWT 없이 보호된 리소스 접근 시 401 응답")
    void shouldReturn401WhenAccessingProtectedResourceWithoutJwt() throws Exception {
        mockMvc.perform(get("/api/v1/boards"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("유효하지 않은 JWT로 보호된 리소스 접근 시 401 응답")
    void shouldReturn401WhenAccessingProtectedResourceWithInvalidJwt() throws Exception {
        mockMvc.perform(get("/api/v1/boards")
                .header("Authorization", "Bearer invalid.jwt.token"))
                .andExpect(status().isUnauthorized());
    }
    
    @Test
    @DisplayName("USER 권한으로 ADMIN 리소스 접근 시 403 응답")
    @WithMockUser(roles = "USER")
    void shouldReturn403WhenAccessingAdminResourceWithUserRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard"))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @DisplayName("ADMIN 권한으로 ADMIN 리소스 접근 성공")
    @WithMockUser(roles = "ADMIN")
    void shouldAccessAdminResourceWithAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/dashboard"))
                .andExpect(status().isOk());
    }
}
```

### 마이그레이션 단계
1. Spring Security 설정 파일 업데이트 및 현대화
2. JWT 인증 시스템 구현 (토큰 유틸리티, 필터 등)
3. 인증 컨트롤러 및 서비스 구현
4. 메서드 보안 및 커스텀 권한 평가기 구현
5. 비밀번호 정책 및 관련 유틸리티 구현
6. 보안 헤더 및 CORS 설정 업데이트
7. 보안 감사 및 로깅 시스템 구현
8. 보안 테스트 케이스 작성 및 실행 