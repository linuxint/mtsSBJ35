package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.common.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserDetailsService userDetailsService;

    private final JwtUtil jwtUtil;

    @PostMapping("/login")
    public String createAuthenticationToken(@ModelAttribute AuthRequest authRequest) throws Exception {
        // ... 기존 코드 ...

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());

        // UserDetails에 Role 정보가 잘 설정되었는지 로그로 확인
        log.info("User Details: {}", userDetails);

        final String jwt = jwtUtil.generateToken(userDetails);

        return jwt;
    }
}
