package com.devkbil.mtssbj.develop.doc.pdf;

import com.devkbil.common.util.PdfUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.RequiredArgsConstructor;

/**
 * PDF 생성 및 출력 컨트롤러
 * - PdfBox 라이브러리를 사용하여 사용자가 요청 시 PDF 생성 및 다운로드를 제공합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "PDF Controller", description = "PDF를 생성하고 다운로드하는 기능을 제공하는 컨트롤러")
public class PdfController {

    private final PdfUtil pdfUtil = new PdfUtil();

    /**
     * PDF 생성 및 다운로드
     * - 요청에 따라 PDF를 동적으로 생성하여 사용자에게 반환합니다.
     * - PDF에는 텍스트, 테이블, 이미지 등의 항목이 포함됩니다.
     *
     * @param request  HTTP 요청 객체
     * @param response HTTP 응답 객체
     * @throws Exception PDF 생성 또는 데이터 처리 중 오류 발생 시 예외 처리
     */
    @Operation(summary = "PDF 생성 및 다운로드", description = "요청 시 동적으로 PDF를 생성하여 사용자에게 반환합니다. PDF에는 텍스트, 테이블 및 이미지가 포함됩니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 PDF를 생성하고 반환합니다.")
    @GetMapping("/pdfdraw")
    public void pdf(HttpServletRequest request, HttpServletResponse response) throws Exception {

        final int pageCount = 2; // 생성할 PDF 페이지 수
        final String webroot = request.getServletContext().getRealPath("/");

        // 리소스 디렉토리 설정
        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";

        // PDF 문서 생성
        final PDDocument doc = new PDDocument();

        // 배경이미지 로드
        //PDImageXObject pdImage = PDImageXObject.createFromFile(webroot + "resources/back.jpg", doc);
        PDImageXObject pdImage = PDImageXObject.createFromFile(absolutePath + "back.jpg", doc);

        // 폰트 생성
        // ttf 파일 사용하기
        InputStream fontStream = new FileInputStream(absolutePath + "/font/" + "NanumGothicLight.ttf");
        PDType0Font fontGulim = PDType0Font.load(doc, fontStream);

        // ttc 파일 사용하기
        //File fontFile = new File("C:/Windows/fonts/gulim.ttc");
        //PDType0Font fontGulim = PDType0Font.load(doc, new TrueTypeCollection(fontFile).getFontByName("Gulim"), true);

        // 두 개의 페이지를 만든다.
        for (int i = 0; i < pageCount; i++) {
            // 페이지 추가
            PDPage blankPage = new PDPage(PDRectangle.A4);
            doc.addPage(blankPage);

            // 현재 페이지 설정
            PDPage page = doc.getPage(i);

            // 컨텐츠 스트림 열기
            PDPageContentStream contentStream = new PDPageContentStream(doc, page);

            // 배경 이미지  그리기
            contentStream.drawImage(pdImage, 0, 0, 595, 842);

            // 글씨 쓰기
            pdfUtil.drawText("PDFBox 라이브러리를 사용하여", fontGulim, 18, 100, 600, contentStream);
            pdfUtil.drawText("PDF파일 만들기", fontGulim, 18, 100, 560, contentStream);

            // 테이블 그리기
            String[][] contents = {
                    {"Apple", "Banana", "1"},
                    {"Chestnut", "Persimmon", "2"},
                    {"Eggplang", "Potato", "3"},
                    {"Guava", "Radish", "4"},
                    {"Lemon", "Lime", "5"}
            };

            pdfUtil.drawTable(page, contentStream, fontGulim, 500, 100, contents);

            // 컨텐츠 스트림 닫기
            contentStream.close();
        }

        // 파일 다운로드 설정
        response.setContentType("application/pdf");
        String fileName = URLEncoder.encode("샘플PDF", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "inline; filename=" + fileName + ".pdf");

        // PDF 파일 출력
        doc.save(response.getOutputStream());
        doc.close(); // PDF 문서 닫기

    }

}