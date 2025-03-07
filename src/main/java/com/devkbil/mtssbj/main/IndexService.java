package com.devkbil.mtssbj.main;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.util.DateUtil;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.project.ProjectService;
import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.search.SearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * 메인 페이지와 관련된 서비스 클래스입니다.
 * 데이터베이스와 연동하여 메인 화면에 필요한 데이터를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    private final SqlSessionTemplate sqlSession;
    final AuthService authService;
    private final EtcService etcService;
    private final ProjectService projectService;


    /**
     * 메인 페이지 데이터를 구성하여 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 화면에 전달할 모델 데이터
     * @return 메인 페이지 데이터를 담은 Map
     */
    public Map<String, Object> prepareMainPage(SearchVO searchVO, ModelMap modelMap) {
        if (searchVO == null) {
            searchVO = new SearchVO();
        }

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap); // 공통 속성 설정

        Date today = DateUtil.getToday();
        modelMap.putAll(calculateCalendarData(today)); // 캘린더 데이터 계산

        if (StringUtils.hasText(searchVO.getSearchKeyword())) {
            searchVO.setSearchType("prtitle"); // 검색어 존재 시 검색 타입 설정
        }

        searchVO.setDisplayRowCount(12); // 페이지당 표시할 데이터 수 설정
        searchVO.pageCalculate(projectService.selectProjectCount(searchVO)); // 페이지 계산

        // 데이터 조회 및 모델 추가
        {
            List<?> projectListView = projectService.selectProjectList(searchVO); // 프로젝트 목록
            List<?> recentNews = selectRecentNews(); // 최신 뉴스
            List<?> top5Notices = selectNoticeListTop5(); // 상위 5개 공지사항
            List<?> timeline = selectTimeLine(); // 타임라인 데이터

            modelMap.put("searchVO", searchVO);
            modelMap.put("projectlistview", projectListView);
            modelMap.put("listview", recentNews);
            modelMap.put("noticeList", top5Notices);
            modelMap.put("listtime", timeline);
        }

        return modelMap;
    }

    /**
     * 캘린더 데이터를 계산하여 반환합니다.
     *
     * @param targetDay 대상 날짜
     * @return 캘린더 데이터 Map
     */
    public Map<String, Object> calculateCalendarData(Date targetDay) {

        String userno = authService.getAuthUserNo();

        List<DateVO> calenList = new ArrayList<>();

        Date today = DateUtil.getToday();
        int month = DateUtil.getMonth(targetDay); // 월 계산
        int week = DateUtil.getWeekOfMonth(targetDay); // 주 계산

        Date fweek = DateUtil.getFirstOfWeek(targetDay); // 주 시작일
        Date lweek = DateUtil.getLastOfWeek(targetDay); // 주 종료일
        Date preWeek = DateUtil.dateAdd(fweek, -1); // 이전 주
        Date nextWeek = DateUtil.dateAdd(lweek, 1); // 다음 주

        ExtFieldVO fld = new ExtFieldVO();
        fld.setField1(userno);

        while (fweek.compareTo(lweek) <= 0) {
            DateVO dvo = DateUtil.date2VO(fweek);
            dvo.setIstoday(DateUtil.dateDiff(fweek, today) == 0); // 오늘 여부 설정
            dvo.setDate(DateUtil.date2Str(fweek)); // 날짜 설정

            fld.setField2(dvo.getDate());
            dvo.setList(selectSchList4Calen(fld)); // 해당 날짜의 일정 조회

            calenList.add(dvo);
            fweek = DateUtil.dateAdd(fweek, 1); // 다음 날짜로 이동
        }

        // 캘린더 데이터를 Map으로 반환
        Map<String, Object> calendarData = new HashMap<>();
        calendarData.put("month", month);
        calendarData.put("week", week);
        calendarData.put("calenList", calenList);
        calendarData.put("preWeek", DateUtil.date2Str(preWeek));
        calendarData.put("nextWeek", DateUtil.date2Str(nextWeek));

        return calendarData;
    }

    /**
     * 최신 뉴스 데이터를 조회합니다.
     *
     * @return 최신 뉴스 목록
     */
    public List<?> selectRecentNews() {
        return sqlSession.selectList("selectRecentNews");
    }

    /**
     * 타임라인 데이터를 조회합니다.
     *
     * @return 타임라인 목록
     */
    public List<?> selectTimeLine() {
        return sqlSession.selectList("selectTimeLine");
    }

    /**
     * 상위 5개의 공지사항 데이터를 조회합니다.
     *
     * @return 공지사항 목록
     */
    public List<?> selectNoticeListTop5() {
        return sqlSession.selectList("selectNoticeListTop5");
    }

    /**
     * 캘린더에 표시할 일정 데이터를 조회합니다.
     *
     * @param param 일정 필터링을 위한 매개변수 (ExtFieldVO)
     * @return 일정 목록
     */
    public List<?> selectSchList4Calen(ExtFieldVO param) {
        return sqlSession.selectList("selectSchList4Calen", param);
    }

}