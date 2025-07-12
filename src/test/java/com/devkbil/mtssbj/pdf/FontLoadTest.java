package com.devkbil.mtssbj.pdf;

import org.apache.fontbox.ttf.TrueTypeCollection;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FontLoadTest {
    public static void main(String[] args) throws IOException {

        Path resourceDirectory = Paths.get("src", "main", "resources");
        String absolutePath = resourceDirectory.toFile().getAbsolutePath() + "/";

        // 문서 만들기
        final PDDocument doc = new PDDocument();

        // 배경이미지 로드
        // PDImageXObject pdImage = PDImageXObject.createFromFile(webroot + "resources/back.jpg", doc);
        PDImageXObject.createFromFile(absolutePath + "back.jpg", doc);

        // ttf
        try (InputStream fontStream = new FileInputStream(absolutePath + "/font/" + "NanumGothicLight.ttf");
             TrueTypeCollection trueTypeCollection = new TrueTypeCollection(fontStream)) {

        PDType0Font fontGulim = PDType0Font.load(doc, fontStream);
        fontGulim.getBaseFont();
        Standard14Fonts.getNames();
        Standard14Fonts.FontName fontName3v = Standard14Fonts.getMappedFontName("NanumGothicLight");
        new PDType1Font(fontName3v);

        // ttf
        trueTypeCollection.processAllFonts(
            new TrueTypeCollection.TrueTypeFontProcessor() {
                @Override
                public void process(TrueTypeFont trueTypeFont) throws IOException {
                    System.out.println(trueTypeFont);
                }
            });
        PDType0Font.load(doc, trueTypeCollection.getFontByName("Menlo-Regular"), true);
        }
    }
}
