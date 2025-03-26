package com.devkbil.mtssbj.api.v1.crud;

import com.devkbil.mtssbj.crud.CrudService;
import com.devkbil.mtssbj.crud.CrudVO;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

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
 * CRUD 작업을 위한 REST API 컨트롤러
 * - CRUD 데이터의 조회, 생성, 수정, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/crud")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "CRUD API", description = "CRUD 데이터 관리 API")
public class CrudRestController {

    private final CrudService crudService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * CRUD 리스트 조회
     * - 검색 조건(SearchVO)에 따라 CRUD 데이터를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return CRUD 리스트와 페이징 정보를 담은 ResponseEntity
     */
    @Operation(summary = "CRUD 리스트 조회", description = "CRUD 데이터를 조건에 따라 조회하여 리스트로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상적으로 데이터를 반환합니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getCrudList(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        try {
            searchVO.pageCalculate(crudService.selectCrudCount(searchVO)); // startRow, endRow
            List<?> listview = crudService.selectCrudList(searchVO);

            result.put("searchVO", searchVO);
            result.put("list", listview);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error getting CRUD list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRUD 데이터 상세 조회
     *
     * @param crno CRUD 데이터의 고유 번호
     * @return CRUD 데이터 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "CRUD 상세 조회", description = "CRUD 데이터를 상세 조회하여 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상적으로 상세 정보를 반환합니다."),
        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{crno}")
    public ResponseEntity<CrudVO> getCrud(@PathVariable String crno) {
        try {
            CrudVO crudVO = new CrudVO();
            crudVO.setCrno(crno);

            CrudVO crudInfo = crudService.selectCrudOne(crudVO);
            
            if (crudInfo == null) {
                return ResponseEntity.notFound().build();
            }
            
            return ResponseEntity.ok(crudInfo);
        } catch (Exception e) {
            log.error("Error getting CRUD detail", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRUD 데이터 생성
     *
     * @param crudInfo 생성할 CRUD 데이터 정보
     * @return 생성된 CRUD 데이터 정보를 담은 ResponseEntity
     */
    @Operation(summary = "CRUD 생성", description = "새로운 CRUD 데이터를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "정상적으로 데이터를 생성합니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<CrudVO> createCrud(@RequestBody @Valid CrudVO crudInfo) {
        try {
            String userno = authService.getAuthUserNo();
            crudInfo.setUserno(userno);
            
            // crno가 없는 경우에만 새로 생성
            if (crudInfo.getCrno() != null) {
                return ResponseEntity.badRequest().build();
            }
            
            crudService.insertCrud(crudInfo);
            
            // 생성 후 최신 정보 조회
            CrudVO createdCrud = crudService.selectCrudOne(crudInfo);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdCrud);
        } catch (Exception e) {
            log.error("Error creating CRUD", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRUD 데이터 수정
     *
     * @param crno CRUD 데이터의 고유 번호
     * @param crudInfo 수정할 CRUD 데이터 정보
     * @return 수정된 CRUD 데이터 정보를 담은 ResponseEntity
     */
    @Operation(summary = "CRUD 수정", description = "기존 CRUD 데이터를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "정상적으로 데이터를 수정합니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/{crno}")
    public ResponseEntity<CrudVO> updateCrud(@PathVariable String crno, @RequestBody @Valid CrudVO crudInfo) {
        try {
            // 경로 변수와 요청 본문의 값이 일치하는지 확인
            crudInfo.setCrno(crno);
            
            // 존재하는지 확인
            CrudVO existingCrud = crudService.selectCrudOne(crudInfo);
            if (existingCrud == null) {
                return ResponseEntity.notFound().build();
            }
            
            String userno = authService.getAuthUserNo();
            crudInfo.setUserno(userno);
            
            crudService.insertCrud(crudInfo);
            
            // 수정 후 최신 정보 조회
            CrudVO updatedCrud = crudService.selectCrudOne(crudInfo);
            return ResponseEntity.ok(updatedCrud);
        } catch (Exception e) {
            log.error("Error updating CRUD", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * CRUD 데이터 삭제
     *
     * @param crno CRUD 데이터의 고유 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "CRUD 삭제", description = "CRUD 데이터를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "정상적으로 데이터를 삭제합니다."),
        @ApiResponse(responseCode = "404", description = "데이터를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{crno}")
    public ResponseEntity<Void> deleteCrud(@PathVariable String crno) {
        try {
            CrudVO crudVO = new CrudVO();
            crudVO.setCrno(crno);
            
            // 존재하는지 확인
            CrudVO existingCrud = crudService.selectCrudOne(crudVO);
            if (existingCrud == null) {
                return ResponseEntity.notFound().build();
            }
            
            crudService.deleteCrud(crudVO);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting CRUD", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}