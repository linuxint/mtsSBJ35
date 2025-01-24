package com.devkbil.mtssbj.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Slf4j
public class PdfUtil {

    public void PdfUtil() {
    }

    /**
     * 박스를 그린다.
     *
     * @param content
     * @param color
     * @param rect
     * @param fill
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
     * 글씨를 쓴다.
     *
     * @param text
     * @param font
     * @param fontSize
     * @param left
     * @param bottom
     * @param contentStream
     * @throws Exception
     */
    public void drawText(String text, PDFont font, int fontSize, float left, float bottom, PDPageContentStream contentStream) throws Exception {
        contentStream.beginText();
        contentStream.setFont(font, fontSize);
        contentStream.newLineAtOffset(left, bottom);
        contentStream.showText(text);
        contentStream.endText();
    }

    /**
     * 라인을 그린다.
     *
     * @param contentStream
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @throws IOException
     */
    private void drawLine(PDPageContentStream contentStream, float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }

    /**
     * 테이블을 그린다.
     *
     * @param page
     * @param contentStream
     * @param y
     * @param margin
     * @param content
     * @throws Exception
     */
    public void drawTable(PDPage page, PDPageContentStream contentStream, PDFont font, float y, float margin, String[][] content) throws Exception {
        final int rows = content.length;
        final int cols = content[0].length;

        final float rowHeight = 20f;
        final float tableWidth = page.getMediaBox().getWidth() - (2 * margin);
        final float tableHeight = rowHeight * rows;

        final float colWidth = tableWidth / (float) cols;
        final float cellMargin = 5f;

        // 행을 그린다.
        float nexty = y;
        for (int i = 0; i <= rows; i++) {
            drawLine(contentStream, margin, nexty, margin + tableWidth, nexty);
            nexty -= rowHeight;
        }

        // 열을 그린다.
        float nextx = margin;
        for (int i = 0; i <= cols; i++) {
            drawLine(contentStream, nextx, y, nextx, y - tableHeight);
            nextx += colWidth;
        }

        float textx = margin + cellMargin;
        float texty = y - 15;

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

    public static boolean separate(File file, int startPage, int endPage) {
        return separate(file, endPage - startPage + 1, startPage, endPage);
    }

    public static boolean separate(File file, int page, int startPage, int endPage) {
        try {
            //pdf 파일 로드
            PDDocument document = Loader.loadPDF(file);
            // 분리함수사용하기 위한 객체 생성.
            Splitter splitter = new Splitter();
            splitter.setStartPage(startPage);
            splitter.setSplitAtPage(page);
            splitter.setEndPage(endPage);
            // 분리후 파일 리스트
            List<PDDocument> Pages = splitter.split(document);

            Iterator<PDDocument> iterator = Pages.listIterator();
            //분리된 파일 저장
            int i = 1;
            while (iterator.hasNext()) {
                PDDocument pd = iterator.next();
                String folderPath = file.getParent();
                String fileName = file.getName().substring(0, file.getName().lastIndexOf("."));
                int start = startPage + (i - 1) * page;
                int end = i * page > endPage ? i * page : endPage;
                pd.save(folderPath + File.separator + fileName + (start + "~" + end) + ".pdf");
            }
            System.out.println("PDF 분리완료!");
            document.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

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
     * pdf to images
     *
     * @param pdf
     * @param type
     * @throws Exception
     */
    public static void convertToSeparateImageFiles(File pdf, String type) throws Exception {
        int DPI = 300;
        ImageType IMAGE_TYPE = ImageType.RGB;//This can be GRAY,ARGB,BINARY, BGR

//        try (PDDocument document = PDDocument.load(pdf)) { // pdfbox 2.x
        try (PDDocument document = Loader.loadPDF(pdf)) { // pdfbox 3.x
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); page++) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, IMAGE_TYPE);

                File outputFile = new File(pdf.getAbsoluteFile().getParent() +
                        File.separator + pdf.getName() + "-P-" + page + "." + type);
                ImageIO.write(bim, type, outputFile);
            }
        }
    }

    /**
     * pdf to image with compression
     *
     * @param pdf
     * @param type
     * @throws Exception
     */
    public static void convertToSeparateImageFilesWithCompression(File pdf, String type) throws Exception {
        int DPI = 300;
        ImageType IMAGE_TYPE = ImageType.RGB;//This can be GRAY,ARGB,BINARY, BGR

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
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, IMAGE_TYPE);

                    File outputFile = new File(pdf.getAbsoluteFile().getParent() +
                            File.separator + pdf.getName() + "-compressed-P-" + page + "." + type);
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
     * PDF to single page tiff files
     *
     * @param pdf
     * @throws Exception
     */
    public static void convertToSinglePageTiffs(File pdf) throws Exception {
        int DPI = 300;
        ImageType IMAGE_TYPE = ImageType.BINARY;//This can be GRAY,ARGB,BINARY, BGR

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
                    BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, IMAGE_TYPE);

                    File outputFile = new File(pdf.getAbsoluteFile().getParent() +
                            File.separator + pdf.getName() + "-compressed-P-" + page + ".tif");
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
     * PDF to a multi-page tiff file
     *
     * @param pdf
     * @param outputTiff
     * @throws Exception
     */
    public static void convertToMultipageTiff(File pdf, File outputTiff) throws Exception {
        int DPI = 300;
        ImageType IMAGE_TYPE = ImageType.GRAY;//This can be GRAY,ARGB,BINARY, BGR

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
                        BufferedImage bim = pdfRenderer.renderImageWithDPI(page, DPI, IMAGE_TYPE);

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


}
