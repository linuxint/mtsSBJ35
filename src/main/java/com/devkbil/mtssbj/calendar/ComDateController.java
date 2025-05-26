package com.devkbil.mtssbj.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * ComDateController
 * - COM_DATE 테이블 관련 컨트롤러 클래스.
 * - makeCalendar 함수를 REST API로 노출합니다.
 */
@Tag(name = "Calendar", description = "날짜 데이터 관리 API")
@RestController
@RequestMapping("/api/calendar")
public class ComDateController {

    private final ComDateBatchService comDateBatchService;

    /**
     * 생성자
     *
     * @param comDateBatchService COM_DATE 배치 서비스
     */
    public ComDateController(ComDateBatchService comDateBatchService) {
        this.comDateBatchService = comDateBatchService;
    }

    /**
     * makeCalendar 함수를 수동으로 실행합니다.
     * 
     * 이 API는 다음과 같은 경우에 사용합니다:
     * 1. 특정 기간의 날짜 데이터를 수동으로 생성해야 할 때
     * 2. 자동 생성(애플리케이션 시작 시와 매일 자정)이 충분하지 않을 때
     * 3. 특정 기간의 날짜 데이터를 다시 생성해야 할 때
     * 4. 시스템 관리자가 날짜 데이터를 미리 생성하고자 할 때
     * 
     * 일반적인 사용 시나리오:
     * - 시스템 초기 설정 시 대량의 날짜 데이터 생성
     * - 특정 기간(예: 다음 회계연도)에 대한 날짜 데이터 미리 생성
     * - 날짜 데이터에 오류가 발생한 경우 특정 기간 재생성
     *
     * @param startDate 시작일 (yyyy-MM-dd 형식, 옵션)
     * @param endDate   종료일 (yyyy-MM-dd 형식, 옵션)
     * @return 생성된 레코드 수
     */
    @Operation(
        summary = "날짜 데이터 수동 생성", 
        description = "지정된 기간의 날짜 데이터를 COM_DATE 테이블에 생성합니다. 시작일이 지정되지 않으면 마지막 날짜 다음 날부터 300일간의 데이터를 생성합니다. " +
                "이 API는 특정 기간의 날짜 데이터를 수동으로 생성하거나, 자동 생성이 충분하지 않을 때, 또는 특정 기간의 데이터를 재생성해야 할 때 사용합니다. " +
                "일반적으로 시스템 관리자가 시스템 초기 설정, 특정 기간 데이터 미리 생성, 또는 데이터 오류 수정 시 사용합니다.")
    @PostMapping("/make-calendar")
    public ResponseEntity<Map<String, Object>> makeCalendar(
            @Parameter(description = "시작일 (yyyy-MM-dd 형식)", example = "2023-01-01") @RequestParam(required = false) String startDate,
            @Parameter(description = "종료일 (yyyy-MM-dd 형식)", example = "2023-12-31") @RequestParam(required = false) String endDate) {

        int count = comDateBatchService.manualMakeCalendar(startDate, endDate);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", count + " records created");
        response.put("count", count);

        return ResponseEntity.ok(response);
    }
}
