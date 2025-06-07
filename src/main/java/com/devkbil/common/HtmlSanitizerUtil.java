package com.devkbil.common;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

public class HtmlSanitizerUtil {

    private static final PolicyFactory POLICY = new HtmlPolicyBuilder()
        .allowElements("b", "i", "u", "p", "br", "ul", "ol", "li", "a")
        .allowAttributes("href").onElements("a")
        .toFactory();

    public static String sanitize(String input) {
        return POLICY.sanitize(input);
    }
}