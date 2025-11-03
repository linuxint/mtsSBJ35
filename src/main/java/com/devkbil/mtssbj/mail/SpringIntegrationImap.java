package com.devkbil.mtssbj.mail;

import com.devkbil.common.util.DateUtil;
import com.devkbil.common.util.FileUtil;
import com.devkbil.mtssbj.common.FileVO;

import com.google.common.collect.ImmutableList;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

import org.springframework.integration.mail.inbound.ImapMailReceiver;
import org.springframework.messaging.MessagingException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Integration 기반 IMAP 클래스.
 * IMAP 이메일 서버에 연결하고, 이메일 메시지를 관리하며,
 * 첨부 파일을 포함한 이메일 콘텐츠를 추출하는 기능을 제공합니다.
 */
@Slf4j
public class SpringIntegrationImap {

    private static final String INBOX_FOLDER = "INBOX";
    private static final boolean DEBUG = true;
    private final String filePath = System.getProperty("user.dir") + "/fileupload/";

    private ImapMailReceiver mailReceiver;
    private Message[] msgs;

    /**
     * 애플리케이션의 메인 메서드로, IMAP 서버에 연결하고,
     * 이메일을 배치로 가져온 후 서버 연결을 해제합니다.
     * 메일 데이터를 가져오고 처리된 메시지 수를 콘솔에 출력합니다.
     *
     * @param args 애플리케이션에 전달된 명령줄 인수
     * @throws Exception IMAP 연결, 메시지 가져오기, 연결 해제 중 발생하는 오류
     */
    public static void main(String[] args) throws Exception {
        SpringIntegrationImap imap = new SpringIntegrationImap();
        imap.connect("", "993", "", "");
        imap.patchMessage(null);

        int count = 0;
        while (count < imap.msgs.length) {
            ArrayList<MailVO> msgList = (ArrayList<MailVO>) imap.getMail(0, 100);
            count += msgList.size();
            System.out.println(count);
            break;
        }
        imap.disconnect();
    }

    /**
     * 이메일 서버에 연결
     *
     * @param host     이메일 서버의 호스트 주소
     * @param user     사용자 계정
     * @param password 사용자 비밀번호
     */
    public void connect(String host, String port, String user, String password) {
        try {
            if (host == null || host.isBlank()) {
                throw new IllegalArgumentException("IMAP 호스트가 비어 있습니다.");
            }
            if (user == null || user.isBlank()) {
                throw new IllegalArgumentException("IMAP 사용자명이 비어 있습니다.");
            }
            if (password == null || password.isBlank()) {
                throw new IllegalArgumentException("IMAP 비밀번호가 설정되지 않았습니다.");
            }
            // Gmail 앱 비밀번호 등 공백 표시되는 경우 제거
            String normalizedPassword = password.replaceAll("\\s+", "");
            // 포트/프로토콜을 사용자별 DB 값에 맞춰 동적으로 구성
            String resolvedPort = (port == null || port.isBlank()) ? "993" : port.trim();
            boolean useImaps = "993".equals(resolvedPort);
            String protocol = useImaps ? "imaps" : "imap";

            // Spring Integration의 ImapMailReceiver 사용 (URL에 포트 포함)
            String encodedUser = java.net.URLEncoder.encode(user, java.nio.charset.StandardCharsets.UTF_8);
            String encodedPass = java.net.URLEncoder.encode(normalizedPassword, java.nio.charset.StandardCharsets.UTF_8);
            String url = protocol + "://" + encodedUser + ":" + encodedPass + "@" + host + ":" + resolvedPort + "/" + INBOX_FOLDER;
            mailReceiver = new ImapMailReceiver(url);

            Properties javaMailProperties = new Properties();
            javaMailProperties.put("mail.store.protocol", protocol);
            javaMailProperties.put("mail.debug", String.valueOf(DEBUG));
            // 사용자/비밀번호를 명시적으로 전달 (URL 내 포함 외에 자카르타 메일 속성에도 지정)
            if (useImaps) {
                javaMailProperties.put("mail.imaps.user", user);
                javaMailProperties.put("mail.imaps.password", normalizedPassword);
            } else {
                javaMailProperties.put("mail.imap.user", user);
                javaMailProperties.put("mail.imap.password", normalizedPassword);
            }

            if (useImaps) {
                // 993: SSL IMAPS
                javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                javaMailProperties.put("mail.imap.socketFactory.port", resolvedPort);
                javaMailProperties.put("mail.imap.socketFactory.fallback", "false");
            } else {
                // 일반 IMAP (예: 143)에서는 STARTTLS를 시도하도록 설정 (서버가 지원하지 않으면 무시)
                javaMailProperties.put("mail.imap.starttls.enable", "true");
            }

            mailReceiver.setJavaMailProperties(javaMailProperties);
            mailReceiver.setShouldDeleteMessages(false);
            mailReceiver.setShouldMarkMessagesAsRead(true);

            // BeanFactory 설정 (스프링 컨텍스트 외부 실행 시 필요)
            DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
            // IntegrationContextUtils가 요구하는 EvaluationContext 선등록 (ExpressionUtils 호출 시 미존재하면 예외 발생)
            org.springframework.expression.spel.support.StandardEvaluationContext evalContext =
                    new org.springframework.expression.spel.support.StandardEvaluationContext();
            beanFactory.registerSingleton(
                    org.springframework.integration.context.IntegrationContextUtils.INTEGRATION_EVALUATION_CONTEXT_BEAN_NAME,
                    evalContext);
            // ImapMailReceiver 초기화 시 필요로 하는 taskScheduler 등록
            java.util.concurrent.ScheduledExecutorService scheduledExecutorService =
                    java.util.concurrent.Executors.newScheduledThreadPool(2);
            org.springframework.scheduling.concurrent.ConcurrentTaskScheduler concurrentTaskScheduler =
                    new org.springframework.scheduling.concurrent.ConcurrentTaskScheduler(scheduledExecutorService);
            beanFactory.registerSingleton(
                    org.springframework.integration.context.IntegrationContextUtils.TASK_SCHEDULER_BEAN_NAME,
                    concurrentTaskScheduler);
            // BeanFactory 주입 및 초기화
            mailReceiver.setBeanFactory(beanFactory);
            // 안전장치: 직접 TaskScheduler 주입 (빈 검색 실패 대비)
            mailReceiver.setTaskScheduler(concurrentTaskScheduler);
            // 연결 초기화
            mailReceiver.afterPropertiesSet();

            log.info("IMAP 서버 연결 성공: {}:{} ({})", host, resolvedPort, protocol);
        } catch (Exception e) {
            log.error("IMAP 서버 연결 실패: {}", e.getMessage());
            throw new RuntimeException("IMAP 연결 오류", e);
        }
    }

