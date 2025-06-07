package com.devkbil.mtssbj.manager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 이 클래스는 @Controller 어노테이션이 있는 파일을 스캔하고 URL 매핑을 추출하는 유틸리티입니다.
 */
public class ControllerUrlScanner {

    private static final Pattern CONTROLLER_PATTERN = Pattern.compile("@(Controller|RestController)");
    private static final Pattern REQUEST_MAPPING_PATTERN = Pattern.compile("@(RequestMapping|GetMapping|PostMapping|PutMapping|DeleteMapping|PatchMapping)\\((?:.*?value\\s*=\\s*)?([\"'])(.*?)\\2.*?\\)");
    private static final Pattern CLASS_LEVEL_MAPPING_PATTERN = Pattern.compile("@RequestMapping\\((?:.*?value\\s*=\\s*)?([\"'])(.*?)\\1.*?\\)");

    public static void main(String[] args) {
        try {
            Map<String, List<String>> controllerUrls = scanControllers();
            printResults(controllerUrls);
        } catch (Exception e) {
            System.err.println("스캔 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * '@Controller' 어노테이션이 있는 모든 클래스를 스캔하고 URL 매핑을 추출합니다.
     * 
     * @return 컨트롤러 파일 이름과 URL 매핑 목록을 포함하는 맵
     * @throws IOException 파일 읽기 오류 발생 시
     */
    private static Map<String, List<String>> scanControllers() throws IOException {
        Map<String, List<String>> controllerUrls = new TreeMap<>();

        // 프로젝트 루트 디렉토리
        String projectRoot = System.getProperty("user.dir");
        Path sourcePath = Paths.get(projectRoot, "src", "main", "java");

        if (!Files.exists(sourcePath)) {
            System.err.println("소스 디렉토리를 찾을 수 없습니다: " + sourcePath);
            return controllerUrls;
        }

        // 모든 Java 파일 검색
        try (Stream<Path> paths = Files.walk(sourcePath)) {
            List<Path> javaFiles = paths
                .filter(Files::isRegularFile)
                .filter(path -> path.toString().endsWith(".java"))
                .collect(Collectors.toList());

            for (Path javaFile : javaFiles) {
                // 파일 내용 읽기
                String content = new String(Files.readAllBytes(javaFile), StandardCharsets.UTF_8);

                // @Controller 또는 @RestController 어노테이션이 있는지 확인
                Matcher controllerMatcher = CONTROLLER_PATTERN.matcher(content);
                if (controllerMatcher.find()) {
                    // URL 매핑 추출
                    List<String> urls = extractUrlMappings(content);
                    if (!urls.isEmpty()) {
                        // 파일 이름만 추출 (경로 제외)
                        String fileName = javaFile.getFileName().toString();
                        controllerUrls.put(fileName, urls);
                    }
                }
            }
        }

        return controllerUrls;
    }

    /**
     * 소스 파일 내용에서 URL 매핑을 추출합니다.
     * 
     * @param content 소스 파일 내용
     * @return URL 매핑 목록
     */
    private static List<String> extractUrlMappings(String content) {
        List<String> urls = new ArrayList<>();
        String classLevelMapping = "";

        // 클래스 레벨 매핑 추출
        Matcher classLevelMatcher = CLASS_LEVEL_MAPPING_PATTERN.matcher(content);
        if (classLevelMatcher.find()) {
            classLevelMapping = classLevelMatcher.group(2);
            if (classLevelMapping != null && !classLevelMapping.startsWith("/")) {
                classLevelMapping = "/" + classLevelMapping;
            }
            if (classLevelMapping != null && classLevelMapping.endsWith("/")) {
                classLevelMapping = classLevelMapping.substring(0, classLevelMapping.length() - 1);
            }
        }

        // 메소드 레벨 매핑 추출
        Matcher matcher = REQUEST_MAPPING_PATTERN.matcher(content);
        while (matcher.find()) {
            String mappingType = matcher.group(1);
            String url = matcher.group(3);

            if (url != null && !url.isEmpty()) {

                if (!url.startsWith("/")) {
                    url = "/" + url;
                }

                // 클래스 레벨 매핑과 메소드 레벨 매핑 결합
                if (!classLevelMapping.isEmpty()) {
                    // 메소드 레벨 매핑이 이미 클래스 레벨 매핑으로 시작하는 경우 중복 방지
                    if (!url.startsWith(classLevelMapping)) {
                        url = classLevelMapping + url;
                    }
                }

                urls.add(mappingType + ": " + url);
            }
        }

        return urls;
    }

    /**
     * 결과를 출력합니다.
     * 
     * @param controllerUrls 컨트롤러 파일 이름과 URL 매핑 목록을 포함하는 맵
     */
    private static void printResults(Map<String, List<String>> controllerUrls) {
        System.out.println("=== @Controller 어노테이션이 있는 파일 및 URL 매핑 ===");
        System.out.println();

        if (controllerUrls.isEmpty()) {
            System.out.println("@Controller 어노테이션이 있는 파일을 찾을 수 없습니다.");
            return;
        }

        int totalControllers = controllerUrls.size();
        int totalUrls = 0;

        for (Map.Entry<String, List<String>> entry : controllerUrls.entrySet()) {
            String fileName = entry.getKey();
            List<String> urls = entry.getValue();
            totalUrls += urls.size();

            System.out.println("파일: " + fileName);
            System.out.println("URL 매핑:");

            for (String url : urls) {
                System.out.println("  - " + url);
            }

            System.out.println();
        }

        System.out.println("=== 요약 ===");
        System.out.println("총 컨트롤러 수: " + totalControllers);
        System.out.println("총 URL 매핑 수: " + totalUrls);
    }
}
