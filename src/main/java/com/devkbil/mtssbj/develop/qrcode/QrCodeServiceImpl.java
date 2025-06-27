package com.devkbil.mtssbj.develop.qrcode;

import com.google.zxing.WriterException;

import org.springframework.stereotype.Service;

import java.io.IOException;

import lombok.RequiredArgsConstructor;

/**
 * QR 코드 서비스 구현 클래스
 * - QR 코드를 생성하고 필요한 데이터베이스 처리를 포함합니다.
 */
@Service
@RequiredArgsConstructor
public class QrCodeServiceImpl extends QRCodeGenerator implements QrCodeService {

    /**
     * QR 코드 생성 메서드
     * - 전달받은 링크를 QR 코드로 변환하여 바이트 배열로 반환합니다.
     *
     * @param link QR 코드로 생성할 링크
     * @return 생성된 QR 코드 이미지의 바이트 배열
     * @throws IOException     QR 코드 생성 또는 입출력 오류 발생 시 예외 처리
     * @throws WriterException QR 코드 생성 과정에서 발생하는 오류 처리
     */
    @Override
    public byte[] generateQrCode(String link) throws IOException, WriterException {

        // QR 코드 생성
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCodeImage(link);

        // DB에 QR 코드 삽입
        //qrCodeMapper.insertQrCode(link, qrCodeBytes);

        return qrCodeBytes; // 생성된 QR 코드 바이트 배열을 반환
    }
}