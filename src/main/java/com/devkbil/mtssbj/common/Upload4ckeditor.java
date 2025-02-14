package com.devkbil.mtssbj.common;

import com.devkbil.mtssbj.common.util.FileUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * CKEditor 관련 파일 업로드 처리 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "CKEditor File Upload", description = "CKEditor를 위한 파일 업로드 처리 API") // Swagger 태그 추가
public class Upload4ckeditor {

    /**
     * CKEditor의 이미지 업로드 처리 메서드
     *
     * @param callback 콜백 함수 번호 (CKEditor에서 제공)
     * @param response 응답 객체
     * @param request  요청 객체
     * @param upload   업로드된 파일
     */
    @Operation(
        summary = "CKEditor 파일 업로드",
        description = "CKEditor에서 업로드된 이미지를 서버에 저장하고 URL을 반환합니다."
    )
    @GetMapping("/upload4ckeditor")
    public void upload(
            @RequestParam(value = "CKEditorFuncNum", required = false) String callback, // 콜백 함수 식별 값
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam("upload") MultipartFile upload // 업로드된 파일 파라미터
    ) {

        // 파일 저장 경로 설정 (서버 실행 위치 기준)
        String filePath = System.getProperty("user.dir") + "/fileupload/"; // localeMessage.getMessage("info.filePath");
        String newName = FileUtil.getNewName(); // 새 파일명 생성
        String realPath = FileUtil.getRealPath(filePath, newName); // 실제 저장 경로 가져오기

        // 파일 저장 처리
        FileUtil.saveFileOne(upload, realPath, newName);

        // 업로드된 파일 유효 URL 생성
        String url = request.getRequestURL().toString();
        int inx = url.lastIndexOf("/");
        url = url.substring(0, inx);
        url = url + "/fileDownload?downname=" + newName;

        // 콜백 함수 내 URL 전달을 위한 JavaScript 스크립트 작성
        url = "<script type='text/javascript'>window.parent.CKEDITOR.tools.callFunction(" + callback + ",'" + url
                + "','upload completed!')</script>";

        // 응답 객체에 결과 작성
        try {
            response.getWriter().write(url);
        } catch (IOException ex) {
            log.error("Error in CKEditor upload (upload4ckeditor).", ex);
        }
    }

}
