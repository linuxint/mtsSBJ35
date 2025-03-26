package com.devkbil.mtssbj.api.v1.etc;

import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자와 관련된 리스트, 특히 Alert를 처리하는 REST API 컨트롤러 클래스입니다.
 * 현재 사용자와 관련된 Alert 리스트를 조회하고 페이징 처리된 데이터를 JSON 형태로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/user")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "User List API", description = "사용자 관련 리스트 API")
public class List4UserRestController {

    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 사용자의 Alert 리스트를 페이징 처리하여 JSON 형태로 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @return Alert 리스트를 담은 ResponseEntity
     */
    @Operation(summary = "사용자의 Alert 리스트", description = "현재 사용자와 관련된 Alert 리스트를 페이징 처리하여 JSON 형태로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Alert 리스트 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/alerts")
    public ResponseEntity<Map<String, Object>> getUserAlerts(@ModelAttribute BoardSearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        try {
            String userno = authService.getAuthUserNo();
            searchVO.setSearchExt1(userno);

            // 페이징 계산
            searchVO.pageCalculate(etcService.selectList4UserCount(searchVO)); // startRow, endRow

            // 리스트 조회
            List<?> listview = etcService.selectList4User(searchVO);

            result.put("list", listview);
            result.put("searchParams", searchVO);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving user alerts: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}
