package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GradleVersionMinorExtractorTxt {

    // 의존성 추출을 위한 패턴 정의 (정규식 캐싱)
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+:\\S+)['\"]");

    // 병렬 처리용 스레드 풀
    private static final ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Test
    public void main() {
        String filePath = "build.gradle"; // 수정 대상 파일 경로
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();

        try {
            // 1. build.gradle 파일에서 의존성 추출
            List<String> dependencies = extractDependencies(filePath);

            // 2. 최신 버전 병렬 확인
            List<CompletableFuture<Void>> futures = dependencies.stream().map(dependency ->
                CompletableFuture.runAsync(() -> {
                    String currentVersion = getVersion(dependency);
                    if (currentVersion == null) return;

                    String latestVersion = findLatestVersion(dependency);
                    if (latestVersion != null && isMinorUpdate(currentVersion, latestVersion)) {
                        synchronized (dependencyUpdates) { // 멀티스레드 환경에서 공유 Map 보호
                            dependencyUpdates.put(dependency, replaceVersion(dependency, latestVersion));
                        }
                    }
                }, executor)
            ).collect(Collectors.toList());

            // 모든 작업 완료 대기
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 3. build.gradle 파일 수정
            if (!dependencyUpdates.isEmpty()) {
                //updateBuildGradleFile(filePath, dependencyUpdates);

                System.out.println("\nDependency Updates Applied:");
                dependencyUpdates.forEach((key, value) -> System.out.printf("Updated: %s -> %s%n", key, value));
            } else {
                System.out.println("\nNo minor updates found for dependencies.");
            }

        } catch (IOException e) {
            System.err.println("Error processing the Gradle file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown(); // 스레드풀 종료
        }
    }

    /** ⬇️ 최적화된 의존성 추출 */
    private static List<String> extractDependencies(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                .map(String::trim)
                .filter(line -> !line.startsWith("//")) // 주석 제거
                .filter(line -> !line.startsWith("/*") && !line.startsWith("*/")) // 블록 주석
                .map(line -> DEPENDENCY_PATTERN.matcher(line))
                .filter(Matcher::find)
                .map(matcher -> matcher.group(1))
                .collect(Collectors.toList());
        }
    }

    /** ⬇️ 병렬 처리 지원 (HTTP 요청) */
    private static String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) return null;

        String groupId = parts[0];
        String artifactId = parts[1];

        try {
            String urlString = String.format(
                "https://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json",
                URLEncoder.encode(groupId, StandardCharsets.UTF_8),
                URLEncoder.encode(artifactId, StandardCharsets.UTF_8)
            );

            HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                String jsonResponse = new BufferedReader(new InputStreamReader(connection.getInputStream()))
                    .lines().collect(Collectors.joining());

                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray docs = jsonObject.getJSONObject("response").optJSONArray("docs");
                System.out.println(docs.toString());
                if (docs != null && docs.length() > 0) {
                    return docs.getJSONObject(0).getString("latestVersion");
                }
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error occurred while fetching the latest version: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    /** ⬇️ 빌드 파일 업데이트 최적화 (읽기/쓰기 최소화) */
    private static void updateBuildGradleFile(String filePath, Map<String, String> dependencyUpdates) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(filePath), StandardCharsets.UTF_8);
        List<String> updatedLines = new ArrayList<>();

        for (String line : lines) {
            String updatedLine = line;
            for (Map.Entry<String, String> entry : dependencyUpdates.entrySet()) {
                if (line.contains(entry.getKey())) {
                    updatedLine = line.replace(entry.getKey(), entry.getValue());
                    break;
                }
            }
            updatedLines.add(updatedLine);
        }

        Files.write(Path.of(filePath), updatedLines, StandardCharsets.UTF_8);
    }

    // 안전한 URL 생성 메서드
    private static URL createSafeURL(String urlString) {
        try {
            URI uri = URI.create(urlString);
            return uri.toURL();
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Invalid URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // 의존성에서 현재 버전을 가져옵니다.
    private static String getVersion(String dependency) {
        String[] parts = dependency.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    // 의존성의 새 버전으로 업데이트합니다.
    private static String replaceVersion(String dependency, String newVersion) {
        String[] parts = dependency.split(":");
        if (parts.length < 3) return dependency;
        return String.join(":", parts[0], parts[1], newVersion);
    }

    // 숫자 부분만 추출하는 헬퍼 메서드
    private static int extractNumeric(String str) {
        String numeric = str.replaceAll("[^0-9]", "");
        return numeric.isEmpty() ? 0 : Integer.parseInt(numeric);
    }

    private static boolean isMinorUpdate(String currentVersion, String latestVersion) {
        String[] current = currentVersion.split("\\.");
        String[] latest = latestVersion.split("\\.");

        // 버전 문자열이 적어도 "주버전.부버전" 형태여야 함
        if (current.length < 2 || latest.length < 2) {
            return false;
        }

        // 주버전이 동일한 경우에만 부버전을 비교
        if (!current[0].equals(latest[0])) {
            return false;
        }

        int currentMinor = extractNumeric(current[1]);
        int latestMinor = extractNumeric(latest[1]);

        return latestMinor > currentMinor;
    }
}