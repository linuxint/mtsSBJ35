package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.common.LocaleMessage;
import com.devkbil.mtssbj.config.ConfigConstant;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

@Tag(name = "LoginController", description = "회원 로그인 및 로그아웃 처리 컨트롤러")
@Controller
@RequiredArgsConstructor
@Slf4j
public class LoginController {

    private final LocaleMessage localeMessage;
    private final MemberService memberService;
    private final AuthService authService; // 인증 서비스

    /**
     * 쿠키 저장 메서드.
     *
     * @param response HttpServletResponse 객체
     * @param key      저장할 쿠키 키
     * @param value    저장할 쿠키 값
     * @param maxAge   쿠키 유효 기간(초 단위)
     */
    private void saveCookie(HttpServletResponse response, String key, String value, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**
     * 쿠키 읽기 메서드.
     *
     * @param request HttpServletRequest 객체
     * @param key     읽을 쿠키 키
     * @return 저장된 쿠키 값 (값이 없으면 빈 문자열 반환)
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
     * 세션에 사용자 정보 저장.
     *
     * @param session HttpSession 객체
     * @param userVO  사용자 정보 VO 객체
     */
    private void saveUserSession(HttpSession session, UserVO userVO) {
        session.setAttribute("userid", userVO.getUserid());
        session.setAttribute("userrole", userVO.getUserrole());
        session.setAttribute("userno", userVO.getUserno());
        session.setAttribute("usernm", userVO.getUsernm());
    }

    /**
     * 로그인 페이지를 반환합니다.
     *
     * @param error     오류 메시지(로그인 실패 시 표시)
     * @param exception 예외 메시지(로그인 실패에 대한 명확한 이유)
     * @param request   HttpServletRequest 객체 (세션 정보 확인)
     * @param modelMap  모델 객체 (화면으로 데이터 전달)
     * @return 로그인 페이지(html 파일 이름)
     */
    @Operation(summary = "로그인 페이지", description = "회원 로그인 페이지를 렌더링합니다.")
    @GetMapping("memberLogin")
    public String memberLogin(@RequestParam(value = "error", required = false) String error,
                              @RequestParam(value = "exception", required = false) String exception,
                              HttpServletRequest request,
                              ModelMap modelMap) {

        if (!ObjectUtils.isEmpty(authService.enticatedUserId())) {
            return "redirect:/index";
        }

        String userid = readCookie(request, "sid");
        modelMap.addAttribute("userid", userid);
        modelMap.addAttribute("error", error);
        modelMap.addAttribute("exception", exception);

        return "member/memberLogin";
    }

    /**
     * 로그인 처리.
     *
     * @param request   HttpServletRequest 객체
     * @param response  HttpServletResponse 객체
     * @param loginInfo 로그인 요청 정보 VO
     * @return 성공 시 이전 페이지 또는 index 페이지로 리다이렉트
     */
    @Operation(summary = "로그인 처리", description = "사용자가 입력한 정보로 인증하고 세션을 생성합니다.")
    @PostMapping("memberLoginChk")
    public String memberLoginChk(HttpServletRequest request, HttpServletResponse response, @ModelAttribute @Valid UserVO loginInfo) {

        String userno = loginInfo.getUserno();

        UserVO userVO = Objects.requireNonNull(memberService.findOne(userno), () -> {
            throw new UsernameNotFoundException("User not found with ID: " + userno);
        });

        // 로그인 로직 수행
        memberService.insertLogIn(userVO.getUserno());
        saveUserSession(request.getSession(), userVO);

        // 이중 로그인 방지 처리
//        EgovHttpSessionBindingListener listener = new EgovHttpSessionBindingListener();
//        request.getSession().setAttribute(userVO.getUserid(), listener);

        if ("Y".equalsIgnoreCase(loginInfo.getRemember())) {
            saveCookie(response, "sid", loginInfo.getUserid(), ConfigConstant.COOKIE_EXPIRE);
        } else {
            saveCookie(response, "sid", "", 0);
        }

        // 이전 페이지로 리다이렉트 처리
        HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if (savedRequest != null) {
            String redirectUrl = savedRequest.getRedirectUrl();
            return "redirect:" + redirectUrl;
        }

        return "redirect:/index";
    }

    /**
     * 로그아웃 처리.
     *
     * @return 로그아웃 후 로그인 페이지로 리다이렉트
     */
    @Operation(summary = "로그아웃", description = "사용자의 세션을 종료하고 로그아웃합니다.")
    @GetMapping("memberLogout")
    public String memberLogout() {

        String userno = authService.getAuthUserNo();

        if (StringUtils.hasText(userno)) {
            memberService.insertLogOut(userno);
        }

        log.info("사용자가 로그아웃 했습니다. userno: {}", userno);

        return "redirect:/memberLogin";
    }

    /**
     * 관리자 페이지 접근 시 오류 메시지 페이지 반환.
     *
     * @param modelMap 모델 객체
     * @return "권한 없음" 메시지 페이지(html 파일 이름)
     */
    @Operation(summary = "권한 없음 메시지 페이지", description = "사용자가 관리자 페이지에 접근할 경우 '권한 없음' 메시지를 출력합니다.")
    @GetMapping("noAuthMessage")
    public String noAuthMessage(ModelMap modelMap) {

        modelMap.put("msg", localeMessage.getMessage("msg.err.noAuth"));

        return "common/noAuth";
    }

}
