package com.devkbil.mtssbj.admin.code;

import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * 공통 코드를 관리하기 위한 REST 컨트롤러.
 * 검색 조건에 따라 공통 코드 리스트를 조회하는 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/code") // API 기본 URL
@RequiredArgsConstructor
@Tag(name = "CodeRestController", description = "공통 코드 관리 API") // Swagger 문서 상단 태그로 표시
public class CodeRestController {

    private final CodeService codeService;

    @PostMapping("/upload")
    @Operation(
        summary = "엑셀 파일 업로드",
        description = "엑셀 파일을 업로드하여 공통 코드 데이터를 처리합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "엑셀 데이터 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "엑셀 파일에 헤더 또는 데이터가 없습니다."),
        @ApiResponse(responseCode = "500", description = "엑셀 데이터 처리 중 서버 오류 발생")
    })
    public ResponseEntity<String> uploadExcelFile(@RequestParam("file") MultipartFile file) {
        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            // 엑셀 파일을 서비스에서 처리
            String result = codeService.processExcelFile(is);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            log.error("엑셀 데이터를 처리하는 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("엑셀 데이터를 처리하는 중 오류가 발생했습니다.");
        }
    }

    /**
     * 공통 코드 목록 조회
     *
     * @param searchVO 검색 조건을 포함한 요청 정보
     * @return 검색 조건에 맞는 공통 코드 리스트
     */
    @PostMapping("/list")
    @Operation(summary = "공통 코드 목록 조회", description = "검색 조건을 이용하여 공통 코드 리스트를 조회합니다.") // API 메서드의 동작 설명
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "공통 코드 목록 조회 성공"), // 성공 응답
        @ApiResponse(responseCode = "400", description = "입력값이 올바르지 않음"),       // 잘못된 요청
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")        // 서버 오류
    })
    public ResponseEntity<List<?>> getCodeList(@ModelAttribute @Valid SearchVO searchVO) {
        try {
            log.debug("코드 리스트 조회 요청: {}", searchVO);

            // 검색 조건에 따른 페이징 계산
            searchVO.pageCalculate(codeService.selectCodeCount(searchVO));

            // 코드 리스트 조회
            List<?> resultList = codeService.selectCodeList(searchVO);

            // 200 상태와 함께 코드 리스트 반환
            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: 공통 코드 리스트 조회 실패", e);

            // 500 상태 반환 (내부 서버 오류)
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}