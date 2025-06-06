package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.InputStream;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 동적 테이블 데이터 처리를 위한 REST 컨트롤러.
 * 지정된 테이블에 데이터를 삽입하거나 업데이트하기 위한 Excel 파일 업로드 및 처리를 담당합니다.
 */
@RestController
@RequestMapping("/api/dbtool")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dynamic DBTool RestController", description = "동적 테이블 데이터를 처리하는 REST 컨트롤러")
public class DbtoolRestController {

    private final DbtoolService dbtoolService;

    /**
     * 엑셀 파일을 업로드하고 지정된 테이블에 데이터를 동적으로 삽입하거나 업데이트합니다.
     *
     * @param file 사용자가 업로드한 엑셀 파일로, 테이블명, 키, 컬럼, 데이터를 포함합니다
     * @return 처리 성공 시 성공 메시지를 포함한 ResponseEntity,
     *         실패 시 오류 메시지를 포함한 ResponseEntity를 반환합니다
     */
    @PostMapping("/uploadDynamic")
    @Operation(
            summary = "엑셀 파일 업로드 및 동적 테이블 처리",
            description = "엑셀 파일을 업로드해 동적으로 지정된 테이블에 데이터를 삽입 또는 업데이트합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "엑셀 데이터 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "유효하지 않은 테이블 또는 컬럼 정보"),
        @ApiResponse(responseCode = "500", description = "엑셀 데이터 처리 중 오류 발생")
    })
    public ResponseEntity<String> uploadExcelFileDynamic(@RequestParam("file") MultipartFile file) {
        try (InputStream is = new BufferedInputStream(file.getInputStream())) {
            String result = dbtoolService.processDynamicExcelFile(is);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("엑셀 데이터를 처리하는 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body("엑셀 데이터를 처리하는 중 오류가 발생했습니다.");
        }
    }
}
