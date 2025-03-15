package com.devkbil.common.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import lombok.extern.slf4j.Slf4j;

/**
 * {@code FileClasspath} 클래스는 클래스패스, JAR 파일 관리 및 리소스 검색과 관련된 유틸리티 메서드를 제공합니다.
 * 주로 파일 기반 리소스 관리와 관련된 작업을 지원하기 위해 설계되었습니다.
 */
@Slf4j
public class FileClasspath {
    /**
     * 제공된 클래스패스의 루트 디렉토리 절대 경로를 검색합니다.
     * 컨텍스트 ClassLoader를 사용하여 리소스를 로드하고 이를 절대 파일 경로로 변환합니다.
     *
     * @param classpath 루트 디렉토리의 절대 경로를 검색할 리소스의 클래스패스
     * @return 지정된 클래스패스의 루트 디렉토리 절대 경로
     * @throws Exception 지정된 클래스패스가 존재하지 않거나 확인할 수 없는 경우
     */
    public static String getClasspathRootAbsPath(String classpath) throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(classpath);
        if (url == null || url.getFile() == null) {
            throw new Exception(classpath + " is not exists.");
        }

        File file = new File(url.getFile());
        if (file == null || !file.exists()) {
            throw new Exception(classpath + " is not exists.");
        }
        String path = file.getAbsolutePath();
        return path;
    }

    /**
     * 주어진 클래스의 패키지 또는 JAR 파일에서 완전히 정의된 클래스 이름 목록을 검색합니다.
     * 클래스가 위치한 디렉토리 또는 JAR 파일을 확인하고 모든 .class 파일의 이름을 추출합니다.
     *
     * @param clz 패키지를 검색할 클래스
     * @return 지정된 클래스와 동일한 패키지 안에 있는 클래스 이름 목록
     * (검색 중 오류가 발생한 경우 null을 반환)
     */
    public static List<String> getClassList(Class<?> clz) {
        List<String> classNames = new ArrayList<String>();
        File resourceFile = null;

        try {
            resourceFile = new File(clz.getResource("").getFile());
        } catch (Exception e) {
            return null;
        }

        if (resourceFile.exists() && resourceFile.isDirectory()) {
            String packageName = FileUtil.class.getPackage().getName();
            for (String fileName : resourceFile.list()) {
                classNames.add(packageName + "." + fileName.replace(".class", ""));
            }
        } else {
            String path = resourceFile.getPath().substring(6).replace('\\', '/');
            String[] info = path.split("!/");
            JarFile jar = null;
            try {
                jar = new JarFile(info[0]);
            } catch (IOException e1) {
            }
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                String entryName = entries.nextElement().getName();
                if (entryName.startsWith(info[1]) && entryName.length() > 16) {
                    classNames.add(entryName.replace("/", ".").replace(".class", ""));
                }
            }
        }

        return classNames;
    }

    /**
     * 특정 클래스패스와 파일 확장자를 기반으로 JAR 파일의 파일 이름과 해당 InputStream의 맵을 검색합니다.
     * JAR 파일의 항목들을 스캔하고 {@code extList}에 제공된 확장자와 일치하는 파일을 필터링합니다.
     *
     * @param classpath JAR 파일에서 파일을 검색할 경로
     * @param extList   필터링할 파일 확장자 목록
     * @return 확장자와 일치하는 파일 이름(키)과 파일 InputStream(값)을 포함하는 맵
     */
    public static Map<String, InputStream> getFileListInJar(String classpath, List<String> extList) {
        if (classpath == null || extList == null) {
            throw new IllegalArgumentException("Classpath and extension list cannot be null");
        }

        Map<String, InputStream> resultMap = new HashMap<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL resourceUrl = classLoader.getResource(classpath);

        if (resourceUrl == null) {
            log.warn("Resource not found: {}", classpath);
            return resultMap;
        }

        File resourceFile = new File(resourceUrl.getFile());

        // Handle directory resources
        if (resourceFile.exists() && resourceFile.isDirectory()) {
            processDirectory(resourceFile, extList, resultMap, classpath, classLoader);
            return resultMap;
        }

        // Handle JAR resources
        String resourcePath = resourceUrl.getFile().replace('\\', '/');
        if (resourcePath.contains("!")) {
            // JAR file path
            String[] resourceInfo = resourcePath.split("!");
            String jarPath = resourceInfo[0].replaceFirst("^file:", "");
            String basePath = resourceInfo[1].substring(1); // Remove leading '/'

            try (JarFile jarFile = new JarFile(jarPath)) {
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry jarEntry = jarEntries.nextElement();
                    String entryName = jarEntry.getName();

                    if (entryName.startsWith(basePath)) {
                        String[] fileSplit = entryName.split("\\.");
                        if (fileSplit.length > 1 && extList.contains(fileSplit[1])) {
                            try {
                                resultMap.put(entryName, jarFile.getInputStream(jarEntry));
                            } catch (IOException e) {
                                log.error("Failed to get input stream for entry: {}", entryName, e);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("Error processing JAR file: {}", jarPath, e);
            }
        } else {
            // Handle regular file resources
            processDirectory(resourceFile, extList, resultMap, classpath, classLoader);
        }

        return resultMap;
    }

    private static void processDirectory(File directory, List<String> extList,
                                      Map<String, InputStream> resultMap, String basePath,
                                      ClassLoader classLoader) {
        File[] files = directory.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                processDirectory(file, extList, resultMap, basePath, classLoader);
            } else {
                String extension = getFileExtension(file.getName());
                if (extList.contains(extension)) {
                    try {
                        String relativePath = file.getAbsolutePath()
                            .substring(file.getAbsolutePath().indexOf(basePath));
                        InputStream inputStream = classLoader.getResourceAsStream(relativePath);
                        if (inputStream != null) {
                            resultMap.put(relativePath, inputStream);
                        }
                    } catch (Exception e) {
                        log.error("Failed to process file: {}", file.getName(), e);
                    }
                }
            }
        }
    }

    private static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        return lastDotIndex > 0 ? fileName.substring(lastDotIndex + 1) : "";
    }

    /**
     * 지정된 클래스패스를 기반으로 {@link JarFile}을 가져옵니다.
     * 컨텍스트 클래스 로더를 사용하여 리소스를 찾고 해당 JAR 파일에 접근할 수 있도록 엽니다.
     *
     * @param classpath 리소스를 찾고 JarFile을 가져올 클래스패스
     * @return 지정된 클래스패스에 해당하는 {@link JarFile} 객체
     * @throws IOException JAR 파일에 접근 중 I/O 오류가 발생한 경우
     */
    public static JarFile getJarFile(String classpath) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(classpath);

        if (url == null) {
            throw new IOException("Resource not found: " + classpath);
        }

        String path = url.getFile().replace('\\', '/').replaceFirst("^file:", "");
        if (path == null || path.isEmpty()) {
            throw new IOException("Invalid path for resource: " + classpath);
        }

        String[] info = path.split("!/");
        if (info.length == 0) {
            throw new IOException("Invalid JAR path format: " + path);
        }

        try {
            return new JarFile(info[0]);
        } catch (IOException e) {
            throw new IOException("Failed to open JAR file: " + info[0], e);
        }
    }
}