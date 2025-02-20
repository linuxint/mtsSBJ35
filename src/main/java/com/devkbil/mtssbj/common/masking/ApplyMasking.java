package com.devkbil.mtssbj.common.masking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 특정 메서드의 반환값에 마스킹 로직을 적용하는 어노테이션.
 *
 * 이 어노테이션은 반환값의 특정 필드가 데이터 프라이버시나 보안을 위해
 * 마스킹되어야 하는 메서드에 사용됩니다. 반환 객체 타입과 리스트와 같은 컬렉션
 * 타입을 처리할 때의 제네릭 타입을 선택적으로 지정할 수 있습니다.
 *
 * 구성된 마스킹 로직은 관련된 MaskingAspect 클래스에서 정의된
 * AOP 방식을 통해 적용됩니다.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyMasking {
    Class<?> typeValue();

    Class<?> genericTypeValue() default Void.class; // 제네릭 사용 시
}