# 설정 변경사항

## Application
### 변경 전
```java
@SpringBootApplication
public class MtssbjApplication {
    public static void main(String[] args) {
        SpringApplication.run(MtssbjApplication.class, args);
    }
}
```

### 변경 후
```java
@SpringBootApplication
@EnableAsync
@EnableCaching
@MapperScan("com.devkbil.mtssbj.**.mapper")
public class MtssbjApplication {
    public static void main(String[] args) {
        SpringApplication.run(MtssbjApplication.class, args);
    }
    
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
```

## Web MVC
### 변경 전
```java
@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/login", "/logout", "/error");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
```

### 변경 후
```java
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final JwtAuthenticationInterceptor jwtAuthenticationInterceptor;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthenticationInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/**", "/api/health");
    }
    
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.defaultContentType(MediaType.APPLICATION_JSON);
    }
    
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(10485760); // 10MB
        return resolver;
    }
}
```

## Security
### 변경 전
```java
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
            .and()
            .logout()
                .logoutSuccessUrl("/login");
    }
}
```

### 변경 후
```java
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()
            .and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/**").authenticated()
            .and()
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                           UsernamePasswordAuthenticationFilter.class);
    }
    
    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
           .antMatchers("/h2-console/**", "/favicon.ico")
           .antMatchers(HttpMethod.OPTIONS, "/**");
    }
    
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

## MyBatis Config
### 변경 전
```java
@Configuration
@MapperScan("com.devkbil.mtssbj.**.mapper")
public class MyBatisConfig {
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        return sessionFactory.getObject();
    }
}
```

### 변경 후
```java
@Configuration
@MapperScan("com.devkbil.mtssbj.**.mapper")
@RequiredArgsConstructor
public class MyBatisConfig {
    private final ApplicationContext applicationContext;
    
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setMapperLocations(applicationContext.getResources("classpath:mapper/**/*.xml"));
        
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setCallSettersOnNulls(true);
        configuration.setCacheEnabled(true);
        sessionFactory.setConfiguration(configuration);
        
        return sessionFactory.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
}
```

## JPA
### 변경 전
```java
@Configuration
@EnableJpaRepositories("com.devkbil.mtssbj.**.repository")
public class JPAConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.devkbil.mtssbj");
        
        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        return em;
    }
}
```

### 변경 후
```java
@Configuration
@EnableJpaRepositories("com.devkbil.mtssbj.**.repository")
@EnableJpaAuditing
public class JPAConfig {
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            DataSource dataSource,
            JpaProperties jpaProperties) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.devkbil.mtssbj");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setGenerateDdl(true);
        em.setJpaVendorAdapter(vendorAdapter);
        
        Properties properties = new Properties();
        properties.putAll(jpaProperties.getProperties());
        properties.put("hibernate.physical_naming_strategy",
                      "org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy");
        properties.put("hibernate.implicit_naming_strategy",
                      "org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        em.setJpaProperties(properties);
        
        return em;
    }
    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                           .map(SecurityContext::getAuthentication)
                           .filter(Authentication::isAuthenticated)
                           .map(Authentication::getName);
    }
}
```

## CORS
### 변경 전
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
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
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

## SpringDoc
### 변경 전
```java
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.devkbil.mtssbj"))
                .paths(PathSelectors.any())
                .build();
    }
}
```

### 변경 후
```java
@Configuration
public class SpringDocConfig {
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("MTSSBJ API")
                .version("v1.0")
                .description("MTSSBJ API Documentation");
        
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");
        
        return new OpenAPI()
                .info(info)
                .components(new Components().addSecuritySchemes("bearerAuth", securityScheme))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
    
    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("public")
                .pathsToMatch("/api/**")
                .build();
    }
    
    @Bean
    public GroupedOpenApi adminApi() {
        return GroupedOpenApi.builder()
                .group("admin")
                .pathsToMatch("/api/admin/**")
                .build();
    }
}
```

## Rate Limiting Config
### 변경 전
```java
// 별도의 속도 제한 설정 없음
```

### 변경 후
```java
@Configuration
@EnableAspectJAutoProxy
public class RateLimitConfig {
    
