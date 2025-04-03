package com.devkbil.mtssbj;

import com.devkbil.mtssbj.util.PdfToHtmlConverter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class PdfToHtmlTest {
    public static void main(String[] args) {
        try {
            // Get the test resources directory path
            Path testResourcesDir = Paths.get("src", "test", "resources", "pdf");

            // List of PDF files to convert
            List<String> pdfFiles = Arrays.asList("1.pdf", "2.pdf", "3.pdf");

            PdfToHtmlConverter converter = new PdfToHtmlConverter();

            for (String pdfFile : pdfFiles) {
                System.out.println("Converting " + pdfFile + "...");
                
                // Get the PDF file from test resources
                File pdfFileObj = testResourcesDir.resolve(pdfFile).toFile();
                
                // Convert PDF to HTML in the same directory as the PDF
                String outputPath = testResourcesDir.resolve(pdfFile.replace(".pdf", "")).toString();
                converter.convertPdfToHtml(pdfFileObj, outputPath);
            }

            System.out.println("Conversion completed successfully!");
        } catch (IOException e) {
            System.err.println("Error converting PDF files: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 