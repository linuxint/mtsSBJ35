package com.devkbil.mtssbj.api.v1.main;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.main.IndexService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.ui.ModelMap;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메인 페이지 관련 REST API 컨트롤러
 * - 메인 페이지 데이터 및 캘린더 데이터를 JSON 형태로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/main")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Main API", description = "메인 페이지 관련 API")
public class IndexRestController {

    private final IndexService indexService;

    /**
     * 메인 페이지 데이터를 JSON 형태로 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @return 메인 페이지 데이터를 담은 ResponseEntity
     */
    @Operation(summary = "메인 페이지 데이터 조회", description = "메인 화면에 필요한 데이터를 JSON 형태로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메인 페이지 데이터 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping
    public ResponseEntity<Map<String, Object>> getMainPageData(@ModelAttribute @Validated SearchVO searchVO) {
        try {
            ModelMap modelMap = new ModelMap();
            Map<String, Object> mainPageData = indexService.prepareMainPage(searchVO, modelMap);

            return ResponseEntity.ok(mainPageData);
        } catch (Exception e) {
            log.error("Error retrieving main page data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * 주간 캘린더 데이터를 JSON 형태로 반환합니다.
     *
     * @param date 새 캘린더 데이터를 계산하기 위한 날짜 문자열
     * @return 캘린더 데이터를 담은 ResponseEntity
     */
    @Operation(summary = "캘린더 데이터 조회", description = "지정된 날짜를 기준으로 주간 캘린더 데이터를 JSON 형태로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "캘린더 데이터 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/calendar")
    public ResponseEntity<Map<String, Object>> getCalendarData(
            @RequestParam(value = "date", required = false) String date) {
        try {
            // 전달된 날짜 문자열을 Date 객체로 변환
            Date today = DateUtil.stringToDate(date);

            // 주간 캘린더 데이터를 계산
            Map<String, Object> calendarData = indexService.calculateCalendarData(today);

            return ResponseEntity.ok(calendarData);
        } catch (Exception e) {
            log.error("Error retrieving calendar data: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}
