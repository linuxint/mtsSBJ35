package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleVersionMinorExtractorFileWrite {

    // 의존성 추출을 위한 패턴 정의
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+:\\S+)['\"]");

    @Test
    public void main() {
        String filePath = "build.gradle"; // 수정 대상 파일 경로
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();

        try {
            // build.gradle 파일에서 의존성 추출
            List<String> dependencies = extractDependencies(filePath);

            // 의존성별로 최신 버전 확인
            for (String dependency : dependencies) {
                String currentVersion = getVersion(dependency);
                if (currentVersion == null) {
                    // 버전 정보가 없는 경우 스킵
                    continue;
                }

                String latestVersion = findLatestVersion(dependency);
                if (latestVersion != null && isMinorUpdate(currentVersion, latestVersion)) {
                    // 업데이트 가능성이 있는 경우 기록
                    dependencyUpdates.put(dependency, replaceVersion(dependency, latestVersion));
                }
            }

            // build.gradle 파일 수정
            if (!dependencyUpdates.isEmpty()) {
                updateBuildGradleFile(filePath, dependencyUpdates);

                System.out.println("\nDependency Updates Applied:");
                dependencyUpdates.forEach((key, value) -> System.out.printf("Updated: %s -> %s%n", key, value));
            } else {
                System.out.println("\nNo minor updates found for dependencies.");
            }

        } catch (IOException e) {
            System.err.println("Error processing the Gradle file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * build.gradle 파일에서 의존성을 추출합니다.
     */
    private static List<String> extractDependencies(String filePath) throws IOException {
        List<String> dependencies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean inBlockComment = false;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // 블록 주석 처리
                if (line.startsWith("/*")) inBlockComment = true;
                if (line.endsWith("*/")) inBlockComment = false;

                // 주석 제외
                if (inBlockComment || line.startsWith("//")) continue;

                // 의존성 추출
                Matcher matcher = DEPENDENCY_PATTERN.matcher(line);
                if (matcher.find()) {
                    dependencies.add(matcher.group(1));
                }
            }
        }

        return dependencies;
    }

    /**
     * Maven 레지스트리를 사용하여 최신 버전을 찾습니다.
     */
    private static String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) return null; // 의존성 형식이 잘못된 경우

        String groupId = parts[0];
        String artifactId = parts[1];

        try {
            String encodedGroupId = URLEncoder.encode(groupId, StandardCharsets.UTF_8.toString());
            String encodedArtifactId = URLEncoder.encode(artifactId, StandardCharsets.UTF_8.toString());
            String urlString = String.format(
                    "https://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json",
                    encodedGroupId, encodedArtifactId
            );

            URL url = createSafeURL(urlString);
            if (url == null) return null;

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

    // Minor 업데이트인지 확인합니다.
    private static boolean isMinorUpdate(String currentVersion, String latestVersion) {
        String[] currentParts = currentVersion.split("\\.");
        String[] latestParts = latestVersion.split("\\.");

        if (currentParts.length < 2 || latestParts.length < 2) return false;

        // Major version은 같아야 함
        if (!currentParts[0].equals(latestParts[0])) return false;

        // Minor version이 증가해야 함
        return !currentParts[1].equals(latestParts[1]) &&
                Integer.parseInt(latestParts[1]) > Integer.parseInt(currentParts[1]);
    }

    // build.gradle 파일을 업데이트합니다.
    private static void updateBuildGradleFile(String filePath, Map<String, String> dependencyUpdates) throws IOException {
        File inputFile = new File(filePath);
        File tempFile = new File("build.gradle.tmp");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();

                // 업데이트할 의존성이 있는지 확인
                for (Map.Entry<String, String> entry : dependencyUpdates.entrySet()) {
                    String currentDependency = entry.getKey();
                    String updatedDependency = entry.getValue();

                    if (trimmedLine.contains(currentDependency)) {
                        line = line.replace(currentDependency, updatedDependency);
                        break;
                    }
                }

                writer.write(line);
                writer.newLine();
            }
        }

        // 원본 파일을 덮어씌움
        if (!tempFile.renameTo(inputFile)) {
            System.err.println("Failed to update the build.gradle file.");
        }
    }
}