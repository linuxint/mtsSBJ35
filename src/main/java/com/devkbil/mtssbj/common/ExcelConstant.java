package com.devkbil.mtssbj.common;

public class ExcelConstant {
    public static final int MAX_ROW_COUNT = 100; // 메모리 행 100개로 제한, 초과 시 Disk로 flush
    public static final String HEADER_KEY_NAME = "header"; // excel export - header String key
    public static final String DATA_KEY_NAME = "listview"; // excel export - data String key
    public static final String SHEET_KEY_NAME = "sheetname"; // excel export - excel sheet name key

}
