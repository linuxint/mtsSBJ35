package com.devkbil.mtssbj.common.masking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메소드 반환 값에 마스킹 로직을 적용하기 위한 애너테이션입니다.
 * <p>
 * 이 애너테이션은 반환 값에서 특정 필드를 데이터 프라이버시 또는 보안 목적을 위해
 * 마스킹 처리해야 하는 메소드에서 사용됩니다. 반환 객체의 타입과 리스트 같은 컬렉션 유형의
 * 제네릭 타입을 선택적으로 지정할 수 있습니다.
 * <p>
 * MaskingAspect 클래스에서 정의된 AOP 방식을 통해 구성된 마스킹 로직이 적용됩니다.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApplyMasking {

    /**
     * 마스킹을 적용할 대상 객체의 타입을 반환합니다.
     * <p>
     * 이 메소드는 데이터 마스킹 로직이 적용될 객체의 클래스 타입을 지정하기 위해 사용됩니다.
     * 타입 값은 {@code @ApplyMasking} 애너테이션 설정에서 정의됩니다.
     *
     * @return 마스킹이 적용될 객체의 클래스 타입
     */
    Class<?> typeValue();

    /**
     * 마스킹을 적용할 컬렉션 요소의 제네릭 타입을 지정합니다.
     * <p>
     * 이 메소드는 반환 객체가 컬렉션(List, Set 등)인 경우 제네릭 요소의 클래스 타입을 정의하고,
     * 해당 타입에 마스킹 로직을 적용해야 할 때 사용됩니다.
     * 제네릭 타입이 지정되지 않으면 기본값은 {@code Void.class} 입니다.
     *
     * @return 대상 컬렉션의 제네릭 요소의 클래스 타입
     */
    Class<?> genericTypeValue() default Void.class; // 제네릭 사용 시
}
