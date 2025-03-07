package com.devkbil.mtssbj.member.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * AuthConfig 클래스는 Spring MVC 애플리케이션에서 웹 요청을 처리하기 위해 사용자 정의 인자 리졸버를 구성합니다.
 * 이 구성은 특히 {@link AuthPrincipal} 애노테이션이 적용된 메서드 매개변수를 해석하는 지원을 추가합니다.
 *
 * {@link WebMvcConfigurer} 인터페이스를 구현하여 MVC 설정을 사용자화합니다.
 *
 * 이 클래스의 주요 역할은 {@link AuthArguResolver}와 같은 사용자 정의 인자 리졸버를 등록함으로써
 * 인증 정보를 기반으로 컨트롤러 메서드에 사용자별 정보를 주입할 수 있도록 하는 것입니다.
 */
@Configuration
@RequiredArgsConstructor
public class AuthConfig implements WebMvcConfigurer {

    /**
     * Spring MVC 프레임워크에서 사용하는 리졸버 목록에 사용자 정의 인자 리졸버를 추가합니다.
     * 특히, {@link AuthPrincipal} 애노테이션이 적용된 매개변수를 처리하기 위해 {@link AuthArguResolver}를 등록합니다.
     *
     * @param resolvers 사용자 정의 리졸버를 추가할 수 있는 인자 리졸버 목록
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArguResolver());
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }
}
