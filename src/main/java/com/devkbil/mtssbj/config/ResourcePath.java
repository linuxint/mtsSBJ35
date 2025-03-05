package com.devkbil.mtssbj.config;

public enum ResourcePath {
    // Classpath 경로
    CLASSPATH_CSS("classpath:/static/css/"), CLASSPATH_IMAGES("classpath:/static/images/"), CLASSPATH_JS("classpath:/static/js/"),

    // Resource 경로
    RESOURCES_JS("resources/js/**"), RESOURCES_IMAGES("resources/images/**"), RESOURCES_CSS("resources/css/**");

    private final String path;

    ResourcePath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
