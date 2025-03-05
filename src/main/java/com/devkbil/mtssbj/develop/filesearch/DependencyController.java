package com.devkbil.mtssbj.develop.filesearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.NoArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Dependency Controller
 * - Gradle Dependencies 명령어를 실행하여 의존성 트리를 반환합니다.
 * - 플랫폼(Windows/Linux)에 따라 적절한 명령어를 실행합니다.
 */
@Controller
@Tag(name = "Dependency Controller", description = "Gradle 의존성 트리를 조회하기 위한 컨트롤러")
public class DependencyController {

    /**
     * Executes the Gradle 'dependencies --configuration compileClasspath' command
     * to retrieve the dependency tree and passes the output to the view.
     *
     * @param modelMap the Spring ModelMap object used to pass data to the view
     * @return the name of the Thymeleaf template to render the dependency tree view
     */
    @Operation(summary = "Gradle 의존성 검색", description = "Gradle 'dependencies --configuration compileClasspath' 명령어를 실행하여 의존성 트리를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 의존성 트리가 반환되었습니다.")
    @GetMapping("/dependencySearch")
    public String dependencySearch(ModelMap modelMap) {

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
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }

        modelMap.put("listview", dependenciesList); // View로 데이터 전달

        return "thymeleaf/dependencySearch"; // 결과 페이지 반환
    }

}
