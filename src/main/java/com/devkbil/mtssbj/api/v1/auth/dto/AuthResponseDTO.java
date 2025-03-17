package com.devkbil.mtssbj.api.v1.auth.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * 인증 응답 DTO 클래스입니다.
 * 로그인 성공 시 클라이언트에게 반환되는 데이터를 담습니다.
 */
@Getter
@Builder
public class AuthResponseDTO {
    
    /**
     * 액세스 토큰
     * 클라이언트가 API 요청 시 인증을 위해 사용합니다.
     */
    private final String accessToken;
    
    /**
     * 리프레시 토큰
     * 액세스 토큰이 만료되었을 때 새로운 액세스 토큰을 발급받기 위해 사용합니다.
     */
    private final String refreshToken;
    
    /**
     * 토큰 타입
     * 일반적으로 "Bearer"로 설정됩니다.
     */
    private final String tokenType;
    
    /**
     * 액세스 토큰 만료 시간 (초 단위)
     */
    private final long expiresIn;
    
    /**
     * 사용자 이름
     */
    private final String username;
    
    /**
     * 사용자 역할
     */
    private final String role;
}