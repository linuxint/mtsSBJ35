package com.devkbil.mtssbj.common.interceptor;

import com.devkbil.mtssbj.config.security.Role;
import com.devkbil.mtssbj.member.auth.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * AdminInterceptor는 관리자 페이지 접근 요청에 대한 인증 및 권한 확인을 처리합니다.
 *
 * 주요 역할:
 * 1. 요청 사용자에 대한 인증 상태 확인.
 * 2. 사용자가 관리자 권한을 가지고 있는지 검증.
 * 3. 인증 또는 권한이 없을 경우 로그인 페이지 또는 접근 제한 페이지로 리다이렉트.
 */
@Slf4j
@Component
public class AdminInterceptor implements HandlerInterceptor {

    // 관리자 접근이 필요한 URL 패턴
    private static final String LOGIN_PAGE = "memberLogin"; // 로그인 페이지 URL
    private static final String NO_AUTH_PAGE = "noAuthMessage"; // 권한 없음 페이지 URL

    private final List<String> ADMIN_ESSENTIAL = Collections.singletonList("/ad**"); // 관리자 필수 URL 목록

    /**
     * @return 관리자 접근이 필요한 URL 패턴 리스트를 반환합니다.
     */
    public List<String> getAdminEssential() {
        return ADMIN_ESSENTIAL;
    }

    /**
     * 관리자 페이지에 대한 접근 제어를 수행합니다.
     *
     * @param req  요청 객체
     * @param res  응답 객체
     * @param handler  요청 핸들러 (컨트롤러)
     * @return 관리자로 인증된 경우 true, 그렇지 않은 경우 false
     * @throws IOException 예외 발생 시 처리
     */
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {

        AuthService authService = new AuthService();
        String userno = authService.getAuthUserNo();

        try {
            // 1. 세션 확인
            if (!StringUtils.hasText(userno)) {
                log.warn("접근 권한 없음: 유저 세션 없음");
                redirectToPage(res, LOGIN_PAGE); // 로그인 페이지로 이동
                return false;
            }

            // 2. 관리자 권한 확인
            String userrole = authService.getAuthUserrole();
            if (Role.ROLE_ADMIN != Role.getRoleByValue(userrole)) {
                log.warn("접근 권한 없음: 관리자 권한이 아님");
                redirectToPage(res, NO_AUTH_PAGE); // 권한 없음 페이지로 이동
                return false;
            }

        } catch (IOException ex) {
            log.error("예외 발생: AdminInterceptor 처리 중 오류", ex);
        }

        // 관리자 인증/권한 확인 완료
        return true;
    }

    /**
     * 컨트롤러 요청 완료 후 추가 작업을 처리할 수 있습니다.
     *
     * @param req 요청 객체
     * @param res 응답 객체
     * @param handler 요청 핸들러
     * @param mv 모델 및 뷰 객체 (필요 시 사용)
     */
    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView mv) {
        // 필요한 경우 후 처리 로직 추가 가능
    }

    /**
     * 요청이 완료된 후 (뷰 렌더링 완료 이후) 클린업 작업을 수행합니다.
     *
     * @param req 요청 객체
     * @param res 응답 객체
     * @param handler 요청 핸들러
     * @param ex 발생한 예외 객체 (존재하는 경우)
     */
    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        // 요청 완료 후 처리 로직 추가 가능
    }

    /**
     * 사용자 페이지 전환을 처리하는 유틸리티 메서드입니다.
     *
     * @param response 응답 객체
     * @param page 리다이렉트할 페이지 경로
     * @throws IOException 전환 중 예외 발생 시
     */
    private void redirectToPage(HttpServletResponse response, String page) throws IOException {
        response.sendRedirect(page);
    }
}