package com.devkbil.mtssbj.common;

import com.devkbil.common.util.FileUpload;
import com.devkbil.common.util.FileUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CKEditor 관련 파일 업로드 처리 컨트롤러
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "CKEditor File Upload", description = "CKEditor를 위한 파일 업로드 처리 API") // Swagger 태그 추가
public class Upload4ckeditor {

    /**
     * CKEditor의 파일 업로드 요청을 처리합니다. 업로드된 이미지를 서버에 저장하고
     * 접근할 수 있는 URL을 반환합니다.
     *
     * @param callback CKEditor에서 제공된 응답을 처리하기 위한 콜백 함수 번호.
     * @param response 응답을 클라이언트로 보내기 위해 사용되는 HttpServletResponse 객체.
     * @param request  HTTP 요청의 세부 정보를 포함하는 HttpServletRequest 객체.
     * @param upload   요청을 통해 업로드된 파일로, MultipartFile로 제공됩니다.
     * @throws IOException 파일 저장이나 응답 처리 중 입출력 오류가 발생할 경우.
     */
    @Operation(summary = "CKEditor 파일 업로드", description = "CKEditor에서 업로드된 이미지를 서버에 저장하고 URL을 반환합니다.")
    @GetMapping("/upload4ckeditor")
    public void upload(
            @RequestParam(value = "CKEditorFuncNum", required = false) String callback, // 콜백 함수 식별 값
            HttpServletResponse response,
            HttpServletRequest request,
            @RequestParam("upload") MultipartFile upload // 업로드된 파일 파라미터
    ) throws IOException {

        // 파일 저장 경로 설정 (서버 실행 위치 기준)
        String filePath = System.getProperty("user.dir") + "/fileupload/"; // localeMessage.getMessage("info.filePath");
        String newName = FileUtil.getNewName(); // 새 파일명 생성
        String realPath = FileUtil.getRealPath(filePath, newName); // 실제 저장 경로 가져오기

        // 파일 저장 처리
        FileUpload.saveFileOne(upload, realPath, newName);

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