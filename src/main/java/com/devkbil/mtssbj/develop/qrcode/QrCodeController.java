package com.devkbil.mtssbj.develop.qrcode;

import com.devkbil.common.util.PdfUtil;
import com.google.zxing.WriterException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.Color;
import java.awt.Rectangle;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * QR 코드 컨트롤러
 * - QR 코드 생성 및 PDF 다운로드와 관련된 HTTP 요청을 처리합니다.
 * - Thymeleaf를 활용하여 QR 코드 데이터를 View로 전달합니다.
 */
@Controller
@Tag(name = "QR Code Controller", description = "QR 코드 생성 및 PDF 다운로드 API")
@RequiredArgsConstructor
@Slf4j
public class QrCodeController {

    // QrCodeService 클래스에 대한 의존성 주입
    private final QrCodeService qrCodeService;

    /**
     * 랜덤 UUID를 기반으로 QR 코드를 생성하고 Base64 형식으로 인코딩합니다.
     * Thymeleaf 뷰에 표시할 QR 코드와 그에 해당하는 키를 모델에 추가합니다.
     *
     * @param model Thymeleaf 뷰에서 접근할 수 있도록 QR 코드 데이터와 키를 저장하는 Model 객체
     * @return 생성된 QR 코드를 렌더링하기 위한 Thymeleaf 뷰 이름 ('thymeleaf/qrcode')
     * @throws IOException     QR 코드 생성 또는 Base64 인코딩 과정에서 오류가 발생한 경우
     * @throws WriterException QR 코드 데이터 작성 중 오류가 발생한 경우
     */
    @Operation(summary = "QR 코드 생성 및 뷰 반환", description = "랜덤한 UUID를 사용해 QR 코드를 생성하고, Base64로 인코딩한 데이터를 Thymeleaf 뷰에 전달합니다.")
    @ApiResponse(responseCode = "200", description = "QR 코드가 성공적으로 생성되었습니다.")
    @ApiResponse(responseCode = "500", description = "QR 코드 생성 중 오류가 발생했습니다.")
    @GetMapping("/qrdraw")
    public String showQrCode(Model model) throws IOException, WriterException {

        for (int nLoop = 0; nLoop <= 6; nLoop++) {
            UUID uuidOne = UUID.randomUUID();
            byte[] qrCodeBytes = qrCodeService.generateQrCode(uuidOne.toString());
            String qrCode = Base64.getEncoder().encodeToString(qrCodeBytes);
            model.addAttribute("qrCode" + nLoop, qrCode);
            model.addAttribute("qrCodeKey" + nLoop, uuidOne.toString());
        }
        return "thymeleaf/qrcode";
    }

    /**
     * QR 코드가 포함된 PDF 파일 생성 및 다운로드를 처리합니다.
     * 이 메서드는 QR 코드와 관련된 텍스트 정보가 포함된 PDF 문서를 생성한 후,
     * 클라이언트에서 다운로드할 수 있도록 응답으로 PDF 파일을 제공합니다.
     *
     * @param response HTTP 응답 객체로 PDF 콘텐츠를 쓰기 위한 헤더를 설정합니다.
     */
    @Operation(summary = "QR 코드 PDF 다운로드", description = "생성된 QR 코드를 PDF 형식으로 다운로드합니다.")
    @ApiResponse(responseCode = "200", description = "PDF 파일이 성공적으로 생성 및 다운로드되었습니다.")
    @ApiResponse(responseCode = "500", description = "PDF 생성 중 오류가 발생했습니다.")
    @GetMapping("/qrpdf")
    public void downQrCodePdf(HttpServletResponse response) {
        PdfUtil pdfUtil = new PdfUtil();
        Date nowDate = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            PDImageXObject pdImage = null;

            Path resourceDirectory = Paths.get("src", "test", "resources");
            String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";
            InputStream fontStream = new FileInputStream(absolutePath + "/font/" + "NanumGothicBold.ttf");
            PDType0Font pdType0Font = PDType0Font.load(document, fontStream);

            // 안내 메시지
            String outDate = simpleDateFormat.format(nowDate);

            drawText(QrConstant.TEXT_MSG, Color.BLACK, pdType0Font, QrConstant.TEXT_FONT_SIZE, QrConstant.TEXT_STARTX, QrConstant.TEXT_STARTY, contentStream);
            drawText(outDate, Color.BLACK, pdType0Font, QrConstant.SEQNO_FONT_SIZE, QrConstant.TEXT_STARTX + 370, QrConstant.TEXT_STARTY, contentStream);

            // 세로 반복
            for (int nYloopCnt = 1; nYloopCnt <= QrConstant.QR_Y_COUNT; nYloopCnt++) {
                // 가로 반복
                for (int nXloopCnt = 1; nXloopCnt <= QrConstant.QR_X_COUNT; nXloopCnt++) {
                    // QR 코드 박스 생성
                    getQrBox(pdImage, document, contentStream,
                        QrConstant.QRCODE_STARTX + (QrConstant.QRCODE_WIDTH + QrConstant.QRCODE_XGAP) * (nXloopCnt - 1), // 가로 좌표
                        QrConstant.QRCODE_STARTY + (QrConstant.QRCODE_YGAP + QrConstant.QRCODE_HEIGHT) * (nYloopCnt - 1) // 세로 좌표
                    ); // Y행 X열
                }
            }

            contentStream.close();

            responsePdf(response, QrConstant.PDF_FILE_NAME); // 브라우저에서 PDF 열기 설정

            document.save(response.getOutputStream()); // 파일 저장

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    /**
     * PDF 페이지에 QR 코드를 생성하고 해당 박스를 추가합니다.
     *
     * @param pdImage       QR 코드 이미지로 사용할 PDImageXObject
     * @param document      PDF 문서를 나타내는 PDDocument
     * @param contentStream QR 코드와 박스를 작성할 PDPageContentStream
     * @param xpos          QR 코드가 위치할 x 좌표
     * @param ypos          QR 코드가 위치할 y 좌표
     * @throws IOException     PDF 콘텐츠 스트림에 쓰기 시 오류가 발생한 경우
     * @throws WriterException QR 코드 생성 중 오류가 발생한 경우
     */
    private void getQrBox(PDImageXObject pdImage, PDDocument document, PDPageContentStream contentStream, int xpos, int ypos) throws IOException, WriterException {
        pdImage = PDImageXObject.createFromByteArray(document, qrCodeService.generateQrCode(UUID.randomUUID().toString()), null);
        contentStream.drawImage(pdImage, xpos, ypos, QrConstant.QRCODE_WIDTH, QrConstant.QRCODE_HEIGHT); // QR 코드 배치
        Rectangle signBox = new Rectangle(xpos + QrConstant.QRCODE_WIDTH, ypos, -QrConstant.SEALBOX_WIDTH, QrConstant.SEALBOX_HEIGHT);
        drawRect(contentStream, QrConstant.SEALBOX_BGCOLOR, signBox, QrConstant.SEALBOX_LINECOLOR);
    }

    /**
     * HttpServletResponse를 PDF 파일 반환으로 설정합니다.
     *
     * @param response Content-Disposition 헤더에 파일명을 포함하도록 설정된 HttpServletResponse 객체
     * @param filename Content-Disposition 헤더에 사용될 PDF 파일명
     * @return 설정된 ServletResponse 객체
     */
    private ServletResponse responsePdf(HttpServletResponse response, String filename) {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filename);

        return response;
    }

