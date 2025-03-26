package com.devkbil.mtssbj.api.v1.develop.filesearch;

import com.devkbil.common.util.FileSearchUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 파일 검색 REST API 컨트롤러
 * 특정 디렉토리 내의 파일을 검색하고 목록을 JSON 형태로 제공하는 컨트롤러입니다.
 * <p>
 * 주요 기능:
 * - 지정된 디렉토리 내 모든 파일 검색
 * - 검색된 파일 목록을 JSON 형태로 반환
 * - 파일 정보(이름, 크기, 수정일 등) 제공
 * 기본 검색 경로는 애플리케이션의 'fileupload' 디렉토리입니다.
 */
@RestController
@RequestMapping("/api/v1/develop/file-search")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "File Search API", description = "파일 검색 및 파일 목록 조회를 처리하는 API")
public class FileSearchRestController {

    /**
     * 지정된 디렉토리 내 모든 파일을 검색하고 결과를 JSON 형태로 반환합니다.
     *
     * @param path 검색할 디렉토리 경로 (선택적, 기본값은 fileupload 디렉토리)
     * @return 파일 목록을 담은 ResponseEntity
     */
    @Operation(summary = "디렉토리 내 모든 파일 검색", description = "지정된 디렉토리의 모든 파일을 검색하고 파일 목록을 JSON 형태로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "파일 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/files")
    public ResponseEntity<Map<String, Object>> getFileList(
            @RequestParam(value = "path", required = false) String path) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 검색 경로 설정 (path 파라미터가 없으면 기본 경로 사용)
            String filePath = path;
            if (filePath == null || filePath.isEmpty()) {
                filePath = System.getProperty("user.dir") + "/fileupload/";
            }
            
            log.info("Searching files in directory: {}", filePath);
            
            // 디렉토리 내 파일 검색
            List<?> fileList = FileSearchUtil.showFIlesInDir3(filePath);
            
            result.put("files", fileList);
            result.put("path", filePath);
            result.put("count", fileList.size());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error searching files: {}", e.getMessage());
            result.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(result);
        }
    }
}