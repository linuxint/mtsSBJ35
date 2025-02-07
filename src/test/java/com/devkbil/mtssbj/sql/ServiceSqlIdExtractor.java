package com.devkbil.mtssbj.sql;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ServiceSqlIdExtractor {

    // SQL Session에서 사용된 SQL ID 패턴
    private static final Pattern SQL_ID_PATTERN = Pattern.compile("sqlSession\\.(\\w+)\\(\"([^\"]+)\"");

    // @Service 어노테이션 검출 패턴
    private static final Pattern SERVICE_ANNOTATION_PATTERN = Pattern.compile("@Service");

    /**
     * @Service가 있는 *Service.java 파일에서 SQL ID 추출
     * @param servicePath 서비스 경로 (예: src/main/java/com/devkbil/mtssbj)
     * @return 추출된 SQL ID 목록
     */
    public static List<String> extractSqlIds(String servicePath) {
        List<String> sqlIds = new ArrayList<>();

        try {
            Files.walk(Paths.get(servicePath))
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith("Service.java"))
                    .forEach(path -> {
                        try {
                            String content = Files.readString(path);

                            // @Service 어노테이션이 적용된 파일만 포함
                            if (isServiceClass(content)) {
                                Matcher matcher = SQL_ID_PATTERN.matcher(content);
                                while (matcher.find()) {
                                    sqlIds.add(matcher.group(2));
                                }
                            }
                        } catch (IOException e) {
                            log.error("파일 읽기 실패: {}", path, e);
                        }
                    });
        } catch (IOException e) {
            log.error("서비스 경로 처리 실패: {}", servicePath, e);
        }
        return sqlIds;
    }

    /**
     * @Service 어노테이션이 있는 클래스인지 확인
     * @param content 파일 내용
     * @return @Service 어노테이션 존재 여부
     */
    private static boolean isServiceClass(String content) {
        return SERVICE_ANNOTATION_PATTERN.matcher(content).find();
    }
}