package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SqlIdValidationTest {

    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    @Test
    void testSqlIdsMatch() {
        long startTime = System.currentTimeMillis();

        // 1. MyBatis XML에서 SQL ID 로드
        SqlXmlLoader.loadSqlQueries("classpath:mapper/oracle/*.xml");
        Set<String> sqlXmlIdsWithNamespace = SqlXmlLoader.getAllSqlIds();
        Set<String> sqlXmlIdsWithoutNamespace = SqlXmlLoader.getPureSqlIds();
        Set<String> namespaces = SqlXmlLoader.getNamespaces();

        // 2. Service 클래스에서 SQL ID 추출
        List<String> serviceSqlIds = ServiceSqlIdExtractor.extractSqlIds("src/main/java/com/devkbil/mtssbj");

        // 3. 결과 저장 (병렬 검증)
        Map<String, String> resultMap = verifySqlIds(
            serviceSqlIds, namespaces, sqlXmlIdsWithoutNamespace);

        // 4. 출력 및 결과 요약
        printResults(resultMap);

        long endTime = System.currentTimeMillis();
        log.info("SQL ID 매칭 테스트 완료 - 처리 시간: {}ms", (endTime - startTime));
    }

    private Map<String, String> verifySqlIds(List<String> serviceSqlIds, Set<String> namespaces, Set<String> xmlSqlIdsWithoutNamespace) {
        Map<String, String> resultMap = new ConcurrentHashMap<>();
        serviceSqlIds.parallelStream().forEach(sqlId -> {
            if (sqlId.contains(".")) {
                String[] parts = sqlId.split("\\.", 2);
                String namespace = parts[0];
                String pureSqlId = parts[1];

                if (!namespaces.contains(namespace)) {
                    resultMap.put(sqlId, ANSI_RED + " 실패 (Namespace 없음)" + ANSI_RESET);
                } else if (!xmlSqlIdsWithoutNamespace.contains(pureSqlId)) {
                    resultMap.put(sqlId, ANSI_RED + " 실패 (SQL ID 없음)" + ANSI_RESET);
                } else {
                    resultMap.put(sqlId, "성공");
                }
            } else {
                if (!xmlSqlIdsWithoutNamespace.contains(sqlId)) {
                    resultMap.put(sqlId, ANSI_RED + " 실패 (SQL ID 없음)" + ANSI_RESET);
                } else {
                    resultMap.put(sqlId, "성공");
                }
            }
        });
        return resultMap;
    }

    private void printResults(Map<String, String> resultMap) {
        long successCount = resultMap.values().stream().filter(result -> result.equals("성공")).count();
        long failureCount = resultMap.size() - successCount;

        resultMap.forEach((sqlId, result) -> log.info("SQL ID: {} -> 결과: {}", sqlId, result));
        log.info("=========================");
        log.info("성공 건수: {}", successCount);
        log.info("실패 건수: {}", failureCount);
        log.info("=========================");
    }
}