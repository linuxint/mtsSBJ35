package com.devkbil.mtssbj.manager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.common.base.Splitter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;

/**
 * Gradle 빌드 파일에서 의존성을 파싱하고,
 * Maven Central에서 최신 버전을 확인하여
 * 의존성이 최신인지 아니면 구버전인지 결과를 표시하는 기능을 제공합니다.
 * <p>
 * 이 유틸리티는 병렬 처리를 위해 스레드 풀을 사용하며,
 * 네트워크 실패를 처리하기 위해 Maven 메타데이터 요청을 반복 실행하고
 * 중복 확인을 방지하기 위해 캐싱을 활용합니다.
 */
public class GradleDependencyChecker {


    // ANSI 컬러 코드 (콘솔 출력을 위한 색상 설정)
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";

    // 프로세스 병렬화를 위한 스레드 풀 구성
    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

    // 의존성 버전에 대한 캐시를 저장 (이미 확인된 의존성이 저장될 맵)
    private static final Map<String, String> CACHE = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // 프로그램 시작 시간 기록

        String gradleFilePath = "build.gradle"; // 대상 Gradle 파일 경로

        // 1. Gradle 파일에서 의존성 목록 추출
        List<String> dependencies = parseDependencies(gradleFilePath);
        // 1-1. Gradle 파일에서 플러그인 목록 추출
        List<PluginInfo> plugins = parsePlugins(gradleFilePath);

        // 2. 의존성 검사 수행 (병렬로 처리)
        List<DependencyCheckResult> results = checkDependenciesInParallel(dependencies);
        // 2-1. 플러그인 검사 수행 (병렬로 처리)
        List<DependencyCheckResult> pluginResults = checkPluginsInParallel(plugins);

        // 3. 검사 결과를 출력
        printResults(results);
        printPluginResults(pluginResults);

        // 스레드 풀 종료 (자원 반환)
        THREAD_POOL.shutdown();

        long endTime = System.currentTimeMillis(); // 프로그램 종료 시간 기록
        long elapsedTime = endTime - startTime; // 작업 시간 계산