    @Bean
    public RateLimitingAspect rateLimitingAspect() {
        return new RateLimitingAspect();
    }
    
    @Bean
    public RateLimitingService rateLimitingService() {
        return new RateLimitingService();
    }
}

@Aspect
@Component
@RequiredArgsConstructor
public class RateLimitingAspect {
    private final RateLimitingService rateLimitingService;
    
    @Around("@annotation(rateLimit)")
    public Object limitRate(ProceedingJoinPoint joinPoint, RateLimit rateLimit) throws Throwable {
        String key = generateKey(joinPoint);
        if (!rateLimitingService.tryAcquire(key, rateLimit.value(), rateLimit.timeUnit())) {
            throw new TooManyRequestsException("API 호출 횟수가 초과되었습니다.");
        }
        return joinPoint.proceed();
    }
    
    private String generateKey(ProceedingJoinPoint joinPoint) {
        // 키 생성 로직
        return joinPoint.getSignature().toLongString();
    }
}

@Service
@RequiredArgsConstructor
public class RateLimitingService {
    private final LoadingCache<String, RateLimiter> rateLimiters = CacheBuilder.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .build(new CacheLoader<String, RateLimiter>() {
            @Override
            public RateLimiter load(String key) {
                return RateLimiter.create(1.0);
            }
        });
    
    public boolean tryAcquire(String key, int permits, TimeUnit timeUnit) {
        try {
            RateLimiter limiter = rateLimiters.get(key);
            return limiter.tryAcquire(permits, 1, timeUnit);
        } catch (ExecutionException e) {
            return false;
        }
    }
}

## Application Event Listeners
### 변경 전
```java
// 개별 이벤트 리스너 클래스들
```

### 변경 후
```java
@Component
@Slf4j
public class ApplicationEventListeners {
    
    @EventListener
    public void handleContextRefreshed(ContextRefreshedEvent event) {
        log.info("애플리케이션 컨텍스트가 초기화되었습니다.");
    }
    
    @EventListener
    public void handleContextStarted(ContextStartedEvent event) {
        log.info("애플리케이션 컨텍스트가 시작되었습니다.");
    }
    
    @EventListener
    public void handleContextStopped(ContextStoppedEvent event) {
        log.info("애플리케이션 컨텍스트가 중지되었습니다.");
    }
    
    @EventListener
    public void handleContextClosed(ContextClosedEvent event) {
        log.info("애플리케이션 컨텍스트가 종료되었습니다.");
    }
    
    @EventListener
    public void handleApplicationFailed(ApplicationFailedEvent event) {
        log.error("애플리케이션 시작 실패: {}", event.getException().getMessage());
    }
    
    @EventListener
    public void handleApplicationReady(ApplicationReadyEvent event) {
        log.info("애플리케이션이 요청을 처리할 준비가 되었습니다.");
    }
}

## Redis 설정
### 변경 전
레거시 시스템에서는 로컬 세션이나 EhCache를 사용했습니다.

### 변경 후
```java
@Configuration
@EnableCaching
public class RedisConfig {
    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(redisHost, redisPort);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return redisTemplate;
    }

    @Bean
    public RedisCacheManager cacheManager() {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(configuration)
                .build();
    }
}

## Cache 설정
### 변경 전
레거시 시스템에서는 개별 서비스에서 캐시 처리를 수동으로 관리했습니다.

### 변경 후
```java
@Configuration
@EnableCaching
public class CacheConfig {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        List<Cache> caches = new ArrayList<>();
        
        caches.add(new ConcurrentMapCache("codeCache", false));
        caches.add(new ConcurrentMapCache("menuCache", false));
        caches.add(new ConcurrentMapCache("userCache", false));
        
        cacheManager.setCaches(caches);
        return cacheManager;
    }
}

## 캐시 예열 서비스
### 변경 전
레거시 시스템에서는 캐시 예열 기능이 없었습니다.

### 변경 후
```java
@Service
public class CacheWarmupService {
    
    private final CodeCacheService codeCacheService;
    private final MenuService menuService;
    
