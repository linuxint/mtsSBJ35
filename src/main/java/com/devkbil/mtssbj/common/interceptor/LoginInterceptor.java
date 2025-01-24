package com.devkbil.mtssbj.common.interceptor;

import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.member.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.*;

/**
 * LoginInterceptor는 모든 요청 전에 사용자의 인증 상태를 확인하여
 * 인증되지 않은 사용자를 로그인 페이지로 리다이렉트하는 역할을 수행합니다.
 */
@Slf4j
@Component
public class LoginInterceptor implements HandlerInterceptor {

    // 상수로 문자열 및 설정 분리
    private static final String LOG_ID = "logId"; // 세션에서 사용할 로그 ID 키
    private static final String REDIRECT_LOGIN_PAGE = "memberLogin"; // 로그인 페이지 URL

    private static final List<String> LOGIN_ESSENTIAL = Collections.singletonList("/**"); // 인증 필수 URL
    private static final List<String> LOGIN_INESSENTIAL = Arrays.asList(
            "/api/**", "/memberLogin", "/memberLoginChk", "/js/**", "/css/**", "/images/**"); // 인증 불필요 URL

    /**
     * @return 인증이 필수적인 URL 패턴 목록을 반환합니다.
     */
    public List<String> getLoginEssential() {
        return LOGIN_ESSENTIAL;
    }

    /**
     * @return 인증이 필요하지 않은 URL 패턴 목록을 반환합니다.
     */
    public List<String> getLoginInessential() {
        return LOGIN_INESSENTIAL;
    }

    /**
     * 컨트롤러 실행 요청 전에 로그인 상태를 확인합니다.
     *
     * @param req 요청 객체
     * @param res 응답 객체
     * @param handler 요청 핸들러
     * @return 로그인된 사용자인 경우 true, 아니면 false
     */
    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) {
        logRequestInfo(req);

        AuthenticationService authenticationService = new AuthenticationService();
        Optional<UserVO> userVO = authenticationService.getAuthenticated();

        if (userVO.isEmpty()) {
            log.warn("[LoginInterceptor] 로그인 세션이 존재하지 않음. 로그인 페이지로 이동");
            redirectToLoginPage(res);
            return false;
        }

        return true;
    }

    /**
     * 컨트롤러 작업 이후 후속 작업을 처리합니다. (현재 로깅 목적)
     *
     * @param req 요청 객체
     * @param res 응답 객체
     * @param handler 요청 핸들러
     * @param mv 모델 및 뷰 객체 (필요 시 사용)
     */
    @Override
    public void postHandle(HttpServletRequest req, HttpServletResponse res, Object handler, ModelAndView mv) {
        log.info("[LoginInterceptor] postHandle 실행: Method - {}", getMethodName());
    }

    /**
     * 요청 처리 후 클린업 작업 또는 최종 처리합니다.
     *
     * @param req 요청 객체
     * @param res 응답 객체
     * @param handler 요청 핸들러
     * @param ex 예외 객체 (있을 경우)
     */
    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        log.info("[LoginInterceptor] afterCompletion 실행: Method - {}, Exception - {}",
                getMethodName(), ex != null ? ex.getMessage() : "없음");
    }

    /**
     * 로그인 페이지로 리다이렉트합니다.
     *
     * @param response 응답 객체
     */
    private void redirectToLoginPage(HttpServletResponse response) {
        try {
            response.sendRedirect(REDIRECT_LOGIN_PAGE);
        } catch (IOException e) {
            log.error("[LoginInterceptor] 로그인 페이지 리다이렉트 중 오류 발생", e);
        }
    }

    /**
     * 요청 정보와 UUID를 로그로 기록합니다.
     *
     * @param request 요청 객체
     */
    private void logRequestInfo(HttpServletRequest request) {
        String uuid = UUID.randomUUID().toString();
        String methodName = getMethodName();
        String logId = getLogId(request);

        log.info("[LoginInterceptor] Method: {}, Log ID: {}, UUID: {}", methodName, logId, uuid);
    }

    /**
     * 세션에서 LogId 정보를 가져옵니다. 세션이 없으면 "N/A"를 반환합니다.
     *
     * @param req 요청 객체
     * @return LogId 또는 "N/A"
     */
    private String getLogId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (ObjectUtils.isEmpty(session)) {
            return "N/A"; // 세션이 없는 경우 기본값.
        }

        // 세션 내 속성 로그 출력
        Enumeration<String> attributeNames = session.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String key = attributeNames.nextElement();
            log.debug("[LoginInterceptor] 세션 속성 - Key: {}, Value: {}", key, session.getAttribute(key));
        }

        return (String) session.getAttribute(LOG_ID);
    }

    /**
     * 호출된 메서드 이름을 반환합니다.
     *
     * @return 현재 호출된 메서드 이름
     */
    private String getMethodName() {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }
}