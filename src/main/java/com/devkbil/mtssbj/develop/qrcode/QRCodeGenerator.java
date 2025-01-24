package com.devkbil.mtssbj.develop.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * QR 코드 생성 유틸리티 클래스
 * - QR 코드를 생성하고 결과를 바이트 배열 형태로 반환합니다.
 */
public class QRCodeGenerator {

    /**
     * QR 코드 생성 메서드 (Static)
     * - 입력된 문자열 데이터를 기반으로 QR 코드를 생성하고, 이미지 데이터를 바이트 배열로 반환합니다.
     *
     * @param qrString QR 코드에 삽입할 문자열 데이터
     * @return QR 코드 이미지의 바이트 배열
     * @throws WriterException QR 코드 생성 과정에서 발생하는 예외
     * @throws IOException     QR 코드 이미지를 바이트 배열로 변환하는 과정에서 발생하는 입출력 예외
     */
    public static byte[] generateQRCodeImage(String qrString) throws WriterException, IOException {

        // QR코드 생성 설정: 힌트를 이용하여 마진, 문자 집합, 오류 정정 수준 등을 설정
        Map<EncodeHintType, Object> hintMap = new HashMap<>();
        hintMap.put(EncodeHintType.MARGIN, QrConstant.QRCODE_HINT_MARGIN); // QR 코드의 마진 설정
        hintMap.put(EncodeHintType.CHARACTER_SET, QrConstant.QRCODE_HINT_CHARACTER_SET); // 문자열의 문자 집합 지정 (UTF-8)
        hintMap.put(EncodeHintType.ERROR_CORRECTION, QrConstant.QRCODE_HINT_ERROR_CORRECTION); // 오류 정정 수준 설정
        hintMap.put(EncodeHintType.QR_VERSION, QrConstant.QRCODE_HINT_VERSION); // QR 코드의 버전 설정

        // QR 코드 생성
        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // 입력 받은 문자열 데이터를 기준으로 QR 코드 비트 매트릭스(BitMatrix) 생성
        // BitMatrix는 QR 코드의 흑백 픽셀 데이터를 저장하는 데 사용됩니다.
        BitMatrix bitMatrix = qrCodeWriter.encode(
                qrString
                , BarcodeFormat.QR_CODE
                , QrConstant.QRCODE_HINT_WIDTH
                , QrConstant.QRCODE_HINT_HEIGHT
                , hintMap
        );

        // BitMatrix를 BufferedImage로 변환 (QR 코드 이미지를 흑백 픽셀로 시각화)
        BufferedImage qrCodeImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // BufferedImage를 바이트 배열로 변환
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            ImageIO.write(qrCodeImage, QrConstant.QRCODE_HINT_IMG_TYPE, byteArrayOutputStream); // 이미지를 PNG 형식으로 변환
            byteArrayOutputStream.flush();
            return byteArrayOutputStream.toByteArray(); // 변환된 바이트 배열 반환
        }
    }

    /**
     * QR 코드 생성 메서드
     * - 주어진 문자열 데이터를 기반으로 QR 코드를 생성하고, 바이트 배열을 반환합니다.
     *
     * @param qrString QR 코드에 인코딩할 문자열
     * @return QR 코드의 이미지 데이터 (바이트 배열)
     * @throws IOException     QR 코드 생성 과정에서 발생하는 입출력 오류
     * @throws WriterException QR 코드 생성 과정에서 발생하는 오류
     */
    public byte[] generateQrCode(String qrString) throws IOException, WriterException {

        // QR 코드 생성: 입력된 문자열을 사용해 QR 코드 이미지를 생성하는 정적 메서드 호출
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCodeImage(qrString);

        // 생성된 QR 코드 데이터를 반환
        return qrCodeBytes;
    }
}
