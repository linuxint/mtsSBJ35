package com.devkbil.mtssbj.api.v1.develop.naver.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;

/**
 * 지도 주소 변환 REST API 컨트롤러
 * - 네이버 지도 API를 활용한 주소 변환 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/develop/map")
@RequiredArgsConstructor
@Tag(name = "Map Address Converter API", description = "네이버 지도 API를 활용한 주소 변환 REST API")
public class MapAddrConvRestController {

    /**
     * 주소 정보 조회 API
     * - 주소 정보를 JSON 형태로 반환합니다.
     *
     * @param address 변환할 주소 (선택적)
     * @return 주소 정보를 담은 ResponseEntity
     */
    @Operation(summary = "주소 정보 조회", description = "네이버 지도 API를 사용하여 주소 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "주소 정보 조회 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/address")
    public ResponseEntity<Map<String, Object>> getAddressInfo(@RequestParam(required = false) String address) {
        Map<String, Object> result = new HashMap<>();
        
        // 여기에 실제 주소 변환 로직이 들어갈 수 있습니다.
        // 현재는 간단한 응답만 반환합니다.
        result.put("address", address);
        result.put("status", "success");
        result.put("message", "주소 정보가 성공적으로 조회되었습니다.");
        
        return ResponseEntity.ok(result);
    }
}