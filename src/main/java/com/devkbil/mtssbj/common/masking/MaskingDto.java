package com.devkbil.mtssbj.common.masking;

/**
 * 마스킹 처리 제어를 위한 인터페이스.
 * 마스킹이 필요한 DTO 클래스에서 구현하여 마스킹 적용 여부를 제어할 수 있습니다.
 */
public interface MaskingDto {
    /**
     * 마스킹 비활성화 여부를 반환합니다.
     *
     * @return 마스킹 비활성화 여부
     * - "Y": 마스킹 비활성화 (원본 데이터 반환)
     * - null 또는 다른 값: 마스킹 활성화 (마스킹된 데이터 반환)
     */
    String getDisableMaskingYn();
}