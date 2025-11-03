package com.devkbil.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import lombok.extern.slf4j.Slf4j;

/**
 * DecompressZip 클래스는 ZIP 파일을 특정 디렉토리에 압축 해제하는 기능을 제공합니다.
 * ZIP 파일의 확장자 타입, 위치 등을 지정해 압축 해제를 수행하며 디렉토리나 파일을 처리하는 관련
 * 기능을 포함합니다.
 */
@Slf4j
public class DecompressZip {

    /**
     * 주어진 압축파일을 지정된 디렉토리에 압축해제합니다.
     *
     * @param zipPath        압축파일이 있는 디렉토리 경로
     * @param zipFileName    압축파일의 이름
     * @param zipUnzipPath   압축파일의 내용이 압축해제될 디렉토리
     * @param type          압축파일의 확장자 타입 (예: .zip, .tar)
     * @return 압축해제 프로세스 성공 시 true, 실패 시 false
     */
    public boolean unZip(String zipPath, String zipFileName, String zipUnzipPath, String type) {
        log.debug("unZip() - zipPath : " + zipPath);
        log.debug("unZip() - zipFileName : " + zipFileName);
        log.debug("unZip() - zipUnzipPath : " + zipUnzipPath);
        boolean isChk = false;

        zipUnzipPath = zipUnzipPath + zipFileName.replace(type, "");
        File zipFile = new File(zipPath + zipFileName);

        // try-with-resources 사용
        try (FileInputStream fis = new FileInputStream(zipFile);
             ZipInputStream zis = new ZipInputStream(fis, Charset.forName("EUC-KR"))) {

            if (makeFolder(zipUnzipPath)) {
                log.debug(" --- 폴더를 생성 완료 ");
            }

            ZipEntry zipentry;
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();
                log.debug("filename(zipentry.getName()) => " + filename);

                File file = new File(zipUnzipPath, filename);

                if (zipentry.isDirectory()) {
                    log.debug(" --- zipentry가 디렉토리입니다.");
                    file.mkdirs();
                } else {
                    log.debug(" --- zipentry가 파일입니다.");
                    try {
                        createFile(file, zis);
                    } catch (Throwable e) {
                        log.error(e.getMessage(), e);
                    }
                }
            }

            isChk = true;

        } catch (IOException e) {
            log.error("압축 해제 중 오류 발생: {}", e.getMessage(), e);
            isChk = false;
        }

        return isChk;
    }

    /**
     * 폴더가 존재하지 않는 경우 폴더를 생성합니다.
     *
     * @param folder 생성할 폴더의 경로
     * @return 폴더가 성공적으로 생성되었거나 이미 존재하는 경우 true, 그렇지 않으면 false
     */
    private boolean makeFolder(String folder) {
        log.debug(" makeFolder() - folder : " + folder);

        boolean result = false;

        if (folder.length() < 0) {
            result = false;
        }

        File file = new File(folder);

        //해당 디렉토리가 없을경우 디렉토리 생성

        //디렉토리 폴더 경로 체크
        if (!file.exists()) {
            file.mkdirs();  //폴더 생성
            log.info(" makeFolder() --------- 폴더 생성 완료 ---------");
        } else {
            log.info(" makeFolder() --------- 이미 폴더가 존재함 ---------");
        }
        result = true;

        return result;
    }

    /**
     * ZipInputStream의 데이터로부터 새 파일을 생성합니다. 지정된 파일의 상위 디렉토리가
     * 존재하지 않는 경우 생성됩니다. 제공된 ZipInputStream에서 데이터를 읽어
     * 지정된 파일에 기록합니다.
     *
     * @param file 생성되고 기록될 파일
     * @param zis 파일에 기록할 데이터를 제공하는 ZipInputStream
     * @throws IOException 파일 생성 또는 데이터 쓰기 중 입출력 오류가 발생한 경우
     */
    private void createFile(File file, ZipInputStream zis) throws IOException {
        log.debug(" createFile() - file : " + file);
        //디렉토리 확인
        File parentDir = new File(file.getParent());

        //디렉토리 폴더 경로 체크
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        //파일 스트림 선언
        try (FileOutputStream fos = new FileOutputStream(file)) {
            byte[] buffer = new byte[256];
            int size = 0;

            //zip스트림으로부터 byte뽑아내기
            while ((size = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, size);  //스트림 작성
            }

        } catch (IOException e) {
            throw e;
        }
    }

}