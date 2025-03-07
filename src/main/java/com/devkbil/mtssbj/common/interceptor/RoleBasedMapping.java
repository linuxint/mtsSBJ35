package com.devkbil.mtssbj.common.interceptor;

import java.util.Map;

/**
 * RoleBasedMapping 클래스는 역할(Role)에 따른 URL 매핑 정보를 관리하는 데이터 객체입니다.
 */
public class RoleBasedMapping {

    /**
     * 역할별 매핑 정보를 저장하는 Map 객체입니다.
     * Key는 역할명("ROLE_ADMIN", "ROLE_USER", "GUEST", "ANONYMOUS")이며,
     * Value는 해당 역할에 대한 URL 매핑 정보({@link UrlMapping})입니다.
     */
    private Map<String, UrlMapping> roleMappings;

    /**
     * 역할별 매핑 정보를 반환합니다.
     *
     * @return 역할별 URL 매핑 정보를 담고 있는 Map 객체
     */
    public Map<String, UrlMapping> getRoleMappings() {
        return roleMappings;
    }

    /**
     * 역할별 매핑 정보를 설정합니다.
     *
     * @param roleMappings 설정할 역할별 URL 매핑 정보를 담고 있는 Map 객체
     */
    public void setRoleMappings(Map<String, UrlMapping> roleMappings) {
        this.roleMappings = roleMappings;
    }

    /**
     * URL 매핑 정보를 담는 내부 클래스입니다.
     * 각 역할에 대한 성공 URL과 실패 URL을 관리합니다.
     */
    public static class UrlMapping {
        /**
         * 요청 성공 시 리디렉션될 URL입니다.
         */
        private String successUrl;

        /**
         * 요청 실패 시 리디렉션될 URL입니다.
         */
        private String errorUrl;

        /**
         * 성공 URL을 반환합니다.
         *
         * @return 요청 성공 시 리디렉션될 URL
         */
        public String getSuccessUrl() {
            return successUrl;
        }

        /**
         * 성공 URL을 설정합니다.
         *
         * @param successUrl 요청 성공 시 리디렉션될 URL
         */
        public void setSuccessUrl(String successUrl) {
            this.successUrl = successUrl;
        }

        /**
         * 실패 URL을 반환합니다.
         *
         * @return 요청 실패 시 리디렉션될 URL
         */
        public String getErrorUrl() {
            return errorUrl;
        }

        /**
         * 실패 URL을 설정합니다.
         *
         * @param errorUrl 요청 실패 시 리디렉션될 URL
         */
        public void setErrorUrl(String errorUrl) {
            this.errorUrl = errorUrl;
        }
    }
}
