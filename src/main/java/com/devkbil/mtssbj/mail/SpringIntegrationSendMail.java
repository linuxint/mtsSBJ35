package com.devkbil.mtssbj.mail;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

/**
 * SpringIntegrationSendMail
 * Spring Integration Mail을 사용하여 이메일 전송을 처리하는 클래스.
 * SMTP 설정을 기반으로 메일을 전송하며, 수신자, 참조자, 숨은 참조자 및 메일 내용을 포함한 메일 발송 기능을 제공합니다.
 */
@Slf4j
public class SpringIntegrationSendMail {

    private final String smtpHost;
    private final String smtpPort;
    private final String smtpAccount;
    private final String smtpPasswd;
    private final String smtpUsernm;
    private String smtpssl = "true";  // SSL 활성화 여부
    private final JavaMailSender mailSender;

    /**
     * SMTP 설정 정보를 사용하여 SendMail 객체를 초기화합니다.
     *
     * @param host   SMTP 서버 호스트명
     * @param port   SMTP 서버 포트 번호
     * @param user   SMTP 계정 (발신 메일 계정)
     * @param usernm SMTP 사용자 이름
     * @param pw     SMTP 계정 비밀번호
     */
    public SpringIntegrationSendMail(String host, String port, String user, String usernm, String pw) {
        this.smtpHost = host;
        this.smtpPort = port;
        this.smtpAccount = user;
        this.smtpUsernm = usernm;
        this.smtpPasswd = pw;
        if (!"465".equals(port)) {  // 465 포트가 아니면 SSL 비활성화
            smtpssl = "false";
        }
        
        // Spring의 JavaMailSender 설정
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        sender.setHost(smtpHost);
        sender.setPort(Integer.parseInt(smtpPort));
        sender.setUsername(smtpAccount);
        sender.setPassword(smtpPasswd);
        
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.debug", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.EnableSSL.enable", "true");
        
        if ("true".equals(smtpssl)) {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.port", smtpPort);
        }
        props.put("mail.smtp.socketFactory.fallback", "false");
        
        sender.setJavaMailProperties(props);
        this.mailSender = sender;
    }

    /**
     * 메일 발송 테스트 메인 메서드.
     *
     * @param args 실행 매개변수
     * @throws Exception 예외 발생 시
     */
    public static void main(String[] args) throws Exception {
        SpringIntegrationSendMail sm = new SpringIntegrationSendMail("", "", "", "", "");
        sm.send(true, new String[]{""}, new String[]{}, new String[]{}, "test", "body1111");
    }

    /**
     * 이메일 발송 메서드.
     *
     * @param debug      디버그 모드 활성화 여부
     * @param recipients 수신자(TO) 목록
     * @param cc         참조자(CC) 목록
     * @param bcc        숨은 참조자(BCC) 목록
     * @param subject    이메일 제목
     * @param contents   이메일 본문 내용 (HTML 형식)
     * @throws MessagingException 이메일 전송 중 문제 발생 시 예외 처리
     */
    public void send(boolean debug, String[] recipients, String[] cc, String[] bcc, String subject,
                     String contents) throws MessagingException {
        try {
            // Spring의 MimeMessageHelper를 사용하여 메시지 구성
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // 발신자 설정
            helper.setFrom(new InternetAddress(smtpAccount, smtpUsernm, "UTF-8"));
            
            // 수신자 설정
            for (String recipient : recipients) {
                if (recipient != null && !recipient.isEmpty()) {
                    helper.addTo(recipient);
                }
            }
            
            // 참조자 설정
            if (cc != null && cc.length > 0) {
                for (String ccRecipient : cc) {
                    if (ccRecipient != null && !ccRecipient.isEmpty()) {
                        helper.addCc(ccRecipient);
                    }
                }
            }
            
            // 숨은 참조자 설정
            if (bcc != null && bcc.length > 0) {
                for (String bccRecipient : bcc) {
                    if (bccRecipient != null && !bccRecipient.isEmpty()) {
                        helper.addBcc(bccRecipient);
                    }
                }
            }
            
            // 제목 및 내용 설정
            helper.setSubject(subject);
            helper.setText(contents, true); // HTML 형식 활성화
            
            // 메일 전송
            mailSender.send(message);
            log.info("이메일 전송 성공: 수신자={}, 제목={}", String.join(", ", recipients), subject);
        } catch (UnsupportedEncodingException e) {
            log.error("이메일 발신자 정보 인코딩 실패", e);
            throw new MessagingException("이메일 발신자 정보 인코딩 실패", e);
        }
    }

    /**
     * 메일 템플릿 파일에서 내용을 읽어와 문자열로 반환.
     *
     * @param filename 템플릿 파일 경로
     * @return 메일 내용 문자열
     */
    public String get_mailFile(String filename) {
        File fileDir = new File(filename);
        StringBuilder mailBody = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileDir), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                mailBody.append(line);
            }
        } catch (FileNotFoundException e) {
            log.error("메일 템플릿 파일을 찾을 수 없습니다", e);
        } catch (IOException e) {
            log.error("메일 템플릿 파일 읽기 오류", e);
        }

        return mailBody.toString();
    }
}