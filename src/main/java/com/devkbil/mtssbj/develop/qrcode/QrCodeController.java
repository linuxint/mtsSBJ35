package com.devkbil.mtssbj.develop.qrcode;

import com.devkbil.mtssbj.common.util.PdfUtil;
import com.google.zxing.WriterException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.awt.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

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

    // QrCodeService 클래스에 대한 객체 생성
    private final QrCodeService qrCodeService;

    /**
     * QR 코드 생성 후 Thymeleaf 뷰 반환
     * - QR 코드 이미지를 생성하고 Base64로 인코딩하여 View에 전달합니다.
     *
     * @param model Model 객체를 사용하여 생성된 QR 코드를 View에 전달
     * @return Thymeleaf 템플릿 페이지 ("thymeleaf/qrcode")
     * @throws IOException     입출력 예외
     * @throws WriterException QR 코드 생성 중 오류
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
     * QR 코드 PDF 생성 및 다운로드
     * - QR 코드를 생성하고, 생성된 코드를 포함한 PDF 파일을 브라우저에 다운로드합니다.
     *
     * @param response HttpServletResponse 객체를 사용하여 PDF 응답을 설정
     * @throws IOException     입출력 예외
     * @throws WriterException QR 코드 생성 중 오류
     */
    @Operation(summary = "QR 코드 PDF 다운로드", description = "생성된 QR 코드를 PDF 형식으로 다운로드합니다.")
    @ApiResponse(responseCode = "200", description = "PDF 파일이 성공적으로 생성 및 다운로드되었습니다.")
    @ApiResponse(responseCode = "500", description = "PDF 생성 중 오류가 발생했습니다.")
    @GetMapping("/qrpdf")
    public void downQrCodePdf(HttpServletResponse response) throws IOException, WriterException {
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
            PDType0Font fontName = PDType0Font.load(document, fontStream);

            // 안내메시지
            String outDate = simpleDateFormat.format(nowDate);

            drawText(outDate, fontName, QrConstant.SEQNO_FONT_SIZE, QrConstant.TEXT_STARTX * 25, QrConstant.TEXT_STARTY, contentStream);

            drawText(QrConstant.TEXT_MSG, Color.RED, fontName, QrConstant.TEXT_FONT_SIZE, QrConstant.TEXT_STARTX, QrConstant.TEXT_STARTY, contentStream);

            //drawText("21:13:33 -1", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY-15, contentStream);
            //drawText("21:13:33 -2", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY+(QrConstant.QRCODE_YGAP+QrConstant.QRCODE_HEIGHT)*2-15, contentStream);
            //drawText("21:13:33 -3", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY-15, contentStream);
            //drawText("21:13:33 -4", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY-15, contentStream);
            //drawText("21:13:33 -5", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY-15, contentStream);
            //cnfdrawText("21:13:33 -6", fontGulim,QrConstant.SEQNO_FONT_SIZE,QrConstant.QRCODE_STARTX + 80,QrConstant.QRCODE_STARTY-15, contentStream);

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX, QrConstant.QRCODE_STARTY);//0

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX, QrConstant.QRCODE_STARTY + QrConstant.QRCODE_YGAP + QrConstant.QRCODE_HEIGHT);//2

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX, QrConstant.QRCODE_STARTY + (QrConstant.QRCODE_YGAP + QrConstant.QRCODE_HEIGHT) * 2);//4

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX + QrConstant.QRCODE_WIDTH + QrConstant.QRCODE_XGAP, QrConstant.QRCODE_STARTY);//1

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX + QrConstant.QRCODE_WIDTH + QrConstant.QRCODE_XGAP, QrConstant.QRCODE_STARTY + QrConstant.QRCODE_YGAP + QrConstant.QRCODE_HEIGHT);//3

            getQrBox(pdImage, document, contentStream, QrConstant.QRCODE_STARTX + QrConstant.QRCODE_WIDTH + QrConstant.QRCODE_XGAP, QrConstant.QRCODE_STARTY + (QrConstant.QRCODE_YGAP + QrConstant.QRCODE_HEIGHT) * 2);//5

            contentStream.close();

            responsePdf(response, QrConstant.PDF_FILE_NAME); // 브라우저에서 PDF open 설정

            document.save(response.getOutputStream()); // 파일 저장

        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }

    private void getQrBox(PDImageXObject pdImage, PDDocument document, PDPageContentStream contentStream, int xpos, int ypos) throws IOException, WriterException {
        pdImage = PDImageXObject.createFromByteArray(document, qrCodeService.generateQrCode(UUID.randomUUID().toString()), null);
        contentStream.drawImage(pdImage, xpos, ypos, QrConstant.QRCODE_WIDTH, QrConstant.QRCODE_HEIGHT); //0
        Rectangle signBox = new Rectangle(xpos + QrConstant.QRCODE_WIDTH, ypos, -QrConstant.SEALBOX_WIDTH, QrConstant.SEALBOX_HEIGHT);
        drawRect(contentStream, QrConstant.SEALBOX_BGCOLOR, signBox, QrConstant.SEALBOX_LINECOLOR);
    }

    /**
     * Pdf file download response
     *
     * @param response
     * @param filename
     * @return
     * @throws UnsupportedEncodingException
     */
    private ServletResponse responsePdf(HttpServletResponse response, String filename) throws UnsupportedEncodingException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=" + filename);

        return response;
    }

    /**
     * 인감박스 draw
     *
     * @param content
     * @param fillcolor
     * @param rect
     * @param linecolor
     * @throws IOException
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
     * 글씨를 쓴다.
     *
     * @param text
     * @param left
     * @param bottom
     * @param contentStream
     * @throws Exception
     */
    public void drawText(String text, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) throws IOException {

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
     * 글씨를 쓴다.
     *
     * @param text
     * @param left
     * @param bottom
     * @param contentStream
     * @throws Exception
     */
    public void drawText(String text, Color color, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) throws IOException {

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