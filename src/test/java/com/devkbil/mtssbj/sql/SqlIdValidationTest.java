package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class SqlIdValidationTest {

    private static final String ANSI_RED = "\u001B[31m"; // 출력에서 실패 메시지를 빨간색으로 포맷팅
    private static final String ANSI_RESET = "\u001B[0m"; // 컬러 포맷팅 초기화

    @Test
    void testSqlIdsMatch() {
        long startTime = System.currentTimeMillis(); // 처리 시간 측정을 위한 시작 시간 저장

        // 1. MyBatis XML에서 SQL ID 로드
        SqlXmlLoader.loadSqlQueries("classpath:mapper/oracle/*.xml"); // XML 파일에서 SQL ID 로드
        Set<String> sqlXmlIdsWithNamespace = SqlXmlLoader.getAllSqlIds(); // Namespace 포함 SQL ID 집합
        Set<String> sqlXmlIdsWithoutNamespace = SqlXmlLoader.getPureSqlIds(); // Namespace 제거 SQL ID 집합
        Set<String> namespaces = SqlXmlLoader.getNamespaces(); // Namespace 목록 추출

        // 2. Service 클래스에서 SQL ID 추출
        List<String> serviceSqlIds = ServiceSqlIdExtractor.extractSqlIds("src/main/java/com/devkbil/mtssbj"); // Service 파일에서 SQL ID 추출

        // 3. 결과 저장 (병렬 검증)
        // SQL ID가 올바른지 Namespace 및 XML의 SQL ID와 비교
        Map<String, String> resultMap = verifySqlIds(
            serviceSqlIds, namespaces, sqlXmlIdsWithoutNamespace);

        // 4. 출력 및 결과 요약
        printResults(resultMap); // 검증 결과 출력 및 요약

        long endTime = System.currentTimeMillis(); // 처리 종료 시간 측정
        log.info("SQL ID 매칭 테스트 완료 - 처리 시간: {}ms", (endTime - startTime)); // 실행 시간 출력
    }

    private Map<String, String> verifySqlIds(List<String> serviceSqlIds, Set<String> namespaces, Set<String> xmlSqlIdsWithoutNamespace) {
        Map<String, String> resultMap = new ConcurrentHashMap<>(); // 검증 결과를 저장할 ConcurrentHashMap

        int batchSize = 100; // SQL ID를 처리할 배치 크기 설정
        List<List<String>> batches = partitionList(serviceSqlIds, batchSize); // SQL ID 목록을 배치 단위로 나눔

        // 병렬로 각 배치를 처리하며 SQL ID를 검증
        batches.parallelStream().forEach(batch -> batch.forEach(sqlId -> {
            if (sqlId.contains(".")) { // SQL ID에 Namespace가 포함된 경우
                String[] parts = sqlId.split("\\.", 2); // Namespace와 순수 SQL ID 분리
                String namespace = parts[0];
                String pureSqlId = parts[1];

                // Namespace와 SQL ID가 유효한지 검증
                if (!namespaces.contains(namespace)) { // Namespace가 없으면 실패 처리
                    resultMap.put(sqlId, ANSI_RED + " 실패 (Namespace 없음)" + ANSI_RESET);
                } else if (!xmlSqlIdsWithoutNamespace.contains(pureSqlId)) { // SQL ID가 없으면 실패 처리
                    resultMap.put(sqlId, ANSI_RED + " 실패 (SQL ID 없음)" + ANSI_RESET);
                } else {
                    resultMap.put(sqlId, "성공"); // 검증 성공
                }
            } else { // SQL ID에 Namespace가 없는 경우
                if (!xmlSqlIdsWithoutNamespace.contains(sqlId)) {
                    resultMap.put(sqlId, ANSI_RED + " 실패 (SQL ID 없음)" + ANSI_RESET); // SQL ID가 없으면 실패 처리
                } else {
                    resultMap.put(sqlId, "성공"); // 검증 성공
                }
            }
        }));
        return resultMap; // 검증 결과를 리턴
    }

    private <T> List<List<T>> partitionList(List<T> list, int size) {
        // 리스트를 고정된 크기의 서브리스트로 나눔
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            partitions.add(list.subList(i, Math.min(i + size, list.size()))); // 서브리스트 생성
        }
        return partitions; // 파티션 리스트 반환
    }

    private void printResults(Map<String, String> resultMap) {
        // 검증 결과 요약 출력
        long successCount = resultMap.values().stream().filter(result -> result.equals("성공")).count(); // 성공 건수
        long failureCount = resultMap.size() - successCount; // 실패 건수 계산

        resultMap.forEach((sqlId, result) -> log.info("SQL ID: {} -> 결과: {}", sqlId, result)); // 모든 결과 출력
        log.info("=========================");
        log.info("성공 건수: {}", successCount); // 성공 건수 출력
        log.info("실패 건수: {}", failureCount); // 실패 건수 출력
        log.info("=========================");
    }
}