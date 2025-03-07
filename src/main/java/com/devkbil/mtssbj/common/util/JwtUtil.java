package com.devkbil.mtssbj.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
            .encodeToString("N8smKe2pXyZCd3Rsv7nNni0gfZsl7J7MfinPxaO2Bgk=".getBytes())
            .getBytes());

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
        return extractExpiration(token).before(new Date());
    }

    /**
     * 사용자 정보를 기반으로 JWT 토큰을 생성합니다.
     *
     * @param userDetails 사용자 정보
     * @return 생성된 JWT 토큰
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()); // 역할 정보 추가
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * 클레임과 서브젝트를 사용하여 JWT 토큰을 생성합니다.
     *
     * @param claims  클레임 정보
     * @param subject 서브젝트 (사용자 이름)
     * @return 생성된 JWT 토큰
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .setClaims(claims) // 클레임 정보 설정
            .setSubject(subject) // 서브젝트 설정
            .setIssuedAt(new Date(System.currentTimeMillis())) // 생성 시점 설정
            .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 만료 시간 설정
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
}
