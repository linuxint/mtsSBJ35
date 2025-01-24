package com.devkbil.mtssbj;

import org.springframework.validation.DefaultMessageCodesResolver;

public class ErrorMessageTest {
    public static void main(String[] args) throws Exception {

        DefaultMessageCodesResolver codesResolver = new DefaultMessageCodesResolver();
        //codesResolver.setMessageCodeFormatter(Format.POSTFIX_ERROR_CODE);
        String[] codes = codesResolver.resolveMessageCodes(
                "Min", "productRequest", "price", int.class);

        for (String code : codes) {
            System.out.println(code);
        }

    }

}
