package com.devkbil.mtssbj.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Value("${info.contact.mail}")
    String myEmail;
    @Value("${info.contact.phone}")
    String myPhone;

    @Bean
    public GroupedOpenApi publicApi() {
        return GroupedOpenApi.builder()
                .group("MTS-public")
                .pathsToMatch("/**")
                .build();
    }

    /*
        @Bean
        public GroupedOpenApi adminApi() {
            return GroupedOpenApi.builder()
                    .group("springshop-admin")
                    .pathsToMatch("/admin/**")
                    .addOpenApiMethodFilter(method -> method.isAnnotationPresent(Admin.class))
                    .build();
        }
    */
    @Bean
    public OpenAPI springOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("My MTS API")
                        .description("Some custom description of API")
                        .version("v0.1")
                        .contact(new Contact().name("admin").email(myEmail))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation()
                        .description("SpringShop Wiki Documentation")
                        .url("https://springshop.wiki.github.org/docs"));
    }
}
