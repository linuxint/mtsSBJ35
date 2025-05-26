package com.devkbil.mtssbj.schedule;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.schedule.DateVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.search.SearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * SchService
 * - 일정 관리 비즈니스 로직을 처리 (CRUD, 상세 등록, 반복 일정 처리 등).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final SqlSessionTemplate sqlSession;

    /**
     * 캘린더 일정 조회
     *
     * @param param  월별 검색 조건 (MonthVO)
     * @param userno 사용자 ID
     * @return 월별 캘린더 일정
     */
    public List<?> selectCalendar(MonthVO param, String userno) {

        List<CalendarVO> list = sqlSession.selectList("selectCalendar", param);

        ExtFieldVO fld = new ExtFieldVO();
        fld.setField1(userno);

        for (CalendarVO cvo : list) {
            fld.setField2(cvo.getCddate());
            cvo.setList(sqlSession.selectList("selectSchList4Calen", fld));
        }

        return list;
    }

    /**
     * 일정 개수 조회
     *
     * @param param 검색 조건
     * @return 일정 개수
     */
    @SuppressWarnings("unused") // 사용되지 않는 경고를 억제
    public int selectSchCount(SearchVO param) {
        return sqlSession.selectOne("selectSchCount", param);
    }

    /**
     * 일정 리스트 조회
     *
     * @param param 검색 조건
     * @return 일정 리스트
     */
    @SuppressWarnings("unused") // 사용되지 않는 경고를 억제
    public List<?> selectSchList(SearchVO param) {
        return sqlSession.selectList("selectSchList", param);
    }

    /**
     * 일정 저장/수정
     *
     * @param param 일정 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSch(SchVO param) {
        try {
            // 1. 저장 또는 업데이트
            if (!StringUtils.hasText(param.getSsno())) {
                sqlSession.insert("insertSch", param);
            } else {
                sqlSession.update("updateSch", param);
            }

            // 2. 기존 상세 일정 삭제
            sqlSession.delete("deleteSchDetail", param.getSsno());

            // 3. 일정 반복 상세 등록 처리
            insertSchDetails(param);
        } catch (Exception ex) {
            log.error("일정 저장 오류: {}", ex.getMessage(), ex);
            throw ex;  // 트랜잭션 롤백
        }
    }

    /**
     * 상세 일정 등록
     *
     * @param param 일정 정보
     */
    private void insertSchDetails(SchVO param) {

        SchDetailVO param2 = new SchDetailVO();
        param2.setSsno(param.getSsno());
        param2.setSdhour(param.getSsstarthour());
        param2.setSdminute(param.getSsstartminute());

        // 반복 일정 유형에 따른 처리
        switch (param.getSsrepeattype()) {
            case "1":  // 반복 없음
                processDailyRepeat(param, param2);
                break;

            case "2":  // 주간 반복
                processWeeklyRepeat(param, param2);
                break;

            case "3":  // 월간 반복
                processMonthlyRepeat(param, param2);
                break;

            default:
                log.warn("잘못된 반복 유형: {}", param.getSsrepeattype());
        }
    }

    /**
     * 반복 없음 처리
     *
     * @param param  일정 정보
     * @param param2 상세 일정 정보
     */
    private void processDailyRepeat(SchVO param, SchDetailVO param2) {

        Date startDate = DateUtil.str2Date(param.getSsstartdate());
        Date endDate = DateUtil.str2Date(param.getSsenddate());
        int seq = 1;

        while (!startDate.after(endDate)) {
            param2.setSdseq(seq++);
            param2.setSddate(DateUtil.date2Str(startDate));
            sqlSession.insert("insertSchDetail", param2);

            startDate = DateUtil.dateAdd(startDate, 1); // 다음 날짜로 이동
        }
    }

    /**
     * 주간 반복 처리
     *
     * @param param  일정 정보
     * @param param2 상세 일정 정보
     */
    private void processWeeklyRepeat(SchVO param, SchDetailVO param2) {

        Date startDate = DateUtil.str2Date(param.getSsstartdate());
        Date endDate = DateUtil.str2Date(param.getSsrepeatend());
        int dayOfWeek = Integer.parseInt(param.getSsrepeatoption());
        int seq = 1;

        // 시작일을 지정된 요일로 이동
        while (DateUtil.getDayOfWeek(startDate) != dayOfWeek) {
            startDate = DateUtil.dateAdd(startDate, 1);
        }

        // 반복 일정 처리
        while (!startDate.after(endDate)) {
            param2.setSdseq(seq++);
            param2.setSddate(DateUtil.date2Str(startDate));
            sqlSession.insert("insertSchDetail", param2);

            startDate = DateUtil.dateAdd(startDate, 7); // 다음 주로 이동
        }
    }

    /**
     * 월간 반복 처리
     *
     * @param param  일정 정보
     * @param param2 상세 일정 정보
     */
    private void processMonthlyRepeat(SchVO param, SchDetailVO param2) {

        Date startDate = DateUtil.str2Date(param.getSsstartdate());
        Date endDate = DateUtil.str2Date(param.getSsrepeatend());
        int dayOfMonth = Integer.parseInt(param.getSsrepeatoption());
        int seq = 1;

        while (!startDate.after(endDate)) {
            int year = DateUtil.getYear(startDate);
            int month = DateUtil.getMonth(startDate);

            // 현재 월의 반복 날짜 계산
            Date repeatDate = DateUtil.str2Date(String.format("%d-%02d-%02d", year, month, dayOfMonth));
            if (startDate.after(repeatDate)) {
                month++; // 다음 달로 이동
                repeatDate = DateUtil.str2Date(String.format("%d-%02d-%02d", year, month, dayOfMonth));
            }

            // 반복 조건 확인
            if (!startDate.after(repeatDate) && !repeatDate.after(endDate)) {
                param2.setSdseq(seq++);
                param2.setSddate(DateUtil.date2Str(repeatDate));
                sqlSession.insert("insertSchDetail", param2);
            }

            // 다음 월로 이동
            startDate = DateUtil.monthAdd(startDate, 1);
        }
    }

    /**
     * 일정 조회
     *
     * @param param 일정 정보
     * @return 일정 정보
     */
    public SchVO selectSchOne(SchVO param) {
        return sqlSession.selectOne("selectSchOne", param);
    }

    /**
     * 일정 읽기 (일반)
     *
     * @param param 일정 정보
     * @return 일정 정보
     */
    public SchVO selectSchOne4Read(SchVO param) {
        return sqlSession.selectOne("selectSchOne4Read", param);
    }

    /**
     * 일정 삭제
     *
     * @param param 일정 정보
     * @return 삭제된 일정의 수. 성공적으로 삭제된 경우 양수 값을 반환하며, 삭제할 일정이 없는 경우 0을 반환
     */
    public int deleteSch(SchVO param) {
        return sqlSession.delete("deleteSch", param);
    }

    /**
     * makeCalendar 함수를 구현합니다.
     * 지정된 시작일부터 종료일까지의 날짜 데이터를 COM_DATE 테이블에 생성합니다.
     * 시작일이 null이면 COM_DATE 테이블의 마지막 날짜 다음 날부터 300일간의 데이터를 생성합니다.
     * 테이블이 비어있으면 2020-01-01부터 오늘 이후 300일까지의 데이터를 생성합니다.
     *
     * @param startDateStr 시작일 (yyyy-MM-dd 형식, null 가능)
     * @param endDateStr   종료일 (yyyy-MM-dd 형식, null 가능)
     * @return 생성된 레코드 수
     */
    @Transactional
    public int makeCalendar(String startDateStr, String endDateStr) {
        LocalDate startDate;
        LocalDate endDate;

        // 시작일이 null이면 COM_DATE 테이블의 마지막 날짜 다음 날부터 시작
        if (startDateStr == null || startDateStr.isEmpty()) {
            String maxDateStr = sqlSession.selectOne("com.devkbil.mtssbj.calendar.ComDateMapper.selectMaxDate");
            if (maxDateStr == null || maxDateStr.isEmpty()) {
                // 테이블이 비어있으면 2020-01-01부터 시작
                startDate = LocalDate.of(2020, 1, 1);
                // 종료일은 오늘 이후 300일
                endDate = LocalDate.now().plusDays(300);
            } else {
                startDate = LocalDate.parse(maxDateStr, DATE_FORMATTER).plusDays(1);
                // 종료일은 시작일로부터 300일 후
                endDate = startDate.plusDays(299);
            }
        } else {
            startDate = LocalDate.parse(startDateStr, DATE_FORMATTER);
            if (endDateStr == null || endDateStr.isEmpty()) {
                // 종료일이 null이면 시작일로부터 300일 후
                endDate = startDate.plusDays(299);
            } else {
                endDate = LocalDate.parse(endDateStr, DATE_FORMATTER);
            }
        }

        log.info("makeCalendar: startDate={}, endDate={}", startDate, endDate);

        // 종료일이 시작일보다 이전이면 오류
        if (endDate.isBefore(startDate)) {
            log.error("makeCalendar: endDate is before startDate");
            return 0;
        }

        int count = 0;
        LocalDate currentDate = startDate;
        WeekFields weekFields = WeekFields.of(Locale.getDefault());

        // 시작일부터 종료일까지 반복
        while (!currentDate.isAfter(endDate)) {
            String dateStr = currentDate.format(DATE_FORMATTER);

            // 이미 존재하는 날짜는 건너뜀
            if (sqlSession.selectOne("com.devkbil.mtssbj.calendar.ComDateMapper.existsByDate", dateStr)) {
                log.info("makeCalendar: date {} already exists, skipping", dateStr);
                currentDate = currentDate.plusDays(1);
                continue;
            }

            DateVO dateVO = new DateVO();
            dateVO.setDate(dateStr);
            dateVO.setYear(currentDate.getYear());
            dateVO.setMonth(currentDate.getMonthValue());
            dateVO.setDay(currentDate.getDayOfMonth());
            dateVO.setWeekOfYear(currentDate.get(weekFields.weekOfYear()));
            dateVO.setWeekOfMonth(currentDate.get(weekFields.weekOfMonth()));

            // 주 계산 (Oracle의 TO_CHAR(sdate + 1, 'IW') - 1 와 유사하게 구현)
            int week = currentDate.plusDays(1).get(weekFields.weekOfYear()) - 1;
            if (week <= 0) {
                // 연도가 바뀌는 경우 처리
                week = LocalDate.of(currentDate.getYear() - 1, 12, 31).get(weekFields.weekOfYear());
            }
            dateVO.setWeek(week);

            // 요일 계산 (1: 일요일, 2: 월요일, ..., 7: 토요일)
            int dayOfWeek = currentDate.getDayOfWeek().getValue() % 7 + 1;
            dateVO.setDayOfWeek(dayOfWeek);

            // 데이터 삽입
            sqlSession.insert("com.devkbil.mtssbj.calendar.ComDateMapper.insertComDate", dateVO);
            count++;

            // 다음 날짜로 이동
            currentDate = currentDate.plusDays(1);
        }

        log.info("makeCalendar: {} records created", count);
        return count;
    }
}
