# 백엔드 공통 모듈 변경 계획

## 현재 상태
- XSS 방지를 위한 기본적인 HTML 이스케이프 처리가 부족
- 파일 처리 유틸리티에 보안 취약점 존재 가능성
- 문자열 유틸리티의 검증 로직 미흡
- 트리 구조 기능이 Java로만 구현되어 있음
- JSON 처리 시 오류 핸들링 부족
- 세션 기반 인증 사용으로 REST API에 적합하지 않음
- 민감 정보 마스킹 처리가 불완전

## 변경 계획 개요
공통 모듈을 현대화하고 보안을 강화하여 REST API 기반 아키텍처에 적합하도록 개선합니다. 특히 보안, 파일 처리, 데이터 변환 유틸리티를 중점적으로 개선하고, 프론트엔드(React)와의 호환성을 고려한 모듈로 재구성합니다.

## 상세 변경 내용

### XSS 방지를 위한 HTML 이스케이프 유틸리티 구현
```java
package com.devkbil.common.util;

import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class HtmlSanitizer {
    
    /**
     * HTML 태그를 이스케이프 처리하여 XSS 공격을 방지합니다.
     * 
     * @param input 이스케이프할 텍스트
     * @return 이스케이프된 텍스트
     */
    public String sanitize(String input) {
        if (input == null) {
            return null;
        }
        return StringEscapeUtils.escapeHtml4(input);
    }
    
    /**
     * JSON 데이터를 이스케이프 처리합니다.
     * 
     * @param input 이스케이프할 JSON 텍스트
     * @return 이스케이프된 JSON 텍스트
     */
    public String sanitizeJson(String input) {
        if (input == null) {
            return null;
        }
        return StringEscapeUtils.escapeJson(input);
    }
}
```

### 보안 강화된 파일 처리 유틸리티 구현
```java
package com.devkbil.common.util;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class SecureFileHandler {

    private final String uploadDir;
    
    public SecureFileHandler(@Value("${app.upload.dir}") String uploadDir) {
        this.uploadDir = uploadDir;
        createUploadDirectoryIfNotExists();
    }
    
    private void createUploadDirectoryIfNotExists() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 디렉토리를 생성할 수 없습니다.", e);
        }
    }
    
    /**
     * 파일을 안전하게 저장하고 고유 파일명을 반환합니다.
     */
    public String storeFile(MultipartFile file) throws IOException {
        // 원본 파일명 검증
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new IOException("파일명이 유효하지 않습니다.");
        }
        
        // 고유한 파일명 생성
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 파일 저장
        Path targetLocation = Paths.get(uploadDir).resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFilename;
    }
    
    /**
     * 파일 확장자 추출
     */
    private String getFileExtension(String filename) {
        if (filename.lastIndexOf(".") != -1) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
    
    /**
     * 파일 삭제
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            return false;
        }
    }
    
    /**
     * 파일 존재 여부 확인
     */
    public boolean fileExists(String filename) {
        Path filePath = Paths.get(uploadDir).resolve(filename);
        return Files.exists(filePath);
    }
}
```

### 강화된 문자열 유틸리티 구현
```java
package com.devkbil.common.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class StringUtils {

    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$");
    
    /**
     * 이메일 형식 검증
     */
    public boolean isValidEmail(String email) {
        if (email == null) return false;
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * 전화번호 형식 검증
     */
    public boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null) return false;
        return PHONE_PATTERN.matcher(phoneNumber).matches();
    }
    
    /**
     * 문자열이 비어있는지 확인
     */
    public boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 문자열 길이가 최소/최대 범위 내인지 검증
     */
    public boolean isValidLength(String str, int minLength, int maxLength) {
        if (str == null) return false;
        int length = str.length();
        return length >= minLength && length <= maxLength;
    }
    
    /**
     * 문자열에 특수문자가 포함되어 있는지 확인
     */
    public boolean containsSpecialCharacters(String str) {
        if (str == null) return false;
        return Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(str).find();
    }
}
```

### JWT 토큰 유틸리티 구현
```java
package com.devkbil.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.access-token-validity}")
    private long accessTokenValidity;
    
    @Value("${jwt.refresh-token-validity}")
    private long refreshTokenValidity;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    /**
     * 토큰에서 사용자명 추출
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    /**
     * 토큰에서 만료일 추출
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    /**
     * 토큰에서 클레임 추출
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 토큰에서, 비밀키를 사용하여 모든 클레임 추출
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 토큰 만료 여부 확인
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    /**
     * 액세스 토큰 생성
     */
    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "access");
        return generateToken(claims, username, accessTokenValidity);
    }
    
    /**
     * 리프레시 토큰 생성
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        return generateToken(claims, username, refreshTokenValidity);
    }
    
    /**
     * JWT 토큰 생성
     */
    private String generateToken(Map<String, Object> claims, String subject, long validityPeriod) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + validityPeriod * 1000))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }
    
    /**
     * 토큰 검증
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = getUsernameFromToken(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
}
```

