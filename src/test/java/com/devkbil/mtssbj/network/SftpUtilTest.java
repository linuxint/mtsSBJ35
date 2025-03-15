package com.devkbil.mtssbj.network;

import com.devkbil.common.util.SftpUtil;

import java.util.ArrayList;

public class SftpUtilTest {
    public static void main(String[] args) {
        SftpUtil sftpUtil = new SftpUtil();

        try {
            // SFTP 서버 접속
            // 접속할 SFTP 서버 IP, SFTP 포트, 계정 ID, 계정비밀번호, Pem Key
            sftpUtil.sftpInit("192.168.0.1", 22, "ediyaid", "ediyapw12");

            // SFTP서버 경로내 파일 찾기 (C300.D20221219.*인 파일명을 찾는다.)
            String fileName = sftpUtil.sftpSearchFile("/workspace/real/", "C300.D20221219");
            System.out.println(fileName);

            // SFTP 파일 다운로드
            // 다운로드 SFTP 서버 경로, 다운로드 받을 로컬 파일 경로
            sftpUtil.sftpFileDownload("/workspace/real/", fileName, "/ediya/work/");

            // SFTP 파일 업로드
            // 업로드 SFTP 서버 경로, 업로드할 파일 경로, 업로드 파일명
            sftpUtil.sftpFileUpload("/workspace/real/", "/ediya/work/", "C300.D20221219");

            ArrayList<String> files = new ArrayList<String>();
            files.add("D500.D221219_1");
            files.add("D500.D221219_2");
            files.add("D500.D221219_3");

            // SFTP 다중 파일 업로드
            // 업로드 SFTP 서버 경로, 업로드할 파일 경로, 업로드 파일명 리스트
            sftpUtil.sftpMultiFileUpload("/workspace/real/", "/ediya/work/", files);

        } catch (Exception e) {
            System.out.println(e);
        } finally {
            sftpUtil.disconnect();
        }
    }
}