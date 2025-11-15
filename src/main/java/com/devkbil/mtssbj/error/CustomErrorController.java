package com.devkbil.mtssbj.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

@Controller
public class CustomErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";

    @RequestMapping(value = ERROR_PATH, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorHtml(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            try {
                httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));
            } catch (Exception ex) {
                // status code가 숫자가 아닌 경우, 기본값 사용
            }
        }

        ErrorCode errorCode = ErrorCode.resolve(httpStatus.value());
        ErrorResponse errorResponse = ErrorResponse.of(errorCode, httpStatus.getReasonPhrase());

        ModelAndView modelAndView = new ModelAndView();
        if (httpStatus == HttpStatus.NOT_FOUND) {
            modelAndView.setViewName("error/404");
        } else if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            modelAndView.setViewName("error/500");
        } else {
            modelAndView.setViewName("error/error");
        }

        modelAndView.addObject("status", httpStatus.value());
        modelAndView.addObject("errorResponse", errorResponse);
        try {
            modelAndView.addObject("errorJson", new ObjectMapper().writeValueAsString(errorResponse));
        } catch (JsonProcessingException e) {
            modelAndView.addObject("errorJson", "{}");
        }

        return modelAndView;
    }

    @RequestMapping(value = ERROR_PATH)
    public ResponseEntity<ErrorResponse> error(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        if (status != null) {
            try {
                httpStatus = HttpStatus.valueOf(Integer.parseInt(status.toString()));
            } catch (Exception ex) {
                // status code가 숫자가 아닌 경우, 기본값 사용
            }
        }

        ErrorCode errorCode;
        if (httpStatus == HttpStatus.NOT_FOUND) {
            errorCode = ErrorCode.NOT_FOUND_ERROR;
        } else if (httpStatus == HttpStatus.BAD_REQUEST) {
            errorCode = ErrorCode.BAD_REQUEST_ERROR;
        } else {
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        }

        ErrorResponse response = ErrorResponse.of(errorCode, httpStatus.getReasonPhrase());
        return new ResponseEntity<>(response, httpStatus);
    }
}
