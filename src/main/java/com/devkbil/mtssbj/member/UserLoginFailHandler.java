package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.common.LocaleMessage;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 로그인 실패를 처리하기 위한 핸들러 클래스.
 * Spring Security의 `SimpleUrlAuthenticationFailureHandler`를 확장하여 로그인 실패 시의 추가 로직을 정의합니다.
 */
@Component
@RequiredArgsConstructor
public class UserLoginFailHandler extends SimpleUrlAuthenticationFailureHandler {

    private final MessageSource messageSource;
    private final LocaleMessage localeMessage;

    /**
     * 로그인 실패 시 호출되는 메서드.
     * 실패 이유를 분석하고 적절한 에러 메시지를 설정하여 클라이언트에 반환합니다.
     *
     * @param request   HTTP 요청 객체
     * @param response  HTTP 응답 객체
     * @param exception 인증 실패 예외 객체
     * @throws IOException      IO 처리 중 예외 발생 시
     * @throws ServletException Servlet 처리 중 예외 발생 시
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        // 예외의 원인에 따라 메시지 결정
        String errorMessage = determineErrorMessage(exception);

        // 요청 속성에 예외 메시지 추가
        setUseForward(true);
        request.setAttribute("exception", errorMessage);

        // 에러 메시지 URL 인코딩 처리
        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);

        // 실패 후 이동할 URL 설정
        setDefaultFailureUrl("/memberLogin?error=true&exception=" + encodedMessage);

        // 부모 클래스의 기본 실패 처리 호출
        super.onAuthenticationFailure(request, response, exception);
    }

    /**
     * 예외 유형에 기반하여 적절한 에러 메시지를 반환합니다.
     *
     * @param exception 인증 실패 예외
     * @return 예외에 대한 에러 메시지
     */
    private String determineErrorMessage(AuthenticationException exception) {
        // 예외 유형별 메시지 키 매핑
        Map<Class<? extends AuthenticationException>, String> exceptionMapping = new HashMap<>();
        exceptionMapping.put(BadCredentialsException.class, "AbstractUserDetailsAuthenticationProvider.badCredentials");
        exceptionMapping.put(LockedException.class, "AbstractUserDetailsAuthenticationProvider.locked");
        exceptionMapping.put(CredentialsExpiredException.class, "AccountStatusUserDetailsChecker.expired");
        exceptionMapping.put(DisabledException.class, "AccountStatusUserDetailsChecker.disabled");
        exceptionMapping.put(UsernameNotFoundException.class, "DigestAuthenticationFilter.usernameNotFound");
        exceptionMapping.put(AccountExpiredException.class, "AbstractUserDetailsAuthenticationProvider.expired");
        exceptionMapping.put(AuthenticationCredentialsNotFoundException.class, "AuthenticationProvider.credentialsNotFound");

        // 예외에 해당하는 메시지 키 조회 (기본 메시지 키는 "AuthenticationProvider.notFound")
        String messageKey = exceptionMapping.getOrDefault(exception.getClass(), "AuthenticationProvider.notFound");

        // 현재 Locale에 따른 에러 메시지 반환
        return messageSource.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}