package com.devkbil.mtssbj.develop.filesearch;

import com.devkbil.mtssbj.common.util.FileSearchUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 파일 검색 컨트롤러
 * 특정 디렉토리 내의 파일을 검색하고 목록을 제공하는 컨트롤러입니다.
 * <p>
 * 주요 기능:
 * - 지정된 디렉토리 내 모든 파일 검색
 * - 검색된 파일 목록을 웹 페이지에 표시
 * - 파일 정보(이름, 크기, 수정일 등) 제공
 * 기본 검색 경로는 애플리케이션의 'fileupload' 디렉토리입니다.
 */
@Controller
@Tag(name = "File Search Controller", description = "파일 검색 및 파일 목록 조회를 처리하는 컨트롤러")
public class FileSearchController {

    /**
     * 지정된 디렉토리 내 모든 파일을 검색하고 결과 뷰를 반환합니다.
     *
     * @param modelMap 컨트롤러와 뷰 간 데이터 공유에 사용되는 ModelMap 객체입니다.
     * @return 템플릿 페이지 "etc/fileAllIndex"의 경로를 반환합니다.
     */
    @Operation(summary = "디렉토리 내 모든 파일 검색", description = "지정된 디렉토리의 모든 파일을 검색하고 파일 목록을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 파일 목록이 반환되었습니다.")
    @GetMapping("/fileAllIndex")
    public String fileAllIndex(ModelMap modelMap) {
        // 검색 경로 설정
        //showFilesInDIr("C:\\dev_x64\\apache-tomcat-8.5.78", ".txt");
        String filePath = System.getProperty("user.dir") + "/fileupload/"; //localeMessage.getMessage("info.filePath") + "/";  //  첨부 파일 경로

        // 디렉토리 내 파일 검색
        List<?> list = FileSearchUtil.showFIlesInDir3(filePath);

        // View로 데이터 전달
        modelMap.put("listview", list);

        return "etc/fileAllIndex"; // 결과 페이지 반환
    }
}