package com.devkbil.mtssbj.config;

import net.ttddyy.dsproxy.transform.ParameterReplacer;
import net.ttddyy.dsproxy.transform.ParameterTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

/**
 * 쿼리 매개변수 변환을 처리하기 위해 ParameterTransformer 인터페이스를 구현한 클래스입니다.
 * 이 구현은 제공된 ParameterReplacer와 TransformInfo를 활용하여 매개변수를 수정하는 데 사용됩니다.
 *
 * {@code transformParameters} 메서드는 런타임 중 쿼리의 매개변수를 동적으로 변환하고
 * 필요한 사용자 정의 논리를 적용할 수 있도록 합니다.
 *
 * 이 클래스의 역할은 다음과 같습니다:
 * - 쿼리 매개변수 처리 및 수정.
 * - ParameterReplacer 인스턴스를 사용하여 매개변수 값 조작.
 * - TransformInfo를 활용하여 현재 쿼리의 컨텍스트 제공.
 *
 * 이 구현은 특정 매개변수 변환 요구사항에 맞게 사용자화할 수 있습니다.
 * 제공된 논리는 null 값 처리 또는 기본값 대체와 같은 다양한 용도에 맞게 확장하거나 수정할 수 있습니다.
 */
public class MyParameterTransformer implements ParameterTransformer {

    @Override
    public void transformParameters(ParameterReplacer parameterReplacer, TransformInfo transformInfo) {
        // 원본 쿼리 확인
//        String query = transformInfo.getQuery();
//        System.out.println("Original Query: " + query);

        // ParameterReplacer를 통해 매개변수 변환
//        Map<String, Object> parameters = parameterReplacer.getModifiedParameters();
//        parameterReplacer.getModifiedParameters().forEach((key, value) -> {
//            if (value == null) {
        // null 값을 "DEFAULT"로 변환
//                parameterReplacer.setParameter(key, "DEFAULT");
//            }
//        });
    }
}