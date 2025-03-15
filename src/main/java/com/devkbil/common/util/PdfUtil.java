package com.devkbil.common.util;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * PDF 파일 조작을 위한 유틸리티 클래스입니다.
 * PDF 파일의 생성, 수정, 분할, 병합 및 이미지 변환 기능을 제공합니다.
 */
@Slf4j
public class PdfUtil {

    /**
     * PDF 이미지 변환시 사용할 DPI 값
     */
    static int dpi = 300;
    /**
     * PDF 이미지 변환시 사용할 이미지 타입 (GRAY, ARGB, BINARY, BGR 중 선택)
     */
    static ImageType imageType = ImageType.RGB;

    /**
     * PDF 파일을 지정된 페이지 범위로 분할합니다.
     *
     * @param pdfFile 분할할 PDF 파일
     * @param numPages 각 분할된 파일에 포함될 페이지 수
     * @param start 분할 시작 페이지 번호
     * @param end 분할 종료 페이지 번호
     * @return 분할 성공 시 true, 실패 시 false
     */
    public static boolean separate(File pdfFile, int numPages, int start, int end) {
        try {
            //pdf 파일 로드
            PDDocument pdfDocument = Loader.loadPDF(pdfFile);
            // 분리함수사용하기 위한 객체 생성.
            Splitter pdfSplitter = new Splitter();
            pdfSplitter.setStartPage(start);
            pdfSplitter.setSplitAtPage(numPages);
            pdfSplitter.setEndPage(end);
            // 분리후 파일 리스트
            List<PDDocument> pdfPages = pdfSplitter.split(pdfDocument);

            Iterator<PDDocument> pageIterator = pdfPages.listIterator();
            //분리된 파일 저장
            int iteration = 1;
            while (pageIterator.hasNext()) {
                PDDocument splitDoc = pageIterator.next();
                String folderPath = pdfFile.getParent();
                String baseName = pdfFile.getName().substring(0, pdfFile.getName().lastIndexOf("."));
                int startPageNum = start + (iteration - 1) * numPages;
                int endPageNum = iteration * numPages > end ? iteration * numPages : end;
                splitDoc.save(folderPath + File.separator + baseName + (startPageNum + "~" + endPageNum) + ".pdf");
            }
            System.out.println("PDF 분리완료!");
            pdfDocument.close();
            return true;
        } catch (Exception error) {
            return false;
        }
    }

    /**
     * PDF 파일을 특정 페이지를 기준으로 두 개의 파일로 분할합니다.
     *
     * @param file 분할할 PDF 파일
     * @param page 분할 기준이 되는 페이지 번호
     * @return 분할 성공 시 true, 실패 시 false
     */
    public static boolean separate(File file, int page) {
        try {
            PDDocument document = Loader.loadPDF(file);
            int pages = document.getNumberOfPages();
            if (pages > page) {
                separate(file, 1, page);
                return separate(file, page + 1, pages);
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }

    /**
     * PDF 파일을 지정된 시작 페이지부터 종료 페이지까지 분할합니다.
     *
     * @param file 분할할 PDF 파일
     * @param startPage 분할 시작 페이지 번호
     * @param endPage 분할 종료 페이지 번호
     * @return 분할 성공 시 true, 실패 시 false
     */
    public static boolean separate(File file, int startPage, int endPage) {
        return separate(file, endPage - startPage + 1, startPage, endPage);
    }

    /**
     * PDF 파일을 이미지 파일로 변환합니다.
     *
     * @param pdf 변환할 PDF 파일
     * @param type 생성할 이미지의 파일 형식 (예: "jpg", "png")
     * @throws Exception PDF 파일 로드 실패 또는 이미지 변환/저장 중 오류 발생 시
     */
    public static void convertToSeparateImageFiles(File pdf, String type) throws Exception {
//        try (PDDocument document = PDDocument.load(pdf)) { // pdfbox 2.x
        try (PDDocument document = Loader.loadPDF(pdf)) { // pdfbox 3.x
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, imageType);

                File outputFile = new File(pdf.getAbsoluteFile().getParent() + File.separator + pdf.getName() + "-P-" + page + "." + type);
                ImageIO.write(bim, type, outputFile);
            }
        }
    }

