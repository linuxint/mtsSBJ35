package com.devkbil.mtssbj.admin.ldap;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {
    @Bean
    @ConditionalOnProperty(name = "spring.ldap.urls")
    public LdapContextSource ldapContextSource() {
        LdapContextSource contextSource = new LdapContextSource();
        contextSource.setUrl(System.getProperty("spring.ldap.urls", ""));
        contextSource.setBase(System.getProperty("spring.ldap.base", ""));
        contextSource.setUserDn(System.getProperty("spring.ldap.username", ""));
        contextSource.setPassword(System.getProperty("spring.ldap.password", ""));
        return contextSource;
    }

    @Bean
    @ConditionalOnProperty(name = "spring.ldap.urls")
    public LdapTemplate ldapTemplate(LdapContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}

