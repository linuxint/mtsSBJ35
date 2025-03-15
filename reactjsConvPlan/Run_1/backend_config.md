# 백엔드 설정 변경 계획

## 현재 상태
- 기본적인 Spring MVC 기반 설정 구성
- 제한적인 비동기 처리 및 캐싱 지원
- 간단한 보안 설정으로 세션 기반 인증 사용
- 기본적인 MyBatis 설정 구성
- Swagger 문서화 설정
- CORS 설정 미흡
- 캐시 및 성능 최적화 설정 부족
- 환경별 설정 분리 부족

## 변경 계획 개요
Spring Boot 3.5.0 기반 REST API 서비스에 적합한 설정으로 개선하고, 모듈화된 구성을 통해 유지보수성을 높이며, JWT 기반 인증, 캐싱, 비동기 처리 등 현대적인 애플리케이션 기능을 지원하는 설정을 구현합니다.

## 상세 변경 내용

### 애플리케이션 진입점 업데이트
```java
package com.devkbil.mtssbj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync // 비동기 처리 활성화
@EnableCaching // 캐싱 기능 활성화
@EnableScheduling // 스케줄링 기능 활성화
@EnableAspectJAutoProxy // AOP 활성화
@EnableJpaRepositories(basePackages = "com.devkbil.mtssbj.member.auth") // JPA 리포지토리 설정 (인증 모듈에만 적용)
public class MtsSbjApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(MtsSbjApplication.class, args);
    }
}
```

### JWT 기반 보안 구성 (SpringSecurityConfig)
```java
package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.common.JwtRequestFilter;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig {

    private final AuthService authService;
    private final JwtRequestFilter jwtRequestFilter;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .httpBasic().disable()
            .formLogin().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/boards/*/public").permitAll()
                .anyRequest().authenticated()
            .and()
            .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(), Collections.singletonMap("error", "인증이 필요합니다."));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    objectMapper.writeValue(response.getOutputStream(), Collections.singletonMap("error", "접근 권한이 없습니다."));
                })
            .and()
            .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://yourdomain.com"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
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

### MyBatis 설정 개선
```java
package com.devkbil.mtssbj.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@MapperScan(basePackages = "com.devkbil.mtssbj.**.mapper")
public class MyBatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource, ApplicationContext applicationContext) throws Exception {
        SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
        sqlSessionFactoryBean.setDataSource(dataSource);
        
        // XML 매퍼 위치 설정
        Resource[] postgresqlMappers = new PathMatchingResourcePatternResolver()
                .getResources("classpath:mapper/postgresql/**/*.xml");
        sqlSessionFactoryBean.setMapperLocations(postgresqlMappers);
        
        // MyBatis 설정 파일 위치
        Resource myBatisConfig = new PathMatchingResourcePatternResolver()
                .getResource("classpath:mybatis-config.xml");
        sqlSessionFactoryBean.setConfigLocation(myBatisConfig);
        
        // 타입 별칭 패키지 설정
        sqlSessionFactoryBean.setTypeAliasesPackage("com.devkbil.mtssbj");
        
        // 추가 구성 설정
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(true);
        configuration.setCallSettersOnNulls(true);
        configuration.setUseGeneratedKeys(true);
        configuration.setDefaultFetchSize(100);
        sqlSessionFactoryBean.setConfiguration(configuration);
        
        return sqlSessionFactoryBean.getObject();
    }
    
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
```

### Redis 및 캐시 구성
```java
package com.devkbil.mtssbj.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;

    @Value("${spring.redis.port}")
    private int redisPort;
    
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setHostName(redisHost);
        redisConfig.setPort(redisPort);
        return new LettuceConnectionFactory(redisConfig);
    }
    
    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
    
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 기본 캐시 설정
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        
        // 캐시별 맞춤 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("userCache", defaultConfig.entryTtl(Duration.ofHours(1)));
        cacheConfigurations.put("boardCache", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("fileCache", defaultConfig.entryTtl(Duration.ofMinutes(60)));
        
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
```

### OpenAPI 문서화 설정
```java
package com.devkbil.mtssbj.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth";
        
        return new OpenAPI()
                .info(new Info()
                        .title("MTS-SBJ API")
                        .description("Spring Boot REST API for MTS-SBJ Application")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DevKBil")
                                .email("contact@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .name(securitySchemeName)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
```

### 속도 제한(Rate Limiting) 구성
```java
package com.devkbil.mtssbj.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class RateLimitConfig {

    @Bean
    public Map<String, Bucket> buckets() {
        return new ConcurrentHashMap<>();
    }

    /**
     * 표준 API 요청에 대한 버킷 구성을 생성합니다.
     * - 분당 30개의 요청 허용
     * - 초당 5개의 요청으로 제한
     */
    @Bean
    public Bucket standardBucket() {
        // 분당 30개 요청 제한
        Bandwidth minuteLimit = Bandwidth.classic(30, Refill.greedy(30, Duration.ofMinutes(1)));
        // 초당 5개 요청 제한 (버스트 방지)
        Bandwidth secondLimit = Bandwidth.classic(5, Refill.greedy(5, Duration.ofSeconds(1)));
        
        return Bucket4j.builder()
                .addLimit(minuteLimit)
                .addLimit(secondLimit)
                .build();
    }
    
    /**
     * 관리자용 API 요청에 대한 버킷 구성을 생성합니다.
     * - 분당 100개의 요청 허용
     */
    @Bean
    public Bucket adminBucket() {
        Bandwidth limit = Bandwidth.classic(100, Refill.greedy(100, Duration.ofMinutes(1)));
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }
}
```

### 이메일 템플릿용 Thymeleaf 구성
```java
package com.devkbil.mtssbj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.core.io.ClassPathResource;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

import java.nio.charset.StandardCharsets;

@Configuration
public class ThymeleafConfig {

    @Bean
    @Description("Thymeleaf Template Resolver for Email Templates")
    public SpringResourceTemplateResolver emailTemplateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/email/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false); // 개발 시에는 캐시 비활성화
        return templateResolver;
    }

    @Bean
    @Description("Thymeleaf Template Engine for Email Templates")
    public SpringTemplateEngine emailTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(emailTemplateResolver());
        return templateEngine;
    }
}
```

### 환경별 구성 프로필
```yaml
# application.yml
spring:
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:local}
  
  # 공통 설정
  application:
    name: mts-sbj
  datasource:
    hikari:
      maximum-pool-size: 10
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  servlet:
    multipart:
      max-file-size: 10MB
      max-request-size: 50MB
  jpa:
    open-in-view: false
  jackson:
    date-format: yyyy-MM-dd HH:mm:ss
    serialization:
      write-dates-as-timestamps: false
      fail-on-empty-beans: false
  mvc:
    pathmatch:
      matching-strategy: ant_path_matcher

