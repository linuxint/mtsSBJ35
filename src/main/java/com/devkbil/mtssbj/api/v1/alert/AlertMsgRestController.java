package com.devkbil.mtssbj.api.v1.alert;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Alert 메시지 관련 REST API를 처리하는 컨트롤러입니다.
 * 현재 사용자의 Alert 메시지 리스트를 다양한 형식으로 제공하는 엔드포인트를 포함합니다.
 */
@RestController
@RequestMapping("/api/v1/alert")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Alert API", description = "Alert 메시지 관련 API")
public class AlertMsgRestController {

    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 현재 사용자의 전체 Alert 메시지 리스트를 반환합니다.
     *
     * @return Alert 메시지 리스트를 담은 ResponseEntity
     */
    @Operation(summary = "전체 Alert 메시지 리스트", description = "현재 사용자에 해당하는 전체 Alert 메시지 리스트를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alert 메시지 리스트 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getAlertList() {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();
        List<?> listview = etcService.selectAlertList(userno);

        result.put("list", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 현재 사용자의 상위 5개의 Alert 메시지 리스트를 반환합니다.
     *
     * @return 상위 5개 Alert 메시지 리스트를 담은 ResponseEntity
     */
    @Operation(summary = "Top 5 Alert 메시지 리스트", description = "현재 사용자에 해당하는 상위 5개의 Alert 메시지 리스트를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top 5 Alert 메시지 리스트 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/top5")
    public ResponseEntity<Map<String, Object>> getTop5AlertList() {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();
        List<?> listview = etcService.selectAlertList4Ajax(userno);

        result.put("list", listview);

        return ResponseEntity.ok(result);
    }
}