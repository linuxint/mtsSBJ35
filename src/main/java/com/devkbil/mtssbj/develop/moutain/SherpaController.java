package com.devkbil.mtssbj.develop.moutain;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;

/**
 * 셰르파(Sherpa) 관련 기능을 처리하는 컨트롤러입니다.
 * 산악 가이드 서비스와 관련된 웹 요청을 처리하고 해당 뷰로 연결합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "Sherpa Controller", description = "셰르파 서비스 관련 API")
public class SherpaController {

    /**
     * 셰르파 인증 페이지를 표시합니다.
     *
     * @param request  HTTP 요청 객체
     * @param modelMap 뷰에 전달할 데이터를 담는 모델 객체
     * @return 셰르파 인증 페이지의 뷰 이름
     */
    @Operation(summary = "셰르파 인증 페이지", description = "셰르파 서비스 인증을 위한 페이지를 제공합니다.")
    @GetMapping("/sherpa")
    public String authSherpa(HttpServletRequest request, ModelMap modelMap) {
        return "thymeleaf/sherpa";
    }
}
