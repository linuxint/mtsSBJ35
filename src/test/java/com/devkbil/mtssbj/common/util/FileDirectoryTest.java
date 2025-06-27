package com.devkbil.mtssbj.common.util;

import com.devkbil.common.util.FileDirectory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileDirectoryTest {

    @TempDir
    Path tempDir;

    private File createTestFile(String name, String content) throws IOException {
        File file = new File(tempDir.toFile(), name);
        try (java.io.Writer writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void makeBasePath() {
        String dirPath = tempDir.toFile().getAbsolutePath() + "/test/path";
        FileDirectory.makeBasePath(dirPath);

        File dir = new File(dirPath);
        assertTrue(dir.exists());
        assertTrue(dir.isDirectory());
    }

    @Test
    void removeDir_File() throws IOException {
        File testFile = createTestFile("test.txt", "test content");
        FileDirectory.removeDir(testFile);
        assertFalse(testFile.exists());
    }

    @Test
    void removeDir_Directory() throws IOException {
        // Create directory with files
        File testDir = new File(tempDir.toFile(), "testDir");
        testDir.mkdir();
        createTestFile("testDir/file1.txt", "content1");
        createTestFile("testDir/file2.txt", "content2");

        FileDirectory.removeDir(testDir);
        assertFalse(testDir.exists());
    }

    @Test
    void removeDir_String() throws IOException {
        // Create directory with files
        File testDir = new File(tempDir.toFile(), "testDir");
        testDir.mkdir();
        createTestFile("testDir/file1.txt", "content1");

        FileDirectory.removeDir(testDir.getAbsolutePath());
        assertFalse(testDir.exists());
    }

    @Test
    void makeDirectory() {
        String dirPath = tempDir.toFile().getAbsolutePath() + "/newDir";
        assertTrue(FileDirectory.makeDirectory(dirPath));
        assertTrue(new File(dirPath).exists());
        assertTrue(new File(dirPath).isDirectory());
    }

    @Test
    void move() throws Exception {
        // Create source file
        File sourceFile = createTestFile("source.txt", "test content");
        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();

        FileDirectory.move(sourceFile.getAbsolutePath(), targetDir.getAbsolutePath());

        assertFalse(sourceFile.exists());
        assertTrue(new File(targetDir, sourceFile.getName()).exists());
    }

    @Test
    void moveTree() throws Exception {
        // Create source directory with files
        File sourceDir = new File(tempDir.toFile(), "source");
        sourceDir.mkdir();
        createTestFile("source/file1.txt", "content1");
        createTestFile("source/file2.txt", "content2");

        File targetDir = new File(tempDir.toFile(), "target");
        targetDir.mkdir();

        FileDirectory.moveTree(sourceDir.getAbsolutePath(), targetDir.getAbsolutePath());

        assertFalse(sourceDir.exists());
        assertTrue(new File(targetDir, sourceDir.getName()).exists());
        assertTrue(new File(targetDir, "source/file1.txt").exists());
        assertTrue(new File(targetDir, "source/file2.txt").exists());
    }

    @Test
    void makeAndMove() throws Exception {
        // Create source file
        File sourceFile = createTestFile("source.txt", "test content");
        String targetPath = tempDir.toFile().getAbsolutePath() + "/target";

        FileDirectory.makeAndMove(sourceFile.getAbsolutePath(), targetPath);

        assertFalse(sourceFile.exists());
        assertTrue(new File(targetPath).exists());
        assertTrue(new File(targetPath, sourceFile.getName()).exists());
    }

    @Test
    void makeAndMoveTree() throws Exception {
        // Create source directory with files
        File sourceDir = new File(tempDir.toFile(), "source");
        sourceDir.mkdir();
        createTestFile("source/file1.txt", "content1");
        createTestFile("source/file2.txt", "content2");

        String targetPath = tempDir.toFile().getAbsolutePath() + "/target";

        FileDirectory.makeAndMoveTree(sourceDir.getAbsolutePath(), targetPath);

        assertFalse(sourceDir.exists());
        assertTrue(new File(targetPath).exists());
        assertTrue(new File(targetPath, sourceDir.getName()).exists());
        assertTrue(new File(targetPath, "source/file1.txt").exists());
        assertTrue(new File(targetPath, "source/file2.txt").exists());
    }
}