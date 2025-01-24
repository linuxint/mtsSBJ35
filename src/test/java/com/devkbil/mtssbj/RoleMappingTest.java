package com.devkbil.mtssbj;

import com.devkbil.mtssbj.common.interceptor.RoleBasedMapping;
import com.devkbil.mtssbj.common.interceptor.RoleMappingLoader;
import com.devkbil.mtssbj.common.interceptor.RoleMappingsJson;

import java.util.Map;

public class RoleMappingTest {

    public static void main(String[] args) {
        RoleMappingLoader loader = new RoleMappingLoader();
        Map<String, Map<String, RoleBasedMapping.UrlMapping>> mappings = loader.loadMappingsFromString(RoleMappingsJson.ROLE_MAPPINGS_JSON);

        // "/adBoardGroupList"의 "ROLE_ADMIN" 정보 확인
        Map<String, RoleBasedMapping.UrlMapping> adBoardMappings = mappings.get("/adBoardGroupList");
        RoleBasedMapping.UrlMapping adminMapping = adBoardMappings.get("ADMIN");

        System.out.println("ADMIN Success URL: " + adminMapping.getSuccessUrl());
        System.out.println("ADMIN Error URL: " + adminMapping.getErrorUrl());
    }
}