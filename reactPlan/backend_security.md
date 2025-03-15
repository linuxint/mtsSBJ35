# 보안 관련 변경사항

## 공통 사항
1. 세션 기반 인증에서 JWT 기반 인증으로 전환
2. Spring Security 설정 변경
3. 권한 체계 개선
4. 비밀번호 암호화 강화
5. CORS 설정 추가

## Spring Security 설정
### 변경 전
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/login").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
            .and()
            .logout()
                .logoutSuccessUrl("/login")
            .and()
            .csrf();
    }
}
```

### 변경 후
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            
            .and()
            .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/api/**").permitAll()
                .anyRequest().authenticated()
            
            .and()
            .addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider),
                UsernamePasswordAuthenticationFilter.class
            );
        
        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
            .antMatchers(
                "/h2-console/**",
                "/favicon.ico",
                "/error",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**"
            );
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## JWT 토큰 관리
### 변경 전
```java
// 세션 기반 인증으로 별도의 토큰 관리 없음
```

### 변경 후
```java
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("${jwt.secret}")
    private String secretKey;
    
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidityInMilliseconds;
    
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidityInMilliseconds;
    
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }
    
    public String createAccessToken(String memberId, Set<String> roles) {
        Claims claims = Jwts.claims().setSubject(memberId);
        claims.put("roles", roles);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenValidityInMilliseconds);
        
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
    
    public String createRefreshToken(String memberId) {
        Claims claims = Jwts.claims().setSubject(memberId);
        
        Date now = new Date();
        Date validity = new Date(now.getTime() + refreshTokenValidityInMilliseconds);
        
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(SignatureAlgorithm.HS256, secretKey)
            .compact();
    }
    
    public Authentication getAuthentication(String token) {
        String memberId = getMemberId(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
    
    public String getMemberId(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

## JWT 인증 필터
### 변경 전
```java
// 세션 기반 인증으로 별도의 인증 필터 없음
```

### 변경 후
```java
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = resolveToken(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

## CORS 설정
### 변경 전
```java
// 별도의 CORS 설정 없음
```

### 변경 후
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## 권한 체계
### 변경 전
```java
// 단순 권한 체계 사용
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
```

### 변경 후
```java
@Entity
@Table(name = "tb_role")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @Column(length = 20)
    private String id;
    
    @Column(length = 100, nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Builder
    public Role(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

@Entity
@Table(name = "tb_permission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Permission {
    @Id
    @Column(length = 50)
    private String id;
    
    @Column(length = 100, nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
    
    @Builder
    public Permission(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}

@Entity
@Table(name = "tb_role_permission")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id", nullable = false)
    private Permission permission;
    
    @Builder
    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
    }
}
```

## 인증
### LoginInterceptor
- JWT 기반 인증으로 전환
- 토큰 검증 로직 추가
- 세션 관리 제거

### AdminInterceptor
- 권한 체크 강화
- 역할 기반 접근 제어
- 감사 로깅 추가

### CommonInterceptor
- CORS 설정 통합
- XSS 방지
- CSRF 보호

## 권한 관리
### RoleBasedMapping
- 세분화된 권한 체계
- 동적 권한 할당
- 캐시 적용

### RoleMappingLoader
- 설정 기반 권한 로딩
- 실시간 권한 갱신
- 감사 추적

## 보안 필터
### JwtRequestFilter
- 토큰 생명주기 관리
- 리프레시 토큰 처리
- 블랙리스트 관리

### DeviceDetectorInterceptor
- 디바이스 인증
- 접근 제어
- 보안 로깅

## 모니터링
### MonitorSecurityConfig
- 보안 메트릭 수집
- 접근 로그 분석
- 알림 설정 

## 권한 어노테이션 설정
### 변경 전
레거시 시스템에서는 Custom Interceptor로 권한을 체크했습니다.

### 변경 후

#### AdminAuthorize 어노테이션
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_ADMIN')")
public @interface AdminAuthorize {
}
```

#### AdminAuthorizeWithUser 어노테이션
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_ADMIN') and authentication.principal.username == #username")
public @interface AdminAuthorizeWithUser {
}
```

#### UserAuthorize 어노테이션
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ROLE_USER')")
public @interface UserAuthorize {
}
```

#### GuestAuthorize 어노테이션
```java
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("permitAll()")
public @interface GuestAuthorize {
}
```

## 세션 만료 처리
### 변경 전
레거시 시스템에서는 세션 만료 시 로그인 페이지로 리다이렉트되었습니다.

### 변경 후
```java
@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException {
        HttpServletResponse response = event.getResponse();
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.UNAUTHORIZED.value());
        body.put("error", "Unauthorized");
        body.put("message", "Session expired");
        body.put("timestamp", System.currentTimeMillis());
        
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
```

## 인증 성공 핸들러
### 변경 전
레거시 시스템에서는 로그인 성공 시 메인 페이지로 리다이렉트되었습니다.

### 변경 후
```java
@Component
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Autowired
    public MyAuthenticationSuccessHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String token = jwtTokenProvider.generateToken(userDetails);
        
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", HttpStatus.OK.value());
        body.put("message", "로그인 성공");
        body.put("token", token);
        
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
```

## 역할 상수 정의
### 변경 전
레거시 시스템에서는 권한 정보가 데이터베이스에만 저장되었습니다.

### 변경 후
```java
public class Role {
    public static final String USER = "ROLE_USER";
    public static final String ADMIN = "ROLE_ADMIN";
    public static final String MANAGER = "ROLE_MANAGER";
    public static final String GUEST = "ROLE_GUEST";
    
    private Role() {
        // 유틸리티 클래스 생성자 방지
    }
}
```

## 역할 기반 매핑
### 변경 전
레거시 시스템에서는 사용자 역할과 URL 매핑이 코드에 하드코딩되어 있었습니다.

### 변경 후
```java
@Component
public class RoleBasedMapping {
    
    private Map<String, List<String>> roleUrlMap = new HashMap<>();
    
    public void addMapping(String role, String url) {
        List<String> urls = roleUrlMap.getOrDefault(role, new ArrayList<>());
        urls.add(url);
        roleUrlMap.put(role, urls);
    }
    
    public List<String> getUrlsByRole(String role) {
        return roleUrlMap.getOrDefault(role, Collections.emptyList());
    }
    
    public boolean isAllowed(String role, String url) {
        List<String> allowedUrls = getUrlsByRole(role);
        return allowedUrls.stream()
                .anyMatch(pattern -> antPathMatcher.match(pattern, url));
    }
    
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
}
```

## 역할 매핑 로더
### 변경 전
레거시 시스템에서는 역할과 URL 매핑이 정적으로 관리되었습니다.

### 변경 후
```java
@Component
public class RoleMappingLoader {
    
    private final RoleBasedMapping roleBasedMapping;
    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    @Autowired
    public RoleMappingLoader(RoleBasedMapping roleBasedMapping, ResourceLoader resourceLoader) {
        this.roleBasedMapping = roleBasedMapping;
        this.resourceLoader = resourceLoader;
        this.objectMapper = new ObjectMapper();
    }
    
    @PostConstruct
    public void init() throws IOException {
        loadMappings();
    }
    
    private void loadMappings() throws IOException {
        Resource resource = resourceLoader.getResource("classpath:role-mappings.json");
        RoleMappingsJson mappings = objectMapper.readValue(resource.getInputStream(), RoleMappingsJson.class);
        
        for (Map.Entry<String, List<String>> entry : mappings.getRoleMappings().entrySet()) {
            String role = entry.getKey();
            List<String> urls = entry.getValue();
            
            for (String url : urls) {
                roleBasedMapping.addMapping(role, url);
            }
        }
    }
} 