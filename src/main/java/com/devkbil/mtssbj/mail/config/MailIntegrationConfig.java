package com.devkbil.mtssbj.mail.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.integration.mail.MailSendingMessageHandler;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.messaging.MessageChannel;

import java.util.Properties;

/**
 * Spring Integration Mail 설정 클래스.
 * 메일 송수신을 위한 Spring Integration 컴포넌트를 구성합니다.
 */
@Configuration
@EnableIntegration
public class MailIntegrationConfig {

    /**
     * 메일 수신 채널을 생성합니다.
     *
     * @return 메일 수신을 위한 메시지 채널
     */
    @Bean
    public MessageChannel receiveEmailChannel() {
        return new DirectChannel();
    }

    /**
     * 메일 발신 채널을 생성합니다.
     *
     * @return 메일 발신을 위한 메시지 채널
     */
    @Bean
    public MessageChannel sendEmailChannel() {
        return new DirectChannel();
    }

    /**
     * 기본 JavaMailSender 빈을 생성합니다.
     * 실제 사용 시에는 동적으로 설정이 변경됩니다.
     *
     * @return JavaMailSender 인스턴스
     */
    @Bean
    public JavaMailSender defaultMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        // 기본 설정은 비어있으며, 실제 사용 시 동적으로 설정됩니다.
        return mailSender;
    }

    /**
     * 메일 발신 메시지 핸들러를 생성합니다.
     *
     * @param mailSender JavaMailSender 인스턴스
     * @return 메일 발신 메시지 핸들러
     */
    @Bean
    public MailSendingMessageHandler mailSendingHandler(JavaMailSender mailSender) {
        MailSendingMessageHandler handler = new MailSendingMessageHandler(mailSender);
        return handler;
    }

    /**
     * IMAP 메일 수신기 팩토리 메서드.
     * 동적으로 설정된 IMAP 메일 수신기를 생성합니다.
     *
     * @param host IMAP 서버 호스트
     * @param username 사용자 이름
     * @param password 비밀번호
     * @return 구성된 ImapMailReceiver
     */
    public ImapMailReceiver createImapMailReceiver(String host, String username, String password) {
        String url = "imaps://" + username + ":" + password + "@" + host + "/INBOX";
        ImapMailReceiver receiver = new ImapMailReceiver(url);

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        javaMailProperties.put("mail.imap.socketFactory.fallback", "false");
        javaMailProperties.put("mail.store.protocol", "imaps");
        javaMailProperties.put("mail.debug", "false");

        receiver.setJavaMailProperties(javaMailProperties);
        receiver.setShouldDeleteMessages(false);
        receiver.setShouldMarkMessagesAsRead(true);

        return receiver;
    }
}