package com.devkbil.mtssbj.develop.filesearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
     * Gradle 의존성 검색
     * - `gradlew dependencies` 명령을 실행하여 프로젝트의 의존성 트리를 조회합니다.
     * - Windows와 Linux 환경에 맞는 명령어를 실행합니다.
     * - 실행 결과를 `modelMap`에 저장하여 View에 전달합니다.
     *
     * @param modelMap View와 데이터를 공유하기 위한 모델 객체
     * @return 의존성 검색 결과 페이지
     */
    @Operation(summary = "Gradle 의존성 검색", description = "Gradle 'dependencies --configuration compileClasspath' 명령어를 실행하여 의존성 트리를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 의존성 트리가 반환되었습니다.")
    @GetMapping("/dependencySearch")
    public String dependencySearch(ModelMap modelMap) {

        List<String> list = new ArrayList<>();

        String[] cmd;
        boolean isWindows = System.getProperty("os.name").toLowerCase().startsWith("windows");
        String s;
        try {
            ProcessBuilder builder = new ProcessBuilder();
            //builder.redirectInput(ProcessBuilder.Redirect.INHERIT);
            //builder.redirectOutput(ProcessBuilder.Redirect.PIPE);
            if (isWindows) {
                cmd = new String[]{"cmd.exe", "/c", "gradlew dependencies --configuration compileClasspath"};
            } else {
                cmd = new String[]{"sh", "-c", "gradlew dependencies --configuration compileClasspath"};
            }

            builder.command(cmd);
            builder.redirectErrorStream(true);

            // 명령어 실행
            Process process = builder.start();
            BufferedReader sI = new BufferedReader(new InputStreamReader(process.getInputStream()));

            // 결과 읽기
            while ((s = sI.readLine()) != null) {
                list.add(s);
            }
            int exitCode = process.waitFor(); // 처리 완료 대기
        } catch (IOException | InterruptedException e) {
            e.getMessage();
        }

        modelMap.put("listview", list); // View로 데이터 전달

        return "thymeleaf/dependencySearch"; // 결과 페이지 반환
    }

}
