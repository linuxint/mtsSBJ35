package com.devkbil.mtssbj.develop.qrcode;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

/**
 * QR 코드 데이터를 데이터베이스에 저장하기 위한 Mapper 인터페이스
 * - MyBatis를 활용하여 데이터베이스에 직접 접근합니다.
 */
@Mapper
public interface QrCodeMapper {

    /**
     * 데이터베이스에 QR 코드 정보를 삽입하는 메서드
     *
     * @param link   QR 코드로 인코딩된 URL 또는 문자 데이터
     * @param qrCode QR 코드 이미지 데이터를 나타내는 바이트 배열
     */
    void insertQrCode(@Param("link") String link, @Param("qrCode") byte[] qrCode);
}
