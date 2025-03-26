package com.devkbil.mtssbj.api.v1.admin.server;

import com.devkbil.mtssbj.admin.server.ConnService;
import com.devkbil.mtssbj.admin.server.ConnVO;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.ServerSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
 * 서버 서비스 접속 정보 관리 REST API 컨트롤러
 * - 서비스 접속 정보 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/server/conn")
@Slf4j
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "Server Connection API", description = "서버 서비스 접속 정보 관리 API")
public class ConnRestController {

    private final ConnService connService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 모든 서비스 접속 정보의 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 서비스 접속 정보 리스트를 담은 ResponseEntity
     */
    @Operation(summary = "서비스 접속 정보 목록 조회", description = "모든 서비스 접속 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getServerConnList(ServerSearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        try {
            List<?> listview = connService.getServiceConnList(searchVO);
            result.put("list", listview);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting server connection list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 서비스 접속 상세 정보를 조회합니다.
     *
     * @param connId 읽을 서비스 접속 정보를 식별하는 ID
     * @return 서비스 접속 정보를 담은 ResponseEntity
     */
    @Operation(summary = "서비스 접속 정보 조회", description = "특정 서비스 접속 정보의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "서비스 접속 정보를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @GetMapping("/{connId}")
    public ResponseEntity<ConnVO> getServerConn(@PathVariable String connId) {
        if (!StringUtils.hasText(connId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            ConnVO serviceConnVO = connService.getServiceConnDetail(connId);
            
            if (serviceConnVO == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(serviceConnVO);
        } catch (Exception e) {
            log.error("Error getting server connection detail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 서비스 접속 정보를 저장합니다.
     *
     * @param serviceConnVO 저장할 서비스 접속 정보 데이터 객체
     * @return 저장된 서비스 접속 정보를 담은 ResponseEntity
     */
    @Operation(summary = "서비스 접속 정보 저장", description = "새로운 서비스 접속 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @PostMapping
    public ResponseEntity<ConnVO> createServerConn(@RequestBody ConnVO serviceConnVO) {
        try {
            int affectedRows = connService.insertServiceConn(serviceConnVO);
            
            if (affectedRows > 0) {
                // 저장 후 최신 정보 조회
                ConnVO savedConn = connService.getServiceConnDetail(serviceConnVO.getConnId());
                return ResponseEntity.ok(savedConn);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error creating server connection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 서비스 접속 정보를 업데이트합니다.
     *
     * @param connId 업데이트할 서비스 접속 정보를 식별하는 ID
     * @param serviceConnVO 업데이트할 서비스 접속 정보 데이터 객체
     * @return 업데이트된 서비스 접속 정보를 담은 ResponseEntity
     */
    @Operation(summary = "서비스 접속 정보 업데이트", description = "기존 서비스 접속 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "서비스 접속 정보를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @PutMapping("/{connId}")
    public ResponseEntity<ConnVO> updateServerConn(@PathVariable String connId, @RequestBody ConnVO serviceConnVO) {
        if (!StringUtils.hasText(connId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 경로 변수와 요청 본문의 값이 일치하는지 확인
            serviceConnVO.setConnId(connId);
            
            // 존재하는지 확인
            ConnVO existingConn = connService.getServiceConnDetail(connId);
            if (existingConn == null) {
                return ResponseEntity.notFound().build();
            }
            
            int affectedRows = connService.updateServiceConn(serviceConnVO);
            
            if (affectedRows > 0) {
                // 업데이트 후 최신 정보 조회
                ConnVO updatedConn = connService.getServiceConnDetail(connId);
                return ResponseEntity.ok(updatedConn);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error updating server connection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 서비스 접속 데이터를 삭제합니다.
     *
     * @param connId 삭제할 서비스 접속 데이터를 식별하는 ID
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "서비스 접속 정보 삭제", description = "특정 서비스 접속 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "서비스 접속 정보를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @DeleteMapping("/{connId}")
    public ResponseEntity<Void> deleteServerConn(@PathVariable String connId) {
        if (!StringUtils.hasText(connId)) {
            return ResponseEntity.badRequest().build();
        }

        try {
            // 존재하는지 확인
            ConnVO existingConn = connService.getServiceConnDetail(connId);
            if (existingConn == null) {
                return ResponseEntity.notFound().build();
            }
            
            int affectedRows = connService.deleteServiceConn(connId);
            
            if (affectedRows > 0) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error deleting server connection", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}