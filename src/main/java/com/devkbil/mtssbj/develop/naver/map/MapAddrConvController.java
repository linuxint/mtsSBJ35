package com.devkbil.mtssbj.develop.naver.map;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 지도 주소 변환 페이지 컨트롤러
 * - 사용자가 지도와 관련된 주소 변환 페이지를 볼 수 있도록 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class MapAddrConvController {

    /**
     * 지도 주소 변환 페이지 이동
     * - 사용자 요청 시 `thymeleaf/mapaddr` 페이지를 반환합니다.
     *
     * @return 주소 변환 페이지 View 이름
     */
    @GetMapping("/mapaddr")
    public String mapaddr() {
        return "thymeleaf/mapaddr"; // 주소 변환 페이지 반환
    }
}
