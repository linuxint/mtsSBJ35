package com.devkbil.mtssbj.common.util;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 및 디렉토리 작업을 위한 유틸리티 메서드를 제공하며, 파일 목록 검색, 특정 조건으로 파일 필터링,
 * 상대 경로 또는 절대 경로 추출 등을 수행합니다.
 */
@Slf4j
public class FileList {

    /**
     * 지정된 디렉토리에서 파일 목록을 검색합니다.
     * 제공된 경로가 존재하지 않거나 디렉토리가 아닌 경우 null을 반환합니다.
     *
     * @param path 파일들을 검색할 디렉토리 경로
     * @return 디렉토리의 파일들을 나타내는 File 객체 배열,
     * 경로가 존재하지 않거나 디렉토리가 아닌 경우 null
     */
    public static File[] getFileListFromDir(String path) {
        File dir = new File(path);
        File[] list = null;

        if (dir.exists()) {
            if (dir.isDirectory()) {
                list = dir.listFiles();
            }
        }
        return list;
    }

    /**
     * 지정된 디렉토리에서 주어진 파일 필터 조건에 맞는 파일 및 디렉토리 목록을 재귀적으로 검색합니다.
     *
     * @param path       검색할 디렉토리 경로
     * @param fileFilter 파일 및 디렉토리에 적용할 필터
     * @return 지정된 파일 필터에 맞는 파일 및 디렉토리 목록. 필터에 맞는 하위 디렉토리의 파일 및 디렉토리도 포함됩니다.
     */
    public static List<File> getFileListFromDirWithSubFile(String path, FileFilter fileFilter) {
        File dir = new File(path);
        List<File> fileList = new ArrayList<File>();
        if (dir.exists()) {
            if (dir.isDirectory()) {
                for (File subFile : dir.listFiles(fileFilter)) {
                    fileList.add(subFile);
                    if (subFile.isDirectory()) {
                        fileList.addAll(getFileListFromDirWithSubFile(subFile.getAbsolutePath(), fileFilter));
                    }
                }
            }
        }
        return fileList;
    }

    /**
     * 지정된 디렉토리에서 파일 이름 목록을 검색합니다.
     * 경로가 유효하고 디렉토리를 가리킬 경우, 디렉토리 내의 파일 이름 배열을 반환합니다.
     * 디렉토리가 존재하지 않거나 경로가 유효하지 않은 경우 null을 반환합니다.
     *
     * @param path 파일 이름을 검색할 디렉토리 경로
     * @return 디렉토리가 존재하는 경우 문자열로 된 파일 이름 배열;
     * 디렉토리가 존재하지 않거나 경로가 유효하지 않은 경우 null
     */
    public static String[] getAllFileStrListFromDir(String path) {
        File dir = new File(path);
        String[] list = null;

        if (dir.exists()) {
            if (dir.isDirectory()) {
                list = dir.list();
            }
        }
        return list;
    }

    /**
     * 지정된 디렉토리 경로에서 파일 이름 목록을 검색합니다.
     * 이 메서드는 디렉토리를 반환 목록에서 제외합니다.
     *
     * @param path 파일 이름을 검색할 디렉토리 경로
     * @return 지정된 디렉토리의 파일 이름 배열. 디렉토리가 존재하지 않거나 파일이 없는 경우 빈 배열을 반환합니다.
     */
    public static String[] getFileStrListFromDir(String path) {
        File dir = new File(path);
        String[] lists = null;
        List<String> files = new ArrayList<String>();

        if (dir.exists()) {
            if (dir.isDirectory()) {
                lists = dir.list();
            }
        }

        if (lists != null) {
            for (String list : lists) {
                File file = new File(path + File.separator + list);
                if (file.isDirectory()) {
                    continue;
                }
                files.add(list);
            }
        }
        return files.toArray(new String[0]);
    }

    /**
     * 지정된 디렉토리의 모든 하위 디렉토리를 상대 경로 형식으로 검색합니다.
     * 이 메서드는 지정된 시작 디렉토리 아래의 모든 하위 디렉토리를 수집하며,
     * 절대 경로를 주어진 기준 디렉토리를 기준으로 상대 경로로 변환합니다.
     *
     * @param fromPath 하위 디렉토리를 나열할 디렉토리의 절대 경로
     * @param basePath 절대 경로를 상대 경로로 변환하는 데 사용할 기준 경로
     * @return 모든 하위 디렉토리의 상대 경로를 나타내는 문자열 배열
     */
    public static String[] getAllSubRelDirs(String fromPath, String basePath) {
        String[] toPath = null;
        ArrayList<Object> toPathArray = new ArrayList<Object>();

        File folder = new File(fromPath);
        toPathArray.add(folder.getAbsolutePath());

        getAllSubDirs(fromPath, toPathArray);

        toPath = new String[toPathArray.size()];
        toPathArray.toArray(toPath);

        for (int i = 0; i < toPath.length; i++) {
            toPath[i] = FileUtil.ltrim(toPath[i], basePath);
            toPath[i] = toPath[i].replace('\\', '/');
            toPath[i] = FileUtil.ltrim(toPath[i], "/");
            if (!toPath[i].endsWith("/")) {
                toPath[i] = toPath[i] + "/";
            }
        }

        return toPath;
    }

