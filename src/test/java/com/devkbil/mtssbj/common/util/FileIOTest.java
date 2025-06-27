package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileIO;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileIOTest {

    @TempDir
    Path tempDir;

    @Test
    void readFileForText() throws IOException {
        File testFile = new File(tempDir.toFile(), "test.txt");
        String testContent = "Hello, World!\nTest line 2";
        try (java.io.Writer writer = Files.newBufferedWriter(testFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(testContent);
        }

        String content = FileIO.readFileForText(testFile.getAbsolutePath());
        assertEquals(testContent, content);
    }

    @Test
    void readFileForBinary() throws IOException {
        File testFile = new File(tempDir.toFile(), "test.bin");
        byte[] testData = {1, 2, 3, 4, 5};
        try (FileOutputStream fos = new FileOutputStream(testFile)) {
            fos.write(testData);
        }

        byte[] data = FileIO.readFileForBinary(testFile.getAbsolutePath());
        assertArrayEquals(testData, data);
    }

    @Test
    void readFile() throws IOException {
        String testContent = "Test content";
        ByteArrayInputStream inputStream = new ByteArrayInputStream(testContent.getBytes(StandardCharsets.UTF_8));

        String content = FileIO.readFile(inputStream);
        assertEquals(testContent, content);
    }

    @Test
    void readFileByLine() throws IOException {
        File testFile = new File(tempDir.toFile(), "test.txt");
        String[] testLines = {"Line 1", "Line 2", "Line 3"};
        try (PrintWriter writer = new PrintWriter(testFile, StandardCharsets.UTF_8)) {
            for (String line : testLines) {
                writer.println(line);
            }
        }

        String[] lines = FileIO.readFileByLine(testFile.getAbsolutePath());
        assertArrayEquals(testLines, lines);
    }

    @Test
    void addLineToFile() throws IOException {
        File testFile = new File(tempDir.toFile(), "test.txt");
        String initialContent = "Initial line";
        String additionalLine = "Additional line";

        try (java.io.Writer writer = Files.newBufferedWriter(testFile.toPath(), StandardCharsets.UTF_8)) {
            writer.write(initialContent + "\n");
        }

        FileIO.addLineToFile(testFile.getAbsolutePath(), additionalLine);

        String[] lines = FileIO.readFileByLine(testFile.getAbsolutePath());
        assertEquals(2, lines.length);
        assertEquals(initialContent, lines[0]);
        assertEquals(additionalLine, lines[1]);
    }

    @Test
    void updateFile() throws IOException {
        File testFile = new File(tempDir.toFile(), "test.txt");
        String[] newContent = {"Line 1", "Line 2", "Line 3"};

        FileIO.updateFile(testFile.getAbsolutePath(), newContent);

        String[] lines = FileIO.readFileByLine(testFile.getAbsolutePath());
        assertArrayEquals(newContent, lines);
    }

    @Test
    void readNonExistentFile() {
        File nonExistentFile = new File(tempDir.toFile(), "nonexistent.txt");
        assertThrows(IOException.class, () -> FileIO.readFileForText(nonExistentFile.getAbsolutePath()));
    }
}