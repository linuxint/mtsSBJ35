package com.devkbil.mtssbj.config.security;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Role {

    ROLE_ADMIN("ADMIN"),  // 관리자
    ROLE_USER("USER"),  // 인가 사용자
    ROLE_GUEST("GUEST"); // 미인가 사용자

    private final String value;

    /**
     * 역할 문자열로부터 Role Enum 반환
     *
     * @param value 역할 문자열 (e.g., "ADMIN")
     * @return 매칭되는 Role Enum, 없으면 null 반환
     */
    public static Role getRoleByValue(String value) {
        return Arrays.stream(Role.values())
                .filter(role -> role.getValue().equals(value))
                .findFirst()
                .orElse(null); // 값이 없는 경우 null 반환 (Optional로 처리 가능)
    }

}