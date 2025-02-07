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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleVersionMinorExtractorTxt {

    // Pattern to extract dependencies with valid versions
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+:\\S+)['\"]");

    @Test
    public void main() {
        String filePath = "build.gradle";
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();

        try {
            List<String> dependencies = extractDependencies(filePath);

            for (String dependency : dependencies) {
                String currentVersion = getVersion(dependency);
                if (currentVersion == null) {
                    // Skip dependencies without explicit versions
                    continue;
                }

                String latestVersion = findLatestVersion(dependency);
                if (latestVersion != null && isMinorUpdate(currentVersion, latestVersion)) {
                    dependencyUpdates.put(
                            dependency,
                            "Current: " + dependency + " -> Latest Minor: " + replaceVersion(dependency, latestVersion)
                    );
                }
            }

            // Print all updates at the end
            System.out.println("\nDependency Updates:");
            dependencyUpdates.forEach((key, value) -> System.out.println(value));

        } catch (IOException e) {
            System.err.println("Error reading the Gradle file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> extractDependencies(String filePath) throws IOException {
        List<String> dependencies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inBlockComment = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Handle block comments
                if (line.startsWith("/*")) inBlockComment = true;
                if (line.endsWith("*/")) inBlockComment = false;

                // Skip lines inside block comments or single-line comments
                if (inBlockComment || line.startsWith("//")) continue;

                // Extract valid dependencies
                Matcher matcher = DEPENDENCY_PATTERN.matcher(line);
                if (matcher.find()) {
                    dependencies.add(matcher.group(1));
                }
            }
        }

        return dependencies;
    }

    private static String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) return null; // Invalid dependency format

        String groupId = parts[0];
        String artifactId = parts[1];

        try {
            String encodedGroupId = URLEncoder.encode(groupId, StandardCharsets.UTF_8.toString());
            String encodedArtifactId = URLEncoder.encode(artifactId, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json",
                    encodedGroupId, encodedArtifactId
            );

            URL url = createSafeURL(urlString); // 변경된 URL 생성 방식
            if (url == null) return null; // URL이 유효하지 않을 경우 처리

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    StringBuilder response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray docs = jsonObject.getJSONObject("response").optJSONArray("docs");
                    if (docs != null && docs.length() > 0) {
                        return docs.getJSONObject(0).getString("latestVersion");
                    }
                }
            } else {
                System.err.println("Server returned non-OK status: " + responseCode);
            }
        } catch (IOException | JSONException e) {
            System.err.println("Error occurred while fetching the latest version: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    // 추가된 안전한 URL 생성 메서드
    private static URL createSafeURL(String urlString) {
        try {
            // URI를 먼저 사용하여 유효성 검사 및 URL 변환
            URI uri = URI.create(urlString);
            return uri.toURL();
        } catch (IllegalArgumentException | IOException e) {
            System.err.println("Invalid URL: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static String getVersion(String dependency) {
        String[] parts = dependency.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    private static String replaceVersion(String dependency, String newVersion) {
        String[] parts = dependency.split(":");
        if (parts.length < 3) return dependency;
        return String.join(":", parts[0], parts[1], newVersion);
    }

    private static boolean isMinorUpdate(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        if (currentParts.length < 2 || latestParts.length < 2) {
            return false; // Invalid version format
        }

        // Major version must match
        if (!currentParts[0].equals(latestParts[0])) {
            return false;
        }

        // Minor version must increase
        if (!currentParts[1].equals(latestParts[1]) && Integer.parseInt(latestParts[1]) > Integer.parseInt(currentParts[1])) {
            return true;
        }

        return false;
    }
}