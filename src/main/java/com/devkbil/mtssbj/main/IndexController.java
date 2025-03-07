package com.devkbil.mtssbj.main;

import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * IndexController는 메인 페이지 및 그 관련 기능 요청을 처리하는 컨트롤러 클래스입니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "IndexController", description = "메인 페이지와 관련된 컨트롤러")
public class IndexController {

    private final IndexService indexService;

    /**
     * Thymeleaf 테스트 페이지를 반환합니다.
     *
     * @param model Thymeleaf 뷰에 전달할 모델 객체
     * @return Thymeleaf 테스트 페이지(html 파일 이름)
     */
    @Operation(summary = "Thymeleaf 테스트", description = "Thymeleaf 템플릿 엔진 테스트 페이지를 반환합니다.")
    @GetMapping("/thymeleaftest")
    public String test(Model model) {
        model.addAttribute("name", "kbil test");
        return "thymeleaf/thymeleaftest";
    }

    /**
     * 메인 페이지를 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 뷰에 전달할 속성을 담은 모델 맵 객체
     * @return 메인 페이지(html 파일 이름)
     */
    @Operation(summary = "메인 페이지", description = "메인 화면 데이터를 구성하여 반환합니다.")
    @GetMapping("/index")
    public String indexGet(@ModelAttribute @Validated SearchVO searchVO, ModelMap modelMap) {

        // IndexService에서 데이터를 미리 준비
        Map<String, Object> mainPageData = indexService.prepareMainPage(searchVO, modelMap);

        modelMap.addAllAttributes(mainPageData);

        return "main/index";
    }

    /**
     * 메인 페이지에 대한 POST 요청을 처리하고, 메인 페이지 데이터를 구성하며,
     * 메인 HTML 페이지 이름을 반환합니다.
     *
     * @param searchVO 메인 페이지 데이터를 구성하기 위한 검색 조건 객체
     * @param modelMap 뷰에 전달할 속성을 담은 모델 맵 객체
     * @return 메인 페이지(html 파일 이름)
     */
    @Operation(summary = "메인 페이지", description = "메인 화면 데이터를 구성하여 반환합니다.")
    @PostMapping("/index")
    public String indexPost(@ModelAttribute @Validated SearchVO searchVO, ModelMap modelMap) {

        // IndexService에서 데이터를 미리 준비
        Map<String, Object> mainPageData = indexService.prepareMainPage(searchVO, modelMap);

        modelMap.addAllAttributes(mainPageData);

        return "main/index";
    }

    /**
     * 주간 캘린더 데이터를 업데이트합니다.
     * 전달된 날짜 문자열(date)을 기준으로 캘린더 날짜를 조정하여 처리합니다.
     *
     * @param date      새 캘린더 데이터를 계산하기 위한 날짜 문자열
     *                  null 또는 제공되지 않을 경우 기본 동작을 수행합니다.
     * @param request   HTTP 요청의 세부 정보를 담은 HttpServletRequest 객체
     * @param modelMap  뷰 레이어에 속성을 추가하기 위한 모델 맵 객체
     * @return 업데이트된 주간 캘린더 뷰를 포함하는 HTML 파일 이름("main/indexCalen")
     */
    @PostMapping("/moveDate")
    public String moveDate(
        @RequestParam(value = "date", required = false) String date,
        HttpServletRequest request, ModelMap modelMap) {

        // 전달된 날짜 문자열을 Date 객체로 변환
        Date today = DateUtil.stringToDate(date);

        // 주간 캘린더 데이터를 계산
        Map<String, Object> calendarData = indexService.calculateCalendarData(today);

        // 계산된 데이터를 모델에 추가
        modelMap.addAllAttributes(calendarData);

        // 업데이트된 캘린더 뷰 반환
        return "main/indexCalen";
    }
}