    /**
     * 여러 PDF 파일들을 하나의 파일로 병합합니다.
     *
     * @param files 병합할 PDF 파일들의 리스트
     * @param path 병합된 PDF 파일이 저장될 경로
     * @return 병합 성공 시 true, 실패 시 false
     */
    public static boolean merge(List<File> files, String path) {
        PDFMergerUtility merger = new PDFMergerUtility();
        //파일 merge 설정
        merger.setDestinationFileName(path);
        files.forEach(f -> {
            try {
                merger.addSource(f);
            } catch (FileNotFoundException e) {
                log.error(e.getMessage());
            }
        });
        try {
            //병합시작...
            merger.mergeDocuments(null);
            System.out.println("PDF merge 완료!");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    /**
     * PDF 파일을 압축된 이미지 파일로 변환합니다.
     * 이미지 품질을 조절하여 파일 크기를 최적화합니다.
     *
     * @param pdf 변환할 PDF 파일
     * @param type 생성할 이미지의 파일 형식 (예: "jpg", "png")
     * @throws Exception PDF 파일 로드 실패, 이미지 변환/저장 중 오류, 또는 압축 처리 중 오류 발생 시
     */
    public static void convertToSeparateImageFilesWithCompression(File pdf, String type) throws Exception {
//        try (PDDocument document = PDDocument.load(pdf)) { // pdfbox 2.x
        try (PDDocument document = Loader.loadPDF(pdf)) { // pdfbox 3.x
            ImageWriter writer = null;
            try {
                writer = ImageIO.getImageWritersByFormatName(type).next();

                ImageWriteParam params = writer.getDefaultWriteParam();
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(0.6f);

                PDFRenderer pdfRenderer = new PDFRenderer(document);

                for (int page = 0; page < document.getNumberOfPages(); page++) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, imageType);

                    File outputFile = new File(pdf.getAbsoluteFile().getParent() + File.separator + pdf.getName() + "-compressed-P-" + page + "." + type);
                    ImageOutputStream outputStream = new FileImageOutputStream(outputFile);
                    writer.setOutput(outputStream);

                    writer.write(null, new IIOImage(bim, null, null), params);
                }
            } finally {
                if (writer != null) {
                    writer.dispose();
                }
            }
        }
    }

