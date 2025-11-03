package com.devkbil.mtssbj.config.security;

import java.util.Arrays;
import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    ROLE_ADMIN("ADMIN"),  // 관리자
    ROLE_USER("USER"),    // 인가 사용자
    ROLE_GUEST("GUEST"); // 미인가 사용자

    private final String value; // 화면/세션 표기용: ADMIN, USER, GUEST

    /**
     * 다양한 원천의 역할 문자열을 표준 Role로 매핑(정규화)
     * 허용 입력: A, ADMIN, ROLE_ADMIN, U, USER, ROLE_USER, G, GUEST, ROLE_GUEST
     * null 또는 알 수 없는 값은 ROLE_GUEST로 처리합니다.
     */
    public static Role of(String raw) {
        if (raw == null) return ROLE_GUEST;
        String v = raw.trim().toUpperCase(Locale.ROOT);
        switch (v) {
            case "A":
            case "ADMIN":
            case "ROLE_ADMIN":
                return ROLE_ADMIN;
            case "U":
            case "USER":
            case "ROLE_USER":
                return ROLE_USER;
            case "G":
            case "GUEST":
            case "ROLE_GUEST":
                return ROLE_GUEST;
            default:
                return ROLE_GUEST;
        }
    }

    /**
     * 역할 문자열(ADMIN/USER/GUEST)로부터 Role Enum 반환 (이전 호환)
     */
    public static Role getRoleByValue(String value) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}