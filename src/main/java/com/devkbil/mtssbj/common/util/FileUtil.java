package com.devkbil.mtssbj.common.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 관련 작업을 처리하기 위한 유틸리티 클래스입니다.
 * 파일 이름 생성, 확장자 추출, 경로 생성, 파일 및 디렉토리 존재 여부 확인 등의 기능을 제공합니다.
 */
@Slf4j
public class FileUtil {

    /**
     * 현재 날짜와 시간을 기반으로 새로운 파일 이름을 생성합니다.
     * 결과는 "yyyyMMddhhmmssSSS" 형식의 날짜와 임의의 한 자리 숫자를 조합한 문자열입니다.
     * 생성된 파일명은 중복 가능성을 최소화하기 위해 밀리초 단위까지 포함합니다.
     *
     * @return 생성된 파일 이름 문자열 (18자리)
     */
    public static String getNewName() {
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        return ft.format(new Date()) + (int)(Math.random() * 10);
    }

    /**
     * 파일 이름에서 확장자를 추출합니다.
     * 파일명의 마지막 점(.) 이후의 문자열을 확장자로 인식합니다.
     *
     * @param filename 확장자를 추출할 파일 이름
     * @return 추출된 확장자 (점 제외)
     * @throws StringIndexOutOfBoundsException filename이 null이거나 확장자가 없는 경우 발생
     */
    public static String getFileExtension(String filename) {
        Integer mid = filename.lastIndexOf(".");
        return filename.substring(mid + 1);
    }

    /**
     * 문자열의 왼쪽에서 특정 문자열(trimStr)을 제거합니다.
     * 주로 파일 경로에서 불필요한 접두어를 제거하는 데 사용됩니다.
     *
     * @param source 원본 문자열 (null 허용)
     * @param trimStr 제거할 문자열
     * @return 변환된 문자열. source가 null이면 null 반환,
     *         source가 trimStr로 시작하지 않으면 원본 문자열 그대로 반환
     */
    public static String ltrim(String source, String trimStr) {
        if (source != null && source.startsWith(trimStr)) {
            return source.substring(trimStr.length());
        }
        return source;
    }

    /**
     * 파일 이름을 기반으로 연/월/일 구조의 실제 경로를 생성합니다.
     * 파일명의 앞 8자리가 날짜 형식(YYYYMMDD)이어야 합니다.
     *
     * @param path 기본 경로 (끝에 '/' 포함)
     * @param filename 날짜 형식(YYYYMMDD)으로 시작하는 파일 이름
     * @return 생성된 실제 경로 문자열 (예: "/base/path/2024/01/15/")
     * @throws StringIndexOutOfBoundsException filename이 8자리 미만이거나 null인 경우 발생
     */
    public static String getRealPath(String path, String filename) {
        return path + filename.substring(0, 4) + "/" + filename.substring(4, 6) + "/" + filename.substring(6, 8) + "/";
    }

    /**
     * 특정 디렉토리 내에 지정된 파일 이름이 존재하는지 확인합니다.
     * 디렉토리 내의 모든 파일을 검색하여 정확히 일치하는 파일명을 찾습니다.
     *
     * @param fileName 확인할 파일 이름 (대소문자 구분)
     * @param targetDir 검색할 디렉토리의 절대 경로
     * @return 파일이 존재하면 true, 그렇지 않으면 false. 다음의 경우 false 반환:
     *         - targetDir이 존재하지 않는 경우
     *         - targetDir이 디렉토리가 아닌 경우
     *         - 파일을 찾을 수 없는 경우
     * @throws Exception 디렉토리 접근 권한이 없거나 I/O 오류 발생 시
     */
    public static boolean existFile(String fileName, String targetDir) throws Exception {
        boolean result = false;
        File dir = new File(targetDir);
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (int i = 0; files != null && i < files.length; i++) {
                if (files[i].getName().equals(fileName)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 지정된 전체 경로에 파일이 존재하는지 확인합니다.
     * 파일의 전체 경로를 사용하여 직접적으로 파일의 존재 여부를 확인합니다.
     *
     * @param fileFullPath 확인할 파일의 전체 경로 (절대 경로)
     * @return 파일이 존재하면 true, 그렇지 않으면 false. 다음의 경우 false 반환:
     * - fileFullPath가 null인 경우
     * - 파일이 존재하지 않는 경우
     * - 경로가 디렉토리인 경우
     */
    public static boolean existFile(String fileFullPath) {
        File file = new File(fileFullPath);
        return file.exists();
    }

    /**
     * 지정된 전체 경로에 디렉토리가 존재하는지 확인합니다.
     * 주어진 경로가 실제로 존재하고 디렉토리인지 확인합니다.
     *
     * @param fullPath 확인할 디렉토리의 전체 경로 (절대 경로 권장)
     * @return 디렉토리가 존재하면 true, 그렇지 않으면 false. 다음의 경우 false 반환:
     *         - fullPath가 null인 경우
     *         - 경로가 존재하지 않는 경우
     *         - 경로가 파일인 경우
     */
    public static boolean existDirectory(String fullPath) {
        File file = new File(fullPath);
        return file.isDirectory();
    }

    /**
     * 파일 이름 변경을 위한 유틸리티 메서드입니다.
     * 파일 이름의 마지막에 숫자 "1"을 추가합니다. 확장자가 있는 경우 확장자 앞에 추가됩니다.
     *
     * @param fileName 변경할 파일 이름
     * @return 변경된 파일 이름. 다음과 같이 처리됨:
     *         - 확장자가 있는 경우: 확장자 앞에 "1" 추가 (예: "file.txt" -> "file1.txt")
     *         - 확장자가 없는 경우: 끝에 "1" 추가 (예: "file" -> "file1")
     *         - fileName이 null이거나 빈 문자열인 경우: 입력값 그대로 반환
     * @throws Exception 파일 이름 처리 중 오류가 발생한 경우 예외를 던집니다.
     */
    public static String renameFile(String fileName) throws Exception {
        if (fileName == null || fileName.equals("")) {
            return fileName;
        }
        int lastIdx = fileName.lastIndexOf(".");

        if (lastIdx == -1) {
            fileName = fileName + "1";
        } else {
            fileName = fileName.substring(0, lastIdx) + "1" + fileName.substring(lastIdx);
        }

        return fileName;
    }
}