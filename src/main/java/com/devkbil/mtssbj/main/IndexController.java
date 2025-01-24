package com.devkbil.mtssbj.main;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.project.ProjectService;
import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Tag(name = "IndexController", description = "메인 페이지와 관련된 컨트롤러")
public class IndexController {

    private final IndexService indexService;
    private final EtcService etcService;
    private final ProjectService projectService;
    private final AuthenticationService authenticationService;

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
    public String index(@RequestBody @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        Date today = DateUtil.getToday();

        calCalen(userno, today, modelMap);

        if (StringUtils.hasText(searchVO.getSearchKeyword())) {
            searchVO.setSearchType("prtitle");
        }
        searchVO.setDisplayRowCount(12);
        searchVO.pageCalculate(projectService.selectProjectCount(searchVO)); // startRow, endRow

        List<?> projectlistview = projectService.selectProjectList(searchVO);

        List<?> listview = indexService.selectRecentNews();
        List<?> noticeList = indexService.selectNoticeListTop5();
        List<?> listtime = indexService.selectTimeLine();

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("projectlistview", projectlistview);

        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("noticeList", noticeList);
        modelMap.addAttribute("listtime", listtime);

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
            @RequestParam(value = "date", required = false) String date
            , HttpServletRequest request, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        Date today = DateUtil.stringToDate(date);

        calCalen(userno, today, modelMap);

        return "main/indexCalen";
    }

    /**
     * 주간 캘린더 정보를 계산하고 모델에 추가합니다.
     *
     * @param userno    사용자 번호
     * @param targetDay 대상 날짜
     * @param modelMap  모델 맵 객체
     * @return 업데이트된 캘린더 데이터(html 파일 이름)
     */
    private String calCalen(String userno, Date targetDay, ModelMap modelMap) {

        List<DateVO> calenList = new ArrayList<DateVO>();

        Date today = DateUtil.getToday();
        int month = DateUtil.getMonth(targetDay);
        int week = DateUtil.getWeekOfMonth(targetDay);

        Date fweek = DateUtil.getFirstOfWeek(targetDay);
        Date lweek = DateUtil.getLastOfWeek(targetDay);
        Date preWeek = DateUtil.dateAdd(fweek, -1);
        Date nextWeek = DateUtil.dateAdd(lweek, 1);

        ExtFieldVO fld = new ExtFieldVO();
        fld.setField1(userno);

        while (fweek.compareTo(lweek) <= 0) {
            DateVO dvo = DateUtil.date2VO(fweek);
            dvo.setIstoday(DateUtil.dateDiff(fweek, today) == 0);
            dvo.setDate(DateUtil.date2Str(fweek));

            fld.setField2(dvo.getDate());
            dvo.setList(indexService.selectSchList4Calen(fld));

            calenList.add(dvo);

            fweek = DateUtil.dateAdd(fweek, 1);
        }

        modelMap.addAttribute("month", month);
        modelMap.addAttribute("week", week);
        modelMap.addAttribute("calenList", calenList);
        modelMap.addAttribute("preWeek", DateUtil.date2Str(preWeek));
        modelMap.addAttribute("nextWeek", DateUtil.date2Str(nextWeek));

        return "main/index";
    }

}
