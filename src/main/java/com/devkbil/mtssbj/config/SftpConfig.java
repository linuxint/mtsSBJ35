package com.devkbil.mtssbj.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.sftp.session.DefaultSftpSessionFactory;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.messaging.MessageChannel;

@Configuration
@IntegrationComponentScan
public class SftpConfig {

    @Bean
    public MessageChannel sftpChannel() {
        return new DirectChannel();
    }

    @Bean
    public DefaultSftpSessionFactory sftpSessionFactory() {
        return new DefaultSftpSessionFactory();
    }

    @Bean
    public SftpRemoteFileTemplate sftpRemoteFileTemplate(DefaultSftpSessionFactory sftpSessionFactory) {
        return new SftpRemoteFileTemplate(sftpSessionFactory);
    }
}