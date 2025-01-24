package com.devkbil.mtssbj;

import com.devkbil.mtssbj.common.util.FileSearchUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Slf4j
public class SimpleTextExtractorTest {
    public static void main(String[] args) throws Exception {
        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";

        String[] fileArr = { absolutePath + "인천둘레길스템프북.pptx" , absolutePath + "Demo1.pdf", ""};
        // Create a Tika instance with the default configuration
        List<FileVO> fileArr2 = FileSearchUtil.showFIlesInDir3(System.getProperty("user.dir") + "/fileupload/");
        String filename = "";
        for(FileVO fileVO : fileArr2) {
            filename = fileVO.getFilepath() + "/" + fileVO.getFilename();
            System.out.println(filename);
            System.out.println("=".repeat(100));
            System.out.println(convert(filename));
            System.out.println(convertSimple(filename));
        }
    }

    public static String convertSimple(String fileName) throws TikaException, IOException {
        Tika tika = new Tika();
        String text = tika.parseToString(new File(fileName));
        return text;
    }
    public static String convert(String fileName){
        InputStream stream = null;
        String text = null;
        try {
            stream = new FileInputStream(fileName);

            TesseractOCRConfig config = new TesseractOCRConfig();
            config.setSkipOcr(false);
            config.setLanguage("kor");

            ParseContext context = new ParseContext();
            context.set(TesseractOCRConfig.class, config);

            AutoDetectParser parser = new AutoDetectParser();

            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            parser.parse(stream, handler, metadata, context);
            text = handler.toString();
        }catch (Exception e) {
            log.error(e.getMessage());
        }finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (IOException e) {
                    System.out.println("Error closing stream");
                }
        }
        return text;
    }
}
