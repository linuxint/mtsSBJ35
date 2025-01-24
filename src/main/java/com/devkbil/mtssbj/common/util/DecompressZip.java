package com.devkbil.mtssbj.common.util;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class DecompressZip {

    /**
     * 압축 해제 진행 == GOGO ==!
     *
     * @param zipPath
     * @param zipFileName
     * @param zipUnzipPath
     * @return 파일 압축 해제 성공 여부
     * @throws Exception
     */
    public boolean unZip(String zipPath, String zipFileName, String zipUnzipPath, String type) throws Exception {

        System.out.println(" unZip() - zipPath : " + zipPath);  //압축 파일 위치
        System.out.println(" unZip() - zipFileName : " + zipFileName);  //압축파일 이름
        System.out.println(" unZip() - zipUnzipPath : " + zipUnzipPath);  //압축이 해제될 위치

        //파일 압축 해제 성공 여부
        boolean isChk = false;

        //압축 해재할 파일의 type(.zip or .tar) 제거
        zipUnzipPath = zipUnzipPath + zipFileName.replace(type, "");

        //zip 파일
        File zipFile = new File(zipPath + zipFileName);
        FileInputStream fis = null;
        ZipInputStream zis = null;
        ZipEntry zipentry = null;

        try {

            //zipFileName을 통해서 폴더 생성
            if (makeFolder(zipUnzipPath)) {
                System.out.println(" --- 폴더를 생성 완료 ");
            }

            fis = new FileInputStream(zipFile);  //파일 스트림
            zis = new ZipInputStream(fis, Charset.forName("EUC-KR"));  //Zip 파일 스트림

            //압축되어 있는 ZIP 파일의 목록 조회
            while ((zipentry = zis.getNextEntry()) != null) {
                String filename = zipentry.getName();
                System.out.println("filename(zipentry.getName()) => " + filename);

                File file = new File(zipUnzipPath, filename);

                //entry가 디렉토리인지 파일인지 검색
                if (zipentry.isDirectory()) {

                    //entry가 디렉토리일 경우 폴더 생성
                    System.out.println(" --- zipentry가 디렉토리입니다.");
                    file.mkdirs();

                } else {

                    //entry가 파일일 경우 파일 생성
                    System.out.println(" --- zipentry가 파일입니다.");

                    try {
                        createFile(file, zis);

                    } catch (Throwable e) {
                        log.error(e.getMessage());
                    }
                }
            }

            isChk = true;

        } catch (Exception e) {
            isChk = false;

        } finally {
            if (zis != null) {
                try {
                    zis.close();
                } catch (IOException e) {
                    throw e;
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }

        return isChk;

    }

    /**
     * 폴더 디렉토리 체크 및 생성
     *
     * @param folder
     * @return
     * @throws Exception
     */
    private boolean makeFolder(String folder) {
        System.out.println(" makeFolder() - folder : " + folder);

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
     * 파일 체크 및 생성
     *
     * @param file
     * @param zis
     * @throws Exception
     */
    private void createFile(File file, ZipInputStream zis) throws Exception {
        System.out.println(" createFile() - file : " + file);
        //디렉토리 확인
        File parentDir = new File(file.getParent());

        //디렉토리 폴더 경로 체크
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        FileOutputStream fos = null;  //출력 스트림

        //파일 스트림 선언
        try {
            fos = new FileOutputStream(file);
            byte[] buffer = new byte[256];
            int size = 0;

            //zip스트림으로부터 byte뽑아내기
            while ((size = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, size);  //스트림 작성
            }

        } catch (Exception e) {
            throw e;

        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }
    }

}
