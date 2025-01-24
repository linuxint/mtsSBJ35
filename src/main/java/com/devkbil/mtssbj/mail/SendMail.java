package com.devkbil.mtssbj.mail;

import jakarta.mail.*;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * SendMail
 * 이메일 전송을 처리하는 클래스.
 * SMTP 설정을 기반으로 메일을 전송하며, 수신자, 참조자, 숨은 참조자 및 메일 내용을 포함한 메일 발송 기능을 제공합니다.
 */
@Slf4j
public class SendMail {

    private final String SMTP_HOST;
    private final String SMTP_PORT;        // "465";
    private final String SMTP_ACCOUNT;
    private final String SMTP_PASSWD;
    private final String SMTP_USERNM;
    private String smtpssl = "true";  // SSL 활성화 여부

    /**
     * SMTP 설정 정보를 사용하여 SendMail 객체를 초기화합니다.
     *
     * @param host   SMTP 서버 호스트명
     * @param port   SMTP 서버 포트 번호
     * @param user   SMTP 계정 (발신 메일 계정)
     * @param usernm SMTP 사용자 이름
     * @param pw     SMTP 계정 비밀번호
     */
    public SendMail(String host, String port, String user, String usernm, String pw) {
        this.SMTP_HOST = host;
        this.SMTP_PORT = port;
        this.SMTP_ACCOUNT = user;
        this.SMTP_USERNM = usernm;
        this.SMTP_PASSWD = pw;
        if (!"465".equals(port)) {  // 465 포트가 아니면 SSL 비활성화
            smtpssl = "false";
        }
    }

    /**
     * 메일 발송 테스트 메인 메서드.
     *
     * @param args 실행 매개변수
     * @throws Exception 예외 발생 시
     */
    public static void main(String[] args) throws Exception {

        SendMail sm = new SendMail("", "", "", "", "");
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
        // SMTP 서버 속성 설정
        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);  // SMTP 서버
        props.put("mail.smtp.auth", "true");    // 인증 활성화
        props.put("mail.debug", "true");        // 디버깅 활성화
        props.put("mail.smtp.starttls.enable", "true"); // TLS 활성화
        props.put("mail.smtp.EnableSSL.enable", "true"); // SSL 활성화
        props.put("mail.smtp.port", SMTP_PORT); // SMTP 포트

        props.put("mail.smtp.socketFactory.port", SMTP_PORT);
        if ("true".equals(smtpssl)) {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        }
        props.put("mail.smtp.socketFactory.fallback", "false");

        // 인증 세션 설정
        Session session = Session.getDefaultInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SMTP_ACCOUNT, SMTP_PASSWD);
            }
        });

        session.setDebug(debug);  // 디버깅 설정

        try {
            // 메시지 구성
            MimeMessage msg = new MimeMessage(session);
            InternetAddress addressFrom = new InternetAddress(SMTP_ACCOUNT, SMTP_USERNM, "UTF-8");
            msg.setFrom(addressFrom); // 발신자 설정
            msg.setRecipients(Message.RecipientType.TO, mail2Addr(recipients)); // 수신자 설정

            if (cc.length > 0) {
                msg.setRecipients(Message.RecipientType.CC, mail2Addr(cc)); // 참조자 설정
            }
            if (bcc.length > 0) {
                msg.setRecipients(Message.RecipientType.BCC, mail2Addr(bcc)); // 숨은 참조자 설정
            }

            msg.setSubject(subject, "UTF-8"); // 이메일 제목 설정
            msg.setContent(contents, "text/html;charset=UTF-8"); // 이메일 본문 설정

            // 이메일 전송
            Transport.send(msg);
        } catch (UnsupportedEncodingException e) {
            log.error("Failed to encode email sender information.", e);
        }
    }

    /**
     * 문자열 이메일 주소 배열을 InternetAddress 배열로 변환.
     *
     * @param maillist 이메일 주소 문자열 배열
     * @return InternetAddress 배열
     */
    public InternetAddress[] mail2Addr(String[] maillist) {
        InternetAddress[] addressTo = new InternetAddress[maillist.length];
        try {
            for (int i = 0; i < maillist.length; i++) {
                if (!"".equals(maillist[i])) {
                    addressTo[i] = new InternetAddress(maillist[i]); // 유효한 이메일 주소로 변환
                }
            }
        } catch (AddressException e) {
            log.error("Failed to convert email address list.", e);
        }
        return addressTo;
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
            log.error("Error reading mail template file.", e);
        } catch (IOException e) {
            log.error("Error reading mail template file.", e);
        }

        return mailBody.toString();
    }
}
