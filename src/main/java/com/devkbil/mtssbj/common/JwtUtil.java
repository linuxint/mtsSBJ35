package com.devkbil.mtssbj.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

/**
 * JSON Web Token (JWT)을 처리하기 위한 유틸리티 클래스.
 * <p>
 * JWT의 생성, 검증 및 토큰 내 정보 추출과 같은 작업을 지원하며,
 * 정의된 비밀 키를 이용하여 안전한 토큰 작업을 수행합니다.
 */
@Component
public class JwtUtil {

    /**
     * 비밀 키 (SecretKey)
     * JWT의 서명 및 검증 작업에 사용됩니다.
     * Base64 형식의 문자열에서 생성된 HMAC-SHA 알고리즘용 키입니다.
     */
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
        Base64.getEncoder()
            .encodeToString("N8smKe2pXyZCd3Rsv7nNni0gfZsl7J7MfinPxaO2Bgk=".getBytes(StandardCharsets.UTF_8))
            .getBytes(StandardCharsets.UTF_8));

    // 액세스 토큰 만료 시간 (기본값: 1시간)
    @Value("${jwt.access-token.expiration:3600000}")
    private long accessTokenExpiration;

    // 리프레시 토큰 만료 시간 (기본값: 7일)
    @Value("${jwt.refresh-token.expiration:604800000}")
    private long refreshTokenExpiration;

    /**
     * 토큰에서 사용자 이름을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 사용자 이름 (서브젝트)
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 토큰에서 만료 시간을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 만료 시간
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 토큰에서 특정 클레임 값을 추출합니다.
     *
     * @param token          JWT 토큰
     * @param claimsResolver 클레임 추출을 위한 함수
     * @param <T>            클레임 데이터 타입
     * @return 추출된 클레임 데이터
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 토큰에서 모든 클레임을 추출합니다.
     *
     * @param token JWT 토큰
     * @return 모든 클레임
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    /**
     * 토큰의 만료 여부를 확인합니다.
     *
     * @param token JWT 토큰
     * @return 만료 여부 (true: 만료됨)
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).toInstant().isBefore(java.time.Instant.now());
    }

    /**
     * 사용자 정보를 기반으로 액세스 토큰을 생성합니다.
     *
     * @param userDetails 사용자 정보
     * @return 생성된 액세스 토큰
     */
    public String generateAccessToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()); // 역할 정보 추가
        claims.put("type", "access"); // 토큰 타입 설정
        return createToken(claims, userDetails.getUsername(), accessTokenExpiration);
    }

    /**
     * 사용자 정보를 기반으로 리프레시 토큰을 생성합니다.
     *
     * @param userDetails 사용자 정보
     * @return 생성된 리프레시 토큰
     */
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh"); // 토큰 타입 설정
        claims.put("tokenId", UUID.randomUUID().toString()); // 고유 ID 추가
        return createToken(claims, userDetails.getUsername(), refreshTokenExpiration);
    }

    /**
     * 기존 호환성을 위한 메서드 (액세스 토큰 생성)
     *
     * @param userDetails 사용자 정보
     * @return 생성된 JWT 토큰
     */
    public String generateToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    /**
     * 클레임과 서브젝트를 사용하여 JWT 토큰을 생성합니다.
     *
     * @param claims     클레임 정보
     * @param subject    서브젝트 (사용자 이름)
     * @param expiration 만료 시간 (밀리초)
     * @return 생성된 JWT 토큰
     */
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        return Jwts.builder()
            .setClaims(claims) // 클레임 정보 설정
            .setSubject(subject) // 서브젝트 설정
            .setIssuedAt(new java.util.Date(System.currentTimeMillis())) // JWT 라이브러리 요구로 Date 유지
            .setExpiration(new java.util.Date(System.currentTimeMillis() + expiration)) // JWT 라이브러리 요구로 Date 유지
            .signWith(secretKey) // 비밀 키로 서명
            .compact(); // 직렬화된 토큰 반환
    }

    /**
     * 주어진 토큰이 사용자의 정보와 유효한지 확인합니다.
     *
     * @param token       JWT 토큰
     * @param userDetails 사용자 정보
     * @return 유효 여부 (true: 유효함)
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 리프레시 토큰이 유효한지 확인합니다.
     *
     * @param token 리프레시 토큰
     * @return 유효 여부 (true: 유효함)
     */
    public Boolean validateRefreshToken(String token) {
        try {
            // 토큰이 만료되지 않았는지 확인
            if (isTokenExpired(token)) {
                return false;
            }

            // 토큰 타입이 리프레시인지 확인
            final Claims claims = extractAllClaims(token);
            return "refresh".equals(claims.get("type"));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰을 생성합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @param userDetails  사용자 정보
     * @return 새로 생성된 액세스 토큰
     */
    public String generateNewAccessToken(String refreshToken, UserDetails userDetails) {
        if (!validateRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        return generateAccessToken(userDetails);
    }
}
