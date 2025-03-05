package com.devkbil.mtssbj.schedule;

import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Controller for managing schedules (일정 관리).
 */
@Controller
@Slf4j
@Tag(name = "Schedule Management", description = "일정 관리 API")
@RequiredArgsConstructor
public class SchController {

    private final SchService schService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 기본 날짜를 설정합니다.
     *
     * @param searchVO 검색 조건
     * @return 설정된 검색 조건
     */
    private MonthVO setupDefaultDates(@ModelAttribute @Valid MonthVO searchVO) {

        if (!StringUtils.hasText(searchVO.getYear())) {
            Date today = DateUtil.getToday();
            searchVO.setYear(String.valueOf(DateUtil.getYear(today)));
            searchVO.setMonth(String.valueOf(DateUtil.getMonth(today)));
        }

        if ("0".equals(searchVO.getMonth()) || "13".equals(searchVO.getMonth())) {
            searchVO = DateUtil.monthValid(searchVO);
        }

        return searchVO;
    }

    /**
     * 일정 목록 화면
     *
     * @param searchVO 검색 조건
     * @param modelMap 뷰에 속성을 전달하는 ModelMap 객체
     * @return 일정 목록 화면 (HTML)
     */
    @Operation(summary = "일정 목록 조회", description = "특정 달(month)의 일정 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적으로 일정 목록을 조회했습니다.", headers = @Header(name = AUTHORIZATION, description = "Access Token")),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/schList")
    public String schList(@ModelAttribute @Valid MonthVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        searchVO = setupDefaultDates(searchVO);
        Integer dayOfWeek = DateUtil.getDayOfWeek(DateUtil.str2Date(searchVO.getYear() + "-" + searchVO.getMonth() + "-01"));

        List<?> scheduleList = schService.selectCalendar(searchVO, userno);
        modelMap.addAttribute("listview", scheduleList);
        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("dayofweek", dayOfWeek);

        log.info("일정 목록 조회: UserNo={}, Year={}, Month={}", userno, searchVO.getYear(), searchVO.getMonth());
        return "schedule/SchList";
    }

    /**
     * 일정 입력/수정 화면
     *
     * @param schInfo  일정 정보 VO
     * @param modelMap 뷰에 속성을 전달하는 ModelMap 객체
     * @return 일정 입력/수정 화면 (HTML)
     */
    @Operation(summary = "일정 입력/수정 화면 조회", description = "특정 일정 정보를 입력하거나 수정합니다.")
    @ApiResponse(responseCode = "200", description = "일정 입력/수정 화면을 반환했습니다.")
    @GetMapping("/schForm")
    public String schForm(@RequestParam(value = "cddate", required = false) String cddate,
                          @ModelAttribute @Valid SchVO schInfo, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        // 일정 정보 조회 또는 기본 값 설정
        if (StringUtils.hasText(schInfo.getSsno())) {
            schInfo = schService.selectSchOne(schInfo);

        } else {
            initializeDefaultSchedule(cddate, schInfo);
        }

        List<?> sstypelist = etcService.selectClassCode("4");
        modelMap.addAttribute("schInfo", schInfo);
        modelMap.addAttribute("sstypelist", sstypelist);

        log.info("일정 입력 화면 요청: UserNo={}, Schedule ID={}", userno, schInfo.getSsno());
        return "schedule/SchForm";
    }

    /**
     * 일정 저장
     *
     * @param schInfo 저장할 일정 정보
     * @return 일정 목록 리디렉션 URL
     */
    @Operation(summary = "일정 저장", description = "사용자의 일정 정보를 저장합니다.")
    @ApiResponse(responseCode = "200", description = "일정을 성공적으로 저장했습니다.")
    @PostMapping("/schSave")
    public String schSave(@ModelAttribute @Valid SchVO schInfo) {

        String userno = authService.getAuthUserNo();

        schInfo.setUserno(userno);

        schService.insertSch(schInfo);

        log.info("일정 저장: UserNo={}, Schedule Info={}", userno, schInfo);
        return "redirect:/schList";
    }

    /**
     * 특정 일정 읽기 (Ajax 요청 처리)
     *
     * @param schVO    쿼리할 일정 VO
     * @param cddate   클릭 날짜
     * @param modelMap 뷰에 속성 전달
     * @return Ajax 요청에 대한 응답 템플릿
     */
    @Operation(summary = "Ajax 일정 읽기", description = "특정 사용자의 일정을 Ajax로 조회합니다.")
    @ApiResponse(responseCode = "200", description = "일정을 성공적으로 조회했습니다(Ajax).")
    @PostMapping("/schRead4Ajax")
    public String schRead4Ajax(@ModelAttribute @Valid SchVO schVO, @RequestParam(value = "cddate") String cddate, ModelMap modelMap) {

        SchVO schInfo = schService.selectSchOne4Read(schVO);

        modelMap.addAttribute("schInfo", schInfo);
        modelMap.addAttribute("cddate", cddate);

        log.info("Ajax 조회 일정: Schedule Info={}", schInfo);
        return "schedule/SchRead4Ajax";
    }

    /**
     * 일정 읽기 (일반 요청)
     *
     * 일정 열람 요청을 처리합니다. 특정 일정의 세부정보를 검색합니다.
     * 제공된 일정 정보를 기반으로 표시할 뷰 이름을 반환합니다.
     * 일정 세부정보입니다.
     *
     * @param schVO    쿼리에 필요한 일정 세부 정보가 포함된 SchVO 개체
     * @param modelMap 뷰에 속성을 전달하는 데 사용되는 ModelMap 객체
     * @return 일정 정보를 표시할 뷰 이름
     */
    @Operation(summary = "일정관리-일정읽기Request")
    @PostMapping("/schRead")
    public String schRead(@ModelAttribute @Valid SchVO schVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        SchVO schInfo = schService.selectSchOne4Read(schVO);

        modelMap.addAttribute("schInfo", schInfo);

        log.info("일정 읽기 화면이 요청되었습니다. UserNo: {}, Schedule Info: {}", userno, schInfo);
        return "schedule/SchRead";
    }

    /**
     * 일정 삭제
     *
     * @param schVO 삭제할 일정 VO
     * @return 일정 목록 리디렉션 URL
     */
    @Operation(summary = "일정 삭제", description = "특정 사용자의 일정을 삭제합니다.")
    @ApiResponse(responseCode = "200", description = "일정을 성공적으로 삭제했습니다.")
    @PostMapping("/schDelete")
    public String schDelete(@ModelAttribute @Valid SchVO schVO) {

        int affectedRows = schService.deleteSch(schVO);

        log.info("일정 삭제: Schedule Info={}", schVO);
        return "redirect:/schList";
    }

    /**
     * 일정 기본 설정 초기화
     *
     * @param schInfo 초기화할 일정 정보
     */
    private void initializeDefaultSchedule(@RequestParam(value = "cddate", required = false) String cddate,
                                           @ModelAttribute @Valid SchVO schInfo) {

        schInfo.setSstype("1");
        schInfo.setSsisopen("Y");

        if (!StringUtils.hasText(cddate)) {
            cddate = DateUtil.date2Str(DateUtil.getToday());
        }
        schInfo.setSsstartdate(cddate);
        schInfo.setSsstarthour("09");
        schInfo.setSsenddate(cddate);
        schInfo.setSsendhour("18");
    }
}
