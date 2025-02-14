package com.devkbil.mtssbj.config.security;

import com.devkbil.mtssbj.config.ConfigConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;

import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();

        request.setAttribute("DUPLICATE_LOGIN", true);

        //response.sendRedirect("/memberLogin");
        request.getRequestDispatcher(ConfigConstant.URL_LOGIN).forward(request, response);
    }
}