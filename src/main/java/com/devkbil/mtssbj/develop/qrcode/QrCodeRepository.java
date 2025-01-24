package com.devkbil.mtssbj.develop.qrcode;

import com.google.zxing.WriterException;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * QR 코드 데이터 저장소 클래스
 * - QR 코드 생성을 처리하고, 데이터베이스와 상호작용을 통해 생성된 데이터를 저장합니다.
 */
@Repository
public class QrCodeRepository {

    // QrCodeMapper는 데이터 삽입 작업을 위해 MyBatis 매퍼로 동작합니다.
    private final QrCodeMapper qrCodeMapper;

    /**
     * QrCodeRepository의 생성자
     * - QrCodeMapper 객체를 주입받아 초기화합니다.
     *
     * @param qrCodeMapper 데이터베이스와 상호작용하기 위한 MyBatis 매퍼 객체
     */
    public QrCodeRepository(QrCodeMapper qrCodeMapper) {
        this.qrCodeMapper = qrCodeMapper;
    }

    /**
     * QR 코드를 생성하고 데이터베이스에 저장하는 메서드
     *
     * @param link QR 코드로 인코딩될 URL 또는 텍스트 데이터
     * @param id   고유 식별자 (현재는 사용되지 않음, 예시를 위해 제공됨)
     * @throws IOException     입출력 오류가 발생했을 경우
     * @throws WriterException QR 코드 생성 중 오류가 발생했을 경우
     */
    public void saveQrCode(String link, Long id) throws IOException, WriterException {
        // 예시 링크 및 ID 설정 (현재는 고정된 값 사용 - 실제 애플리케이션에서는 동적 값이 필요)
        link = "https://www.naver.com";
        id = 12L;

        // QR 코드 생성: QRCodeGenerator의 QR 코드 생성 메서드 호출
        byte[] qrCodeBytes = QRCodeGenerator.generateQRCodeImage(link);

        // 생성된 QR 코드 데이터를 데이터베이스(Map)로 삽입
        qrCodeMapper.insertQrCode(link, qrCodeBytes);
    }
}
