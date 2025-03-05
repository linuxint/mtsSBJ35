package com.devkbil.mtssbj.member.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * AuthRequest 클래스는 인증 요청에 사용되는 데이터 구조를 나타냅니다.
 * 이 클래스는 사용자를 인증하기 위해 필요한 자격 증명, 즉 사용자 이름과 비밀번호를 포함합니다.
 *
 * 이 클래스는 로그인 요청과 같은 인증 과정에서 사용자 이름과 비밀번호를 제출할 때와 같이
 * 데이터 전송 객체(DTO)로 일반적으로 사용됩니다.
 *
 * Lombok의 {@code @Getter} 및 {@code @Setter}로 주석 처리되어
 * 필드에 대한 getter 및 setter 메서드를 자동으로 생성합니다.
 *
 * 필드:
 * - username: 인증을 시도하는 사용자의 사용자 이름
 * - password: 제공된 사용자 이름과 연관된 비밀번호
 *
 * 이 클래스를 사용하는 경우 자격 증명에 대한 보안 처리를 가정하며, 암호화 및 네트워크 상의 안전한 전송을 포함합니다.
 */
@Getter
@Setter
public class AuthRequest {
    private String username; // 인증을 시도하는 사용자의 사용자 이름
    private String password; // 제공된 사용자 이름과 연관된 비밀번호
}