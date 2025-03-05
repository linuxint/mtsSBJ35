package com.devkbil.mtssbj.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GradleDependencyChecker {

    private static final String MAVEN_METADATA_URL = "https://repo1.maven.org/maven2/%s/%s/maven-metadata.xml";

    public static void main(String[] args) {
        String gradleFilePath = "build.gradle"; // Gradle 파일 경로
        try {
            // build.gradle 파일 읽기
            String gradleContent = new String(Files.readAllBytes(Paths.get(gradleFilePath)));

            // dependencies 구문 파싱
            Pattern dependencyPattern = Pattern.compile("implementation\\s+'(.+?):(.+?):(.+?)'");
            Matcher matcher = dependencyPattern.matcher(gradleContent);

            System.out.println("Dependencies와 최신 버전 비교:");
            while (matcher.find()) {
                String groupId = matcher.group(1);
                String artifactId = matcher.group(2);
                String currentVersion = matcher.group(3);

                // Maven Central에서 마지막 버전 가져오기
                String latestVersion = fetchLatestVersion(groupId, artifactId);

                // 최신 버전 확인 후 현재 버전과 비교
                if (latestVersion != null && !currentVersion.equals(latestVersion)) {
                    System.out.printf(
                        "Dependency: %s:%s:%s -> 마지막 버전: %s%n",
                        groupId, artifactId, currentVersion, latestVersion);
                }
            }

        } catch (Exception e) {
            System.err.println("파일 읽기 중 오류 발생: " + e.getMessage());
        }
    }

    private static String fetchLatestVersion(String groupId, String artifactId) {
        try {
            // Maven Metadata URL 생성
            String groupPath = groupId.replace(".", "/");
            String metadataUrl = String.format(MAVEN_METADATA_URL, groupPath, artifactId);

            // 버전 목록 가져오기
            List<String> allVersions = fetchAllVersions(metadataUrl);

            if (!allVersions.isEmpty()) {
                // 가장 최신 버전을 반환
                return allVersions.get(allVersions.size() - 1);
            }
        } catch (Exception e) {
            System.err.println("Maven Metadata 조회 중 오류: " + e.getMessage());
        }

        return null; // 버전 조회 실패
    }

    private static List<String> fetchAllVersions(String metadataUrl) {
        List<String> versions = new ArrayList<>();
        try {
            // Metadata XML 파일 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(metadataUrl).openStream()));
            String content = reader.lines().reduce("", (acc, line) -> acc + line); // XML 내용 연결

            // 모든 <version> 태그 추출
            Pattern versionPattern = Pattern.compile("<version>(.*?)</version>");
            versionPattern.matcher(content).results().map(match -> match.group(1)).forEach(versions::add);

            // 버전 정렬 (오름차순, Semantic Versioning 기반)
            versions.sort(
                new Comparator<String>() {
                    @Override
                    public int compare(String v1, String v2) {
                        return compareVersions(v1, v2);
                    }
                });

        } catch (Exception e) {
            System.err.println("모든 버전 목록을 가져오는 중 오류 발견: " + e.getMessage());
        }
        return versions;
    }

    // 필요한 경우 문자열 버전을 숫자 및 프리릴리스 기반으로 비교 (Semantic Versioning)
    private static int compareVersions(String version1, String version2) {
        Version v1 = new Version(version1);
        Version v2 = new Version(version2);
        return v1.compareTo(v2);
    }

    // Version 클래스: Semantic Versioning 구현
    private static class Version implements Comparable<Version> {
        private final List<Integer> majorMinorPatch = new ArrayList<>();
        private final String preRelease;

        public Version(String version) {
            String[] parts = version.split("-", 2);
            String[] versionNumbers = parts[0].split("\\.");

            // 숫자 버전: Major, Minor, Patch 파싱
            for (String num : versionNumbers) {
                try {
                    majorMinorPatch.add(Integer.parseInt(num));
                } catch (NumberFormatException e) {
                    majorMinorPatch.add(0); // 문제가 있으면 기본값 0 설정
                }
            }

            // 프리릴리스 정보 (예: alpha, beta, RC)
            preRelease = parts.length > 1 ? parts[1] : null;
        }

        @Override
        public int compareTo(Version other) {
            // Major, Minor, Patch 비교
            for (int i = 0; i < Math.max(this.majorMinorPatch.size(), other.majorMinorPatch.size()); i++) {
                int thisPart = i < this.majorMinorPatch.size() ? this.majorMinorPatch.get(i) : 0;
                int otherPart = i < other.majorMinorPatch.size() ? other.majorMinorPatch.get(i) : 0;

                if (thisPart != otherPart) {
                    return Integer.compare(thisPart, otherPart);
                }
            }

            // 프리릴리스 정보 비교 (null > alpha > beta > RC 등)
            if (this.preRelease == null && other.preRelease == null) {
                return 0; // 동일
            }
            if (this.preRelease == null) {
                return 1; // null (릴리스) > 프리릴리스
            }
            if (other.preRelease == null) {
                return -1; // 릴리스 < 프리릴리스
            }

            // 프리릴리스 비교 (사전순으로 정렬)
            return this.preRelease.compareTo(other.preRelease);
        }
    }
}
