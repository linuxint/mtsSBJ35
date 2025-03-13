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

/**
 * GradleDependencyChecker 클래스는 Gradle 빌드 파일을 분석하여 의존성 버전을 식별하고
 * Maven Central Repository에서 사용 가능한 최신 버전과 지정된 버전을 비교합니다.
 * <p>
 * 프로젝트 주요 기능:
 * - Gradle 빌드 파일을 파싱해 선언된 의존성을 찾는다.
 * - Maven Central에서 의존성의 최신 메타 데이터를 가져온다.
 * - 현재 선언된 버전과 최신 버전을 비교하고 오래된 의존성을 찾아낸다.
 * - 버전을 Semantic Versioning 규칙에 따라 정렬한다.
 */
public class GradleDependencyChecker {

    private static final String MAVEN_METADATA_URL = "https://repo1.maven.org/maven2/%s/%s/maven-metadata.xml";

    /**
     * 프로그램 진입점. `build.gradle` 파일을 파싱하여 의존성을 확인하고
     * Maven Central에 사용 가능한 최신 버전과 비교합니다.
     *
     * @param args 명령줄 인수; 이 구현에서는 사용되지 않음.
     */
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
                String groupId = matcher.group(1); // groupId 추출
                String artifactId = matcher.group(2); // artifactId 추출
                String currentVersion = matcher.group(3); // 현재 버전 추출

                // Maven Central에서 최신 버전 가져오기
                String latestVersion = fetchLatestVersion(groupId, artifactId);

                // 최신 버전과 현재 버전 비교
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

    /**
     * Maven Central 메타 데이터 저장소에서 Maven 아티팩트의 최신 버전을 가져온다.
     * <p>
     * groupId 및 artifactId를 기반으로 Maven 메타 데이터 URL을 생성하고,
     * 사용 가능한 모든 버전을 가져온 후 최신 버전을 반환한다.
     * 과정에서 오류가 발생하거나 버전을 찾지 못하는 경우 null을 반환한다.
     *
     * @param groupId    Maven 아티팩트의 groupId
     * @param artifactId Maven 아티팩트의 artifactId
     * @return 아티팩트의 최신 버전 문자열 또는 없을 경우 null
     */
    private static String fetchLatestVersion(String groupId, String artifactId) {
        try {
            // Maven Metadata URL 생성
            String groupPath = groupId.replace(".", "/");
            String metadataUrl = String.format(MAVEN_METADATA_URL, groupPath, artifactId);

            // 버전 목록 가져오기
            List<String> allVersions = fetchAllVersions(metadataUrl);

            if (!allVersions.isEmpty()) {
                // 최신 버전 반환
                return allVersions.get(allVersions.size() - 1);
            }
        } catch (Exception e) {
            System.err.println("Maven Metadata 조회 중 오류: " + e.getMessage());
        }

        return null; // 조회 실패 시 null 반환
    }

    /**
     * 제공된 메타 데이터 URL에서 Maven 아티팩트의 모든 사용 가능한 버전을 가져온다.
     * <p>
     * 지정된 URL에서 Maven 메타 데이터 XML 파일을 가져와
     * <version> 태그에 포함된 모든 버전을 추출하고,
     * Semantic Versioning에 따라 정렬된 버전 목록을 반환한다.
     * 오류가 발생하면 빈 목록 반환.
     *
     * @param metadataUrl 버전 정보를 담고 있는 Maven 메타 데이터 파일의 URL
     * @return 버전 문자열 리스트 (오름차순 정렬), 오류가 발생할 경우 빈 리스트 반환
     */
    private static List<String> fetchAllVersions(String metadataUrl) {
        List<String> versions = new ArrayList<>();
        try {
            // Metadata XML 파일 읽기
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(metadataUrl).openStream()));
            String content = reader.lines().reduce("", (acc, line) -> acc + line); // XML 내용을 하나의 문자열로 연결

            // <version> 태그로 나열된 값 추출
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
            System.err.println("모든 버전 목록을 가져오는 중 오류: " + e.getMessage());
        }
        return versions;
    }

    // 필요한 경우 문자열 버전을 숫자 및 프리릴리스 기반으로 비교 (Semantic Versioning)

    /**
     * 두 개의 semantic version 문자열을 비교하여 순서를 결정한다.
     * Major, Minor, Patch, 그리고 프리릴리스 요소를 기준으로 비교한다.
     *
     * @param version1 비교할 첫 번째 버전 문자열
     * @param version2 비교할 두 번째 버전 문자열
     * @return 첫 번째가 두 번째보다 작으면 음수, 크면 양수, 같으면 0.
     */
    private static int compareVersions(String version1, String version2) {
        Version v1 = new Version(version1); // 첫 번째 버전 파싱
        Version v2 = new Version(version2); // 두 번째 버전 파싱
        return v1.compareTo(v2);
    }

    // Version 클래스: Semantic Versioning 구현

    /**
     * Semantic Version을 표현하며 파싱, 비교, 관리 기능을 제공한다.
     * 이 클래스는 일반적인 semantic version과 프리릴리스 컴포넌트를 지원한다.
     * <p>
     * Version 클래스의 인스턴스는 immutable하며
     * Comparable 인터페이스를 사용하여 비교 가능하다.
     */
    private static class Version implements Comparable<Version> {
        private final List<Integer> majorMinorPatch = new ArrayList<>(); // 주요 버전, 부 버전, 패치 버전
        private final String preRelease; // 프리릴리스 정보 포함

        public Version(String version) {
            String[] parts = version.split("-", 2); // 프리릴리스 분리
            String[] versionNumbers = parts[0].split("\\."); // major.minor.patch 구분

            // 버전 번호 파싱
            for (String num : versionNumbers) {
                try {
                    majorMinorPatch.add(Integer.parseInt(num));
                } catch (NumberFormatException e) {
                    majorMinorPatch.add(0); // 형식이 잘못된 경우 기본값 0 설정
                }
            }

            // 프리릴리스 정보 설정
            preRelease = parts.length > 1 ? parts[1] : null;
        }

        /**
         * 현재 버전 객체와 다른 버전 객체를 비교하여
         * 순서를 정한다. Semantic Versioning 규칙을 따름.
         * Major, Minor, Patch, 그리고 프리릴리스 정보를 고려한다.
         *
         * @param other 비교 대상 버전 객체
         * @return 현재 버전 < other이면 음수, > other이면 양수, 동일하면 0 반환
         */
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

            // 프리릴리스 정보 사전순 정렬
            return this.preRelease.compareTo(other.preRelease);
        }
    }
}