package com.devkbil.mtssbj.develop.qrcode;

import java.awt.Color;

/**
 * QR 코드 및 PDF 생성에 필요한 상수 정의 클래스
 * - QR 코드의 크기, 위치, 스타일, PDF 파일 이름, Hint Map 설정 등을 포함합니다.
 * - QR 코드 생성 및 PDF 파일 생성 중에 사용됩니다.
 */
public class QrConstant {

    // 안내문구 관련 상수
    static int TEXT_STARTX = 70;    // 안내문구의 시작 X 좌표
    static int TEXT_STARTY = 765;   // 안내문구의 시작 Y 좌표
    static int TEXT_FONT_SIZE = 18; // 안내문구의 폰트 크기
    static String TEXT_MSG = "전자서식에 사용된 인감은 재사용 불가합니다."; // 안내문구 텍스트
    static int SEQNO_FONT_SIZE = 9; // 인감번호(SEQ) 폰트 크기

    static int QR_X_COUNT = 3; // QR코드 가로 개수
    static int QR_Y_COUNT = 4; // QR코드 세로 개수

    // QR 코드 관련 상수
    static int QRCODE_XGAP = (int)(60 * 0.8);     // QR 코드 간의 X축 간격
    static int QRCODE_YGAP = (int)(40 * 0.9);     // QR 코드 간의 Y축 간격
    static int QRCODE_WIDTH = (int)(210 * 0.71);  // QR 코드의 폭 (픽셀 단위) 210
    static int QRCODE_HEIGHT = (int)(210 * 0.71); // QR 코드의 높이 (픽셀 단위)
    static int QRCODE_STARTX = (int)(60 * 0.6);   // 첫 번째 QR 코드의 시작 X 좌표
    static int QRCODE_STARTY = (int)(30 * 1.0);   // 첫 번째 QR 코드의 시작 Y 좌표

    // 인감 박스(SealBox) 관련 상수
    static int SEALBOX_WIDTH = (int)(QRCODE_WIDTH * 0.50);   // SealBox 폭: QR 코드 폭의 비율 크기
    static int SEALBOX_HEIGHT = (int)(QRCODE_HEIGHT * 0.50); // SealBox 높이: QR 코드 높이의 비율 크기
    static Color SEALBOX_BGCOLOR = Color.WHITE;              // SealBox 배경색
    static Color SEALBOX_LINECOLOR = Color.BLACK;            // SealBox 테두리 색

    // PDF 파일 관련 상수
    static String PDF_FILE_NAME = "IBK_TB_QR.pdf"; // 생성되는 PDF 파일 이름

    // QR 코드 Hint Map 구성 요소 (QR 코드 생성에 필요한 세부 설정값)
    static int QRCODE_HINT_MARGIN = 0;                 // QR 코드 여백 (마진) 값
    static int QRCODE_HINT_WIDTH = 800;                // 생성되는 QR 코드 폭 (픽셀 단위)
    static int QRCODE_HINT_HEIGHT = 800;               // 생성되는 QR 코드 높이 (픽셀 단위)
    static String QRCODE_HINT_CHARACTER_SET = "UTF-8"; // QR 코드에 사용되는 문자 세트 (UTF-8)
    static String QRCODE_HINT_ERROR_CORRECTION = "H";  // QR 코드의 오류 정정 수준: "H"는 높은 오류 복구율을 의미
    static String QRCODE_HINT_VERSION = "10";          // QR 코드 버전: QR 코드 크기를 결정 (10은 큰 크기를 의미)
    static String QRCODE_HINT_IMG_TYPE = "png";        // QR 코드 이미지 출력 형식 (PNG)
}