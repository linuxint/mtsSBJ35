package com.devkbil.mtssbj.config;

import com.devkbil.mtssbj.common.LocalDateFormatter;
import com.devkbil.mtssbj.common.interceptor.AdminInterceptor;
import com.devkbil.mtssbj.common.interceptor.CommonInterceptor;
import com.devkbil.mtssbj.common.interceptor.DeviceDetectorInterceptor;
import com.devkbil.mtssbj.common.interceptor.LoginInterceptor;
import com.devkbil.mtssbj.common.interceptor.ThemeInterceptor;
import com.devkbil.mtssbj.common.interceptor.TradingTimeInterceptor;
import com.devkbil.mtssbj.common.interceptor.UrlMappingInterceptor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.CacheControl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.ErrorResponse;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import lombok.RequiredArgsConstructor;

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
        addResourceHandler(registry, ResourcePath.RESOURCES_JS.getPath(), ResourcePath.CLASSPATH_JS.getPath());
        addResourceHandler(registry, ResourcePath.RESOURCES_IMAGES.getPath(), ResourcePath.CLASSPATH_IMAGES.getPath());
        addResourceHandler(registry, ResourcePath.RESOURCES_CSS.getPath(), ResourcePath.CLASSPATH_CSS.getPath());

        // 에러 페이지는 공통 캐싱이 필요 없으므로 직접 추가
        registry.addResourceHandler(ConfigConstant.URL_ERROR)
            .addResourceLocations(ConfigConstant.CLASSPATH_ERROR_PAGE);
    }

    private void addResourceHandler(ResourceHandlerRegistry registry, String path, String location) {
        registry.addResourceHandler(path)
            .addResourceLocations(location)
            .setCacheControl(CacheControl.noCache().cachePrivate());
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
        if (StringUtils.hasText(indexPage)) {
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

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        WebMvcConfigurer.super.configurePathMatch(configurer);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        WebMvcConfigurer.super.configureAsyncSupport(configurer);
    }

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        WebMvcConfigurer.super.configureDefaultServletHandling(configurer);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        WebMvcConfigurer.super.addCorsMappings(registry);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        WebMvcConfigurer.super.configureViewResolvers(registry);
    }

    @Override
    public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> handlers) {
        WebMvcConfigurer.super.addReturnValueHandlers(handlers);
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.configureMessageConverters(converters);
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        WebMvcConfigurer.super.extendMessageConverters(converters);
    }

    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        WebMvcConfigurer.super.configureHandlerExceptionResolvers(resolvers);
    }

    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        WebMvcConfigurer.super.extendHandlerExceptionResolvers(resolvers);
    }

    @Override
    public void addErrorResponseInterceptors(List<ErrorResponse.Interceptor> interceptors) {
        WebMvcConfigurer.super.addErrorResponseInterceptors(interceptors);
    }

    @Override
    public Validator getValidator() {
        return WebMvcConfigurer.super.getValidator();
    }
}