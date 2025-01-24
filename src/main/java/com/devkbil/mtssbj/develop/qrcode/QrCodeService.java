package com.devkbil.mtssbj.develop.qrcode;

import com.google.zxing.WriterException;

import java.io.IOException;

/**
 * QR 코드 생성 인터페이스
 * - QR 코드 생성과 관련된 기능을 정의합니다.
 */
public interface QrCodeService {

    /**
     * QR 코드 생성 메서드
     * - 주어진 링크를 QR 코드로 인코딩하고, 결과를 바이트 배열로 반환합니다.
     *
     * @param link QR 코드에 포함될 URL 또는 데이터
     * @return QR 코드 이미지 데이터를 나타내는 바이트 배열
     * @throws IOException     입출력 오류 발생 시 예외 처리
     * @throws WriterException QR 코드 생성을 실패할 때 발생하는 예외
     */
    public byte[] generateQrCode(String link) throws IOException, WriterException;

}