    /**
     * PDF 파일의 각 페이지를 개별 TIFF 이미지 파일로 변환합니다.
     * LZW 압축을 사용하여 이미지를 최적화하며, 각 페이지는 별도의 TIFF 파일로 저장됩니다.
     *
     * @param pdf 변환할 PDF 파일
     * @throws Exception PDF 파일 로드 실패, TIFF 변환 중 오류, 또는 파일 저장 중 오류 발생 시
     */
    public static void convertToSinglePageTiffs(File pdf) throws Exception {
//        try (PDDocument document = PDDocument.load(pdf)) { // pdfbox 2.x
        try (PDDocument document = Loader.loadPDF(pdf)) { // pdfbox 3.x

            ImageWriter writer = null;
            try {
                writer = ImageIO.getImageWritersByFormatName("tiff").next();

                ImageWriteParam params = writer.getDefaultWriteParam();
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                // Compression Types: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT
                params.setCompressionType("LZW");
                params.setCompressionQuality(0.8f);

                PDFRenderer pdfRenderer = new PDFRenderer(document);

                for (int page = 0; page < document.getNumberOfPages(); page++) {
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, imageType);

                    File outputFile = new File(pdf.getAbsoluteFile().getParent() + File.separator + pdf.getName() + "-compressed-P-" + page + ".tif");
                    ImageOutputStream outputStream = new FileImageOutputStream(outputFile);
                    writer.setOutput(outputStream);
                    writer.write(null, new IIOImage(bim, null, null), params);
                }
            } finally {
                if (writer != null) {
                    writer.dispose();
                }
            }
        }
    }

    /**
     * PDF 파일을 하나의 다중 페이지 TIFF 파일로 변환합니다.
     * 모든 페이지가 하나의 TIFF 파일에 포함되며, LZW 압축을 사용하여 파일 크기를 최적화합니다.
     *
     * @param pdf PDF 소스 파일
     * @param outputTiff 생성될 TIFF 파일
     * @throws Exception PDF 파일 로드 실패, TIFF 변환 중 오류, 또는 파일 저장 중 오류 발생 시
     */
    public static void convertToMultipageTiff(File pdf, File outputTiff) throws Exception {
//        try (PDDocument document = PDDocument.load(pdf)) { // pdfbox 2.x
        try (PDDocument document = Loader.loadPDF(pdf)) { // pdfbox 3.x
            try (ImageOutputStream ios = ImageIO.createImageOutputStream(outputTiff)) {

                ImageWriter writer = null;
                try {
                    writer = ImageIO.getImageWritersByFormatName("tiff").next();
                    writer.setOutput(ios);
                    writer.prepareWriteSequence(null);

                    ImageWriteParam params = writer.getDefaultWriteParam();
                    params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    // Compression Types: None, PackBits, ZLib, Deflate, LZW, JPEG and CCITT
                    params.setCompressionType("LZW");
                    params.setCompressionQuality(0.8f);

                    PDFRenderer pdfRenderer = new PDFRenderer(document);

                    for (int page = 0; page < document.getNumberOfPages(); page++) {
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, imageType);

                        IIOMetadata metadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(bim), params);
                        writer.writeToSequence(new IIOImage(bim, null, metadata), params);
                    }
                } finally {
                    if (writer != null) {
                        writer.dispose();
                    }
                }
            }
        }
    }

    /**
     * Merges PDF files.
     *
     * @param inputPdfFiles array of input files
     * @param outputPdfFile output file
     */
    public static void mergePdf(File[] inputPdfFiles, File outputPdfFile) {
        try {
            PDFMergerUtility mergerUtility = new PDFMergerUtility();
            mergerUtility.setDestinationFileName(outputPdfFile.getPath());
            for (File inputPdfFile : inputPdfFiles) {
                mergerUtility.addSource(inputPdfFile);
            }
            mergerUtility.mergeDocuments(null);
        } catch (IOException ioe) {
        }
    }

    /**
     * PDF 페이지에 사각형을 그립니다.
     *
     * @param content PDF 페이지의 콘텐츠 스트림
     * @param color   사각형의 색상
     * @param rect    사각형의 위치와 크기를 정의하는 Rectangle 객체
     * @param fill    true인 경우 사각형을 채우고, false인 경우 테두리만 그림
     * @throws IOException PDF 파일 조작 중 오류 발생 시
     */
    public void drawRect(PDPageContentStream content, Color color,
        Rectangle rect, boolean fill) throws IOException {
        content.addRect(rect.x, rect.y, rect.width, rect.height);
        if (fill) {
            content.setNonStrokingColor(color);
            content.fill();
        } else {
            content.setStrokingColor(color);
            content.stroke();
        }
    }

    /**
     * PDF 페이지에 텍스트를 작성합니다.
     *
     * @param text          작성할 텍스트 내용
     * @param font          사용할 PDF 폰트
     * @param fontSize      폰트 크기
     * @param left          텍스트의 왼쪽 좌표
     * @param bottom        텍스트의 하단 좌표
     * @param contentStream PDF 페이지의 콘텐츠 스트림
     * @throws Exception PDF 파일 조작 중 오류 발생 시
     */
    public void drawText(String text, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) throws Exception {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(left, bottom);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * PDF 페이지에 직선을 그립니다.
     *
     * @param contentStream PDF 페이지의 콘텐츠 스트림
     * @param xStart        시작점의 X 좌표
     * @param yStart        시작점의 Y 좌표
     * @param xEnd          끝점의 X 좌표
     * @param yEnd          끝점의 Y 좌표
     * @throws IOException PDF 파일 조작 중 오류 발생 시
     */
    private void drawLine(PDPageContentStream contentStream, float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }

    /**
     * PDF 페이지에 테이블을 그립니다.
     * 지정된 내용을 바탕으로 테이블의 셀, 행, 열을 자동으로 계산하여 그립니다.
     *
     * @param page PDF 페이지 객체
     * @param contentStream PDF 페이지의 콘텐츠 스트림
     * @param font 테이블 내용에 사용할 폰트
     * @param posy 테이블의 상단 Y 좌표
     * @param margin 테이블의 좌우 여백
     * @param content 테이블에 표시할 2차원 문자열 배열 (행과 열의 내용)
     * @throws Exception 테이블 그리기 중 오류 발생 시 (폰트 처리, 그리기 작업 등)
     */
    public void drawTable(PDPage page, PDPageContentStream contentStream, PDFont font, float posy, float margin, String[][] content) throws Exception {
        final int rows = content.length;
        final int cols = content[0].length;

        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        final float tableHeight = rowHeight * rows;

        final float colWidth = tableWidth / (float) cols;
        final float cellMargin = 5f;

        // 행을 그린다.
        float nexty = posy;
        for (int i = 0; i <= rows; i++) {
            drawLine(contentStream, margin, nexty, margin + tableWidth, nexty);
            nexty -= rowHeight;
        }

        // 열을 그린다.
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            drawLine(contentStream, nextx, posy, nextx, posy - tableHeight);
            nextx += colWidth;
        }

        float textx = margin + cellMargin;
        float texty = posy - 15;

        for (int i = 0; i < content.length; i++) {
            for (int j = 0; j < content[i].length; j++) {
                String text = content[i][j];
                drawText(text, font, 12, textx, texty, contentStream);
                textx += colWidth;
            }
            texty -= rowHeight;
            textx = margin + cellMargin;
        }
    }


}