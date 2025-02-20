package com.devkbil.mtssbj.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * ThemeInterceptor는 Spring MVC의 HandlerInterceptor를 구현한 클래스입니다.
 * 요청에 대한 테마 설정을 관리하며, 세션에서 테마를 초기화하거나 가져와 뷰에서 사용할 수 있도록 제공합니다.
 *
 * 이 클래스는 모든 요청이 세션에 올바른 테마를 설정하도록 보장하며, 테마 정보를 뷰 레이어에 전달합니다.
 */
public class ThemeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 테마를 세션에서 가져오거나, 새로운 테마 초기화
        String theme = (String) request.getSession().getAttribute("theme");
        if (theme == null) {
            theme = "defaultTheme"; // 기본 테마 설정
            request.getSession().setAttribute("theme", theme);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null) {
            // 뷰에 테마 정보를 추가 (뷰에서 사용할 수 있도록 설정)
            modelAndView.addObject("theme", request.getSession().getAttribute("theme"));
        }
    }
}