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


@Component
public class JwtUtil {

//    private String SECRET_KEY = Base64.getEncoder().encodeToString("secret".getBytes());

    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            Base64.getEncoder().encodeToString("N8smKe2pXyZCd3Rsv7nNni0gfZsl7J7MfinPxaO2Bgk=".getBytes()).getBytes()
    );

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities()); // Role 정보 추가
        return createToken(claims, userDetails.getUsername()); // claims 추가
    }

    private String createToken(Map<String, Object> claims, String subject) { // claims 매개변수 추가
        return Jwts.builder()
                .setClaims(claims)               // Claim 정보 지정
                .setSubject(subject)             // 서브젝트 설정
                .setIssuedAt(new Date(System.currentTimeMillis())) // 토큰 생성 시간
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 만료 시간
            .signWith(secretKey)            // SECRET_KEY로 서명
                .compact();                      // Token 문자열로 직렬화
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
