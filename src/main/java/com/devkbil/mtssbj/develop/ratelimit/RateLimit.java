package com.devkbil.mtssbj.develop.ratelimit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API Rate Limit을 처리하기 위한 커스텀 어노테이션 클래스
 * - 이 어노테이션이 붙은 메서드는 지정된 키 기준으로 호출 횟수를 제한합니다.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RateLimit {
    /**
     * 레이트 리미팅을 위한 고유 키
     *
     * @return 메서드의 호출 횟수를 제한하는데 사용될 고유한 문자열 키.
     *         이 키를 기준으로 동일한 키를 가진 메서드들의 호출 횟수가 집계됨
     */
    String key();
}