    /**
     * 이메일 서버와의 연결 해제
     */
    public void disconnect() {
        try {
            // Spring Integration에서는 명시적인 연결 해제가 필요 없음
            mailReceiver = null;
            log.info("IMAP 서버 연결이 해제되었습니다.");
        } catch (Exception e) {
            log.error("IMAP 서버 연결 해제 실패: {}", e.getMessage());
        }
    }

    /**
     * 이메일 메시지 검색
     *
     * @param chgdate 검색 기준 날짜 (null이면 전체 검색)
     * @return 검색된 메시지 개수
     */
    public int patchMessage(String chgdate) {
        try {
            // 모든 메시지 가져오기
            Object[] receivedMessages = mailReceiver.receive();

            // 날짜 필터링 (필요한 경우)
            if (chgdate != null) {
                Instant startInstant = DateUtil.str2Date(chgdate).toInstant();
                Instant endInstant = startInstant.plus(1, ChronoUnit.DAYS);

                List<Message> filteredMessages = new ArrayList<>();
                for (Object obj : receivedMessages) {
                    if (obj instanceof MimeMessage message) {
                        Date sentDate = message.getSentDate();
                        if (sentDate != null) {
                            Instant sentInstant = sentDate.toInstant();
                            if (!sentInstant.isBefore(startInstant) && sentInstant.isBefore(endInstant)) {
                                filteredMessages.add(message);
                            }
                        }
                    }
                }

                msgs = filteredMessages.toArray(new Message[0]);
            } else {
                // 모든 메시지를 Message 배열로 변환
                msgs = Arrays.stream(receivedMessages)
                        .filter(MimeMessage.class::isInstance)
                        .map(MimeMessage.class::cast)
                        .toArray(Message[]::new);
            }

            log.info("검색된 메시지: {}건", msgs.length);
            return msgs.length;
        } catch (Exception e) {
            log.error("메시지 검색 실패: {}", e.getMessage());
            return 0;
        }
    }

