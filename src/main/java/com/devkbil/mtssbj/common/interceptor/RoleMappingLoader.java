package com.devkbil.mtssbj.common.interceptor;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
 * RoleMappingLoader 클래스는 URL 매핑 데이터를 JSON 문자열로부터 읽어와
 * Java Map 객체로 변환하는 역할을 수행합니다.
 *
 * ### 주요 기능 ###
 * 1. JSON 데이터를 Java의 Map<String, Map<String, UrlMapping>> 구조로 변환.
 * 2. 데이터 파싱 중 오류 발생 시, 기본적으로 빈 Map 객체를 반환하여 애플리케이션에 안정성 제공.
 *
 * ### 사용 라이브러리 ###
 * - **Jackson ObjectMapper**:
 *   - JSON 데이터를 Java 객체나 컬렉션 타입으로 변환하기 위해 사용.
 *   - `readValue()` 메소드를 통해 String 형식의 JSON 데이터를 Map으로 변환.
 *
 * ### 사용 예 ###
 * - RoleMappingsJson.ROLE_MAPPINGS_JSON 문자열을 입력으로 받아,
 *   RoleBasedMapping 구조와 매칭되는 Map 데이터를 생성.
 *
 * ### 확장성 ###
 * - 현재는 JSON 문자열에서 데이터를 읽어오고 있지만, 외부 JSON 파일이나 데이터베이스에서
 *   JSON 데이터를 동적으로 로드하도록 쉽게 확장 가능.
 * - 특정 JSON 파싱 로직 커스터마이징 가능.
 */
public class RoleMappingLoader {

    /**
     * JSON 문자열을 Map<String, Map<String, RoleBasedMapping.UrlMapping>> 구조로 변환합니다.
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