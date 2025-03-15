package com.devkbil.common.util;

import java.util.Collection;

/**
 * Utility class providing various helper methods for conditional operations,
 * similar to commonly used conditional functions in SQL or practical utility cases.
 */
public class IfUtil {

    /**
     * 조건에 따라 대상 값을 디코딩합니다.
     * Oracle의 DECODE 함수와 유사한 방식으로 동작하며, 조건과 값이 쌍으로 구성된 가변 인자를 처리합니다.
     *
     * @param cond    비교할 조건 객체
     * @param targets 대상 값들의 배열. 짝수 개의 인자를 기대하며, 홀수 번째는 조건값, 짝수 번째는 반환값으로 구성.
     *                마지막 인자가 홀수 번째인 경우 해당 값이 기본값으로 사용됨
     * @return 조건에 맞는 대상 값 또는 기본 조건 값. 일치하는 조건이 없고 기본값도 없는 경우 조건 객체 반환
     */
    public static Object decode(Object cond, Object... targets) {
        for (int i = 0; i < targets.length; i = i + 2) {
            Object target = targets[i];
            if (i + 1 == targets.length) {
                return targets[i];
            }
            if (cond == null) {
                if (target == null) {
                    return targets[i + 1];
                }
            } else if (cond.equals(target)) {
                return targets[i + 1];
            }
        }
        return cond;
    }

    /**
     * 소스 객체에 특정 값이 포함되어 있는지 확인합니다.
     * Collection이나 String 타입의 소스 객체를 지원하며, 포함 여부에 따라 지정된 문자열을 반환합니다.
     *
     * @param src 검사할 소스 객체 (Collection 또는 String). null인 경우 ifFalse 반환
     * @param value 찾을 값. String 소스의 경우 자동으로 String으로 변환됨
     * @param ifTrue 값이 포함된 경우 반환할 문자열
     * @param ifFalse 값이 포함되지 않은 경우 또는 src가 null이거나 지원하지 않는 타입인 경우 반환할 문자열
     * @return 포함 여부에 따른 결과 문자열
     */
    public static String contains(Object src, Object value, String ifTrue, String ifFalse) {
        if (src == null) {
            return ifFalse;
        }
        if (src instanceof Collection<?>) {
            return ((Collection<?>)src).contains(value) ? ifTrue : ifFalse;
        } else if (src instanceof String) {
            return ((String)src).contains(String.valueOf(value)) ? ifTrue : ifFalse;
        }
        return ifFalse;
    }

    /**
     * null 값을 기본값으로 대체합니다.
     * Oracle의 NVL 함수와 유사한 기능을 제공하며, 제네릭을 사용하여 모든 타입에 대해 동작합니다.
     *
     * @param <T> 처리할 데이터의 타입
     * @param src 검사할 소스 값
     * @param defaultValue src가 null일 경우 사용할 기본값
     * @return 소스가 null이 아니면 소스값, null이면 기본값
     */
    public static <T> T nvl(T src, T defaultValue) {
        if (src == null) {
            return defaultValue;
        }
        return src;
    }

    /**
     * 문자열이 null이거나 비어있는 경우 기본값으로 대체합니다.
     * nvl()과 유사하지만 문자열에 특화되어 있으며, 공백 문자열도 빈 문자열로 처리합니다.
     *
     * @param src 검사할 문자열
     * @param defaultValue src가 null이거나 비어있을 경우(trim 후 빈 문자열 포함) 사용할 기본값
     * @return 소스가 유효한 문자열이면 소스값, null이거나 비어있으면 기본값
     */
    public static String evl(String src, String defaultValue) {
        if (src == null || src.trim().isEmpty()) {
            return defaultValue;
        }
        return src;
    }
}