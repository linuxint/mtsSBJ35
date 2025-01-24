package com.devkbil.mtssbj.member;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * 인증 성공 이벤트를 수신하고, 인증 성공 시 추가 작업을 수행하는 커스텀 리스너입니다.
 * 인증된 사용자의 이전 세션을 만료시켜 단일 세션 동작을 강제하도록 설계되었습니다.
 * {@code AuthenticationSuccessEvent}에 대한 Spring의 {@code ApplicationListener}를 구현합니다.
 * <p>
 * 이 클래스는 {@code CustomSessionHandler}를 사용하여 사용자 세션을 관리하며,
 * 사용자에 대해 활성 세션만 유효하도록 보장합니다.
 */
@Component
public class CustomAuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private final CustomSessionHandler customSessionHandler;

    /**
     * CustomAuthenticationSuccessListener 생성자.
     *
     * @param customSessionHandler 사용자 세션 관리를 처리하는 핸들러 주입
     */
    public CustomAuthenticationSuccessListener(CustomSessionHandler customSessionHandler) {
        this.customSessionHandler = customSessionHandler;
    }

    /**
     * 인증 성공 이벤트 처리 메서드.
     * 인증된 사용자의 이전 세션들을 만료시킴으로써 단일 세션 규칙을 적용.
     *
     * @param event 인증 성공 이벤트 객체. 인증된 사용자 정보를 포함.
     */
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String username = event.getAuthentication().getName(); // 인증 성공한 사용자의 이름 가져오기
        customSessionHandler.expireOtherSessions(username); // 해당 사용자의 기존 세션 만료
    }
}
