package com.devkbil.mtssbj.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;
import java.time.LocalTime;

import lombok.extern.slf4j.Slf4j;

/**
 * TradingTimeInterceptor는 HTTP 요청에 대해 거래 가능 시간을 기반으로
 * 요청을 처리할지 거부할지를 결정하는 인터셉터입니다.
 * <p>
 * ### 주요 기능 ###
 * 1. 요청이 거래 가능 시간 내에 생성되었는지 확인.
 * 2. 거래 불가 시간에 요청이 발생하면 HTTP 403 상태와 에러 메시지를 반환.
 * 3. 정상 거래 시간에는 요청을 컨트롤러로 전달.
 * <p>
 * ### Spring Framework 연계 ###
 * - **Spring Framework**의 주요 기능 중 하나인 **Interceptor**를 활용하여
 *   컨트롤러에 도달하기 이전에 요청을 공통적으로 처리할 수 있는 전처리(Pre-Processing) 로직을 구현합니다.
 * - TradingTimeInterceptor는 **HandlerInterceptor** 인터페이스를 구현하며,
 *   이는 스프링의 **DispatcherServlet**이 요청을 컨트롤러로 전달하기 전에
 *   전처리(preHandle), 후처리(postHandle), 완료 후 작업(afterCompletion) 등의 로직을 지원합니다.
 * <p>
 * ### 특징 ###
 * - **로깅 및 사용자 피드백 제공**: 로그를 통해 요청 시간과 상태를 기록하며, 사용자에게 친절한 메시지를 제공.
 * - **거래 가능 시간 설정**: 거래 시간은 `START_TRADING_TIME`와 `END_TRADING_TIME`으로 지정되어 있습니다.
 * - **소프트웨어 유연성 제공**: 거래 시간 로직이 변경되더라도 코드는 간단히 수정 가능하고 재사용성 높은 구조입니다.
 */
@Slf4j
@Component
public class TradingTimeInterceptor implements HandlerInterceptor {

    // 거래 가능 시간을 상수로 설정
    private static final LocalTime START_TRADING_TIME = LocalTime.of(0, 1);   // 거래 시작 시간
    private static final LocalTime END_TRADING_TIME = LocalTime.of(23, 59);    // 거래 종료 시간

    /**
     * 요청이 컨트롤러로 전달되기 전에 거래 가능 시간 여부를 확인합니다.
     * <p>
     * **Spring의 역할**
     * - Spring의 `HandlerInterceptor`를 통해 요청 처리 로직을 사전에 실행할 수 있습니다.
     * - DispatcherServlet이 컨트롤러로 요청을 전달하기 전 상호작용을 제어합니다.
     * <p>
     * **로직 설명**
     * 1. 현재 시간을 확인하여 거래 가능 시간인지 판단.
     * 2. 거래 가능 시간이 아닐 경우, HTTP 403 상태 코드와 메시지를 클라이언트에 전달.
     *
     * @param request  HTTP 요청 객체 (요청 정보 포함)
     * @param response HTTP 응답 객체 (클라이언트에 반환 정보 작성)
     * @param handler  요청을 처리할 핸들러 (컨트롤러 또는 기타 로직)
     * @return 거래 가능 시간인 경우 true, 아닐 경우 false (요청 차단)
     * @throws Exception 처리 중 오류 발생 시 예외
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 현재 시간을 가져옵니다.
        LocalTime currentTime = LocalTime.now();

        // 요청을 처리하는 핸들러가 메서드 유형인지 확인 (추가 처리 가능)
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            // 실행 중인 메서드 및 클래스 정보 가져오기
            Method method = handlerMethod.getMethod();
            log.debug("처리 중인 메서드: {}, 소속 클래스: {}", method.getName(), method.getDeclaringClass().getName());
        }

        // 근무시간조회 API 호출
        // 시간체크로직을 if조건에 코딩


        // 거래 가능 시간인지 확인
        if (currentTime.isBefore(START_TRADING_TIME) || currentTime.isAfter(END_TRADING_TIME)) {
            log.warn("[거래 불가] 현재 시간: {}, 요청 URI: {}", currentTime, request.getRequestURI());

            // 거래 불가 시간일 경우, HTTP 403 응답을 반환하고 요청을 중단합니다.
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("현재는 거래 불가 시간입니다. 거래 가능 시간은 "
                    + START_TRADING_TIME + " ~ " + END_TRADING_TIME + "입니다.");
            response.getWriter().flush();

            return false; // 거래 불가 시간에 요청 차단
        }

        // 거래 가능 시간일 경우 로깅 후 요청을 계속 처리
        log.info("[거래 가능] 현재 시간: {}, 요청 URI: {}", currentTime, request.getRequestURI());
        return true;
    }
}