package com.devkbil.mtssbj.api.v1.main;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.api.common.response.ApiResponse;
import com.devkbil.mtssbj.main.IndexService;
import com.devkbil.mtssbj.search.SearchVO;

import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * MainRestController 클래스는 메인 페이지 관련 REST API 요청을 처리하는 컨트롤러입니다.
 * 메인 페이지 데이터 및 캘린더 데이터를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/main")
@Slf4j
@RequiredArgsConstructor
public class MainRestController {

    private final IndexService indexService;

    /**
     * 메인 페이지 데이터를 제공합니다.
     * 프로젝트 목록, 최신 뉴스, 공지사항, 타임라인, 캘린더 데이터 등을 포함합니다.
     *
     * @param searchVO 검색 조건 객체
     * @return 메인 페이지 데이터가 포함된 응답
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> getMainPageData(
            @ModelAttribute @Validated SearchVO searchVO) {

        log.info("Main page data request received");

        // 검색 키워드 추출
        String searchKeyword = searchVO != null ? searchVO.getSearchKeyword() : null;

        // ModelMap 객체 생성
        ModelMap modelMap = new ModelMap();

        // IndexService를 사용하여 메인 페이지 데이터 준비
        Map<String, Object> mainPageData = indexService.prepareMainPage(searchVO, modelMap);

        return ResponseEntity.ok(ApiResponse.success(mainPageData));
    }

    /**
     * 특정 날짜의 캘린더 데이터를 제공합니다.
     *
     * @param date 조회할 날짜 (문자열 형식)
     * @return 캘린더 데이터가 포함된 응답
     */
    @GetMapping("/calendar")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCalendarData(
            @RequestParam(value = "date", required = false) String date) {

        log.info("Calendar data request received for date: {}", date);

        // 날짜가 제공되지 않은 경우 현재 날짜 사용
        Date targetDate = date != null ? DateUtil.stringToDate(date) : DateUtil.getToday();

        // IndexService를 사용하여 캘린더 데이터 계산
        Map<String, Object> calendarData = indexService.calculateCalendarData(targetDate);

        return ResponseEntity.ok(ApiResponse.success(calendarData));
    }
}