    /**
     * 주어진 디렉토리 경로에서 시작하여 모든 하위 디렉토리 경로를 재귀적으로 수집합니다.
     *
     * @param fromPath  하위 디렉토리를 수집할 디렉토리 경로
     * @param pathArray 하위 디렉토리의 절대 경로를 저장할 목록
     */
    public static void getAllSubDirs(String fromPath, List<Object> pathArray) {
        File folder = new File(fromPath);
        File[] subFiles = folder.listFiles();

        if (subFiles != null) {
            for (int i = 0; i < subFiles.length; i++) {
                if (subFiles[i].isDirectory()) {
                    pathArray.add(subFiles[i].getAbsolutePath());
                    getAllSubDirs(subFiles[i].getAbsolutePath(), pathArray);
                }
            }
        }
    }

    /**
     * 주어진 경로로부터 하위의 모든 파일들을 상대 경로로 반환합니다.
     *
     * @param fromPath 파일 및 디렉토리의 모든 하위 항목 목록을 검색할 디렉토리 경로
     * @return 문자열 리스트, 각 문자열은 하위 파일 또는 디렉토리의 경로를 나타냅니다.
     * @throws Exception 파일 경로 검색 중 오류가 발생하면 예외를 던집니다.
     */
    public static List<String> getAllSubFiles(String fromPath) throws Exception {
        return getAllSubFiles(fromPath, null, null);
    }

    /**
     * 주어진 경로로부터 하위의 특정 확장자를 가진 파일들을 상대 경로로 반환합니다.
     *
     * @param fromPath 파일을 검색할 디렉토리 경로.
     * @param ext      파일을 필터링할 확장자 리스트. null 또는 비어 있으면 모든 파일 포함.
     * @return 검색된 파일의 경로를 나타내는 문자열 리스트.
     * @throws Exception 파일 시스템 접근 중 오류가 발생하면 예외를 던집니다.
     */
    public static List<String> getAllSubFiles(String fromPath, List<String> ext) throws Exception {
        return getAllSubFiles(fromPath, null, ext);
    }

    /**
     * 지정된 디렉토리 및 하위 디렉토리에서 모든 파일을 검색하여 경로를 반환하며, 선택적으로 파일 확장자로 필터링합니다.
     *
     * @param fromPath   파일 검색을 시작할 디렉토리 경로
     * @param parentPath 상위 디렉토리 경로; 초기 호출 시 null로 전달
     * @param ext        결과를 필터링할 파일 확장자 리스트, null이면 모든 파일 포함
     * @return 지정된 디렉토리 및 하위 디렉토리에서 검색된 모든 파일 경로의 리스트
     * @throws Exception 파일 시스템 접근 중 오류가 발생하면 예외를 던집니다.
     */
    private static List<String> getAllSubFiles(String fromPath, String parentPath, List<String> ext) throws Exception {
        if (parentPath == null) {
            parentPath = "";
        } else {
            parentPath += "/";
        }

        String dirName = parentPath + fromPath;
        File folder = new File(dirName);

        // First try as a regular file system path
        if (!folder.exists()) {
            // If not found, try as a classpath resource
            try {
                folder = new File(FileClasspath.getClasspathRootAbsPath(dirName));
            } catch (Exception e) {
                // If neither exists, return empty list
                return new ArrayList<>();
            }
        }

        File[] subFiles = folder.listFiles();
        List<String> fileList = new ArrayList<String>();

        if (subFiles != null) {
            if (ext == null) {
                for (int i = 0; i < subFiles.length; i++) {
                    if (subFiles[i].isDirectory()) {
                        fileList.addAll(getAllSubFiles(subFiles[i].getName(), dirName, null));
                    } else {
                        fileList.add(dirName + "/" + subFiles[i].getName());
                    }
                }
            } else {
                for (int i = 0; i < subFiles.length; i++) {
                    if (subFiles[i].isDirectory()) {
                        fileList.addAll(getAllSubFiles(subFiles[i].getName(), dirName, ext));
                    } else {
                        String[] fs = subFiles[i].getName().split("\\.");
                        if (fs.length > 1 && ext.contains(fs[1])) {
                            fileList.add(dirName + "/" + subFiles[i].getName());
                        }
                    }
                }
            }
        }

        return fileList;
    }
}