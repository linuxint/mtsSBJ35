package com.devkbil.mtssbj;

import lombok.extern.slf4j.Slf4j;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.junit.jupiter.api.Test;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class SimpleTextExtractorTest {

    @Test
    public void testConvertPdf() throws IOException, TikaException {
        // 테스트 파일 로드
        File testFile = ResourceUtils.getFile("classpath:Demo1.pdf");
        assertNotNull(testFile);

        String text = convertSimple(testFile.getAbsolutePath());
        assertNotNull(text);
        log.info("Extracted text (Simple): \n{}", text);
    }

    @Test
    public void testConvertPptx() throws IOException {
        // 테스트 파일 로드
        File testFile = ResourceUtils.getFile("classpath:인천둘레길스템프북.pptx");
        assertNotNull(testFile);

        String text = convert(testFile.getAbsolutePath());
        assertNotNull(text);
        log.info("Extracted text (Advanced): \n{}", text);
    }

    @Test
    public void testConvertXlsx() throws IOException {
        // 테스트 파일 로드
        File testFile = ResourceUtils.getFile("classpath:CodeList.xlsx");
        assertNotNull(testFile);

        String text = convert(testFile.getAbsolutePath());
        assertNotNull(text);
        log.info("Extracted text (Advanced): \n{}", text);
    }

    @Test
    public void testConvertHwp() throws IOException {
        // 테스트 파일 로드
        File testFile = ResourceUtils.getFile("classpath:02.입찰유의서.hwp");
        assertNotNull(testFile);

        String text = convert(testFile.getAbsolutePath());
        assertNotNull(text);
        log.info("Extracted text (Advanced): \n{}", text);
    }

    @Test
    public void testConvertJpg() throws IOException {
        // 테스트 파일 로드
        File testFile = ResourceUtils.getFile("classpath:20250103131941_764.jpg");
        assertNotNull(testFile);

        String text = convert(testFile.getAbsolutePath());
        assertNotNull(text);
        log.info("Extracted text (Advanced): \n{}", text);
    }

    /**
     * 간단하게 Tika로 파일에서 텍스트 추출
     */
    private String convertSimple(String fileName) throws TikaException, IOException {
        Tika tika = new Tika();
        return tika.parseToString(new File(fileName));
    }

    /**
     * 고급 파서를 사용한 텍스트 추출
     */
    private String convert(String fileName) {
        InputStream stream = null;
        String text = null;
        try {
            stream = new FileInputStream(fileName);

            // OCR config 설정
            TesseractOCRConfig config = new TesseractOCRConfig();
            config.setSkipOcr(false); // OCR 사용
            config.setLanguage("kor"); // 한국어 OCR 지원

            ParseContext context = new ParseContext();
            context.set(TesseractOCRConfig.class, config);

            AutoDetectParser parser = new AutoDetectParser();

            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata, context);
            text = handler.toString();
        } catch (Exception e) {
            log.error("Error during text extraction: {}", e.getMessage());
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    log.error("Error closing stream");
                }
            }
        }
        return text;
    }
}