package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class GradleVersionExtractor {

    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"]([^'\"]+:[^'\"]+(:[^'\"]+)?)['\"]");

    @Test
    public void main() {
        String filePath = "build.gradle";
        List<String> dependencies = extractDependenciesWithVersions(filePath);
        dependencies.parallelStream()
                    .map(dependency -> {
                        String latest = findLatestVersion(dependency);
                        return (latest != null && !latest.equals(getVersion(dependency)))
                               ? replaceVersion(dependency, latest)
                               : dependency;
                    })
                    .forEach(System.out::println); // 최종 결과 출력
    }

    private List<String> extractDependenciesWithVersions(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            return reader.lines()
                         .map(line -> DEPENDENCY_PATTERN.matcher(line))
                         .filter(Matcher::find)
                         .map(m -> m.group(1))
                         .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String findLatestVersion(String dependency) {
        String[] parts = dependency.split(":");
        if (parts.length < 2) {
            return null; // invalid dependency format
        }

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

    private static String getVersion(String dependency) {
        String[] parts = dependency.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    private static String replaceVersion(String dependency, String newVersion) {
        String[] parts = dependency.split(":");
        if (parts.length < 3) {
            return dependency;
        }
        return String.join(":", parts[0], parts[1], newVersion);
    }

    private static List<String> updateDependenciesInFile(String filePath, List<String> updatedDependencies) throws IOException {
        List<String> updatedContent = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = now.format(formatter);

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int dependencyIndex = 0;
            while ((line = reader.readLine()) != null) {
                Matcher matcher = DEPENDENCY_PATTERN.matcher(line);
                if (matcher.find()) {
                    String updatedLine = line.replace(matcher.group(1), updatedDependencies.get(dependencyIndex++));
                    if (!matcher.group(1).equals(updatedDependencies.get(dependencyIndex - 1))) {
                        updatedLine += " // " + formattedDate;
                    }
                    updatedContent.add(updatedLine);
                } else {
                    updatedContent.add(line);
                }
            }
        }
        return updatedContent;
    }

    private static void writeUpdatedFile(String filePath, List<String> updatedContent) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : updatedContent) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}