    /**
     * 주어진 콘텐츠 스트림에 지정된 색상과 외곽선 색상으로 직사각형을 그립니다.
     *
     * @param content   직사각형을 그릴 PDPageContentStream
     * @param fillcolor 직사각형을 채우는 데 사용할 색상
     * @param rect      직사각형의 위치와 크기를 정의하는 Rectangle 객체
     * @param linecolor 직사각형 외곽선을 그릴 데 사용할 색상
     * @throws IOException 콘텐츠 스트림 쓰기 중 오류가 발생한 경우
     */
    private void drawRect(PDPageContentStream content, Color fillcolor, Rectangle rect, Color linecolor) throws IOException {
        content.setNonStrokingColor(fillcolor);
        content.addRect(rect.x, rect.y, rect.width, rect.height);
        content.fill();
        content.addRect(rect.x, rect.y, rect.width, rect.height);
        content.setStrokingColor(linecolor);
        content.stroke();
    }

    /**
     * 주어진 위치에 제공된 폰트와 크기를 사용하여 PDF 페이지에 텍스트를 렌더링합니다.
     *
     * @param text          PDF 페이지에 렌더링할 텍스트
     * @param font          텍스트 렌더링에 사용할 폰트
     * @param fontSize      텍스트 렌더링에 사용할 폰트 크기
     * @param left          텍스트의 시작점 x 좌표(가로 위치)
     * @param bottom        텍스트의 시작점 y 좌표(세로 위치)
     * @param contentStream PDF 페이지에 콘텐츠를 추가하는 데 사용되는 PDPageContentStream
     */
    public void drawText(String text, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) {

        try {
            contentStream.setFont(font, fontSize);
            contentStream.beginText();
            contentStream.newLineAtOffset(left, bottom);
            contentStream.showText(text);
            contentStream.endText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 주어진 색상, 폰트, 크기를 사용하여 지정된 좌표에 텍스트 문자열을 PDF 문서에 그립니다.
     *
     * @param text          PDF 문서에 그릴 텍스트 문자열
     * @param color         텍스트 색상
     * @param font          텍스트 렌더링에 사용할 폰트
     * @param fontSize      폰트 크기
     * @param left          텍스트의 위치를 나타내는 x 좌표
     * @param bottom        텍스트의 위치를 나타내는 y 좌표
     * @param contentStream PDF 문서에 쓰이는 PDPageContentStream 인스턴스
     */
    public void drawText(String text, Color color, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) {

        try {
            contentStream.setFont(font, fontSize);
            contentStream.setNonStrokingColor(color);
            contentStream.beginText();
            contentStream.newLineAtOffset(left, bottom);
            contentStream.showText(text);
            contentStream.endText();
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}