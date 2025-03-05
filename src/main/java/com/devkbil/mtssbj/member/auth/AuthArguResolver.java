package com.devkbil.mtssbj.member.auth;

import com.devkbil.mtssbj.member.UserVO;

import lombok.extern.slf4j.Slf4j;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * AuthArguResolver는 Spring MVC의 {@link HandlerMethodArgumentResolver}를 구현하여
 * 컨트롤러 메서드의 특정 매개변수를 동적으로 해결하고 주입하는 역할을 합니다.
 *
 * 이 클래스는 컨트롤러 메서드 매개변수에 {@link AuthPrincipal} 애노테이션이 정의된 경우,
 * 해당 매개변수를 현재 인증된 사용자 정보로 변환하여 주입합니다.
 *
 * 인증 정보는 {@link AuthService}를 통해 제공되며, 주로 Spring Security의 SecurityContext
 * 에서 현재 인증된 사용자 정보를 추출하여 사용합니다.
 *
 * 이 리졸버는 다음과 같은 작업을 수행합니다:
 * - {@code supportsParameter}: 매개변수가 {@link AuthPrincipal} 애노테이션을 포함하는지 확인하여 처리 가능 여부를 결정합니다.
 * - {@code resolveArgument}: 현재 요청 컨텍스트의 인증 정보를 기반으로 해당 인자를 해결합니다.
 *
 * 설정 시 Spring의 {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer}
 * 를 구현하여 사용자 정의 리졸버로 등록되어야 합니다.
 *
 * 주요 의도는 Spring 기반 애플리케이션의 컨트롤러에서 인증 정보를 간단히 주입 가능하도록 하는 것입니다.
 */
@Slf4j
public class AuthArguResolver implements HandlerMethodArgumentResolver {

    /**
     * 주어진 메서드 매개변수가 {@link AuthPrincipal} 주석이 달려있는지 확인하여 지원 여부를 결정합니다.
     *
     * @param parameter 평가할 메서드 매개변수
     * @return 매개변수가 {@link AuthPrincipal} 주석을 갖고 있으면 true, 그렇지 않으면 false
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthPrincipal.class);
    }

    /**
     * 현재 인증된 사용자의 정보를 가져와 핸들러 메서드의 인수를 해결합니다.
     *
     * @param parameter    인수가 해결될 메서드 매개변수
     * @param mavContainer 현재 요청에 대한 ModelAndViewContainer
     * @param webRequest   현재 요청을 나타내는 NativeWebRequest
     * @param binderFactory 요청 매개변수로부터 데이터 바인딩을 위한 WebDataBinder 인스턴스를 생성하는 팩토리
     * @return 현재 인증된 사용자를 나타내는 UserVO 인스턴스 또는 인증된 사용자가 없을 경우 null
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        AuthService authServervice = new AuthService();
        UserVO userVO = authServervice.getAuthUser();

        return userVO;
    }
}