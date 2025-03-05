package com.devkbil.mtssbj.common.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * RoleMappingLoader 클래스는 JSON 데이터를 기반으로 역할(Role) 및 URL 매핑 정보를 로드하는 기능을 제공합니다.
 *
 * 이 클래스는 역할 기반 URL 매핑 정보를 JSON 문자열에서 읽어와
 * {@code Map<String, Map<String, RoleBasedMapping.UrlMapping>>} 구조로 변환합니다.
 * URL 매핑은 역할에 따라 성공(successUrl) 또는 실패(errorUrl) URL로의 리디렉션 정보를 포함합니다.
 *
 * 주요 기능:
 * 1. JSON 문자열 데이터를 파싱하여 역할별 URL 매핑 정보를 로드.
 * 2. Jackson ObjectMapper를 사용하여 JSON 데이터 파싱.
 * 3. 데이터 로드 실패 시 빈 Map 반환.
 */
public class RoleMappingLoader {

    /**
     * JSON 문자열을 {@code Map<String, Map<String, RoleBasedMapping.UrlMapping>>} 구조로 변환합니다.
     *
     * @param jsonString JSON 형식의 문자열 데이터
     * @return URL-Role 매핑 정보가 담긴 Map 객체
     */
    public Map<String, Map<String, RoleBasedMapping.UrlMapping>> loadMappingsFromString(String jsonString) {
        ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper 초기화
        try {
            // JSON 문자열을 Map 구조로 변환
            return objectMapper.readValue(jsonString,
                    new TypeReference<Map<String, Map<String, RoleBasedMapping.UrlMapping>>>() {
                    });
        } catch (IOException e) {
            // JSON 파싱 예외 시 빈 Map 반환
            e.printStackTrace();
            return Map.of(); // 실패 시 빈 Map 반환
        }
    }
}
