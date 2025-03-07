package com.devkbil.mtssbj.common.interceptor;

import com.devkbil.mtssbj.member.auth.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * UrlMappingInterceptor는 요청 URL과 사용자의 역할(Role)에 따라 적절한 뷰(View)를 제공하기 위한 역할 기반 URL 매핑 로직을 담당합니다.
 *
 * ### 주요 기능 ###
 * 1. 요청된 URL과 사용자의 역할(Role)에 근거하여 성공 뷰 또는 실패 뷰 설정.
 * 2. 비로그인 사용자 또는 역할(Role)이 매핑되지 않은 경우 기본 에러 페이지로 설정.
 * 3. RoleMappingsLoader를 기반으로 JSON 데이터에서 URL 매핑 정보를 로드하여 초기화.
 *
 * ### Spring Framework와의 연계 ###
 * #### 1. **HandlerInterceptor** ####
 * - UrlMappingInterceptor는 Spring의 `HandlerInterceptor` 인터페이스를 구현하며,
 *   이는 요청 처리 흐름(Flow) 내에서 컨트롤러 호출 전후에 동작할 수 있는 기능을 제공합니다.
 * - Spring MVC의 DispatcherServlet이 컨트롤러를 호출하기 전후로 전처리, 후처리, 완료 후 작업을 정의 가능.
 * - 이 클래스에서는 **`postHandle`** 메서드를 활용하여 컨트롤러 로직 실행 후, 반환된 **ModelAndView 객체를 조정**합니다.
 *
 * #### 2. **ModelAndView 조작**
 * - `postHandle` 메서드는 컨트롤러가 반환한 **ModelAndView를 후처리**하여 적절한 뷰를 설정할 수 있는 유연한 방식을 제공합니다.
 * - 즉, URL 기반 역할 접근 제어 및 뷰 전환 로직을 중앙화할 수 있습니다.
 *
 * #### 3. **유연한 JSON 데이터 활용**
 * - 객체 역할(Role) 매핑 데이터를 외부 JSON 형식으로 관리(예: `RoleMappingsJson`)하여,
 *   유동적으로 URL 및 역할 관련 데이터를 변경 가능.
 * - 이 점은 코드 수정 없이 **구성 데이터를 파일, DB 등** 외부 소스에서 로드할 수 있어 유지보수성 향상.
 */
@Slf4j
@Component
public class UrlMappingInterceptor implements HandlerInterceptor {

    // URL에 대한 역할(Role) 기반 매핑 정보를 저장하는 데이터 구조
    private final Map<String, Map<String, RoleBasedMapping.UrlMapping>> urlRoleMappings;

    /**
     * 생성자: 외부 JSON 데이터를 활용하여 URL-역할(Role) 매핑 정보를 초기화합니다.
     */
    public UrlMappingInterceptor() {
        RoleMappingLoader loader = new RoleMappingLoader(); // JSON 데이터를 로드하여 초기화
        this.urlRoleMappings = loader.loadMappingsFromString(RoleMappingsJson.ROLE_MAPPINGS_JSON);
    }

    /**
     * 컨트롤러 처리 이후, 요청된 URL 및 사용자 역할에 따라 적절한 뷰를 설정합니다.
     *
     * #### 로직 요약 ####
     * 1. 요청 URL과 현재 인증된 사용자의 역할을 확인.
     * 2. URL에 대한 역할(Role) 매핑 데이터를 검사.
     * 3. 성공 URL(successUrl) 또는 에러 URL(errorUrl)을 ModelAndView에 셋팅.
     * 4. 역할(role)이 없는 경우 기본 메시지 뷰 설정 (`noAuthMessage`).
     *
     * **Spring의 역할**
     * - `postHandle` 메서드를 통해 컨트롤러가 반환한 결과(ModelAndView)를 가공하여
     *   적절한 뷰(View)를 설정.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @param handler  요청을 처리하는 핸들러 (컨트롤러 또는 기타 로직)
     * @param modelAndView 컨트롤러가 반환하는 ModelAndView 객체
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView == null) {
            return; // ModelAndView가 null일 경우 더 이상 처리하지 않음
        }

        // 인증된 사용자의 역할(Role)을 가져옴
        AuthService authService = new AuthService();
        String requestUrl = request.getRequestURI(); // 요청 URL
        String userRole = authService.getAuthUserrole();

        // 비로그인 상태는 "ANONYMOUS"로 설정
        if (ObjectUtils.isEmpty(userRole)) {
            userRole = "ANONYMOUS"; // 비로그인 상태 처리
        }

        // 요청된 URL에 대한 역할 기반 매핑 데이터를 가져옴
        Map<String, RoleBasedMapping.UrlMapping> roleMappings = urlRoleMappings.get(requestUrl);
        if (roleMappings != null) { // 매핑 데이터가 존재하는 경우
            RoleBasedMapping.UrlMapping roleMapping = roleMappings.get(userRole);

            if (roleMapping != null) { // 사용자 Role 매핑 확인
                if (roleMapping.getSuccessUrl() != null) {
                    modelAndView.setViewName(roleMapping.getSuccessUrl()); // 성공 URL
                } else {
                    modelAndView.setViewName(roleMapping.getErrorUrl()); // 매핑된 실패 URL
                }
            } else {
                modelAndView.setViewName("noAuthMessage"); // Role 매핑이 없는 경우 기본 실패 메시지
            }
        }
    }

}