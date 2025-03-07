package com.devkbil.mtssbj.file;

import java.io.File;

public class DirListTest {
    public static void main(String[] args) {
        // 절대 경로 또는 상대 경로 사용
        String dirName =  "./fileupload/";
        File dir = new File(dirName);

        // 디렉토리 존재 여부 확인
        if (!dir.exists()) {
            System.out.println("디렉토리가 존재하지 않습니다: " + dirName);
            // 디렉토리 생성 또는 기본 디렉토리로 대체
            dir.mkdirs();
        }

        // list()와 listFiles() 메서드 호출 전 null 체크
        String[] files = dir.list();
        if (files == null) {
            System.out.println("디렉토리 목록을 가져올 수 없습니다.");
            return;
        }

        System.out.println("--------------------------------------");
        System.out.println("파일/DIR명\t size");
        System.out.println("--------------------------------------");

        File[] files2 = dir.listFiles();
        if (files2 == null) {
            System.out.println("디렉토리 파일 목록을 가져올 수 없습니다.");
            return;
        }

        for (File f : files2) {
            String str = f.getName();

            if (f.isDirectory()) {
                System.out.print(str + "\t");
                System.out.print("DIR\n");
            } else { // 파일인 경우 ...
                System.out.print(str + "\t" + f.length() + "bytes\n");
            }
        }
    }
}