package com.devkbil.mtssbj.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * CommonInterceptor는 모든 요청에 대해 사전 및 사후 로직을 공통적으로 처리하기 위한 목적의 인터셉터입니다.
 * 주요 역할:
 * 1. 요청을 처리하기 전에 파라미터를 변경하거나 새로운 작업을 추가.
 * 2. 컨트롤러 작업 이후 추가 로직 실행 가능.
 * 3. 요청 이후 클린업 작업 또는 예외 처리.
 */
@Slf4j
public class CommonInterceptor implements HandlerInterceptor {

    /**
     * 컨트롤러 호출 전에 요청을 사전 처리합니다.
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param handler 요청 핸들러 (컨트롤러)
     * @return 항상 true (요청 흐름 계속)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 요청 파라미터 변경 처리
        modifyRequestParameters(request);
        return true;
    }

    /**
     * 컨트롤러의 작업이 완료된 이후(뷰 렌더링 전에 호출) 추가 작업을 수행합니다.
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param handler 요청 핸들러 (컨트롤러)
     * @param modelAndView 모델 및 뷰 객체
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        // 현재는 구현 필요 없음 (확장 가능)
        log.debug("postHandle: 컨트롤러 작업 완료 후 처리");
    }

    /**
     * 요청이 완료되고 뷰 렌더링이 끝난 이후 호출됩니다.
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param handler 요청 핸들러 (컨트롤러)
     * @param ex 요청 수행 중 발생한 예외 객체 (존재하는 경우)
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            log.error("afterCompletion: 요청 처리 중 예외 발생", ex);
        } else {
            log.debug("afterCompletion: 요청이 성공적으로 완료되었습니다.");
        }
    }

    /**
     * 요청 파라미터를 동적으로 수정합니다.
     *
     * @param request 요청 객체
     */
    private void modifyRequestParameters(HttpServletRequest request) {
        try {
            // ModifiableHttpServletRequest: 요청 파라미터를 수정.
            ModifiableHttpServletRequest modifiableRequest = new ModifiableHttpServletRequest(request);
            modifiableRequest.setParameter("key1", "value1");

            log.debug("preHandle: 요청 파라미터 key1을 value1로 설정");
        } catch (Exception e) {
            log.error("preHandle: 요청 파라미터 수정 중 오류 발생", e);
        }
    }
}