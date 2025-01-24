package com.devkbil.mtssbj.member;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 제공된 Authentication 정보를 활용하여 현재 인증된 사용자와 관련된 정보를 반환하는 서비스 클래스입니다.
 * Spring Security의 SecurityContext를 사용하여 인증된 사용자의 정보를 가져옵니다.
 */
@Service
public class AuthenticationService {

    private static final UserVO EMPTY_USER = new UserVO();

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 현재 인증된 사용자(UserVO)를 반환합니다.
     * 인증 정보가 없거나 Principal이 UserVO가 아닌 경우 null을 반환합니다.
     *
     * @return 인증된 UserVO 객체 또는 인증 정보가 없거나 Principal이 UserVO가 아닌 경우 null
     */
    private UserVO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserVO) {
            return (UserVO) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 현재 인증된 사용자를 Optional로 반환합니다.
     * 인증된 사용자가 없거나 Principal이 UserVO 타입이 아닌 경우 비어 있는 Optional을 반환합니다.
     *
     * @return 인증된 UserVO가 포함된 Optional 또는 인증된 사용자가 없을 경우 비어 있는 Optional
     */
    public Optional<UserVO> getAuthenticated() {
        return Optional.ofNullable(getAuthenticatedUser());
    }

    /**
     * 현재 인증된 사용자의 고유 번호를 반환합니다.
     * 인증된 사용자가 없을 경우 빈 문자열을 반환합니다.
     *
     * @return 인증된 사용자의 고유 번호 또는 인증된 사용자가 없을 경우 빈 문자열
     */
    public String getAuthenticatedUserNo() {
        UserVO userVO = getAuthenticatedUser();
        return userVO != null ? userVO.getUserno() : "";
    }

    /**
     * 현재 인증된 사용자 ID를 반환합니다.
     * 인증된 사용자가 없을 경우 빈 문자열을 반환합니다.
     *
     * @return 인증된 사용자 ID 또는 인증된 사용자가 없을 경우 빈 문자열
     */
    public String getAuthenticatedUserId() {
        return getAuthenticated().map(UserVO::getUserid).orElse("");
    }

    /**
     * 현재 인증된 사용자의 이름을 반환합니다.
     * 인증된 사용자가 없을 경우 빈 문자열을 반환합니다.
     *
     * @return 인증된 사용자의 이름 또는 인증된 사용자가 없을 경우 빈 문자열
     */
    public String getAuthenticatedUsernm() {
        return getAuthenticated().map(UserVO::getUsernm).orElse("");
    }

    /**
     * 현재 인증된 사용자의 역할을 가져옵니다.
     * 인증된 사용자가 없거나 사용자의 역할을 사용할 수 없는 경우 빈 문자열을 반환합니다.
     *
     * @return 현재 인증된 사용자의 역할 또는 인증된 사용자가 없을 경우 빈 문자열
     */
    public String getAuthenticatedUserrole() {
        return getAuthenticated().map(UserVO::getUserrole).orElse("");
    }
}