    /**
     * 이메일 메시지를 배치로 가져오기
     *
     * @param startIndex 시작 인덱스
     * @param maxCount   최대 메시지 수
     * @return 메시지 데이터 리스트
     */
    public List<MailVO> getMail(int startIndex, int maxCount) {
        if (msgs == null || msgs.length == 0) {
            log.warn("가져올 메시지가 없습니다.");
            return ImmutableList.of(); // ✅ 항상 불변 리스트 반환
        }

        List<MailVO> mailList = new ArrayList<>();
        IntStream.range(startIndex, Math.min(startIndex + maxCount, msgs.length))
                .forEach(idx -> {
                    try {
                        MailVO mail = extractMailData(msgs[idx]);
                        mailList.add(mail);
                    } catch (Exception e) {
                        log.error("인덱스 {}에서 메시지 처리 실패: {}", idx, e.getMessage());
                    }
                });

        return ImmutableList.copyOf(mailList); // ✅ 불변 리스트로 변환 후 반환
    }

    /**
     * 단일 메시지 데이터 추출
     *
     * @param message 메시지 객체
     * @return 추출된 메시지 데이터 (MailVO)
     * @throws Exception 데이터 처리 오류
     */
    private MailVO extractMailData(Message message) throws Exception {
        MailVO mail = new MailVO();

        // 발신자 정보 처리
        if (message.getFrom() != null && message.getFrom().length > 0) {
            mail.setEmfrom(MimeUtility.decodeText(message.getFrom()[0].toString()));
        }

        // 수신자 정보 추출
        extractRecipients(mail, message, Message.RecipientType.TO, mail.getEmto());
        extractRecipients(mail, message, Message.RecipientType.CC, mail.getEmcc());

        // 제목, 날짜 및 내용 설정
        mail.setEmsubject(message.getSubject());
        mail.setRegdate(DateUtil.date2Str(message.getSentDate()));
        extractContent(message, mail);

        // 본문을 파일로 저장하고 파일명을 기록, 미리보기는 4000자 이내로 제한
        String contents = mail.getEmcontents();
        if (contents != null) {
            String contentFileName = FileUtil.getNewName() + ".html";
            Path contentPath = Paths.get(filePath + contentFileName);
            Files.writeString(contentPath, contents, java.nio.charset.StandardCharsets.UTF_8);
            mail.setEmcontentFile(contentFileName);

            // 미리보기(요약) 4000자 제한 저장
            int previewLimit = Math.min(contents.length(), 4000);
            mail.setEmcontents(contents.substring(0, previewLimit));
        }

        return mail;
    }

    /**
     * 수신자 정보 처리
     *
     * @param mail       MailVO 객체
     * @param message    메시지 객체
     * @param type       수신자 유형 (TO/CC)
     * @param targetList 결과 저장할 대상 리스트
     * @throws MessagingException           메시지 처리 오류
     * @throws UnsupportedEncodingException 인코딩 오류
     */
    private void extractRecipients(MailVO mail, Message message, Message.RecipientType type, List<String> targetList) throws jakarta.mail.MessagingException, UnsupportedEncodingException {
        jakarta.mail.Address[] recipients = message.getRecipients(type);
        if (recipients != null) {
            for (jakarta.mail.Address address : recipients) {
                targetList.add(MimeUtility.decodeText(address.toString()));
            }
        }
    }

    /**
     * 메시지 내용 추출
     *
     * @param part 이메일 내용
     * @param mail MailVO 객체
     * @throws Exception 데이터 처리 오류
     */
    private void extractContent(Part part, MailVO mail) throws Exception {
        Object content = part.getContent();

        if (content instanceof String) {
            mail.setEmcontents((String) content); // 텍스트 콘텐츠
        } else if (content instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                extractContent(multipart.getBodyPart(i), mail); // 재귀적으로 처리
            }
        } else if (part.getFileName() != null) {
            saveAttachment(part, mail); // 첨부 파일 저장
        }
    }

    /**
     * 첨부 파일 저장
     *
     * @param part 이메일 첨부 파일 콘텐츠
     * @param mail MailVO 객체
     * @throws Exception 첨부 파일 처리 오류
     */
    private void saveAttachment(Part part, MailVO mail) throws Exception {
        String filename = part.getFileName();
        if (filename != null) {
            String realName = FileUtil.getNewName(); // 새로운 파일 이름 생성
            File file = new File(filePath + realName);

            try (InputStream is = part.getInputStream(); OutputStream os = new FileOutputStream(file)) {
                is.transferTo(os); // 파일 저장
            }

            FileVO fileVO = new FileVO();
            fileVO.setFilename(MimeUtility.decodeText(filename));
            fileVO.setRealname(realName);
            fileVO.setFilesize(file.length());
            mail.getFiles().add(fileVO);
        }
    }
}