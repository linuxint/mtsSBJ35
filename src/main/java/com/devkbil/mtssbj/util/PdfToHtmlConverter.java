package com.devkbil.mtssbj.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.PDXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Component
public class PdfToHtmlConverter {

    private static final float BASE_FONT_SIZE = 16.0f; // 기본 폰트 크기
    private static final float FONT_SCALE = 1.0f; // PDF 폰트 크기 스케일링

    public String convertPdfToHtml(MultipartFile pdfFile, String outputDir) throws IOException {
        return convertPdfToHtml(pdfFile.getBytes(), outputDir);
    }

    public String convertPdfToHtml(File pdfFile, String outputDir) throws IOException {
        return convertPdfToHtml(Files.readAllBytes(pdfFile.toPath()), outputDir);
    }

    private String convertPdfToHtml(byte[] pdfBytes, String outputDir) throws IOException {
        Path outputPath = Paths.get(outputDir);
        Path imagesPath = outputPath.resolve("images");
        Files.createDirectories(imagesPath);

        StringBuilder htmlContent = new StringBuilder();
        htmlContent.append("<!DOCTYPE html>\n")
                  .append("<html>\n<head>\n")
                  .append("<meta charset=\"UTF-8\">\n")
                  .append("<style>\n")
                  .append("body { font-family: Arial, sans-serif; margin: 20px; line-height: 1.5; }\n")
                  .append(".page { margin-bottom: 20px; border: 1px solid #ccc; padding: 10px; }\n")
                  .append("img { max-width: 100%; height: auto; margin: 10px 0; }\n")
                  .append(".text-content { white-space: pre-wrap; }\n")
                  .append(".text-line { margin: 0; }\n")
                  .append("</style>\n")
                  .append("</head>\n<body>\n");

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDPageTree pages = document.getDocumentCatalog().getPages();
            
            for (int i = 0; i < pages.getCount(); i++) {
                PDPage page = pages.get(i);
                float pageHeight = page.getMediaBox().getHeight();
                
                htmlContent.append("<div class=\"page\">\n");
                htmlContent.append("<h2>Page ").append(i + 1).append("</h2>\n");
                htmlContent.append("<div class=\"text-content\">\n");

                // 텍스트 요소 수집
                List<TextElement> textElements = new ArrayList<>();
                PDFTextStripper stripper = new PDFTextStripper() {
                    @Override
                    protected void processTextPosition(TextPosition text) {
                        float y = pageHeight - text.getY();
                        float fontSize = Math.max(text.getFontSize() * FONT_SCALE, BASE_FONT_SIZE);
                        String textContent = text.getUnicode();
                        textContent = textContent.replace("<", "&lt;").replace(">", "&gt;");
                        textElements.add(new TextElement(y, fontSize, textContent));
                    }
                };
                stripper.setStartPage(i + 1);
                stripper.setEndPage(i + 1);
                stripper.getText(document);

                // 텍스트 요소들을 y좌표로 정렬
                textElements.sort(Comparator.comparingDouble(e -> e.y));

                // 같은 줄의 텍스트 요소들을 그룹화
                float currentY = -1;
                StringBuilder currentLine = new StringBuilder();
                float currentFontSize = BASE_FONT_SIZE;

                for (TextElement element : textElements) {
                    if (currentY == -1 || Math.abs(element.y - currentY) > 5) {
                        // 새로운 줄 시작
                        if (currentLine.length() > 0) {
                            htmlContent.append("<p class=\"text-line\" ")
                                     .append("style=\"font-size: ").append(currentFontSize).append("px;\">")
                                     .append(currentLine.toString())
                                     .append("</p>\n");
                        }
                        currentY = element.y;
                        currentLine = new StringBuilder();
                        currentFontSize = element.fontSize;
                    }
                    currentLine.append(element.text);
                }

                // 마지막 줄 처리
                if (currentLine.length() > 0) {
                    htmlContent.append("<p class=\"text-line\" ")
                             .append("style=\"font-size: ").append(currentFontSize).append("px;\">")
                             .append(currentLine.toString())
                             .append("</p>\n");
                }

                htmlContent.append("</div>\n");

                // 이미지 처리
                PDResources resources = page.getResources();
                for (COSName name : resources.getXObjectNames()) {
                    PDXObject xObject = resources.getXObject(name);
                    if (xObject instanceof PDImageXObject image) {
                        String imageFileName = String.format("image_%d_%s.png", i, name.getName());
                        Path imagePath = imagesPath.resolve(imageFileName);
                        
                        try (OutputStream out = Files.newOutputStream(imagePath)) {
                            ImageIO.write(image.getImage(), "PNG", out);
                        }
                        
                        htmlContent.append("<img src=\"images/")
                                 .append(imageFileName)
                                 .append("\" alt=\"Image\">\n");
                    }
                }

                htmlContent.append("</div>\n");
            }
        }

        htmlContent.append("</body>\n</html>");

        Path htmlPath = outputPath.resolve("output.html");
        Files.writeString(htmlPath, htmlContent.toString());

        return htmlPath.toString();
    }

    private static class TextElement {
        final float y;
        final float fontSize;
        final String text;

        TextElement(float y, float fontSize, String text) {
            this.y = y;
            this.fontSize = fontSize;
            this.text = text;
        }
    }
} 