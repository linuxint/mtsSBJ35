package com.devkbil.mtssbj.develop.naver.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Naver 지도 API 컨트롤러
 * - 주소에 대한 좌표(Geocode)를 조회하는 API를 제공합니다.
 */
@RestController
@Tag(name = "Naver Map API", description = "네이버 지도 API를 활용한 좌표 조회 기능을 제공합니다.")
@RequiredArgsConstructor
@Slf4j
public class NaverApiController {

    private final NaverMapService naverMapService;

    /**
     * 주소를 기준으로 네이버 지도 API를 사용하여 Geocode(위도, 경도) 정보를 조회합니다.
     *
     * @param address 사용자 입력 주소
     * @return 좌표(Geocode) 정보
     */
    @Operation(summary = "좌표(Geocode) 조회", description = "주소를 입력받아 네이버 지도 API를 통해 좌표(위도, 경도)를 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 좌표 정보가 반환되었습니다.")
    @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 유효하지 않은 주소입니다.")
    @ApiResponse(responseCode = "500", description = "서버 내부 에러")
    @GetMapping(path = "/getGeocode")
    public @ResponseBody String getGeocode(@RequestParam(value = "address") String address) {
        log.info("Received request for geocode with address: {}", address);
        return naverMapService.getGeocode(address);
    }
}