        // 프로그램 실행 시간 출력
        System.out.println("\n=== 프로그램 수행 시간 ===");
        System.out.printf("전체 작업 소요 시간: %d ms (%.2f초)%n", elapsedTime, elapsedTime / 1000.0);
    }

    /**
     * build.gradle 파일에서 의존성을 파싱해 리스트로 반환
     *
     * @param gradleFilePath build.gradle 파일의 경로
     * @return 파싱된 의존성 정보 리스트
     */
    private static List<String> parseDependencies(String gradleFilePath) {
        List<String> dependencies = new ArrayList<>();

        // 의존성을 추출하기 위한 정규표현식 패턴
        Pattern dependencyPattern = Pattern.compile("(implementation|api|runtimeOnly)\\s+'([\\w.-]+):([\\w.-]+):([\\w.-]+)'");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(gradleFilePath), StandardCharsets.UTF_8)) {
            String line;

            // 파일에서 한 줄씩 읽어 의존성 추출
            while ((line = reader.readLine()) != null) {
                Matcher matcher = dependencyPattern.matcher(line);

                if (matcher.find()) {
                    String groupId = matcher.group(2);
                    String artifactId = matcher.group(3);
                    String version = matcher.group(4);

                    // 추출된 의존성을 리스트에 추가
                    dependencies.add(String.format("%s:%s:%s", groupId, artifactId, version));
                }
            }
        } catch (Exception e) {
            // 파일 읽기 및 파싱 중 오류가 발생하면 로그 출력
            System.err.println("Failed to parse dependencies: " + e.getMessage());
        }
        return dependencies;
    }

    /**
     * build.gradle 파일에서 plugins 블록을 파싱해 플러그인 id와 버전 리스트로 반환
     * @param gradleFilePath build.gradle 파일의 경로
     * @return 파싱된 플러그인 정보 리스트
     */
    private static List<PluginInfo> parsePlugins(String gradleFilePath) {
        List<PluginInfo> plugins = new ArrayList<>();
        boolean inPluginsBlock = false;
        Pattern pluginPattern = Pattern.compile("id ['\"]([\\w.\\-]+)['\"](?: version ['\"]([\\w.\\-]+)['\"])?");
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(gradleFilePath), StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("plugins")) {
                    inPluginsBlock = true;
                    continue;
                }
                if (inPluginsBlock && line.startsWith("}")) {
                    break;
                }
                if (inPluginsBlock) {
                    Matcher matcher = pluginPattern.matcher(line);
                    if (matcher.find()) {
                        String id = matcher.group(1);
                        String version = matcher.group(2);
                        if (version != null) {
                            plugins.add(new PluginInfo(id, version));
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse plugins: " + e.getMessage());
        }
        return plugins;
    }

    /**
     * 의존성을 병렬로 검사하여 검사 결과 리스트 반환
     *
     * @param dependencies 검사할 의존성 목록
     * @return 의존성 검사 결과 리스트
     */
    private static List<DependencyCheckResult> checkDependenciesInParallel(List<String> dependencies) {
        // CompletableFuture를 사용해 비동기 병렬 처리
        List<CompletableFuture<DependencyCheckResult>> futures = dependencies.stream()
            .map(dependency -> CompletableFuture.supplyAsync(() -> checkDependency(dependency), THREAD_POOL))
            .toList();

        // 모든 CompletableFuture가 완료될 때까지 대기하고 결과 수집
        return futures.stream()
            .map(CompletableFuture::join)
            .toList();
    }

    /**
     * 특정 의존성의 버전을 검사하고 결과를 반환
     *
     * @param dependency 검사할 의존성 (groupId:artifactId:version 형식)
     * @return 검사 결과 객체 (DependencyCheckResult)
     */
    private static DependencyCheckResult checkDependency(String dependency) {
        List<String> parts = Splitter.on(':').splitToList(dependency);

        // 의존성 형식이 올바르지 않은 경우
        if (parts.size() != 3) {
            return new DependencyCheckResult(dependency, null, "error");
        }

        String groupId = parts.get(0);
        String artifactId = parts.get(1);
        String declaredVersion = parts.get(2);

        // 캐시에 이미 결과가 있는 경우 캐시 결과 반환
        if (CACHE.containsKey(dependency)) {
            String cachedVersion = CACHE.get(dependency);
            String status = declaredVersion.equals(cachedVersion) ? "latest" : "outdated";
            return new DependencyCheckResult(dependency, cachedVersion, status);
        }

        // 최신 버전 가져오기
        String latestVersion = fetchLatestVersion(groupId, artifactId, false);

        // 최신 버전 정보가 없는 경우 오류 처리
        if (latestVersion == null) {
            return new DependencyCheckResult(dependency, null, "error");
        }

        // 결과를 캐시에 저장
        CACHE.put(dependency, latestVersion);

        // 버전 비교 및 상태 결정
        if (declaredVersion.equals(latestVersion)) {
            return new DependencyCheckResult(dependency, latestVersion, "latest");
        } else {
            return new DependencyCheckResult(dependency, latestVersion, "outdated");
        }
    }

    /**
     * Maven Central 또는 Plugin Portal에서 최신 버전 정보를 가져옴
     * @param groupId groupId
     * @param artifactId artifactId
     * @param isPlugin 플러그인 여부 (true면 Plugin Portal, false면 Maven Central)
     * @return 최신 버전 (문자열) 또는 null
     */
    private static String fetchLatestVersion(String groupId, String artifactId, boolean isPlugin) {
        String baseUrl = isPlugin
            ? "https://plugins.gradle.org/m2/%s/%s/maven-metadata.xml"
            : "https://repo1.maven.org/maven2/%s/%s/maven-metadata.xml";
        String metadataUrl = String.format(baseUrl, groupId.replace('.', '/'), artifactId);
        for (int i = 0; i < 3; i++) {
            try {
                List<String> versions = fetchAllVersions(metadataUrl);
                if (!versions.isEmpty()) {
                    return versions.get(versions.size() - 1);
                }
            } catch (Exception e) {
                System.err.println("Attempt " + (i + 1) + " failed for URL: " + metadataUrl);
            }
        }
        return null;
    }

    /**
     * Maven 메타데이터에서 버전 정보를 추출
     *
     * @param metadataUrl Maven 메타데이터 파일 URL
     * @return 버전 리스트
     */
    private static List<String> fetchAllVersions(String metadataUrl) throws Exception {
        List<String> versions = new ArrayList<>();
        HttpURLConnection connection = (HttpURLConnection)URI.create(metadataUrl).toURL().openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;

            // "<version>" 태그에서 버전 추출
            Pattern pattern = Pattern.compile("<latest>(.*?)</latest>");  // 최신버전
//            Pattern pattern = Pattern.compile("<version>(.*?)</version>");  // 모든버전
            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    versions.add(matcher.group(1));
                }
            }
        } finally {
            connection.disconnect();
        }
        return versions;
    }

    /**
     * 검사 결과를 콘솔에 출력
     *
     * @param results 의존성 검사 결과 목록
     */
    private static void printResults(List<DependencyCheckResult> results) {
        System.out.println("\n=== 최신 상태 의존성 ===");
        results.stream()
            .filter(result -> "latest".equals(result.getStatus()))
            .forEach(System.out::println);

        System.out.println("\n=== 업데이트가 필요한 의존성 ===");
        results.stream()
            .filter(result -> "outdated".equals(result.status()))
            .forEach(result -> {
                // "OUTDATED" 상태를 붉은 색으로 강조
                String output = result.toString().replace("[outdated]", ANSI_RED + "[outdated]" + ANSI_RESET);
                System.out.println(output);
            });

        // 의존성 확인 실패 항목 출력
        System.out.println("\n=== 확인 실패한 의존성 ===");
        results.stream()
            .filter(result -> "error".equals(result.getStatus()))
            .forEach(System.out::println);
    }

    /**
     * 플러그인 정보를 병렬로 검사하여 결과 리스트 반환
     * @param plugins 검사할 플러그인 목록
     * @return 플러그인 검사 결과 리스트
     */
    private static List<DependencyCheckResult> checkPluginsInParallel(List<PluginInfo> plugins) {
        List<CompletableFuture<DependencyCheckResult>> futures = plugins.stream()
            .map(plugin -> CompletableFuture.supplyAsync(() -> checkPlugin(plugin), THREAD_POOL))
            .toList();
        return futures.stream().map(CompletableFuture::join).toList();
    }

    /**
     * 특정 플러그인의 버전을 검사하고 결과를 반환
     * @param plugin 검사할 플러그인 정보
     * @return 검사 결과 객체 (DependencyCheckResult)
     */
    private static DependencyCheckResult checkPlugin(PluginInfo plugin) {
        String groupId = plugin.id;
        String artifactId = plugin.id + ".gradle.plugin";
        String declaredVersion = plugin.version;
        String key = groupId + ":" + artifactId + ":" + declaredVersion;
        if (CACHE.containsKey(key)) {
            String cachedVersion = CACHE.get(key);
            String status = declaredVersion.equals(cachedVersion) ? "latest" : "outdated";
            return new DependencyCheckResult("[plugin] " + key, cachedVersion, status);
        }
        String latestVersion = fetchLatestVersion(groupId, artifactId, true); // Plugin Portal 우선 조회
        if (latestVersion == null) {
            return new DependencyCheckResult("[plugin] " + key, null, "error");
        }
        CACHE.put(key, latestVersion);
        if (declaredVersion.equals(latestVersion)) {
            return new DependencyCheckResult("[plugin] " + key, latestVersion, "latest");
        } else {
            return new DependencyCheckResult("[plugin] " + key, latestVersion, "outdated");
        }
    }

    /**
     * 플러그인 검사 결과를 콘솔에 출력
     * @param results 플러그인 검사 결과 목록
     */
    private static void printPluginResults(List<DependencyCheckResult> results) {
        System.out.println("\n=== 최신 상태 플러그인 ===");
        results.stream()
            .filter(result -> "latest".equals(result.getStatus()))
            .forEach(System.out::println);

        System.out.println("\n=== 업데이트가 필요한 플러그인 ===");
        results.stream()
            .filter(result -> "outdated".equals(result.status()))
            .forEach(result -> {
                String output = result.toString().replace("[outdated]", ANSI_RED + "[outdated]" + ANSI_RESET);
                System.out.println(output);
            });

        System.out.println("\n=== 확인 실패한 플러그인 ===");
        results.stream()
            .filter(result -> "error".equals(result.getStatus()))
            .forEach(System.out::println);
    }

    /**
     * 플러그인 정보 record
     */
    private record PluginInfo(String id, String version) {}

    /**
     * 특정 의존성에 대한 버전 확인 결과를 나타내며,
     * 의존성, 최신 버전 정보, 버전 상태를 포함합니다.
     * <p>
     * 이 record는 특정 의존성에 대한 정보와
     * 최신 상태인지, 구버전인지, 또는 오류가 발생했는지 여부를 나타내는 데 사용됩니다.
     */
    private record DependencyCheckResult(String dependency, String latestVersion, @Getter String status) {

        @Override
        public String toString() {
            if ("latest".equals(status)) {
                return String.format("Dependency: %s -> %s [latest]", dependency, latestVersion);
            } else if ("outdated".equals(status)) {
                return String.format("Dependency: %s -> %s [outdated]", dependency, latestVersion);
            } else {
                return String.format("Dependency: %s [error]", dependency);
            }
        }
    }
}
