package com.devkbil.mtssbj.etc;

import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * Alert 메시지 관련 API를 처리하는 컨트롤러입니다.
 * 현재 사용자의 Alert 메시지 리스트를 다양한 형식으로 제공하는 엔드포인트를 포함합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "AlertMsgController", description = "Alert 메시지 관련 API")
public class AlertMsgController {

    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 현재 사용자의 전체 Alert 메시지 리스트를 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return Alert 메시지 리스트 뷰 경로
     */
    @Operation(summary = "전체 Alert 메시지 리스트", description = "현재 사용자에 해당하는 전체 Alert 메시지 리스트를 반환합니다.")
    @GetMapping("/alertList")
    public String alertList(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        List<?> listview = etcService.selectAlertList(userno);

        modelMap.addAttribute("listview", listview);

        return "etc/alertList";
    }

    /**
     * 현재 사용자의 상위 5개의 Alert 메시지 리스트를 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return 상위 5개 Alert 메시지 리스트 뷰 경로
     */
    @Operation(summary = "Top 5 Alert 메시지 리스트", description = "현재 사용자에 해당하는 상위 5개의 Alert 메시지 리스트를 반환합니다.")
    @PostMapping("/alertList4Ajax")
    public String alertList4Ajax(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        List<?> listview = etcService.selectAlertList4Ajax(userno);

        modelMap.addAttribute("listview", listview);

        return "etc/alertList4Ajax";
    }

}
