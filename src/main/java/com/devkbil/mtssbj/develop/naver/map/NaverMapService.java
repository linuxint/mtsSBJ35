package com.devkbil.mtssbj.develop.naver.map;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Naver 지도 API 서비스
 * - 네이버 지도 API를 호출하여 Geocode(좌표) 데이터를 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class NaverMapService {

    @Value("${naver.api.clientid}")
    private String apiKey;  // 네이버 API 클라이언트 ID

    @Value("${naver.api.clientsecret}")
    private String secretKey;  // 네이버 API 클라이언트 Secret Key

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * 주소를 사용해 네이버 지도 API를 호출하고 Geocode 데이터를 반환합니다.
     *
     * @param address 사용자 입력 주소
     * @return 네이버 지도 API의 Geocode 응답 데이터
     */
    public String getGeocode(String address) {
        String url = "https://naveropenapi.apigw.ntruss.com/map-geocode/v2/geocode?query=" + address;

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-NCP-APIGW-API-KEY-ID", apiKey);
        headers.set("X-NCP-APIGW-API-KEY", secretKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        return response.getBody(); // Geocode 데이터 반환
    }
}
