package com.devkbil.mtssbj.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@Slf4j
public class UtilEtc {

    /**
     * JSON (String)을 문자열로 변환하여 클라이언트에 전송.
     * AJax 용.
     */
    public static void responseJsonValue(HttpServletResponse response, Object value) {
        ObjectMapper mapper = new ObjectMapper();
        response.setContentType("application/json;charset=UTF-8");

        try {
            response.getWriter().print(mapper.writeValueAsString(value));
        } catch (IOException ex) {
            log.error("responseJsonValue");
        }
    }

    public static String text2Html(String txt) {
        txt = txt.replaceAll(" ", "&nbsp");
        return txt.replaceAll("\n", "<br>");
    }
}
