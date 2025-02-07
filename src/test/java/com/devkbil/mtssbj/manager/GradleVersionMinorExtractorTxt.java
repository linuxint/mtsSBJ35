package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
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

public class GradleVersionMinorExtractorTxt {

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+:\\S+)['\"]");

    @Test
    public void main() {
        String filePath = "build.gradle";
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();
        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

        try {
            List<String> dependencies = extractDependencies(filePath);

            // 병렬로 의존성 처리
            List<CompletableFuture<Void>> futures = dependencies.stream().map(dependency ->
                    CompletableFuture.runAsync(() -> {
                        String currentVersion = getVersion(dependency);
                        if (currentVersion != null) {
                            String latestVersion = findLatestVersion(dependency);
                            if (latestVersion != null && isMinorUpdate(currentVersion, latestVersion)) {
                                synchronized (dependencyUpdates) {
                                    dependencyUpdates.put(dependency,
                                            String.format("Current: %s -> Latest Minor: %s", dependency, replaceVersion(dependency, latestVersion)));
                                }
                            }
                        }
                    }, executor)
            ).collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

            // 결과 출력
            System.out.println("\nDependency Updates:");
            dependencyUpdates.forEach((key, value) -> System.out.println(value));

        } catch (IOException e) {
            System.err.println("Error reading the Gradle file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    private static List<String> extractDependencies(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                    .map(String::trim)
                    .filter(line -> !line.startsWith("//") && !line.startsWith("/*") && !line.startsWith("*/"))
                    .map(line -> DEPENDENCY_PATTERN.matcher(line))
                    .filter(Matcher::find)
                    .map(m -> m.group(1))
                    .collect(Collectors.toList());
        }
    }

    private static String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) return null;
        String urlString = String.format("https://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json",
                encode(parts[0]), encode(parts[1]));

        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URL(urlString).openStream()))) {
            JSONObject jsonObject = new JSONObject(in.lines().collect(Collectors.joining()));
            JSONArray docs = jsonObject.getJSONObject("response").optJSONArray("docs");
            if (docs != null && docs.length() > 0) {
                return docs.getJSONObject(0).getString("latestVersion");
            }
        } catch (Exception e) {
            System.err.println("Error occurred while fetching the latest version: " + e.getMessage());
        }
        return null;
    }

    private static String getVersion(String dependency) {
        String[] parts = dependency.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    private static String replaceVersion(String dependency, String newVersion) {
        String[] parts = dependency.split(":");
        return parts.length == 3 ? String.format("%s:%s:%s", parts[0], parts[1], newVersion) : dependency;
    }

    private static boolean isMinorUpdate(String currentVersion, String latestVersion) {
        String[] current = currentVersion.split("\\.");
        String[] latest = latestVersion.split("\\.");
        return current.length >= 2 && latest.length >= 2 && current[0].equals(latest[0]) &&
                Integer.parseInt(latest[1]) > Integer.parseInt(current[1]);
    }

    private static String encode(String text) {
        try {
            return URLEncoder.encode(text, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            return text;
        }
    }
}
