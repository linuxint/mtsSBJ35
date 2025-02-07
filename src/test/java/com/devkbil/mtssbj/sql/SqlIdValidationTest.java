package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
public class SqlIdValidationTest {

    private static final String ANSI_RED = "\u001B[31m"; // 빨간색 ANSI 코드
    private static final String ANSI_RESET = "\u001B[0m"; // 색상 초기화 코드

    @Test
    void testSqlIdsMatch() {
        // 결과를 저장할 Map (Key: SQL ID, Value: 성공 또는 실패)
        Map<String, String> resultMap = new HashMap<>();

        // 1. MyBatis XML에서 SQL ID 로드
        SqlXmlLoader.loadSqlQueries("classpath:mapper/oracle/*.xml");

        // XML에서 로드한 SQL ID
        Set<String> sqlXmlIdsWithNamespace = SqlXmlLoader.getAllSqlIds();         // Namespace 포함 SQL ID
        Set<String> sqlXmlIdsWithoutNamespace = SqlXmlLoader.getPureSqlIds();    // Namespace 제외 SQL ID

        // 2. 각 XML 파일의 namespace를 확인
        Set<String> namespaces = SqlXmlLoader.getNamespaces();

        // 3. @Service 어노테이션이 있는 Service에서 사용된 SQL ID 추출
        List<String> serviceSqlIds = ServiceSqlIdExtractor.extractSqlIds("src/main/java/com/devkbil/mtssbj");

        // 4. SQL ID 검증 및 namespace 확인
        int successCount = 0;
        int failureCount = 0;


        for (String serviceSqlId : serviceSqlIds) {
            if (serviceSqlId.contains(".")) {
                // SQL ID에서 Namespace와 순수 SQL ID 분리
                String namespace = serviceSqlId.substring(0, serviceSqlId.indexOf("."));
                String pureSqlId = serviceSqlId.substring(serviceSqlId.indexOf(".") + 1);

                // Namespace와 순수 ID 각각 검증
                if (!namespaces.contains(namespace)) {
                    // Namespace 자체가 로드되지 않은 경우
                    System.err.println("Namespace '" + namespace + "'가 XML에서 로드되지 않음.");
                    resultMap.put(serviceSqlId, ANSI_RED + " 실패 (Namespace 없음) " + ANSI_RESET);
                    failureCount++;
                } else if (!sqlXmlIdsWithoutNamespace.contains(pureSqlId)) {
                    // Namespace는 확인되었으나, 해당 SQL ID가 없는 경우
                    System.err.println("Namespace '" + namespace + "'는 로드되었지만 SQL ID '" + pureSqlId + "'가 없음.");
                    resultMap.put(serviceSqlId, ANSI_RED + " 실패 (SQL ID 없음) " + ANSI_RESET);
                    failureCount++;
                } else {
                    // 성공한 경우
//                    resultMap.put(serviceSqlId, "성공");
                    successCount++;
                }
            } else {
                // Namespace가 없는 SQL ID 처리
                if (!sqlXmlIdsWithoutNamespace.contains(serviceSqlId)) {
                    System.err.println("SQL ID '" + serviceSqlId + "'가 XML에서 로드되지 않음.");
                    resultMap.put(serviceSqlId, ANSI_RED + " 실패 (SQL ID 없음) " + ANSI_RESET);
                    failureCount++;
                } else {
                    // 성공
//                    resultMap.put(serviceSqlId, "성공");
                    successCount++;
                }
            }
        }


        // 5. Map의 결과를 출력 (성공 & 실패 모두 출력)
        resultMap.forEach((sqlId, result) -> System.out.println("SQL ID: " + sqlId + " -> 결과: " + result));

        // 6. 성공 건수와 실패 건수를 별도로 출력
        System.out.println("=========================");
        System.out.println("성공 건수: " + successCount);
        System.out.println("실패 건수: " + failureCount);
        System.out.println("=========================");
    }
}