    @Autowired
    public CacheWarmupService(CodeCacheService codeCacheService, MenuService menuService) {
        this.codeCacheService = codeCacheService;
        this.menuService = menuService;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void warmupCaches() {
        // 코드 캐시 예열
        codeCacheService.loadAllCodes();
        
        // 메뉴 캐시 예열
        menuService.loadAllMenus();
    }
}

## Elasticsearch 설정
### 변경 전
레거시 시스템에서는 검색 기능이 DB 쿼리로 구현되었습니다.

### 변경 후
```java
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.devkbil.mtssbj.search.repository")
public class EsConfig extends AbstractElasticsearchConfiguration {
    
    @Value("${elasticsearch.host}")
    private String host;
    
    @Value("${elasticsearch.port}")
    private int port;
    
    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(host + ":" + port)
                .build();
        return RestClients.create(clientConfiguration).rest();
    }
    
    @Bean
    public ElasticsearchOperations elasticsearchOperations() {
        return new ElasticsearchRestTemplate(elasticsearchClient());
    }
}

## Thymeleaf 설정
### 변경 전
레거시 시스템에서는 JSP를 사용했습니다.

### 변경 후
```java
@Configuration
public class ThymeleafViewResolverConfig {
    
    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCacheable(false);
        return resolver;
    }
    
    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());
        engine.setEnableSpringELCompiler(true);
        return engine;
    }
    
    @Bean
    public ThymeleafViewResolver viewResolver() {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine());
        resolver.setCharacterEncoding("UTF-8");
        resolver.setContentType("text/html; charset=UTF-8");
        return resolver;
    }
}

## XSS 방지 설정
### 변경 전
레거시 시스템에서는 XSS 방지가 일관적으로 구현되지 않았습니다.

### 변경 후
```java
@Configuration
public class XssConfig {
    
    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistrationBean() {
        FilterRegistrationBean<XssFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new XssFilter());
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
    
    @Bean
    public MappingJackson2HttpMessageConverter jacksonMessageConverter() {
        MappingJackson2HttpMessageConverter messageConverter = new MappingJackson2HttpMessageConverter();
        ObjectMapper mapper = new ObjectMapper();
        
        // HTML 문자 이스케이프 처리
        mapper.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        
        messageConverter.setObjectMapper(mapper);
        return messageConverter;
    }
}

## 환경별 설정
### 변경 전
레거시 시스템에서는 환경별 설정이 properties 파일로 관리되었습니다.

### 변경 후
```java
@Configuration
public class EnvConfiguration {
    
    @Autowired
    private Environment env;
    
    @Bean
    @Profile("local")
    public SpecificConfiguration localConfiguration() {
        return new SpecificConfigurationLocal();
    }
    
    @Bean
    @Profile("dev")
    public SpecificConfiguration devConfiguration() {
        return new SpecificConfigurationDev();
    }
    
    @Bean
    @Profile("stag")
    public SpecificConfiguration stagConfiguration() {
        return new SpecificConfigurationStag();
    }
    
    @Bean
    @Profile("prod")
    public SpecificConfiguration prodConfiguration() {
        return new SpecificConfigurationProd();
    }
}

## Git 연동 설정
### 변경 전
레거시 시스템에서는 Git 연동 기능이 없었습니다.

