package com.devkbil.mtssbj.common.masking;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 개인정보 마스킹 처리를 위한 유틸리티 클래스.
 * 이름, 전화번호, 이메일 등 다양한 유형의 개인정보에 대한 마스킹 패턴을 제공합니다.
 */
public class MaskingUtil {
    /**
     * 지정된 마스킹 유형에 따라 문자열을 마스킹 처리합니다.
     *
     * @param maskType 적용할 마스킹 유형 ({@link MaskingType})
     * @param value    마스킹할 원본 문자열
     * @return 마스킹이 적용된 문자열
     */
    public static String maskingOf(MaskingType maskType, String value) {
        return switch (maskType) {
            case NAME -> nameMaskOf(value);
            case PHONE_NUMBER -> phoneNumberMaskOf(value);
            case EMAIL -> emailMaskOf(value);
        };
    }

    /**
     * Mask 어노테이션 기반 마스킹 처리 (정규식 우선, 없으면 타입 기반)
     *
     * @param mask Mask 어노테이션
     * @param value 마스킹할 원본 문자열
     * @return 마스킹된 문자열
     */
    public static String maskingOf(Mask mask, String value) {
        if (mask == null || value == null) return value;
        if (!mask.pattern().isEmpty()) {
            // 정규식 기반 마스킹: 패턴에 매칭되는 부분을 ****로 대체
            return value.replaceAll(mask.pattern(), "****");
        } else {
            return maskingOf(mask.type(), value);
        }
    }

    /**
     * 이름에 대한 마스킹을 적용합니다.
     * 첫 글자와 마지막 글자를 제외한 나머지를 '*'로 마스킹합니다.
     * 예시: "홍길동" → "홍*동"
     *
     * @param value 마스킹할 이름
     * @return 마스킹된 이름
     */
    private static String nameMaskOf(String value) {
        // 홍*동 마스킹
        String regex = "(?<=.{1})(.*)(?=.$)";
        String maskedValue = value.replaceFirst(regex, "*".repeat(value.length() - 2));
        return maskedValue;
    }

    /**
     * 전화번호에 대한 마스킹을 적용합니다.
     * 가운데 번호 4자리를 '*'로 마스킹합니다.
     * 예시: "010-1234-5678" → "010-****-5678"
     *
     * @param value 마스킹할 전화번호
     * @return 마스킹된 전화번호
     */
    private static String phoneNumberMaskOf(String value) {
        // 010-****-1234
        String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";
        Matcher matcher = Pattern.compile(regex).matcher(value);
        if (matcher.find()) {
            String maskedValue = matcher.group(0).replaceAll(matcher.group(2), "****");
            return maskedValue;
        }
        return value;
    }

    /**
     * 이메일 주소에 대한 마스킹을 적용합니다.
     * 로컬 파트의 처음 3자를 제외한 나머지를 '*'로 마스킹합니다.
     * 예시: "user@example.com" → "use****@example.com"
     *
     * @param value 마스킹할 이메일 주소
     * @return 마스킹된 이메일 주소
     */
    private static String emailMaskOf(String value) {
        // abc****@gmail.com
        return value.replaceAll("(?<=.{3}).(?=[^@]*?@)", "*");
    }
}