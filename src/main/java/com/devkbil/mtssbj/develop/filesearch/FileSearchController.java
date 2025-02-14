package com.devkbil.mtssbj.develop.filesearch;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

import static com.devkbil.mtssbj.common.util.FileSearchUtil.showFIlesInDir3;

/**
 * File Search Controller
 * - 특정 디렉토리 내 파일을 조회하는 기능을 제공합니다.
 * - 파일 목록이 View로 전달됩니다.
 */
@Controller
@Tag(name = "File Search Controller", description = "파일 검색 및 파일 목록 조회를 처리하는 컨트롤러")
public class FileSearchController {

    /**
     * 디렉토리 내 모든 파일 검색
     * - 지정된 경로의 파일을 조회합니다.
     * - 검색된 파일 목록을 View에 전달합니다.
     *
     * @param modelMap View와 데이터를 공유하기 위한 모델 객체
     * @return 파일 목록 페이지
     * @throws IOException 디렉토리 접근 중 오류 발생 시 예외 처리
     */
    @Operation(summary = "디렉토리 내 모든 파일 검색", description = "지정된 디렉토리의 모든 파일을 검색하고 파일 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 파일 목록이 반환되었습니다.")
    @GetMapping("/fileAllIndex")
    public String fileAllIndex(ModelMap modelMap) {
        // 검색 경로 설정
        //showFilesInDIr("C:\\dev_x64\\apache-tomcat-8.5.78", ".txt");
        String filePath = System.getProperty("user.dir") + "/fileupload/"; //localeMessage.getMessage("info.filePath") + "/";  //  첨부 파일 경로

        // 디렉토리 내 파일 검색
        List<?> list = showFIlesInDir3(filePath);

        // View로 데이터 전달
        modelMap.put("listview", list);

        return "etc/fileAllIndex"; // 결과 페이지 반환
    }
}
