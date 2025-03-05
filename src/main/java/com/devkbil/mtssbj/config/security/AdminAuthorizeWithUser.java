package com.devkbil.mtssbj.config.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring 애플리케이션에서 특정 클래스나 메서드에 접근할 때 'ADMIN'과 'USER' 역할을
 * 모두 요구하는 보안 어노테이션입니다.
 *
 * 이 어노테이션은 사용자가 어노테이션이 적용된 메서드나 클래스에 접근하기 위해
 * 'ADMIN'과 'USER' 역할을 동시에 보유해야 함을 보장합니다.
 *
 * 이 어노테이션은 여러 역할 요구사항을 충족하는 사용자로 접근을 제한해야 하는
 * 시나리오에 이상적입니다.
 *
 * 작동을 위해 Spring Security의 PreAuthorize 메커니즘이 필요합니다.
 *
 * @see PreAuthorize
 * @see AdminAuthorize
 * @see UserAuthorize
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') and hasRole('USER')")
public @interface AdminAuthorizeWithUser {
}
