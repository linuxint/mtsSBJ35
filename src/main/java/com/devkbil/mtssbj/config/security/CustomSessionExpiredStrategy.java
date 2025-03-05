package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.config.ConfigConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * <p>세션이 Spring Security 컨텍스트에서 만료되었을 때 수행될 작업을 처리하는
 * 사용자 정의 세션 만료 전략.
 *
 * 이 클래스는 {@link SessionInformationExpiredStrategy} 인터페이스를 구현하여
 * 세션 만료 동작을 사용자 정의할 수 있습니다.
 *
 * 만료된 세션을 감지하면 중복 로그인 시도를 나타내는 속성을 설정하고
 * 사용자를 {@code ConfigConstant}에 정의된 로그인 URL로 포워딩합니다.
 *
 * 특징:
 * - 중복 로그인을 알리기 위해 요청에 "DUPLICATE_LOGIN" 속성을 할당.
 * - 리디렉션 대신 사전 정의된 로그인 URL로 사용자 포워딩.
 *
 * 사용 컨텍스트:
 * 일반적으로 이 클래스는 세션 관리 및 세션 만료의 사용자 정의 처리가 필요한
 * Spring Security 구성에서 연결됩니다.</p>
 */
@Component
@RequiredArgsConstructor
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    /**
     * <p>Spring Security 컨텍스트에서 만료된 세션이 감지되었을 때 이벤트를 처리합니다.
     * 이 메서드는 세션 만료 메커니즘에 의해 호출되며 세션 만료 감지 후
     * 사용자 정의 처리를 수행합니다.
     *
     * 만료된 세션 감지 시, "DUPLICATE_LOGIN" 속성을 요청에 추가하여
     * 세션 만료가 중복 로그인의 원인임을 나타냅니다. 이후 {@code ConfigConstant.URL_LOGIN}에
     * 정의된 로그인 페이지 URL로 사용자를 포워딩합니다.</p>
     *
     * @param event 만료된 세션의 세부 정보를 포함한 이벤트. 
     *              HttpServletRequest와 HttpServletResponse 객체에 접근을 제공합니다.
     * @throws IOException 입력 또는 출력 예외가 발생한 경우.
     * @throws ServletException 요청을 포워딩하는 동안 서블릿 관련 오류가 발생한 경우.
     */
    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        request.setAttribute("DUPLICATE_LOGIN", true);

        // response.sendRedirect("/memberLogin");
        request.getRequestDispatcher(ConfigConstant.URL_LOGIN).forward(request, response);
    }
}
