package com.devkbil.mtssbj.config.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * "ADMIN" 역할을 가진 사용자만 접근을 허용하도록 메서드나 클래스를 보호하는 커스텀 어노테이션입니다.
 *
 * 이 어노테이션은 Spring Security 프레임워크를 활용하여 "ADMIN" 역할에 대한
 * {@code @PreAuthorize} 제약 조건을 적용합니다.
 *
 * 이 어노테이션을 메서드나 클래스에 사용하면 적절한 "ADMIN" 역할을 가진
 * 인증된 사용자만 접근할 수 있습니다.
 *
 * 이 어노테이션은 런타임에 유지되며 메서드나 타입(클래스 또는 인터페이스)에
 * 적용할 수 있습니다.
 *
 * 사용된 보안 프레임워크: Spring Security
 *
 * 관련 어노테이션:
 * - {@link UserAuthorize}: "USER" 역할을 가진 사용자에게 접근을 제한합니다.
 * - {@link GuestAuthorize}: "GUEST" 역할을 가진 사용자에게 접근을 제한합니다.
 *
 * 관련 열거형:
 * - {@link Role}: "ADMIN"과 같은 보안 시스템의 역할을 정의합니다.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasAnyRole('ADMIN')")
public @interface AdminAuthorize {
}
