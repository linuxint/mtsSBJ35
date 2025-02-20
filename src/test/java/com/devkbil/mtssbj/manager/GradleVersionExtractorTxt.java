package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GradleVersionExtractorTxt {

    private final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+)(:\\S+)?['\"]");

    @Test
    public void main() {
        String filePath = "build.gradle";
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        try {
            List<String> dependencies = extractDependencies(filePath);

            List<CompletableFuture<Void>> tasks = dependencies.stream().map(dependency ->
                    CompletableFuture.runAsync(() -> {
                        String currentVersion = getVersion(dependency);
                        String latestVersion = findLatestVersion(dependency);

                        if (latestVersion != null) {
                            if (currentVersion == null) {
                                dependencyUpdates.put(dependency, String.format("No version -> Latest: %s", latestVersion));
                            } else if (!currentVersion.equals(latestVersion)) {
                                dependencyUpdates.put(dependency, String.format("Current: %s -> Latest: %s", dependency, latestVersion));
                            }
                        }
                    }, executor)
            ).collect(Collectors.toList());

            CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).join();

            System.out.println("\nDependency Updates:");
            dependencyUpdates.forEach((dependency, update) -> System.out.println(update));

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private List<String> extractDependencies(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                    .map(line -> DEPENDENCY_PATTERN.matcher(line))
                    .filter(Matcher::find)
                    .map(m -> m.group(1))
                    .collect(Collectors.toList());
        }
    }

    private String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) return null;
        String url = String.format("https://search.maven.org/solrsearch/select?q=g:%s%%20a:%s&rows=1&wt=json",
                URLEncoder.encode(parts[0], StandardCharsets.UTF_8), URLEncoder.encode(parts[1], StandardCharsets.UTF_8));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()))) {
            JSONObject json = new JSONObject(in.lines().collect(Collectors.joining()));
            JSONArray docs = json.getJSONObject("response").getJSONArray("docs");
            if (docs.length() == 0) {
                return null;
            }
            return docs.getJSONObject(0).getString("latestVersion");
        } catch (Exception e) {
            System.err.println("Failed HTTP Request: " + e.getMessage());
        }
        return null;
    }

    private String getVersion(String dependency) {
        String[] parts = dependency.split(":");
        return parts.length > 2 ? parts[2] : null;
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
}