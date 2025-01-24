package com.devkbil.mtssbj.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ThymeleafViewResolverConfig {
    /*
    @Value("${spring.thymeleaf.cache}")
    private boolean isCache = true;
    @Value("${spring.thymeleaf.suffix}")
    private String subffix = ".html";
    @Value("${spring.thymeleaf.mode}")
    private String templateMode = "LEGACYHTML5";
    @Value("${spring.thymeleaf.prefix}")
    private String prefix = "classpath:templates/";
    @Value("${spring.thymeleaf.encoding}")
    private String characterEncoding = "UTF-8";
    @Value("${spring.thymeleaf.order}")
    private Integer order = 0;

    @Bean
    public SpringResourceTemplateResolver templateResolver() {
        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix(prefix);
        templateResolver.setCharacterEncoding(characterEncoding);
        templateResolver.setSuffix(subffix);
        templateResolver.setTemplateMode(templateMode);
        templateResolver.setCacheable(isCache);
        return templateResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine(MessageSource messageSource) {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        //templateEngine.setTemplateResolver(templateResolver());
        templateEngine.setTemplateEngineMessageSource(messageSource);
        //templateEngine.addDialect(layoutDialect());

        return templateEngine;
    }

    //@Bean
    //public LayoutDialect layoutDialect() {
    //    return new LayoutDialect();
    //}
    @Bean
    @Autowired
    public ViewResolver viewResolver(MessageSource messageSource) {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine(messageSource));
        viewResolver.setCharacterEncoding(characterEncoding);
        viewResolver.setOrder(order);
        return viewResolver;
    }
    */
}
