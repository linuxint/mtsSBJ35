package com.devkbil.mtssbj.develop.naver.map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

/**
 * 지도 주소 변환 페이지 컨트롤러
 * - 사용자가 지도와 관련된 주소 변환 페이지를 볼 수 있도록 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "Map Address Converter", description = "네이버 지도 API를 활용한 주소 변환 기능")
public class MapAddrConvController {

    /**
     * 지도 주소 변환 페이지 이동
     * - 사용자 요청 시 `thymeleaf/mapaddr` 페이지를 반환합니다.
     *
     * @return 주소 변환 페이지 View 이름
     */
    @Operation(summary = "주소 변환 페이지", description = "네이버 지도 API를 사용하여 주소를 변환할 수 있는 페이지를 제공합니다.")
    @GetMapping("/mapaddr")
    public String mapaddr() {
        return "thymeleaf/mapaddr"; // 주소 변환 페이지 반환
    }
}
