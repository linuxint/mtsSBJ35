package com.devkbil.mtssbj.file;

import java.io.File;

public class DirListTest {

    public static void main(String[] args) {
        // C:\디렉토리 아래 있는 파일 또는 디렉토리 목록을 얻어 도스 콘솔에 출력하세요.
        // File클래스의 list(), listFiles()메소드 이용.

        String dirName = "/Users/kbil/IdeaProjects/mtsSBJ35/fileupload/";
        File dir = new File(dirName);
        String[] files = dir.list();
        // 디렉토리의 파일목록(디렉토리포함)을 String배열로 반환

        /*for(String fn : files) //확상for문 ; for -each문
        System.out.println(fn);
        */

        /*for(int i=0; i<files.length;i++){
        System.out.println(files[i]);
        }*/

        System.out.println("--------------------------------------");
        System.out.println("파일/DIR명\t size");
        System.out.println("--------------------------------------");
        File[] files2 = dir.listFiles();
        // 디렉토리의 파일목록(디렉토리포함)을 File 배열로 반환
        for (File f : files2) {
            String str = f.getName();

            if (f.isDirectory()) {
                System.out.print(str + "\t");
                System.out.print("DIR\n");
            } else { // 파일인 경우 ...
                System.out.print(str + "\t" + f.length() + "bytes\n");
            }
        } // for-------
    }
}
