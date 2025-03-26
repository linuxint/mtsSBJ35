package com.devkbil.mtssbj.api.v1.schedule;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.schedule.MonthVO;
import com.devkbil.mtssbj.schedule.SchService;
import com.devkbil.mtssbj.schedule.SchVO;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 일정 관리를 담당하는 REST API 컨트롤러.
 * 일정과 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/schedule")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Schedule API", description = "일정 관리 API")
public class SchRestController {

    private final SchService schService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 일정 목록 조회 API.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 일정 목록 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "일정 목록 조회", description = "월별 일정 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getScheduleList(@ModelAttribute @Valid MonthVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        if (searchVO.getYear() == null) {
            Date today = new Date();
            searchVO.setYear(DateUtil.formatDate(today, "yyyy"));
            searchVO.setMonth(DateUtil.formatDate(today, "MM"));
        }

        String userno = authService.getAuthUserNo();

        List<?> calList = schService.selectCalendar(searchVO, userno);

        // SearchVO 객체 생성 및 설정
        SearchVO searchParam = new SearchVO();
        searchParam.setUserno(userno);
        searchParam.setSearchKeyword(searchVO.getYear() + searchVO.getMonth());

        List<?> schList = schService.selectSchList(searchParam);

        result.put("calList", calList);
        result.put("schList", schList);
        result.put("searchVO", searchVO);

        return ResponseEntity.ok(result);
    }

    /**
     * 일정 상세 조회 API.
     *
     * @param ssno 조회할 일정 번호
     * @return 일정 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "일정 상세 조회", description = "일정 번호로 일정 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{ssno}")
    public ResponseEntity<SchVO> getSchedule(@PathVariable String ssno) {
        if (!StringUtils.hasText(ssno)) {
            return ResponseEntity.badRequest().build();
        }

        SchVO schVO = new SchVO();
        schVO.setSsno(ssno);

        SchVO schInfo = schService.selectSchOne(schVO);
        if (schInfo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(schInfo);
    }

    /**
     * 일정 저장 API.
     *
     * @param schInfo 저장하려는 일정 정보
     * @return 저장된 일정 정보를 담은 ResponseEntity
     */
    @Operation(summary = "일정 저장", description = "신규 또는 수정된 일정 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일정 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<SchVO> saveSchedule(@RequestBody @Valid SchVO schInfo) {
        String userno = authService.getAuthUserNo();

        if (!StringUtils.hasText(schInfo.getSsno())) {
            // 신규 모드
            schInfo.setUserno(userno);
        } else {
            // 수정 모드
            SchVO schVO = new SchVO();
            schVO.setSsno(schInfo.getSsno());
            SchVO oldSchInfo = schService.selectSchOne(schVO);

            if (oldSchInfo == null) {
                return ResponseEntity.notFound().build();
            }

            // 작성자만 수정 가능
            if (!userno.equals(oldSchInfo.getUserno())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        schService.insertSch(schInfo);

        SchVO savedSch = schService.selectSchOne(schInfo);
        return ResponseEntity.ok(savedSch);
    }

    /**
     * 일정 삭제 API.
     *
     * @param ssno 삭제할 일정의 고유 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "일정 삭제", description = "지정된 일정을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "일정 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{ssno}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String ssno) {
        if (!StringUtils.hasText(ssno)) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();

        SchVO schVO = new SchVO();
        schVO.setSsno(ssno);
        SchVO schInfo = schService.selectSchOne(schVO);

        if (schInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 작성자만 삭제 가능
        if (!userno.equals(schInfo.getUserno())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        schService.deleteSch(schVO);

        return ResponseEntity.noContent().build();
    }
}
