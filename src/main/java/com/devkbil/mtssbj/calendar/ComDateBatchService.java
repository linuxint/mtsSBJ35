package com.devkbil.mtssbj.calendar;

import com.devkbil.mtssbj.schedule.SchService;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * ComDateBatchService
 * - COM_DATE 테이블 관련 배치 서비스 클래스.
 * - makeCalendar 함수를 주기적으로 실행하여 COM_DATE 테이블을 최신 상태로 유지합니다.
 */
@Service
public class ComDateBatchService {

    private static final Logger log = LoggerFactory.getLogger(ComDateBatchService.class);
    private static final Logger logBatch = LoggerFactory.getLogger("BATCH");

    private final SchService schService;

    /**
     * 생성자
     *
     * @param schService 일정 서비스
     */
    public ComDateBatchService(SchService schService) {
        this.schService = schService;
    }

    /**
     * makeCalendar 함수를 주기적으로 실행합니다.
     * 매일 자정에 실행되며, COM_DATE 테이블의 마지막 날짜 다음 날부터 300일간의 데이터를 생성합니다.
     */
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void scheduledMakeCalendar() {
        logBatch.info("Scheduled makeCalendar started");
        try {
            int count = schService.makeCalendar(null, null);
            logBatch.info("Scheduled makeCalendar completed: {} records created", count);
        } catch (Exception e) {
            logBatch.error("Scheduled makeCalendar failed: {}", e.getMessage(), e);
        }
    }

    /**
     * 애플리케이션 시작 시 makeCalendar 함수를 실행합니다.
     * 이 메서드는 수동으로 호출해야 합니다.
     * 자동 실행은 주석 처리되었습니다 (@PostConstruct 제거).
     */
    // @PostConstruct - 자동 실행 비활성화
    public void initMakeCalendar() {
        logBatch.info("Initial makeCalendar started");
        try {
            int count = schService.makeCalendar(null, null);
            logBatch.info("Initial makeCalendar completed: {} records created", count);
        } catch (Exception e) {
            logBatch.error("Initial makeCalendar failed: {}", e.getMessage(), e);
        }
    }

    /**
     * makeCalendar 함수를 수동으로 실행합니다.
     * 이 메서드는 API 엔드포인트 등을 통해 수동으로 호출할 수 있습니다.
     *
     * @param startDateStr 시작일 (yyyy-MM-dd 형식, null 가능)
     * @param endDateStr   종료일 (yyyy-MM-dd 형식, null 가능)
     * @return 생성된 레코드 수
     */
    public int manualMakeCalendar(String startDateStr, String endDateStr) {
        logBatch.info("Manual makeCalendar started: startDate={}, endDate={}", startDateStr, endDateStr);
        try {
            int count = schService.makeCalendar(startDateStr, endDateStr);
            logBatch.info("Manual makeCalendar completed: {} records created", count);
            return count;
        } catch (Exception e) {
            logBatch.error("Manual makeCalendar failed: {}", e.getMessage(), e);
            throw e;
        }
    }
}
