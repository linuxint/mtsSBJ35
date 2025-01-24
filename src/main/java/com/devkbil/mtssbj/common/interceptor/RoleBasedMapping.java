package com.devkbil.mtssbj.common.interceptor;

import java.util.Map;

/**
 * RoleBasedMapping 클래스는 역할(Role)에 따른 URL 매핑 정보를 관리하는 데이터 객체입니다.
 *
 * ### 주요 기능 ###
 * 1. URL 매핑 데이터를 JSON 데이터와 매칭하기 위한 구조를 정의.
 * 2. 역할별로 성공(successUrl) 및 실패(errorUrl) 페이지 URL을 개별적으로 설정.
 *
 * ### 구조 요약 ###
 * - **roleMappings (Map 구조)**:
 *   - Key: 역할(Role) (예: "ADMIN", "USER", "GUEST", "ANONYMOUS").
 *   - Value: RoleBasedMapping.UrlMapping 객체.
 * - **UrlMapping 클래스**:
 *   - successUrl: 성공 시 리디렉션되는 URL.
 *   - errorUrl: 실패 시 리디렉션되는 URL.
 *
 * ### 사용 예 ###
 * - "/settings" 요청에 대해 "ADMIN" 역할인 사용자는 "adminSettings" URL로 이동 (successUrl).
 * - "/settings" 요청에 대해 "USER" 역할인 사용자는 "userSettings" URL로 이동 (successUrl).
 */
public class RoleBasedMapping {

    // 역할별 매핑 정보 ("ROLE_ADMIN", "ROLE_USER", "GUEST", "ANONYMOUS")
    private Map<String, UrlMapping> roleMappings; // JSON의 동적 키 대응

    public Map<String, UrlMapping> getRoleMappings() {
        return roleMappings;
    }

    public void setRoleMappings(Map<String, UrlMapping> roleMappings) {
        this.roleMappings = roleMappings;
    }

    public static class UrlMapping {
        private String successUrl; // 요청 성공 시 이동할 URL
        private String errorUrl;   // 요청 실패 시 이동할 URL

        // Getters and Setters
        public String getSuccessUrl() {
            return successUrl;
        }

        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        public String getErrorUrl() {
            return errorUrl;
        }

        public void setErrorUrl(String errorUrl) {
            this.errorUrl = errorUrl;
        }
    }
}