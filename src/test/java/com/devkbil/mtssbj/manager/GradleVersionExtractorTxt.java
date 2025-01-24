package com.devkbil.mtssbj.manager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleVersionExtractorTxt {

    // Updated Pattern: Includes dependencies with or without versions
    private static final Pattern DEPENDENCY_PATTERN = Pattern.compile("['\"](\\S+:\\S+)(:\\S+)?['\"]");

    public static void main(String[] args) {
        String filePath = "build.gradle";

        // Map to store dependency updates
        Map<String, String> dependencyUpdates = new LinkedHashMap<>();

        try {
            List<String> dependencies = extractDependencies(filePath);
            for (String dependency : dependencies) {
                String currentVersion = getVersion(dependency);
                String latestVersion = findLatestVersion(dependency);

                if (latestVersion != null && !latestVersion.equals(currentVersion)) {
                    if (currentVersion == null) {
                        // Add unresolved versions with latest
                        dependencyUpdates.put(
                                dependency,
                                "Dependency without version -> Latest: " + appendLatestVersion(dependency, latestVersion)
                        );
                    } else {
                        // Add resolved versions
                        dependencyUpdates.put(
                                dependency,
                                "Current: " + dependency + " -> Latest: " + replaceVersion(dependency, latestVersion)
                        );
                    }
                } else if (latestVersion == null) {
                    dependencyUpdates.put(dependency, "Unable to verify dependency: " + dependency);
                } else {
                    //dependencyUpdates.put(dependency, "Dependency is already up-to-date: " + dependency);
                }
            }

            // Print all updates at the end
            System.out.println("\nDependency Updates:");
dependencyUpdates.entrySet().stream()
        .sorted(Map.Entry.comparingByValue((update1, update2) -> {
            if (update1.startsWith("Dependency without version")) {
                return -1;
            } else if (update2.startsWith("Dependency without version")) {
                return 1;
            }
            return 0;
        }))
        .forEachOrdered(entry -> System.out.println(entry.getValue()));

        } catch (IOException e) {
            System.err.println("Error reading the gradle file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static List<String> extractDependencies(String filePath) throws IOException {
        List<String> dependencies = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
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
        if (parts.length < 2) {
            return null; // invalid dependency format
        }
        String groupId = parts[0];
        String artifactId = parts[1];

        try {
            String encodedGroupId = URLEncoder.encode(groupId, StandardCharsets.UTF_8.toString());
            String encodedArtifactId = URLEncoder.encode(artifactId, StandardCharsets.UTF_8.toString());
            String urlString = String.format("https://search.maven.org/solrsearch/select?q=g:%s%%20AND%%20a:%s&rows=1&wt=json", encodedGroupId, encodedArtifactId);

            URL url = (new URI(urlString)).toURL();

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
                    JSONArray docs = jsonObject.getJSONObject("response").getJSONArray("docs");
                    if (docs.length() > 0) {
                        return docs.getJSONObject(0).getString("latestVersion");
                    }
                }
            } else {
                System.err.println("Server returned non-OK status: " + responseCode);
            }
        } catch (IOException | JSONException | URISyntaxException e) {
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

    private static String appendLatestVersion(String dependency, String newVersion) {
        String[] parts = dependency.split(":");
        if (parts.length == 2) {
            return String.join(":", parts[0], parts[1], newVersion);
        }
        return dependency;
    }
}