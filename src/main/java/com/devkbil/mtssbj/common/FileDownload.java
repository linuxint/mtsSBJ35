package com.devkbil.mtssbj.common;

import com.devkbil.common.util.FileUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * A controller class responsible for handling file download requests.
 * This class allows files to be downloaded from a specified directory.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class FileDownload {

    /**
     * Handles file download requests. Enables downloading of files from a specified directory.
     *
     * @param filename the name of the file to be displayed in the download dialog; optional.
     * @param downname the actual name of the file to be downloaded; optional.
     * @param request the HttpServletRequest object that contains the request the client has made.
     * @param response the HttpServletResponse object used to send the file or related errors.
     */
    @GetMapping("fileDownload")
    public void fileDownload(@RequestParam(value = "filename", required = false) String filename, @RequestParam(value = "downname", required = false) String downname, HttpServletRequest request, HttpServletResponse response) {
        String path = System.getProperty("user.dir") + "/fileupload/";
        log.info("Searching files in directory: {}", path); // filePath 확인

        if (!StringUtils.hasText(filename)) {
            filename = downname;
        }

        try {
            // 파일명 UTF-8로 인코딩
            filename = URLEncoder.encode(filename, StandardCharsets.UTF_8);

            String realPath = FileUtil.getRealPath(path, downname) + downname;

            File file = new File(realPath);
            if (!file.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }

            // HTTP 응답 헤더 설정
            response.setHeader("Content-Disposition", "attachment; filename=\"" + filename + "\"");
            response.setContentLengthLong(file.length()); // 파일 크기 설정

            // 파일 데이터를 버퍼링된 스트림으로 전송
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                 BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {

                byte[] buffer = new byte[8192]; // 8KB 버퍼
                int bytesRead;
                while ((bytesRead = bis.read(buffer)) != -1) {
                    bos.write(buffer, 0, bytesRead);
                }

                bos.flush(); // 모든 데이터가 제대로 전송되도록 강제 flush
            }
        } catch (FileNotFoundException e) {
            log.error("File not found error: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            } catch (IOException ioException) {
                log.error("Error sending 404 response: {}", ioException.getMessage());
            }
        } catch (IOException e) {
            log.error("I/O error during file download: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
            } catch (IOException ioException) {
                log.error("Error sending 500 response: {}", ioException.getMessage());
            }
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage());
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unexpected error");
            } catch (IOException ioException) {
                log.error("Error sending unexpected error response: {}", ioException.getMessage());
            }
        }
    }

}