### 개인정보 마스킹 유틸리티 강화
```java
package com.devkbil.common.util;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MaskingUtil {

    /**
     * 이메일 마스킹 처리 - 이메일 아이디 부분을 일부 마스킹 처리
     * ex) te***@example.com
     */
    public String maskEmail(String email) {
        if (email == null || email.isEmpty()) {
            return email;
        }
        
        Pattern pattern = Pattern.compile("^(\\w{2})\\w*(@.*)$");
        Matcher matcher = pattern.matcher(email);
        
        if (matcher.matches()) {
            return matcher.group(1) + "***" + matcher.group(2);
        }
        return email;
    }
    
    /**
     * 전화번호 마스킹 처리 - 가운데 번호 마스킹
     * ex) 010-****-5678
     */
    public String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return phoneNumber;
        }
        
        Pattern pattern = Pattern.compile("^(\\d{2,3}-)(\\d{3,4})(-\\d{4})$");
        Matcher matcher = pattern.matcher(phoneNumber);
        
        if (matcher.matches()) {
            return matcher.group(1) + "****" + matcher.group(3);
        }
        return phoneNumber;
    }
    
    /**
     * 이름 마스킹 처리 - 성을 제외한 이름 마스킹
     * ex) 김**, 홍**
     */
    public String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        
        if (name.length() <= 1) {
            return name;
        }
        
        StringBuilder masked = new StringBuilder();
        masked.append(name.charAt(0));
        for (int i = 1; i < name.length(); i++) {
            masked.append('*');
        }
        
        return masked.toString();
    }
    
    /**
     * 주민등록번호 마스킹 처리
     * ex) 123456-*******
     */
    public String maskRRN(String rrn) {
        if (rrn == null || rrn.isEmpty()) {
            return rrn;
        }
        
        Pattern pattern = Pattern.compile("^(\\d{6})(-?)(\\d{7})$");
        Matcher matcher = pattern.matcher(rrn);
        
        if (matcher.matches()) {
            return matcher.group(1) + matcher.group(2) + "*******";
        }
        return rrn;
    }
    
    /**
     * 카드번호 마스킹 처리
     * ex) 1234-56**-****-7890
     */
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isEmpty()) {
            return cardNumber;
        }
        
        Pattern pattern = Pattern.compile("^(\\d{4}(-?))(\\d{2})\\d{2}\\1(\\d{4})\\1(\\d{4})$");
        Matcher matcher = pattern.matcher(cardNumber);
        
        if (matcher.matches()) {
            return matcher.group(1) + matcher.group(3) + "**-****-" + matcher.group(5);
        }
        return cardNumber;
    }
    
    /**
     * 계좌번호 마스킹 처리
     * ex) 123-45-****67
     */
    public String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.isEmpty()) {
            return accountNumber;
        }
        
        // 계좌번호 형식이 다양하므로 일반적인 형태 가정
        int length = accountNumber.length();
        if (length < 8) {
            return accountNumber;
        }
        
        int maskStart = length / 2 - 2;
        int maskEnd = length / 2 + 2;
        
        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i >= maskStart && i < maskEnd) {
                masked.append('*');
            } else {
                masked.append(accountNumber.charAt(i));
            }
        }
        
        return masked.toString();
    }
}
```

### JSON 처리 개선 유틸리티 구현
```java
package com.devkbil.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JsonUtil {

    private final ObjectMapper objectMapper;
    
    public JsonUtil() {
        this.objectMapper = new ObjectMapper();
        // Java 8 날짜/시간 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());
        // 날짜를 타임스탬프로 출력하지 않고 ISO-8601 형식으로 출력
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 알 수 없는 속성에 대해 실패하지 않도록 설정
        objectMapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    /**
     * 객체를 JSON 문자열로 변환
     */
    public String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * 객체를 예쁘게 포맷팅된 JSON 문자열로 변환
     */
    public String toPrettyJson(Object object) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * JSON 문자열을 객체로 변환
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("JSON 역직렬화 중 오류가 발생했습니다.", e);
        }
    }
    
    /**
     * ObjectMapper 인스턴스 직접 제공
     */
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
```

## 변경 이점
- 강화된 보안 및 XSS 방지
- JWT 기반 인증으로 REST API에 적합한 구현
- 개인정보 보호 강화를 위한 마스킹 처리 개선
- 파일 업로드/다운로드 보안 취약점 제거
- 트리 구조 기능의 TypeScript 변환으로 프론트엔드 재사용 가능
- JSON 처리의 일관성 및 오류 처리 개선
- 유틸리티 클래스의 명확한 역할 분리와 단일 책임 원칙 준수 