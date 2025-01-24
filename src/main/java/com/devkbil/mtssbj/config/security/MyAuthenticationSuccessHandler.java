package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.config.ConfigConstant;
import com.devkbil.mtssbj.member.MemberService;
import com.devkbil.mtssbj.member.UserVO;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 인증 성공 핸들러 - Spring Security 환경에서 인증 성공 시 이벤트 처리
 * 성공적인 인증 후 사용자를 리다이렉트하고 세션 및 쿠키 관리
 * <p>
 * 주요 책임:
 * - 인증 성공 후 저장된 요청 또는 이전 페이지로 리다이렉트 처리
 * - 사용자 정보 및 리멤버 미 기능을 위한 쿠키 관리
 * - 세션에 인증 사용자 정보 저장
 * - 인증 성공 후 사용자 정보 로깅 및 추가 작업 수행
 * <p>
 * 의존성:
 * - MemberService: 로그인 이벤트와 같은 회원 관련 작업 처리 서비스
 * - RequestCache 및 RedirectStrategy: 저장된 요청 검색 및 리다이렉션 로직 관리
 * <p>
 * 스레드 안정성:
 * 이 클래스는 스레드 간 공유 상태를 유지하지 않으므로 Spring 멀티스레드 환경에서 안전하게 사용 가능
 */
@Service
@Slf4j
@AllArgsConstructor
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MemberService memberService;
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    /**
     * 쿠키 저장 메서드
     */
    private static void saveCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 읽기 메서드
     */
    private String readCookie(HttpServletRequest request, String key) {
        Cookie[] cookies = request.getCookies();
        if (!ObjectUtils.isEmpty(cookies)) {
            for (Cookie cookie : cookies) {
                if (key.equals(cookie.getName())) {
                    cookie.setMaxAge(ConfigConstant.COOKIE_EXPIRE); // 유효 기간 연장
                    return cookie.getValue();
                }
            }
        }
        return "";
    }

    /**
     * 세션에 사용자 정보 저장
     */
    private void saveUserSession(HttpSession session, UserVO userVO) {
        session.setAttribute("userid", userVO.getUserid());
        session.setAttribute("userrole", userVO.getUserrole());
        session.setAttribute("userno", userVO.getUserno());
        session.setAttribute("usernm", userVO.getUsernm());
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        // 접근 권한 없는 경로 접근해서 스프링 시큐리티가 인터셉트해서 로그인폼으로 이동 후 로그인 성공한 경우
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("targetUrl = {}", targetUrl);
            redirectStrategy.sendRedirect(request, response, targetUrl);
        }
        // 로그인 버튼 눌러서 로그인한 경우 기존에 있던 페이지로 리다이렉트
        else {
            String prevPage = (String) request.getSession().getAttribute("prevPage");
            log.info("prevPage = {}", prevPage);
            if(!StringUtils.hasText(prevPage)) {
                prevPage = ConfigConstant.URL_MAIN;
            }
            redirectStrategy.sendRedirect(request, response, prevPage);
        }

        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>) authentication.getAuthorities().stream().toList();

        WebAuthenticationDetails web = (WebAuthenticationDetails) authentication.getDetails();
        // Authentication에서 UserVO 객체 가져오기
        UserVO userVO = (UserVO) authentication.getPrincipal();
        if (userVO instanceof UserVO) {
            log.info("UserVO 정보: {}", userVO);
        } else {
            log.warn("Principal 객체가 UserVO 타입이 아닙니다!");
            throw new IllegalStateException("인증된 객체가 UserVO가 아닙니다: " + userVO.getClass());
        }

        List<GrantedAuthority> authList = (List<GrantedAuthority>) authentication.getAuthorities();

        // 로그인 로직 수행
        memberService.insertLogIn(userVO.getUserno());
        saveUserSession(request.getSession(), userVO);

        // 이중 로그인 방지 처리
//        EgovHttpSessionBindingListener listener = new EgovHttpSessionBindingListener();
//        request.getSession().setAttribute(userVO.getUserid(), listener);


        // Remember-me 체크 추가
        rememberMeCheck(request, response, authentication, userVO);

        //if ("Y".equalsIgnoreCase(loginInfo.getRemember())) {
//            saveCookie(response, "sid", loginInfo.getUserid(), COOKIE_EXPIRE);
//        } else {
//            saveCookie(response, "sid", "", 0);
//        }

    }

    /**
     * Remember-Me 옵션 활성화 시 쿠키 저장 또는 처리
     */
    private void rememberMeCheck(HttpServletRequest request, HttpServletResponse response, Authentication authentication, UserVO userVO) {
        WebAuthenticationDetails webDetails = (WebAuthenticationDetails) authentication.getDetails();

        // Remember-Me 여부 확인
        boolean isRememberMe = webDetails == null ? false : request.getParameter(ConfigConstant.PARAMETER_REMEMBER_ME) != null;

        if (isRememberMe) {
            log.info("Remember-Me 옵션이 활성화되었습니다.");
            // 쿠키 저장 로직: 사용자의 ID 또는 필요한 값을 쿠키에 저장
            saveCookie(response, ConfigConstant.SID_COOKIE_NAME, userVO.getUserid(), ConfigConstant.COOKIE_EXPIRE);

        } else {
            log.info("Remember-Me 옵션이 비활성화되었습니다.");
            // Remember-Me 쿠키 제거 (이전에 설정된 값이 있을 경우 제거)
            saveCookie(response, ConfigConstant.SID_COOKIE_NAME, "", 0);
        }
    }
}
