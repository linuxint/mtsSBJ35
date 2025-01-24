package com.devkbil.mtssbj.pdf;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

public class FontLoadTest {
    public static void main(String[] args) throws IOException {

        Path resourceDirectory = Paths.get("src", "test", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";

        // 문서 만들기
        final PDDocument doc = new PDDocument();

        // 배경이미지 로드
        //PDImageXObject pdImage = PDImageXObject.createFromFile(webroot + "resources/back.jpg", doc);
        PDImageXObject pdImage = PDImageXObject.createFromFile(absolutePath + "back.jpg", doc);

        //ttf
        InputStream fontStream = new FileInputStream(absolutePath + "/font/" + "NanumGothicLight.ttf");
        PDType0Font fontGulim = PDType0Font.load(doc, fontStream);
        String basefont = fontGulim.getBaseFont();
        Set<String> fonts = Standard14Fonts.getNames();
        Standard14Fonts.FontName font_name_3v= Standard14Fonts.getMappedFontName("NanumGothicLight");
        PDFont pdfFont=  new PDType1Font(font_name_3v);

        //ttf
        TrueTypeCollection trueTypeCollection = new TrueTypeCollection(fontStream);
        trueTypeCollection.processAllFonts(new TrueTypeCollection.TrueTypeFontProcessor() {
            @Override
            public void process(TrueTypeFont trueTypeFont) throws IOException {
                System.out.println(trueTypeFont);
            }
        });
        PDType0Font fontTimes = PDType0Font.load(doc, trueTypeCollection.getFontByName("Menlo-Regular"), true);
    }
}