### 변경 후
```java
@Configuration
public class GitConfig {
    
    @Value("${git.repository.path}")
    private String repositoryPath;
    
    @Value("${git.username}")
    private String username;
    
    @Value("${git.password}")
    private String password;
    
    @Bean
    public Git gitClient() throws IOException, GitAPIException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File(repositoryPath + "/.git"))
                .readEnvironment()
                .findGitDir()
                .build();
                
        CredentialsProvider credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
        
        Git git = new Git(repository);
        git.pull().setCredentialsProvider(credentialsProvider).call();
        
        return git;
    }
}

## 애플리케이션 생명주기 이벤트 리스너
### 변경 전
레거시 시스템에서는 애플리케이션 생명주기 이벤트 리스너가 구현되지 않았습니다.

### 변경 후

#### 애플리케이션 시작 이벤트 리스너
```java
@Component
public class ApplicationStartingEventListener implements 
        ApplicationListener<ApplicationStartingEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationStartingEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationStartingEvent event) {
        log.info("애플리케이션 시작 이벤트 발생");
    }
}
```

#### 애플리케이션 환경 준비 이벤트 리스너
```java
@Component
public class ApplicationEnvironmentPreparedEventListener implements 
        ApplicationListener<ApplicationEnvironmentPreparedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationEnvironmentPreparedEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        log.info("애플리케이션 환경 준비 이벤트 발생");
        Environment env = event.getEnvironment();
        log.info("활성 프로필: {}", Arrays.toString(env.getActiveProfiles()));
    }
}
```

#### 애플리케이션 컨텍스트 초기화 이벤트 리스너
```java
@Component
public class ApplicationContextInitializedEventListener implements 
        ApplicationListener<ApplicationContextInitializedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextInitializedEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        log.info("애플리케이션 컨텍스트 초기화 이벤트 발생");
    }
}
```

#### 애플리케이션 준비 이벤트 리스너
```java
@Component
public class ApplicationPreparedEventListener implements 
        ApplicationListener<ApplicationPreparedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationPreparedEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("애플리케이션 준비 이벤트 발생");
    }
}
```

#### 애플리케이션 준비 완료 이벤트 리스너
```java
@Component
public class ApplicationReadyEventListener implements 
        ApplicationListener<ApplicationReadyEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationReadyEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("애플리케이션 준비 완료 이벤트 발생");
        // 애플리케이션 시작 후 초기화 작업 수행
    }
}
```

#### 애플리케이션 컨텍스트 새로고침 이벤트 리스너
```java
@Component
public class ApplicationContextRefreshedEventListener implements 
        ApplicationListener<ContextRefreshedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextRefreshedEventListener.class);
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("애플리케이션 컨텍스트 새로고침 이벤트 발생");
    }
}
```

#### 애플리케이션 컨텍스트 종료 이벤트 리스너
```java
@Component
public class ApplicationContextClosedEventListener implements 
        ApplicationListener<ContextClosedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationContextClosedEventListener.class);
    
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("애플리케이션 컨텍스트 종료 이벤트 발생");
        // 애플리케이션 종료 시 정리 작업 수행
    }
}
```

#### 애플리케이션 실패 이벤트 리스너
```java
@Component
public class ApplicationFailedEventListener implements 
        ApplicationListener<ApplicationFailedEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(ApplicationFailedEventListener.class);
    
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        log.error("애플리케이션 시작 실패", event.getException());
        // 애플리케이션 시작 실패 시 알림 전송 등의 작업 수행
    }
}
```

## 트랜잭션 이벤트 리스너
### 변경 전
레거시 시스템에서는 트랜잭션 이벤트를 처리하는 리스너가 없었습니다.

### 변경 후
```java
@Component
public class TransactionEventListener {
    
    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);
    
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleCustomEvent(CustomApplicationEvent event) {
        log.info("트랜잭션 커밋 후 이벤트 처리: {}", event.getMessage());
        // 트랜잭션 커밋 후 비동기 작업 수행
    }
    
    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleTransactionRollback(CustomApplicationEvent event) {
        log.warn("트랜잭션 롤백 후 이벤트 처리: {}", event.getMessage());
        // 롤백 시 알림 또는 로깅 처리
    }
}
```

## 커스텀 애플리케이션 이벤트
### 변경 전
레거시 시스템에서는 커스텀 이벤트 발행 체계가 없었습니다.

### 변경 후
```java
@Component
public class CustomApplicationEventListener implements ApplicationListener<CustomApplicationEvent> {
    
    private static final Logger log = LoggerFactory.getLogger(CustomApplicationEventListener.class);
    
    @Override
    public void onApplicationEvent(CustomApplicationEvent event) {
        log.info("커스텀 이벤트 수신: {}", event.getMessage());
        // 커스텀 이벤트 처리 로직
    }
} 