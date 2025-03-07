package com.devkbil.mtssbj.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;

/**
 * 기능 확인과 테스트를 위한 엔드포인트를 제공합니다.
 * 이 컨트롤러는 특정 엔드포인트에 대해 정의된 작업과 매개변수로 구성된 REST API 역할을 합니다.
 */
@RestController
@AllArgsConstructor
@Tag(name = "HomeControllerTest", description = "HomeController 테스트입니다.") // 컨트롤러에 대한 Swagger 설명
public class CheckController {

    /**
     * "/home/{area}" 엔드포인트에 대한 요청을 처리하며, 지정된 지역과 매개변수에 따라 서비스를 제공합니다.
     *
     * @param area   경로 변수로 필수로 지정된 지역
     * @param param1 첫 번째 매개변수, 요청 매개변수로 필수
     * @param param2 두 번째 매개변수, 요청 매개변수로 선택
     * @return 문자열 응답, 구체적으로 "home" 문자열
     */
    @Operation(  // API에 대한 Swagger 설명
        summary = "서비스",
        description = "서비스입니다."
        //, httpMethod = "POST"
        //, consumes = "application/json"
        //, produces = "application/json"
        //, protocols = "http"
        //, responseHeaders = {/*headers*/}
    )
    @ApiResponses({  // Response Message에 대한 Swagger 설명
        @ApiResponse(responseCode = "200", description = "OK"),
        @ApiResponse(responseCode = "404", description = "파라미터 없음")
    })

    @Parameters({
        @Parameter(name = "area", description = "지역", required = true),
        @Parameter(name = "param1", description = "파라미터1", required = true),
        @Parameter(name = "param2", description = "파라미터2", required = false)
    })
    @GetMapping("/home/{area}")
    public String home(@PathVariable String area, @RequestParam String param1, @RequestParam int param2) {
        return "home";
    }

    /*
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("hi", "Hello~~");
        return "home";
    }
    */

}
