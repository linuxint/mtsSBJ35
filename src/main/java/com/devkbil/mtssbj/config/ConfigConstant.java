package com.devkbil.mtssbj.config;

public class ConfigConstant {

    public static final int COOKIE_EXPIRE = 60 * 60 * 24 * 30; // 1개월

    public static final String URL_ACCESS_DENIED = "/accessDenied";
    public static final String PARAMETER_REMEMBER_ME = "remember-me";

    public static final String JSESSIONID = "JSESSIONID";
    public static final String SID_COOKIE_NAME = "sid";
    public static final String REMEMBER_ME_COOKIE_NAME = "mtsSBJ35-remember-me";
    public static final String REMEMBER_ME_KEY = "remember-me-key";

    public static final String URL_MAIN = "/index";
    public static final String URL_LOGIN = "/memberLogin";
    public static final String URL_LOGIN_PROCESS = "/login-process";
    public static final String URL_ERROR = "/error/**";
    public static final String URL_LOGOUT = "/memberLogout";

    public static final String CLASSPATH_ERROR_PAGE = "classpath:templates/error/";
    public static final String CLASSPATH_CSS = "classpath:/static/css/";
    public static final String CLASSPATH_IMAGES = "classpath:/static/images/";
    public static final String CLASSPATH_JS = "classpath:/static/js/";

    public static final String RESOURCES_JS = "resources/js/**";
    public static final String RESOURCES_IMAGES = "resources/images/**";
    public static final String RESOURCES_CSS = "resources/css/**";

    public static final String PARAMETER_LOGIN_ID = "userid";
    public static final String PARAMETER_LOGIN_PWD = "userpw";

    public static final String[] allAllowList = {
        "/api/v1/auth/**",
        "/css/**",
        "/js/**",
        "/images/**",
        "/favicon.ico",
        "/application/**",
        "/memberLogout",
        "/memberLogin",
        "/login-process",
        "/memberLoginChk",
        "/error/**"
    };
}
