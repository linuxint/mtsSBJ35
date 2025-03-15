package com.devkbil.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

/**
 * 파일 입출력 작업을 수행하는 유틸리티 클래스.
 * 텍스트 및 바이너리 형식의 파일 읽기, 파일 쓰기, 파일 내용 업데이트를 위한 메서드를 제공합니다.
 */
@Slf4j
public class FileIO {

    /**
     * 파일의 내용을 문자열로 읽어옵니다.
     * 이 메서드는 내부적으로 readFileForBinary 메서드를 사용하여 파일의 이진 데이터를 읽고,
     * 이진 데이터를 텍스트 문자열로 변환합니다.
     *
     * @param filePath 읽을 파일의 경로
     * @return 파일의 내용을 문자열로 반환
     * @throws IOException 파일을 읽는 도중 입출력 오류가 발생할 경우
     */
    public static String readFileForText(String filePath) throws IOException {
        return new String(readFileForBinary(filePath));
    }

    /**
     * 지정된 파일 경로에서 이진 파일을 읽고 내용을 바이트 배열로 반환합니다.
     *
     * @param filePath 읽을 파일의 경로
     * @return 파일의 이진 내용을 포함하는 바이트 배열
     * @throws IOException 파일을 읽는 도중 입출력 오류가 발생할 경우
     */
    public static byte[] readFileForBinary(String filePath) throws IOException {
        InputStream is = null;
        ByteArrayOutputStream os = null;

        int size = 1024;
        byte[] buffer = new byte[size];
        int length = -1;
        byte[] retValue = null;
        try {
            is = new FileInputStream(filePath);
            os = new ByteArrayOutputStream();

            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                retValue = os.toByteArray();
                os.close();
            }
        }

        return retValue;
    }

    /**
     * 주어진 InputStream에서 파일의 내용을 읽어 문자열로 반환합니다.
     *
     * @param is 데이터를 읽을 InputStream
     * @return 파일의 내용을 문자열로 반환
     * @throws IOException InputStream을 읽는 도중 입출력 오류가 발생할 경우
     */
    public static String readFile(InputStream is) throws IOException {
        ByteArrayOutputStream os = null;

        int size = 1024;
        byte[] buffer = new byte[size];
        int length = -1;
        String retValue = null;

        try {
            os = new ByteArrayOutputStream();

            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }
        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (is != null) {
                is.close();
            }
            if (os != null) {
                retValue = os.toString();
                os.close();
            }
        }

        return retValue;
    }

    /**
     * 파일의 내용을 한 줄씩 읽고, 비어 있지 않은 잘려진 줄들을 문자열 배열로 반환합니다.
     *
     * @param filePath 읽을 파일의 경로
     * @return 비어 있지 않고 잘려진 줄들의 문자열 배열
     * @throws IOException 파일을 읽는 도중 입출력 오류가 발생할 경우
     */
    public static String[] readFileByLine(String filePath) throws IOException {
        ArrayList<Object> resultList = new ArrayList<Object>();
        String[] result = null;

        String line = "";
        BufferedReader in = null;

        try {
            in = new BufferedReader(new FileReader(filePath));

            while ((line = in.readLine()) != null) {
                if (!line.trim().equals("")) {
                    resultList.add(line.trim());
                }
            }

        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (in != null) {
                in.close();
            }
        }

        result = new String[resultList.size()];
        resultList.toArray(result);

        return result;
    }

    /**
     * 지정된 파일에 새 텍스트 줄을 추가합니다. 파일이 존재하지 않으면 생성됩니다.
     * 텍스트는 줄 구분자와 함께 파일에 추가됩니다.
     *
     * @param filePath 텍스트를 추가할 파일의 경로
     * @param str      새 줄로 추가할 문자열
     * @throws IOException 파일을 쓰는 도중 입출력 오류가 발생할 경우
     */
    public static void addLineToFile(String filePath, String str) throws IOException {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath, true)));
            pw.println(str);
            pw.flush();

        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    /**
     * 지정된 파일의 내용을 갱신하고, 제공된 문자열 배열을 파일에 씁니다.
     * 문자열 배열의 각 요소는 파일의 개별 줄로 기록됩니다.
     *
     * @param filePath 업데이트할 파일의 경로
     * @param strList  파일에 쓸 문자열 배열. 각 문자열은 새 줄로 기록됩니다.
     * @throws IOException 파일을 쓰는 도중 입출력 오류가 발생할 경우
     */
    public static void updateFile(String filePath, String[] strList) throws IOException {
        PrintWriter pw = null;

        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(filePath, false)));
            for (int i = 0; i < strList.length; i++) {
                pw.println(strList[i]);
            }
            pw.flush();

        } catch (IOException ioe) {
            throw ioe;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }
}