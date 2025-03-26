package com.devkbil.mtssbj.api.v1.admin.organ;

import com.devkbil.mtssbj.admin.organ.DeptService;
import com.devkbil.mtssbj.admin.organ.DeptVO;
import com.devkbil.mtssbj.common.TreeMaker;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 부서 관리 REST API 컨트롤러
 * - 부서 트리 조회, 저장, 수정, 삭제와 관련된 REST API를 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/dept")
@Slf4j
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "Department API", description = "부서 관리 API")
public class DeptRestController {

    private final DeptService deptService;
    private final EtcService etcService;
    private final TreeMaker treeMaker = new TreeMaker();
    private final AuthService authService;

    /**
     * 부서 트리 구조를 조회합니다.
     *
     * @return 부서 트리 데이터를 담은 ResponseEntity
     */
    @Operation(summary = "부서 트리 조회", description = "부서의 계층 트리 구조를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서 트리 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getDeptList() {
        Map<String, Object> result = new HashMap<>();

        try {
            List<?> listview = deptService.selectDept();
            String treeStr = treeMaker.makeTreeByHierarchy(listview);

            result.put("deptList", listview);
            result.put("treeStr", treeStr);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error retrieving department list: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(result);
        }
    }

    /**
     * 특정 부서 정보를 조회합니다.
     *
     * @param deptno 조회할 부서 번호
     * @return 부서 정보를 담은 ResponseEntity
     */
    @Operation(summary = "부서 상세 조회", description = "지정된 부서 번호의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "부서를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{deptno}")
    public ResponseEntity<DeptVO> getDept(@PathVariable String deptno) {
        DeptVO deptInfo = deptService.selectDeptOne(deptno);

        if (deptInfo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(deptInfo);
    }

    /**
     * 부서 데이터를 저장하거나 기존 데이터를 업데이트합니다.
     *
     * @param deptInfo 저장 혹은 업데이트할 부서 정보를 포함하는 객체
     * @return 저장된 부서 정보를 담은 ResponseEntity
     */
    @Operation(summary = "부서 저장", description = "새로운 부서를 저장하거나 기존 부서를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<DeptVO> saveDept(@RequestBody @Valid DeptVO deptInfo) {
        try {
            int affectedRows = deptService.insertDept(deptInfo);

            if (affectedRows > 0) {
                // 저장 후 최신 정보 조회
                DeptVO savedDept = deptService.selectDeptOne(deptInfo.getDeptno());
                return ResponseEntity.ok(savedDept);
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error saving department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 특정 부서 정보를 삭제합니다.
     *
     * @param deptno 삭제할 부서 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "부서 삭제", description = "지정된 부서 번호(deptno)에 해당하는 부서를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "부서 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{deptno}")
    public ResponseEntity<Void> deleteDept(@PathVariable String deptno) {
        try {
            int affectedRows = deptService.deleteDept(deptno);

            if (affectedRows > 0) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } catch (Exception e) {
            log.error("Error deleting department: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}