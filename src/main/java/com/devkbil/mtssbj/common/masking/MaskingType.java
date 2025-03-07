package com.devkbil.mtssbj.common.masking;

/**
 * 마스킹 처리 유형을 정의하는 열거형.
 * {@link Mask} 어노테이션과 함께 사용되어 각 필드에 적용할 마스킹 전략을 지정합니다.
 */
public enum MaskingType {
    /**
     * 이름 마스킹.
     * 예시: "홍길동" → "홍**"
     */
    NAME,

    /**
     * 전화번호 마스킹.
     * 예시: "010-1234-5678" → "010-****-5678"
     */
    PHONE_NUMBER,

    /**
     * 이메일 마스킹.
     * 예시: "user@example.com" → "u***@example.com"
     */
    EMAIL
}
