package com.devkbil.mtssbj.api.v1.develop.logview;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.CoreConstants;

import com.devkbil.mtssbj.develop.logview.DevLogbackAppenderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Logback 로그 뷰어 REST API 컨트롤러
 * - 큐에 저장된 로그 메시지를 JSON 형태로 반환합니다.
 */
@RestController
@RequestMapping("/api/v1/develop/logback")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Logback API", description = "Logback 로그 메시지를 가져오기 위한 API")
public class DevLogbackRestController {

    private final DevLogbackAppenderService<ILoggingEvent> devLogbackAppenderService;

    /**
     * 로그 메시지 조회 API
     * - 큐에 저장된 로그 메시지를 JSON 형태로 반환합니다.
     *
     * @return 로그 메시지 목록을 담은 ResponseEntity
     */
    @Operation(summary = "로그 메시지 조회", description = "큐에 저장된 로그 메시지를 JSON 형태로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그 메시지 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping(value = "/logs", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> getLogs() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<String> logMessages = devLogbackAppenderService
                .getLogQueue()
                .stream()
                .map(queue -> queue
                    .getLogMessage()
                    .replaceAll(CoreConstants.LINE_SEPARATOR, "")
                    .replaceAll("\t", ""))
                .collect(Collectors.toList());

            result.put("logs", logMessages);
            result.put("count", logMessages.size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving log messages: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}