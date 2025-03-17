package com.devkbil.mtssbj.manager.sql;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceSqlIdExtractor {

    // SQL ID 탐지 패턴: sqlSession.<method>("SQL_ID") 형식의 문자열에서 SQL_ID를 추출
    private static final Pattern SQL_ID_PATTERN = Pattern.compile("sqlSession\\.(\\w+)\\(\"([^\"]+)\"");

    // @Service 어노테이션 패턴: 특정 클래스가 @Service 어노테이션을 가지고 있는지 확인
    private static final Pattern SERVICE_ANNOTATION_PATTERN = Pattern.compile("@Service");

    /**
     * 주어진 서비스 경로 내에서 모든 SQL ID를 추출하는 메서드
     * @param servicePath 서비스 관련 파일들이 위치한 디렉토리 경로
     * @return 추출된 SQL ID 목록
     */
    public static List<String> extractSqlIds(String servicePath) {
        // Thread-safe한 리스트를 생성하여 병렬 환경에서도 데이터 경쟁을 방지
        List<String> sqlIds = Collections.synchronizedList(new ArrayList<>());

        try {
            // 디렉토리 내의 파일들을 순회하며 Service 파일을 처리
            Files.walk(Paths.get(servicePath)) // 지정된 경로의 파일들을 재귀적으로 탐색
                    .parallel() // 병렬 처리 설정
                    .filter(Files::isRegularFile) // 파일만 필터링 (디렉토리는 제외)
                    .filter(path -> path.toString().endsWith("Service.java")) // 파일 이름이 "Service.java"로 끝나는 경우만 처리
                    .forEach(path -> processServiceFile(path, sqlIds)); // 각 파일을 처리하고 SQL ID를 추출
        } catch (IOException e) {
            // 경로 처리 중 예외 발생 시 로그 기록
            log.error("서비스 경로 처리 실패: {}", servicePath, e);
        }
        return sqlIds; // 추출된 SQL ID 목록 반환
    }

    /**
     * 개별 Service 파일을 처리하여 SQL ID를 추출하는 메서드
     * @param path 파일 경로
     * @param sqlIds 추출된 SQL ID를 저장할 리스트
     */
    private static void processServiceFile(Path path, List<String> sqlIds) {
        try {
            // 파일 내용을 문자열로 읽기
            String content = Files.readString(path);

            // 파일이 @Service 어노테이션을 포함하는 경우에만 처리
            if (isServiceClass(content)) {
                // SQL ID 패턴 매칭
                Matcher matcher = SQL_ID_PATTERN.matcher(content);
                while (matcher.find()) {
                    // 매칭된 SQL ID를 리스트에 추가 (group(2)에서 SQL ID 추출)
                    sqlIds.add(matcher.group(2));
                }
            }
        } catch (IOException e) {
            // 파일 읽기 실패 시 로그 기록
            log.error("파일 읽기 실패: {}", path, e);
        }
    }

    /**
     * 클래스 파일이 @Service 어노테이션을 가지고 있는지 확인
     * @param content 파일 내용
     * @return @Service 어노테이션 존재 여부
     */
    private static boolean isServiceClass(String content) {
        // 파일 내용에서 @Service 패턴을 탐지하여 존재 여부 반환
        return SERVICE_ANNOTATION_PATTERN.matcher(content).find();
    }
}