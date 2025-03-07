package com.devkbil.mtssbj.common.masking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 클래스의 필드에 마스킹 처리를 적용하기 위한 어노테이션.
 * 이 어노테이션은 필드 레벨에서 사용되며, 런타임에 마스킹 규칙을 적용합니다.
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Mask {
    /**
     * 필드에 적용할 마스킹 유형을 지정합니다.
     *
     * @return 마스킹 처리에 사용될 {@link MaskingType} 열거형 값
     */
    MaskingType type();
}
