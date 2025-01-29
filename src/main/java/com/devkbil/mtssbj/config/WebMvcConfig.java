package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.common.LocalDateFormatter;
import com.devkbil.mtssbj.common.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {



    @Value("${server.indexPage}")
    private String indexPage;

    @Profile("Prod")
    @Configuration
    public static class ProdMvcConfiguration {
    }

    @Profile("local")
    @Configuration
    public static class LocalMvcConfiguration {
    }

    @Profile("dev")
    @Configuration
    public static class DevMvcConfiguration {
    }

    @Profile("stag")
    @Configuration
    public static class StagMvcConfiguration {
    }

    /**
     * Interceptor 정의 - admin,login
     *
     * @param registry 등록할 Interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        AdminInterceptor adminInterceptor = new AdminInterceptor();
        registry.addInterceptor(adminInterceptor).order(3) // 우선순위 3
                .addPathPatterns(adminInterceptor.getAdminEssential()); // 사용될 URL
        //.excludePathPatterns(adminInterceptor.adminInessential); // 제외될 URL

        LoginInterceptor loginIntercepter = new LoginInterceptor();
        registry.addInterceptor(loginIntercepter).order(2) // 우선순위 2
                .addPathPatterns(loginIntercepter.getLoginEssential())  // 사용될 URL
                .excludePathPatterns(loginIntercepter.getLoginInessential());  // 제외될 URL

        CommonInterceptor commonInterceptor = new CommonInterceptor();
        registry.addInterceptor(commonInterceptor).order(1); // 우선순위 1

        DeviceDetectorInterceptor deviceDetectorInterceptor = new DeviceDetectorInterceptor();
        registry.addInterceptor(deviceDetectorInterceptor).order(1);

        UrlMappingInterceptor urlMappingInterceptor = new UrlMappingInterceptor();
        registry.addInterceptor(urlMappingInterceptor).addPathPatterns("/**"); // 모든 요청에 대해 Interceptor 실행

        TradingTimeInterceptor tradingTimeInterceptor = new TradingTimeInterceptor();
        registry.addInterceptor(tradingTimeInterceptor).addPathPatterns("/**").excludePathPatterns(ConfigConstant.allAllowList); // 모든 요청에 대해 근무시간 체크 실행

        registry.addInterceptor(new ThemeInterceptor()).addPathPatterns("/**");

    }
    
    /*
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        //전체 resource를 허용할 경우 registry.addMapping("/**")...
        registry.addMapping("/**")   //mapping할 resource를 지정합니다.
                .allowedOrigins("http://localhost:18080");  //CORS를 허용할 origin을 지정합니다.
    }
     */

    /**
     * resourceHandlers정의 (/js, /css)
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/apidoc/**")
//                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler(ConfigConstant.RESOURCES_JS)    //지정된 path pattern에 대한 handler를 추가합니다. ex) "/m/**"
                .addResourceLocations(ConfigConstant.CLASSPATH_JS)  //반드시 /로 끝나야 정상적으로 동작함에 주의합니다. ex) "classpath:/m/"
                .setCacheControl(CacheControl.noCache().cachePrivate());
        registry.addResourceHandler(ConfigConstant.RESOURCES_IMAGES)
                .addResourceLocations(ConfigConstant.CLASSPATH_IMAGES)
                .setCachePeriod(20);    //caching period를 20초로 지정합니다. ex) 60 * 60 * 24 * 365
        registry.addResourceHandler(ConfigConstant.RESOURCES_CSS)
                .addResourceLocations(ConfigConstant.CLASSPATH_CSS)
                .setCacheControl(CacheControl.noCache().cachePrivate());
        registry.addResourceHandler(ConfigConstant.URL_ERROR)
                .addResourceLocations(ConfigConstant.CLASSPATH_ERROR_PAGE);
    }

    /**
     * URL Redirect 및 View Controller 정의
     *
     * @param registry URL과 View 매핑
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // URL Redirect 설정
        registry.addRedirectViewController(ConfigConstant.URL_MAIN, ConfigConstant.URL_LOGIN);

        // Index 페이지 설정
        if (indexPage != null && !indexPage.isBlank()) {
            registry.addViewController("/").setViewName("forward:/" + indexPage);
        }
    }

    /**
     * 메시지 코드 포맷 설정 (POSTFIX_ERROR_CODE 사용)
     *
     * @return MessageCodesResolver
     */
    @Override
    public MessageCodesResolver getMessageCodesResolver() {
        DefaultMessageCodesResolver codesResolver = new DefaultMessageCodesResolver();
        codesResolver.setMessageCodeFormatter(DefaultMessageCodesResolver.Format.POSTFIX_ERROR_CODE);
        return codesResolver;
    }

    /**
     * Formatter 설정
     *
     * @param registry 등록할 Formatter
     */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new LocalDateFormatter());
    }
}