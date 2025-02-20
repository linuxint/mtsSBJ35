package com.devkbil.mtssbj.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.text.StringEscapeUtils;

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
            // 문자열이라면 XSS 방어를 위해 HTML 이스케이프
            if (value instanceof String) {
                value = StringEscapeUtils.escapeHtml4(value.toString());
            }
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
