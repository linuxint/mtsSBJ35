package com.devkbil.mtssbj.common;

import io.jsonwebtoken.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.RequiredArgsConstructor;

/**
 * JwtRequestFilter는 HTTP 요청에 포함된 JWT를 분석하고
 * 사용자의 인증 정보를 설정하는 필터입니다.
 * 이 클래스는 모든 요청마다 실행되며 한 번 실행되는 필터로 동작합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * 필터 내부 작업을 수행하는 메서드입니다.
     * 요청 헤더에서 JWT를 추출하고 이를 검증한 뒤, 유효한 토큰이라면
     * 사용자의 인증 정보를 SecurityContext에 설정합니다.
     *
     * @param request  현재 HTTP 요청 객체
     * @param response 현재 HTTP 응답 객체
     * @param chain    필터 체인의 다음 필터로 요청 전달
     * @throws ServletException    서블릿 예외 처리
     * @throws IOException         입출력 예외 처리
     * @throws java.io.IOException 파일 입출력 예외 처리
     */
    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException,
        IOException,
        java.io.IOException {

        final String authorizationHeader = request.getHeader("Authorization"); // Authorization 헤더에서 토큰 추출

        String username = null;
        String jwt = null;

        // Bearer 토큰 여부 확인 및 토큰 파싱
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Bearer 접두사 삭제 후 토큰만 추출
            username = jwtUtil.extractUsername(jwt); // 토큰에서 사용자 이름 추출
        }

        // 사용자 이름 확인 및 SecurityContext에 인증 정보 설정
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 사용자 정보 로드
            if (jwtUtil.validateToken(jwt, userDetails)) { // 토큰 유효성 확인
                // 사용자 인증 객체 생성 및 설정
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken); // 보안 컨텍스트에 설정
            }
        }
        chain.doFilter(request, response); // 다음 필터로 요청 전달
    }
}