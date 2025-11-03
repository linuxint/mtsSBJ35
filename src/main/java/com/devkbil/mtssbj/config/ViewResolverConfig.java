package com.devkbil.mtssbj.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@Configuration
public class ViewResolverConfig {

    // JSP 경로 설정
    @Value("${spring.mvc.view.prefix}")
    private String jspPrefix;

    @Value("${spring.mvc.view.suffix:.jsp}")
    private String jspSuffix;

    // Thymeleaf 경로 설정
    @Value("${spring.thymeleaf.prefix:classpath:/templates/}")
    private String thymeleafPrefix;

    @Value("${spring.thymeleaf.suffix:.html}")
    private String thymeleafSuffix;

    @Value("${spring.thymeleaf.view-names:thymeleaf/*}")
    private String thymeleafViewNames;

    @Bean
    public InternalResourceViewResolver jspViewResolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setPrefix(jspPrefix);
        resolver.setSuffix(jspSuffix);
        resolver.setOrder(2); // Thymeleaf보다 낮은 우선순위
        return resolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(SpringResourceTemplateResolver templateResolver) {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix(thymeleafPrefix);
        resolver.setSuffix(thymeleafSuffix);
        resolver.setTemplateMode("HTML");
        resolver.setCacheable(false);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(templateEngine);
        resolver.setOrder(1); // JSP보다 높은 우선순위
        resolver.setViewNames(new String[]{thymeleafViewNames});
        resolver.setCharacterEncoding("UTF-8");
        return resolver;
    }

}