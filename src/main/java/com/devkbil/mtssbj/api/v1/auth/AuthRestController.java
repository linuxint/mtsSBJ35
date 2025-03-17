package com.devkbil.mtssbj.api.v1.auth;

import com.devkbil.mtssbj.api.common.response.ApiResponse;
import com.devkbil.mtssbj.api.v1.auth.dto.AuthResponseDTO;
import com.devkbil.mtssbj.common.JwtUtil;
import com.devkbil.mtssbj.member.auth.AuthRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthRestController 클래스는 인증 관련 REST API 요청을 처리하는 컨트롤러입니다.
 * JWT 기반의 인증 시스템을 구현하며, 로그인 및 토큰 갱신 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthRestController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * 사용자 로그인 요청을 처리하고 JWT 토큰을 발급합니다.
     *
     * @param authRequest 로그인 요청 정보 (사용자 이름, 비밀번호)
     * @return JWT 토큰이 포함된 응답
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> login(@RequestBody AuthRequest authRequest) {
        log.info("Login request for user: {}", authRequest.getUsername());

        // 사용자 정보 로드
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // 토큰 생성
        final String accessToken = jwtUtil.generateAccessToken(userDetails);
        final String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // 응답 생성
        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(3600) // 1시간 (초 단위)
                .username(userDetails.getUsername())
                .role(userDetails.getAuthorities().toString())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }

    /**
     * 리프레시 토큰을 사용하여 새 액세스 토큰을 발급합니다.
     *
     * @param refreshTokenRequest 리프레시 토큰 요청
     * @return 새 액세스 토큰이 포함된 응답
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponseDTO>> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        log.info("Token refresh request received");

        // 리프레시 토큰 검증
        if (!jwtUtil.validateRefreshToken(refreshTokenRequest.getRefreshToken())) {
            return ResponseEntity.badRequest().body(ApiResponse.error("INVALID_TOKEN", "Invalid refresh token"));
        }

        // 사용자 정보 로드
        String username = jwtUtil.extractUsername(refreshTokenRequest.getRefreshToken());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        // 새 액세스 토큰 생성
        final String newAccessToken = jwtUtil.generateAccessToken(userDetails);

        // 응답 생성
        AuthResponseDTO authResponse = AuthResponseDTO.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshTokenRequest.getRefreshToken()) // 기존 리프레시 토큰 유지
                .tokenType("Bearer")
                .expiresIn(3600) // 1시간 (초 단위)
                .username(userDetails.getUsername())
                .role(userDetails.getAuthorities().toString())
                .build();

        return ResponseEntity.ok(ApiResponse.success(authResponse));
    }

    /**
     * 리프레시 토큰 요청 DTO
     */
    private static class RefreshTokenRequest {
        private String refreshToken;

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }
    }
}
