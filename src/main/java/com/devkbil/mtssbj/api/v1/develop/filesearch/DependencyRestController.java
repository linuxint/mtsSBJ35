package com.devkbil.mtssbj.api.v1.develop.filesearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Dependency REST Controller
 * - Gradle Dependencies 명령어를 실행하여 의존성 트리를 JSON 형태로 반환합니다.
 * - 플랫폼(Windows/Linux)에 따라 적절한 명령어를 실행합니다.
 */
@RestController
@RequestMapping("/api/v1/develop/dependency")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Dependency API", description = "Gradle 의존성 트리를 조회하기 위한 API")
public class DependencyRestController {

    /**
     * Executes the Gradle 'dependencies --configuration compileClasspath' command
     * to retrieve the dependency tree and returns the output as JSON.
     *
     * @return ResponseEntity containing the dependency tree as a list of strings
     */
    @Operation(summary = "Gradle 의존성 검색", description = "Gradle 'dependencies --configuration compileClasspath' 명령어를 실행하여 의존성 트리를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상적으로 의존성 트리가 반환되었습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> getDependencies() {
        Map<String, Object> result = new HashMap<>();
        List<String> dependenciesList = new ArrayList<>();

        String[] command;
        boolean isWindowsOS = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String outputLine;
        try {
            ProcessBuilder processBuilder = new ProcessBuilder();
            if (isWindowsOS) {
                command = new String[]{"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
            } else {
                command = new String[]{"sh", "-c", "gradlew dependencies --configuration compileClasspath"};
            }

            processBuilder.command(command);
            processBuilder.redirectErrorStream(true);

            // 명령어 실행
            Process process = processBuilder.start();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 결과 읽기
            while ((outputLine = bufferedReader.readLine()) != null) {
                dependenciesList.add(outputLine);
            }
            int exitCode = process.waitFor(); // 처리 완료 대기
            
            result.put("dependencies", dependenciesList);
            result.put("exitCode", exitCode);
            
            return ResponseEntity.ok(result);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing Gradle dependencies command: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}