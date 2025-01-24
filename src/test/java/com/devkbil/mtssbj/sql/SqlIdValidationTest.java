package com.devkbil.mtssbj.sql;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlIdValidationTest {

    private static final String ANSI_RED = "\u001B[31m"; // 빨간색 ANSI 코드
    private static final String ANSI_RESET = "\u001B[0m"; // 색상 초기화 코드

    @Test
    void testSqlIdsMatch() {
        // 결과를 저장할 Map (Key: SQL ID, Value: 성공 또는 실패)
        Map<String, String> resultMap = new HashMap<>();

        // 1. MyBatis XML에서 SQL ID 로드
        SqlXmlLoader.loadSqlQueries("classpath:mapper/oracle/*.xml");
        Set<String> sqlXmlIds = SqlXmlLoader.getAllSqlIds();

        // 2. @Service 어노테이션이 있는 Service에서 사용된 SQL ID 추출
        List<String> serviceSqlIds = ServiceSqlIdExtractor.extractSqlIds("src/main/java/com/devkbil/mtssbj");

        // 3. SQL ID 검증 및 결과를 Map에 저장
        int successCount = 0;
        int failureCount = 0;
        for (String serviceSqlId : serviceSqlIds) {
            if (sqlXmlIds.contains(serviceSqlId)) {
                //resultMap.put(serviceSqlId, "성공");
                successCount++;
            } else {
                resultMap.put(serviceSqlId, ANSI_RED+" 실패 "+ANSI_RESET);
                failureCount++;
            }
        }

        // 4. Map의 결과를 출력
        resultMap.forEach((sqlId, result) ->
                System.out.println("SQL ID: " + sqlId + " -> 결과: " + result)
        );

        // 5. 성공 건수와 실패 건수 출력
        System.out.println("=========================");
        System.out.println("성공 건수: " + successCount);
        System.out.println("실패 건수: " + failureCount);
        System.out.println("=========================");
    }
}