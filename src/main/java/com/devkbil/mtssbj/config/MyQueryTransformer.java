package com.devkbil.mtssbj.config;

import net.ttddyy.dsproxy.transform.QueryTransformer;
import net.ttddyy.dsproxy.transform.TransformInfo;

public class MyQueryTransformer implements QueryTransformer {

    @Override
    public String transformQuery(TransformInfo transformInfo) {
        String query = transformInfo.getQuery();
        // 쿼리 로깅
        System.out.println("Original Query: " + query);

        // 예: 테이블 이름 변경
        String transformedQuery = query.replace("OLD_TABLE", "NEW_TABLE");

        System.out.println("Transformed Query: " + transformedQuery);
        return transformedQuery;
    }
}
