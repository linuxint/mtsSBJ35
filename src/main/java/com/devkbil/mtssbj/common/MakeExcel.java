package com.devkbil.mtssbj.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jxls.builder.JxlsOutputFile;
import org.jxls.transform.poi.JxlsPoiTemplateFillerBuilder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Excel 파일 생성 및 다운로드를 처리하는 유틸리티 클래스.
 * Jxls 3.0을 사용하여 템플릿 기반의 Excel 파일을 생성하고,
 * HTTP 응답을 통해 클라이언트에게 다운로드를 제공합니다.
 */
@Slf4j
public class MakeExcel {

    /**
     * MakeExcel 클래스의 기본 생성자.
     * Excel 파일 생성 및 다운로드 기능을 초기화합니다.
     */
    public MakeExcel() {
    }

    /**
     * 현재 시간을 기반으로 기본 파일 이름을 생성합니다.
     * 형식: yyyyMMddHHmm (예: 202401011230)
     *
     * @return 생성된 파일 이름 문자열
     */
    public String get_Filename() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmm");
        return ft.format(new Date());
    }

    /**
     * 사용자 지정 접두사를 포함한 파일 이름을 생성합니다.
     * 형식: [접두사]yyyyMMddHHmm (예: test202401011230)
     *
     * @param pre 파일 이름 앞에 추가할 접두사
     * @return 접두사가 포함된 파일 이름 문자열
     */
    public String get_Filename(String pre) {
        return pre + get_Filename();
    }

    /**
     * 템플릿 파일 기반으로 엑셀 데이터 바인딩 및 다운로드 (Jxls 3.0 적용)
     *
     * @param request      HTTP Request 객체
     * @param response     HTTP Response 객체
     * @param beans        데이터를 바인딩할 Map (Jxls 템플릿에서 참조)
     * @param filename     다운받을 엑셀 파일 이름
     * @param templateFile 사용할 템플릿 파일 이름
     */
    public void download(HttpServletRequest request, HttpServletResponse response, Map<String, Object> beans,
                         String filename, String templateFile) {

        // 새로운 템플릿 파일 경로: ClassLoader로 리소스를 로드
        String templatePath = "templates/xlsx/" + templateFile;

        // ClassLoader를 사용해 리소스 읽기
        try (InputStream templateInputStream = getClass().getClassLoader().getResourceAsStream(templatePath)) {

            if (templateInputStream == null) {
                throw new FileNotFoundException("템플릿 파일을 찾을 수 없습니다: " + templatePath);
            }
            // 응답 헤더 설정
            responseExcel(response, filename);

            // 템플릿 출력용 파일 생성
            File tempFile = File.createTempFile("tempExcel-", ".xlsx");

            try {
                JxlsOutputFile jxlsOutputFile = new JxlsOutputFile(tempFile);
                // 1. Jxls 3.0 API를 사용하여 템플릿 처리 및 데이터 바인딩
                JxlsPoiTemplateFillerBuilder
                    .newInstance()
                    .withTemplate(templateInputStream) // 템플릿 파일
                    .build()
                    .fill(beans, jxlsOutputFile); // 데이터와 매핑 후 출력 파일에 작성

                // 2. Apache POI를 사용해서 쉬트 이름 변경
                try (FileInputStream fis = new FileInputStream(tempFile);
                     Workbook workbook = new XSSFWorkbook(fis)) {

                    // 기존 쉬트가 존재할 경우 이름을 변경
                    if (workbook.getNumberOfSheets() > 0) {
                        workbook.setSheetName(0, beans.get(ExcelConstant.SHEET_KEY_NAME).toString()); // 첫 번째 시트의 이름 변경
                    }

                    // 다시 파일로 저장
                    try (OutputStream os = new FileOutputStream(tempFile)) {
                        workbook.write(os);
                    }
                }

                // 3. 작성된 엑셀 파일을 응답 스트림으로 전송
                try (InputStream fileInputStream = new FileInputStream(tempFile);
                     OutputStream responseOutputStream = response.getOutputStream()) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        responseOutputStream.write(buffer, 0, bytesRead);
                    }
                }
            } finally {
                if (tempFile.exists()) {
                    tempFile.delete(); // 임시 파일 삭제
                }
            }

        } catch (IOException e) {
            log.error("엑셀 생성 및 다운로드 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("Excel 파일 생성 실패", e);
        }
    }

    /**
     * 엑셀 파일 다운로드 응답 헤더 설정
     *
     * @param response HTTP 응답 객체
     * @param filename 반환할 엑셀 파일 이름
     * @throws UnsupportedEncodingException 지원되지 않는 인코딩 예외
     */
    private void responseExcel(HttpServletResponse response, String filename) throws UnsupportedEncodingException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
            "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + ".xlsx\"");
    }
}
