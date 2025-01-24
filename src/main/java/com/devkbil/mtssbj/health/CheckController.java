package com.devkbil.mtssbj.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 체크와 테스트 서비스를 위한 엔드포인트를 제공하는 RestController입니다.
 */
@RestController
@AllArgsConstructor
@Tag(name = "HomeControllerTest", description = "HomeController 테스트입니다.") // Controller에 대한 Swagger 설명
public class CheckController {

    @Operation(  // API에 대한 Swagger 설명
            summary = "서비스"
            , description = "서비스입니다."
            //, httpMethod = "POST"
            //, consumes = "application/json"
            //, produces = "application/json"
            //, protocols = "http"
            //, responseHeaders = {/*headers*/}
    )
    @ApiResponses({  // Response Message에 대한 Swagger 설명
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "404", description = "No params")
    })

    @Parameters({
            @Parameter(name = "area", description = "지역", required = true),
            @Parameter(name = "param1", description = "파라미터1", required = true),
            @Parameter(name = "param2", description = "파마미터2", required = false)
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
