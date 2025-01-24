package com.devkbil.mtssbj.member;

import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * CustomSessionHandler는 사용자 세션을 관리하는 컴포넌트입니다.
 * Spring Security에서 제공하는 SessionRegistry를 활용하여 세션 관련 작업을 처리합니다.
 * 특정 사용자의 현재 세션을 제외한 모든 활성 세션을 만료시키는 기능을 제공합니다.
 */
@Component
public class CustomSessionHandler {

    private final SessionRegistry sessionRegistry;

    // SessionRegistry를 주입받는 생성자
    public CustomSessionHandler(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * 주어진 사용자 이름(username)에 해당하는
     * 다른 활성 세션을 만료시키는 메서드.
     *
     * @param username 만료 작업을 수행할 사용자의 이름
     */
    public void expireOtherSessions(String username) {
        // 현재 SessionRegistry에 등록된 모든 사용자 가져오기
        List<Object> users = sessionRegistry.getAllPrincipals();

        for (Object principal : users) {
            // 사용자 객체가 UserDetails 타입인지 확인
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                String user = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
                // 현재 처리 중인 사용자가 주어진 username과 동일한지 확인
                if (user.equals(username)) {
                    // 주어진 사용자에 대한 모든 활성 세션 정보 가져오기
                    List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(principal, false);

                    // 각 세션을 순회하며 만료 처리
                    for (SessionInformation sessionInformation : sessionInformations) {
                        sessionInformation.expireNow(); // 기존 세션 만료
                    }
                }
            }
        }
    }
}
