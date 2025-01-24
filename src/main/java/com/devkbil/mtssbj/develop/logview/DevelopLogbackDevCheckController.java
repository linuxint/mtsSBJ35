package com.devkbil.mtssbj.develop.logview;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Logback 로그 뷰어 컨트롤러
 * - 큐에 저장된 로그 메시지를 HTML 또는 JSON 형태로 반환합니다.
 */
@Controller
@Tag(name = "Logback Dev Check Controller", description = "Logback 로그 메시지를 가져오기 위한 API")
@RequiredArgsConstructor
public class DevelopLogbackDevCheckController {

    private final DevelopLogbackAppenderService<ILoggingEvent> developLogbackAppenderService;

    /**
     * 로그 뷰어
     * - 큐에 저장된 로그 메시지를 HTML 또는 JSON 형태로 반환합니다.
     *
     * @param model 모델 객체 (View에 데이터를 전달)
     * @return 로그 뷰어 페이지 (HTML View)
     */
    @Operation(summary = "로그 뷰어", description = "큐에 저장된 로그 메시지를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 로그 메시지가 반환되었습니다.")
    @GetMapping(value = "/dvLogView", produces = MediaType.APPLICATION_JSON_VALUE)
    public String logview(Model model) {
        List<String> logView =
                developLogbackAppenderService
                        .getLogQueue()
                        .stream()
                        .map(queue -> queue
                                .getLogMessage()
                                .replaceAll(CoreConstants.LINE_SEPARATOR, "")
                                .replaceAll("\t", ""))
                        .collect(Collectors.toList());
        model.addAttribute("logView", logView);
        return "thymeleaf/dvLogView";

    }

}
