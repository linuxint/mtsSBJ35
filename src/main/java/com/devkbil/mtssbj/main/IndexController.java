package com.devkbil.mtssbj.main;

import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

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

@Controller
@RequiredArgsConstructor
@Tag(name = "IndexController", description = "메인 페이지와 관련된 컨트롤러")
public class IndexController {

    private final IndexService indexService;

    /**
     * Thymeleaf 테스트 페이지를 반환합니다.
     *
     * @param model 모델 객체
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
     * @param searchVO 검색 조건
     * @param modelMap 모델 맵 객체
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


    @Operation(summary = "메인 페이지", description = "메인 화면 데이터를 구성하여 반환합니다.")
    @PostMapping("/index")
    public String indexPost(@ModelAttribute @Validated SearchVO searchVO, ModelMap modelMap) {

        // IndexService에서 데이터를 미리 준비
        Map<String, Object> mainPageData = indexService.prepareMainPage(searchVO, modelMap);

        modelMap.addAllAttributes(mainPageData);

        return "main/index";
    }


    /**
     * 메인 페이지의 주간 캘린더 데이터를 업데이트합니다.
     *
     * @param request  HttpServletRequest 객체
     * @param modelMap 모델 맵 객체
     * @return 주간 캘린더 데이터(html 파일 이름)
     */
    @Operation(summary = "캘린더 날짜 이동", description = "Ajax 호출로 주간 캘린더 데이터를 업데이트합니다.")
    @PostMapping("/moveDate")
    public String moveDate(
            @RequestParam(value = "date", required = false) String date,
            HttpServletRequest request, ModelMap modelMap) {

        Date today = DateUtil.stringToDate(date);

        Map<String, Object> calendarData = indexService.calculateCalendarData(today);

        modelMap.addAllAttributes(calendarData);

        return "main/indexCalen";
    }
}
