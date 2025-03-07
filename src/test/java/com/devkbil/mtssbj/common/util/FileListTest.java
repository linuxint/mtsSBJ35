package com.devkbil.mtssbj.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileListTest {

    @TempDir
    Path tempDir;

    private File testDir;
    private File subDir;

    @BeforeEach
    void setUp() throws IOException {
        testDir = new File(tempDir.toFile(), "testDir");
        testDir.mkdir();

        // Create test files
        createTestFile(testDir, "file1.txt", "content1");
        createTestFile(testDir, "file2.txt", "content2");

        // Create subdirectory with files
        subDir = new File(testDir, "subDir");
        subDir.mkdir();
        createTestFile(subDir, "subfile1.txt", "subcontent1");
        createTestFile(subDir, "subfile2.txt", "subcontent2");
    }

    private File createTestFile(File dir, String name, String content) throws IOException {
        File file = new File(dir, name);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void getFileListFromDir() {
        File[] files = FileList.getFileListFromDir(testDir.getAbsolutePath());
        assertNotNull(files);
        assertEquals(3, files.length); // 2 files + 1 subdirectory
    }

    @Test
    void getFileListFromDirWithSubFile() {
        FileFilter filter = file -> true; // Accept all files
        List<File> files = FileList.getFileListFromDirWithSubFile(testDir.getAbsolutePath(), filter);

        assertNotNull(files);
        assertEquals(5, files.size()); // 2 files + 1 subdirectory + 2 subfiles
    }

    @Test
    void getAllFileStrListFromDir() {
        String[] files = FileList.getAllFileStrListFromDir(testDir.getAbsolutePath());
        assertNotNull(files);
        assertEquals(3, files.length); // 2 files + 1 subdirectory
        assertTrue(Arrays.asList(files).contains("file1.txt"));
        assertTrue(Arrays.asList(files).contains("file2.txt"));
        assertTrue(Arrays.asList(files).contains("subDir"));
    }

    @Test
    void getFileStrListFromDir() {
        String[] files = FileList.getFileStrListFromDir(testDir.getAbsolutePath());
        assertNotNull(files);
        assertEquals(2, files.length); // Only files, no directories
        assertTrue(Arrays.asList(files).contains("file1.txt"));
        assertTrue(Arrays.asList(files).contains("file2.txt"));
    }

    @Test
    void getAllSubRelDirs() {
        String[] paths = FileList.getAllSubRelDirs(testDir.getAbsolutePath(), tempDir.toFile().getAbsolutePath());
        assertNotNull(paths);
        assertEquals(2, paths.length); // testDir and subDir

        // Verify paths are relative and properly formatted
        assertTrue(Arrays.stream(paths).anyMatch(p -> p.endsWith("testDir/")));
        assertTrue(Arrays.stream(paths).anyMatch(p -> p.endsWith("testDir/subDir/")));
    }

    @Test
    void getAllSubDirs() {
        List<Object> pathArray = new ArrayList<>();
        FileList.getAllSubDirs(testDir.getAbsolutePath(), pathArray);

        assertEquals(1, pathArray.size()); // Only subDir
        assertTrue(pathArray.get(0).toString().endsWith("subDir"));
    }

    @Test
    void getAllSubFiles() throws Exception {
        List<String> files = FileList.getAllSubFiles(testDir.getAbsolutePath());
        assertNotNull(files);
        assertEquals(4, files.size()); // All files including those in subdirectories

        // Test with extension filter
        List<String> txtFiles = FileList.getAllSubFiles(testDir.getAbsolutePath(), Arrays.asList("txt"));
        assertNotNull(txtFiles);
        assertEquals(4, txtFiles.size()); // All .txt files
    }
}
