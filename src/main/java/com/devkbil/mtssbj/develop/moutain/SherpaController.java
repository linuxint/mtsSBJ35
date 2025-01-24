package com.devkbil.mtssbj.develop.moutain;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class SherpaController {

    @GetMapping("/sherpa")
    public String authSherpa(HttpServletRequest request, ModelMap modelMap) {
        return "thymeleaf/sherpa";
    }
}
