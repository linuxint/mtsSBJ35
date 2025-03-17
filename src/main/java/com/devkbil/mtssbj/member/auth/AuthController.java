package com.devkbil.mtssbj.member.auth;

import com.devkbil.mtssbj.common.JwtUtil;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * AuthController 클래스는 인증 관련 요청을 처리하는 REST 컨트롤러입니다.
 * 사용자 인증 및 토큰 생성과 관련된 엔드포인트를 제공합니다.
 * <p>
 * 이 컨트롤러는 인증된 사용자를 위한 JWT 토큰 생성을 지원하며,
 * 사용자의 역할과 신원을 기반으로 애플리케이션에 안전하게 접근할 수 있도록 합니다.
 * <p>
 * 이 컨트롤러는 다음과 같은 구성 요소를 사용합니다:
 * - {@link AuthenticationManager}: 사용자 자격 증명을 인증합니다.
 * - {@link UserDetailsService}: 인증을 위한 사용자 세부 정보를 로드합니다.
 * - {@link JwtUtil}: JWT 토큰을 생성하고 관리합니다.
 * <p>
 * 이 컨트롤러에서 사용된 애노테이션:
 * - {@code @RestController}: 클래스를 RESTful 웹 서비스 컨트롤러로 표시합니다.
 * - {@code @RequestMapping("/api/v1/member/auth")}: 이 컨트롤러의 모든 엔드포인트에 대한 기본 URL을 지정합니다.
 * - {@code @Slf4j}: 컨트롤러에서 로깅 기능을 제공합니다.
 * - {@code @RequiredArgsConstructor}: 필요한 종속성을 주입하기 위한 생성자를 생성합니다.
 */
@RestController
@RequestMapping("/api/v1/member/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * 사용자의 자격 증명을 기반으로 인증 토큰(JWT)을 생성합니다.
     * <p>
     * 이 메서드는 제공된 인증 요청을 사용하여 JWT 토큰을 생성하며,
     * 인증 요청에는 사용자의 사용자 이름과 비밀번호가 포함되어 있습니다.
     * {@link UserDetailsService}를 사용해 사용자의 세부 정보를 로드하며,
     * {@link JwtUtil}을 사용해 토큰을 생성합니다.
     *
     * @param authRequest 사용자의 사용자 이름과 비밀번호가 포함된 인증 요청
     * @return 문자열로 반환되는 JWT 토큰
     */
    @PostMapping("/login")
    public String createAuthenticationToken(@ModelAttribute AuthRequest authRequest) {

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // UserDetails에 Role 정보가 잘 설정되었는지 로그로 확인
        log.info("User Details: {}", userDetails);

        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt;
    }
}
