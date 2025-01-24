package com.devkbil.mtssbj.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * DeviceDetectorInterceptor는 요청의 User-Agent 정보를 분석하여 디바이스의 종류(모바일/태블릿/일반)를 판별합니다.
 */
@Slf4j
@Component
public class DeviceDetectorInterceptor implements HandlerInterceptor {

    private static final String CURRENT_DEVICE_ATTRIBUTE = "currentDevice"; // 요청 속성명
    private static final String DEVICE_MOBILE = "MOBILE"; // 모바일 타입 디바이스
    private static final String DEVICE_TABLET = "TABLET"; // 태블릿 타입 디바이스
    private static final String DEVICE_NORMAL = "NORMAL"; // 일반 디바이스

    private static final String[] MOBILE_KEYWORDS = {"mobil", "ipod", "nintendo ds"}; // 모바일 디바이스 키워드
    private static final String[] TABLET_KEYWORDS = {"ipad", "playbook", "kindle"}; // 태블릿 디바이스 키워드

    /**
     * 요청의 User-Agent를 기반으로 디바이스 유형을 검출하고, 이를 요청 속성에 저장합니다.
     *
     * @param request 요청 객체
     * @param response 응답 객체
     * @param handler 요청 핸들러
     * @return 항상 true (요청 흐름 계속)
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String userAgent = request.getHeader("User-Agent");
        String deviceType = detectDeviceType(userAgent);

        request.setAttribute(CURRENT_DEVICE_ATTRIBUTE, deviceType);
        log.debug("Device detected: {} | User-Agent: {}", deviceType, userAgent);
        return true;
    }

    /**
     * User-Agent 문자열을 통한 디바이스 유형 감지.
     *
     * @param userAgent User-Agent 헤더
     * @return 감지된 디바이스 유형 (MOBILE, TABLET, NORMAL 중 하나)
     */
    private String detectDeviceType(String userAgent) {
        if (!StringUtils.hasText(userAgent)) {
            return DEVICE_NORMAL; // 기본 디바이스
        }

        String lowerUserAgent = userAgent.toLowerCase();

        if (lowerUserAgent.contains("android")) {
            return lowerUserAgent.contains("mobile") ? DEVICE_MOBILE : DEVICE_TABLET;
        }
        if (containsAny(lowerUserAgent, TABLET_KEYWORDS)) {
            return DEVICE_TABLET;
        }
        if (containsAny(lowerUserAgent, MOBILE_KEYWORDS)) {
            return DEVICE_MOBILE;
        }
        return DEVICE_NORMAL;
    }

    /**
     * 주어진 문자열이 키워드 배열 내 단어 중 하나라도 포함하는지 확인합니다.
     *
     * @param source 검색 대상 문자열
     * @param keywords 키워드 목록
     * @return 포함 여부 (boolean)
     */
    private boolean containsAny(String source, String[] keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
