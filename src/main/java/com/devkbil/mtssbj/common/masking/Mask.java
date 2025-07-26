package com.devkbil.mtssbj.common.masking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 마스킹이 필요한 필드에 적용하는 어노테이션.
 * type: Enum 기반 마스킹 타입
 * pattern: 정규식 기반 마스킹 패턴(선택)
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mask {
    MaskingType type() default MaskingType.NAME;
    String pattern() default ""; // 정규식 직접 지정
}
