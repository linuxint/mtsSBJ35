package com.devkbil.mtssbj.member.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 매개변수에 인증된 사용자 세부 정보를 주입하기 위해 사용되는 애노테이션입니다.
 * <p>
 * {@code @AuthPrincipal} 애노테이션은 Spring MVC 컨트롤러 메서드의 매개변수가 현재 인증된 사용자
 * 세부 정보로 해결되어야 함을 나타냅니다. 이는 {@code AuthArguResolver}와 같은 사용자 정의
 * 인자 리졸버와 함께 작동하여 보안 컨텍스트 또는 기타 소스로부터 인증 정보를 추출합니다.
 * <p>
 * 이 애노테이션은 컨트롤러 메서드에서 인증된 사용자의 세부 정보에 손쉽게 접근할 수 있도록 설계되었습니다.
 * 예를 들어, {@code UserVO} 객체 또는 기타 인증 관련 데이터를 직접 주입하는 데 사용될 수 있습니다.
 * <p>
 * Spring MVC 인자 리졸버의 적절한 구성이 요구됩니다. 예를 들면, {@code WebMvcConfigurer}
 * 구현에서 사용자 정의 리졸버(e.g., {@code AuthConfig})를 등록하는 방식이 있습니다.
 * <p>
 * 이 애노테이션은 애플리케이션이 현재 인증된 사용자를 확인하기 위한 메커니즘을 구현했다고 가정합니다.
 * 일반적으로 Spring Security의 보안 컨텍스트 또는 유사한 프레임워크에 의존합니다.
 * <p>
 * 대상: 컨트롤러 클래스의 메서드 매개변수.
 * 유지 기간: 런타임으로, 요청 처리 중 사용자 정의 인자 리졸버에 의해 접근 가능.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthPrincipal {
}