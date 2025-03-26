package com.devkbil.mtssbj.api.v1.member;

import com.devkbil.mtssbj.common.LocaleMessage;
import com.devkbil.mtssbj.config.ConfigConstant;
import com.devkbil.mtssbj.member.MemberService;
import com.devkbil.mtssbj.member.UserVO;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 로그인 및 로그아웃 처리 REST API 컨트롤러
 * - 로그인, 로그아웃 및 권한 관련 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/member/login")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Login API", description = "회원 로그인 및 로그아웃 처리 API")
public class LoginRestController {

    private final LocaleMessage localeMessage;
    private final MemberService memberService;
    private final AuthService authService;

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
     * 로그인 처리 API.
     *
     * @param request   HttpServletRequest 객체
     * @param response  HttpServletResponse 객체
     * @param loginInfo 로그인 요청 정보 VO
     * @return 로그인 결과를 담은 ResponseEntity
     */
    @Operation(summary = "로그인 처리", description = "사용자가 입력한 정보로 인증하고 세션을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공"),
        @ApiResponse(responseCode = "401", description = "인증 실패"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<Map<String, Object>> login(
            HttpServletRequest request, 
            HttpServletResponse response, 
            @RequestBody @Valid UserVO loginInfo) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            String userno = loginInfo.getUserno();
            
            UserVO userVO = Objects.requireNonNull(memberService.findOne(userno), () -> {
                throw new UsernameNotFoundException("User not found with ID: " + userno);
            });
            
            // 로그인 로직 수행
            memberService.insertLogIn(userVO.getUserno());
            saveUserSession(request.getSession(), userVO);
            
            if ("Y".equalsIgnoreCase(loginInfo.getRemember())) {
                saveCookie(response, "sid", loginInfo.getUserid(), ConfigConstant.COOKIE_EXPIRE);
            } else {
                saveCookie(response, "sid", "", 0);
            }
            
            result.put("success", true);
            result.put("user", userVO);
            
            return ResponseEntity.ok(result);
        } catch (UsernameNotFoundException e) {
            log.error("Login failed: {}", e.getMessage());
            result.put("success", false);
            result.put("error", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 로그아웃 처리 API.
     *
     * @return 로그아웃 결과를 담은 ResponseEntity
     */
    @Operation(summary = "로그아웃", description = "사용자의 세션을 종료하고 로그아웃합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String userno = authService.getAuthUserNo();
            
            if (StringUtils.hasText(userno)) {
                memberService.insertLogOut(userno);
                log.info("사용자가 로그아웃 했습니다. userno: {}", userno);
                result.put("success", true);
            } else {
                result.put("success", false);
                result.put("message", "No active session found");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error during logout: {}", e.getMessage());
            result.put("success", false);
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 권한 없음 메시지 API.
     *
     * @return 권한 없음 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "권한 없음 메시지", description = "사용자가 권한이 없는 리소스에 접근할 경우 메시지를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/no-auth")
    public ResponseEntity<Map<String, Object>> noAuth() {
        Map<String, Object> result = new HashMap<>();
        result.put("message", localeMessage.getMessage("msg.err.noAuth"));
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(result);
    }
}