# JWT 설정
jwt:
  secret: ${JWT_SECRET:secretkey_should_be_at_least_32_bytes_long_for_hs256}
  access-token-validity: 1800  # 30분
  refresh-token-validity: 604800  # 7일

# 애플리케이션 특정 설정
app:
  upload:
    dir: ${APP_UPLOAD_DIR:./uploads}
  cors:
    allowed-origins: ${CORS_ALLOWED_ORIGINS:http://localhost:3000,http://localhost:8080}

# 서버 설정
server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: /api
  tomcat:
    max-threads: 200
    min-spare-threads: 20
    max-connections: 10000
    connection-timeout: 20000
    accept-count: 500

---
# 로컬 개발 환경 설정
spring:
  config:
    activate:
      on-profile: local
  datasource:
    url: jdbc:postgresql://localhost:5432/mtssbj
    username: postgres
    password: postgres
  redis:
    host: localhost
    port: 6379
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true

logging:
  level:
    org.springframework: INFO
    com.devkbil.mtssbj: DEBUG
    org.hibernate.SQL: DEBUG

---
# 개발 환경 설정
spring:
  config:
    activate:
      on-profile: dev
  datasource:
    url: jdbc:postgresql://${DB_HOST:dev-db}:${DB_PORT:5432}/${DB_NAME:mtssbj}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: ${REDIS_HOST:dev-redis}
    port: ${REDIS_PORT:6379}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false

logging:
  level:
    org.springframework: INFO
    com.devkbil.mtssbj: DEBUG

---
# 테스트 환경 설정
spring:
  config:
    activate:
      on-profile: test
  datasource:
    url: jdbc:postgresql://${DB_HOST:test-db}:${DB_PORT:5432}/${DB_NAME:mtssbj}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: ${REDIS_HOST:test-redis}
    port: ${REDIS_PORT:6379}
  jpa:
    hibernate:
      ddl-auto: none

logging:
  level:
    org.springframework: WARN
    com.devkbil.mtssbj: INFO

---
# 운영 환경 설정
spring:
  config:
    activate:
      on-profile: prod
  datasource:
    url: jdbc:postgresql://${DB_HOST}:${DB_PORT}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
  redis:
    host: ${REDIS_HOST}
    port: ${REDIS_PORT}
  jpa:
    hibernate:
      ddl-auto: none
    show-sql: false
  thymeleaf:
    cache: true

logging:
  level:
    org.springframework: WARN
    com.devkbil.mtssbj: INFO
  file:
    name: /var/log/mtssbj/application.log
```

### XSS 방지 구성
```java
package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.common.xss.XssFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final ObjectMapper objectMapper;

    public WebConfig(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        // ObjectMapper 구성
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(objectMapper);
        converters.add(converter);
    }

    @Bean
    public FilterRegistrationBean<XssFilter> xssFilterRegistration() {
        FilterRegistrationBean<XssFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new XssFilter());
        registration.addUrlPatterns("/*");
        registration.setName("xssFilter");
        registration.setOrder(1);
        return registration;
    }
}
```

## 변경 이점
- JWT 기반 인증으로 REST API 적합한 무상태 통신 지원
- CORS 설정 구현으로 React 프론트엔드와의 호환성 확보
- SpringDoc OpenAPI 도입으로 API 문서화 개선
- 캐싱 설정으로 성능 최적화
- 속도 제한 구현으로 서비스 안정성 보장
- MyBatis 구성 개선으로 DB 성능 향상
- 이메일 템플릿 지원으로 사용자 커뮤니케이션 강화
- 환경별 설정 프로필을 통한 배포 환경 관리 용이
- XSS 방지 필터 추가로 보안 강화 