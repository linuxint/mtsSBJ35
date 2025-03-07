package com.devkbil.mtssbj.common.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileClasspathTest {

    @TempDir
    Path tempDir;

    private File testResourceDir;
    private ClassLoader originalClassLoader;

    @BeforeEach
    void setUp() throws IOException {
        // Save the original class loader
        originalClassLoader = Thread.currentThread().getContextClassLoader();

        // Create test resource directory
        testResourceDir = new File(tempDir.toFile(), "test-resources");
        testResourceDir.mkdir();

        // Create test files
        createTestFile(testResourceDir, "test.txt", "test content");
        createTestFile(testResourceDir, "test.properties", "key=value");

        // Create a subdirectory with files
        File subDir = new File(testResourceDir, "subdir");
        subDir.mkdir();
        createTestFile(subDir, "subtest.txt", "sub content");

        // Set up custom class loader
        URLClassLoader urlClassLoader = new URLClassLoader(
            new URL[] {tempDir.toFile().toURI().toURL()},
            originalClassLoader
        );
        Thread.currentThread().setContextClassLoader(urlClassLoader);
    }

    private File createTestFile(File dir, String name, String content) throws IOException {
        File file = new File(dir, name);
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(content);
        }
        return file;
    }

    @Test
    void getClasspathRootAbsPath() throws Exception {
        String path = FileClasspath.getClasspathRootAbsPath("test-resources");
        assertNotNull(path);
        assertTrue(path.endsWith("test-resources"));
        assertTrue(new File(path).exists());
    }

    @Test
    void getClasspathRootAbsPath_NonExistent() {
        assertThrows(Exception.class, () -> FileClasspath.getClasspathRootAbsPath("non-existent-path")
        );
    }

    @Test
    void getClassList() {
        List<String> classList = FileClasspath.getClassList(FileClasspathTest.class);
        assertNotNull(classList);
        assertTrue(classList.size() > 0);
        assertTrue(classList.stream().anyMatch(name -> name.contains("FileClasspathTest")));
    }

    @Test
    void getFileListInJar() {
        List<String> extensions = Arrays.asList("txt", "properties");
        Map<String, InputStream> fileMap = FileClasspath.getFileListInJar("test-resources", extensions);

        assertNotNull(fileMap);
        assertTrue(fileMap.size() > 0);
        assertTrue(fileMap.keySet().stream().anyMatch(name -> name.endsWith(".txt")));
        assertTrue(fileMap.keySet().stream().anyMatch(name -> name.endsWith(".properties")));
    }

    @Test
    void getJarFile() {
        assertThrows(IOException.class, () -> FileClasspath.getJarFile("non-existent.jar")
        );
    }

    @Test
    void getClassList_NonExistentClass() {
        // Create a non-existent resource URL
        List<String> classList = FileClasspath.getClassList(Object.class);
        assertNull(classList);
    }
}