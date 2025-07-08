package com.devkbil.mtssbj.api.v1.admin.server;

import com.devkbil.mtssbj.admin.server.HWVO;
import com.devkbil.mtssbj.admin.server.HWService;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.search.ServerSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서버 하드웨어 관리 REST API 컨트롤러
 * - 서버 하드웨어 데이터의 조회, 저장, 읽기, 삭제 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/server/hw")
@Slf4j
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "Server Hardware API", description = "서버 하드웨어 관리 API")
public class HWRestController {

    private final HWService hwService;

    /**
     * 모든 서버 하드웨어 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 서버 하드웨어 목록을 담은 ResponseEntity
     */
    @Operation(summary = "서버 하드웨어 리스트 조회", description = "모든 서버 하드웨어의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "하드웨어 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getHWList(ServerSearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<?> listview = hwService.getHWList(searchVO);
            
            result.put("list", listview);
            result.put("searchParams", searchVO);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving hardware list: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 특정 서버 하드웨어의 상세 정보를 조회합니다.
     *
     * @param hwId 조회할 하드웨어 ID
     * @return 하드웨어 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "서버 하드웨어 상세 조회", description = "서버 하드웨어 ID(hwId)에 해당하는 정보를 읽어옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "하드웨어 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "하드웨어를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{hwId}")
    public ResponseEntity<HWVO> getHW(@PathVariable String hwId) {
        if (!StringUtils.hasText(hwId)) {
            return ResponseEntity.badRequest().build();
        }

        HWVO hwVO = hwService.getHWDetail(hwId);
        
        if (hwVO == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(hwVO);
    }

    /**
     * 서버 하드웨어 데이터를 저장하거나 업데이트합니다.
     *
     * @param hwInfo 저장 또는 업데이트할 서버 하드웨어 데이터 객체
     * @return 저장된 하드웨어 정보를 담은 ResponseEntity
     */
    @Operation(summary = "서버 하드웨어 저장", description = "새로운 서버 하드웨어를 저장하거나 기존 데이터를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "하드웨어 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<HWVO> saveHW(@RequestBody @Valid HWVO hwInfo) {
        try {
            int affectedRows = !StringUtils.hasText(hwInfo.getHwId())
                    ? hwService.insertHW(hwInfo)
                    : hwService.updateHW(hwInfo);
                    
            if (affectedRows > 0) {
                HWVO savedHW = hwService.getHWDetail(hwInfo.getHwId());
                return ResponseEntity.ok(savedHW);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error saving hardware: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 서버 하드웨어 데이터를 삭제합니다.
     *
     * @param hwId 삭제할 하드웨어 ID
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "서버 하드웨어 삭제", description = "서버 하드웨어 ID(hwId)에 해당하는 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "하드웨어 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{hwId}")
    public ResponseEntity<Void> deleteHW(@PathVariable String hwId) {
        if (!StringUtils.hasText(hwId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            int affectedRows = hwService.deleteHW(hwId);
            
            if (affectedRows > 0) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error deleting hardware: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}