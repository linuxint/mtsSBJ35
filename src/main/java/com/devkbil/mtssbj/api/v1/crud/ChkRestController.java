package com.devkbil.mtssbj.api.v1.crud;

import com.devkbil.mtssbj.crud.CrudService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CRUD 작업을 위해 사용되는 REST API 컨트롤러
 * - 리스트 확인 및 선택된 행의 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/crud/chk")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Chk API", description = "CRUD 체크 관련 API")
public class ChkRestController {

    private final CrudService crudService;

    /**
     * 체크 리스트 조회
     * - CRUD 데이터 리스트를 페이징 조건에 맞게 반환합니다.
     *
     * @param searchVO 검색 조건을 담고 있는 객체
     * @return 조회된 데이터와 페이징 정보를 담은 ResponseEntity
     */
    @Operation(
        summary = "체크 리스트 조회",
        description = "CRUD 데이터 리스트를 반환합니다. 페이징 관련 계산과 리스트 데이터를 제공합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상적으로 리스트 반환"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getChkList(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        // CRUD 관련 작업: 페이징 계산 및 리스트 생성
        searchVO.pageCalculate(crudService.selectCrudCount(searchVO)); // startRow, endRow 계산
        List<?> listview = crudService.selectCrudList(searchVO);

        // 결과 데이터 설정
        result.put("searchVO", searchVO);
        result.put("list", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 선택된 행 삭제
     * - 사용자가 선택한 행을 삭제 처리합니다.
     *
     * @param checkRows 삭제할 행(row)의 아이디 배열
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(
        summary = "선택된 행 삭제",
        description = "사용자가 선택한 행을 삭제합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "삭제할 데이터가 없거나 유효하지 않은 경우"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteChk(@RequestBody(required = false) String[] checkRows) {
        if (ObjectUtils.isEmpty(checkRows)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 체크된 행 삭제
            crudService.deleteChk(checkRows);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting checked rows: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
