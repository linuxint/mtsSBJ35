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
     * 지정된 키, 값 및 최대 유효 기간을 사용하여 쿠키를 생성하고 응답 객체에 저장합니다.
     *
     * @param response 쿠키가 추가될 {@code HttpServletResponse} 객체
     * @param key 쿠키의 이름
     * @param value 쿠키의 값
     * @param maxAge 쿠키의 최대 유효 기간(초 단위); 음수 값은 쿠키가 영구적으로 저장되지 않음을 나타냄
     */
    private static void saveCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 지정된 키를 기반으로 HttpServletRequest에서 쿠키 값을 읽어옵니다.
     * 쿠키가 발견되면 해당 쿠키의 만료 시간이 연장됩니다.
     *
     * @param request 쿠키를 포함하는 {@code HttpServletRequest} 객체
     * @param key 검색할 쿠키의 이름
     * @return 쿠키가 존재하면 해당 값, 쿠키가 없으면 빈 문자열을 반환
     */
    @SuppressWarnings("unused")
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
     * 제공된 HttpSession 객체에 사용자의 세션 속성을 저장합니다.
     *
     * @param session 사용자의 세션 데이터를 저장할 {@code HttpSession} 객체
     * @param userVO 세션에 저장될 사용자 데이터를 포함하는 {@code UserVO} 객체
     */
    private void saveUserSession(HttpSession session, UserVO userVO) {
        session.setAttribute("userid", userVO.getUserid());
        session.setAttribute("userrole", userVO.getUserrole());
        session.setAttribute("userno", userVO.getUserno());
        session.setAttribute("usernm", userVO.getUsernm());
    }

    /**
     * 사용자가 성공적으로 인증한 후의 이벤트를 처리합니다.
     * 저장된 요청 또는 이전 페이지를 기준으로 사용자를 적절한 URL로 리다이렉트하고,
     * 사용자 세부 정보를 기록하며, 추가적인 로그인 후 로직을 수행합니다.
     *
     * @param request 사용자에 의해 발생한 요청을 나타내는 {@code HttpServletRequest} 객체
     * @param response 사용자에게 전송될 응답을 나타내는 {@code HttpServletResponse} 객체
     * @param authentication 인증된 사용자 및 자격 증명을 나타내는 {@code Authentication} 객체
     * @throws IOException 리다이렉션 중 입력 또는 출력 오류가 발생한 경우
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        SavedRequest savedRequest = requestCache.getRequest(request, response);
        // 접근 권한 없는 경로 접근해서 스프링 시큐리티가 인터셉트해서 로그인폼으로 이동 후 로그인 성공한 경우
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            log.info("targetUrl = {}", targetUrl);
            redirectStrategy.sendRedirect(request, response, targetUrl);
        } else {
            // 로그인 버튼 눌러서 로그인한 경우 기존에 있던 페이지로 리다이렉트
            String prevPage = (String)request.getSession().getAttribute("prevPage");
            log.info("prevPage = {}", prevPage);
            if (!StringUtils.hasText(prevPage)) {
                prevPage = ConfigConstant.URL_MAIN;
            }
            redirectStrategy.sendRedirect(request, response, prevPage);
        }
/*
        List<GrantedAuthority> grantedAuthorities = (List<GrantedAuthority>)authentication.getAuthorities().stream().toList();
        WebAuthenticationDetails web = (WebAuthenticationDetails)authentication.getDetails();
        List<GrantedAuthority> authList = (List<GrantedAuthority>)authentication.getAuthorities();
*/

        // Authentication에서 UserVO 객체 가져오기
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserVO userVO) {
            log.info("UserVO 정보: {}", userVO);
        } else {
            log.warn("Principal 객체가 UserVO 타입이 아니거나 null 입니다!");
            throw new IllegalStateException(
                "인증된 객체가 UserVO가 아니거나 null: " + (principal != null ? principal.getClass() : "null"));
        }

        // 로그인 로직 수행
        memberService.insertLogIn(userVO.getUserno());
        saveUserSession(request.getSession(), userVO);

        // Remember-me 체크 추가
        rememberMeCheck(request, response, authentication, userVO);

    }

    /**
     * 사용자 인증을 위한 "Remember-Me" 기능을 처리합니다.
     * <p>
     * - "Remember-Me"가 활성화된 경우, 사용자의 ID가 포함된 쿠키를 생성하여 저장합니다.<br>
     * - "Remember-Me"가 비활성화된 경우, 관련된 쿠키를 삭제합니다.
     * </p>
     *
     * @param request        클라이언트 요청 세부 정보를 포함하는 {@link HttpServletRequest}
     * @param response       HTTP 응답을 전송하는 데 사용되는 {@link HttpServletResponse}
     * @param authentication 로그인된 사용자 세부 정보를 포함하는 {@link Authentication}
     * @param userVO         사용자별 데이터를 포함하는 {@link UserVO}
     */
    private void rememberMeCheck(HttpServletRequest request, HttpServletResponse response, Authentication authentication, UserVO userVO) {
        WebAuthenticationDetails webDetails = (WebAuthenticationDetails)authentication.getDetails();

        // Remember-Me 여부 확인
        boolean isRememberMe = webDetails != null && request.getParameter(ConfigConstant.PARAMETER_REMEMBER_ME) != null;

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
