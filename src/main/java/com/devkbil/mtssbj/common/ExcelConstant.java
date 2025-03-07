package com.devkbil.mtssbj.common;

/**
 * ExcelConstant 클래스는 Excel 파일 생성 및 데이터 처리를 위해 사용되는
 * 상수 값을 정의합니다. 이 클래스는 Excel 데이터 작업 시 필요한 키 값이나
 * 설정 값을 중앙에서 관리할 수 있도록 설계되었습니다.
 */
public class ExcelConstant {
    /**
     * 메모리에 저장할 최대 행 수입니다.
     * 이 값(100개)을 초과하는 데이터는 디스크에 자동으로 플러시되어
     * 대용량 데이터 처리 시 메모리 부족 현상을 방지합니다.
     */
    public static final int MAX_ROW_COUNT = 100;

    /**
     * 엑셀 파일의 헤더 정보를 저장하는 키 이름입니다.
     * Map에서 헤더 정보를 저장하고 조회할 때 사용됩니다.
     * 값: "header"
     */
    public static final String HEADER_KEY_NAME = "header";

    /**
     * 엑셀 파일에 출력할 데이터 목록을 저장하는 키 이름입니다.
     * Map에서 실제 데이터 목록을 저장하고 조회할 때 사용됩니다.
     * 값: "listview"
     */
    public static final String DATA_KEY_NAME = "listview";

    /**
     * 엑셀 시트의 이름을 저장하는 키 이름입니다.
     * Map에서 생성될 엑셀 시트의 이름을 저장하고 조회할 때 사용됩니다.
     * 값: "sheetname"
     */
    public static final String SHEET_KEY_NAME = "sheetname";
}
