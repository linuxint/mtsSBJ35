package com.devkbil.mtssbj.mail;

import com.devkbil.common.util.DateUtil;
import com.devkbil.common.util.FileUtil;
import com.devkbil.mtssbj.common.FileVO;

import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeUtility;

import org.springframework.integration.mail.ImapMailReceiver;
import org.springframework.messaging.MessagingException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring Integration 기반 IMAP 클래스.
 * IMAP 이메일 서버에 연결하고, 이메일 메시지를 관리하며,
 * 첨부 파일을 포함한 이메일 콘텐츠를 추출하는 기능을 제공합니다.
 */
@Slf4j
public class SpringIntegrationImap {

    private static final String PROTOCOL = "imaps";
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
        imap.connect("", "", "");
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
    public void connect(String host, String user, String password) {
        try {
            // Spring Integration의 ImapMailReceiver 사용
            String url = "imaps://" + user + ":" + password + "@" + host + "/" + INBOX_FOLDER;
            mailReceiver = new ImapMailReceiver(url);

            Properties javaMailProperties = new Properties();
            javaMailProperties.put("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            javaMailProperties.put("mail.imap.socketFactory.port", "993");
            javaMailProperties.put("mail.imap.socketFactory.fallback", "false");
            javaMailProperties.put("mail.store.protocol", PROTOCOL);
            javaMailProperties.put("mail.debug", DEBUG);

            mailReceiver.setJavaMailProperties(javaMailProperties);
            mailReceiver.setShouldDeleteMessages(false);
            mailReceiver.setShouldMarkMessagesAsRead(true);

            // 연결 초기화
            mailReceiver.afterPropertiesSet();

            log.info("IMAP 서버 연결 성공: {}", host);
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
                Date startDate = DateUtil.str2Date(chgdate);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(startDate);
                calendar.add(Calendar.DATE, 1);
                Date endDate = calendar.getTime();

                List<Message> filteredMessages = new ArrayList<>();
                for (Object obj : receivedMessages) {
                    if (obj instanceof MimeMessage message) {
                        Date sentDate = message.getSentDate();
                        if (sentDate != null &&
                            sentDate.compareTo(startDate) >= 0 &&
                            sentDate.compareTo(endDate) < 0) {
                            filteredMessages.add(message);
                        }
                    }
                }

                msgs = filteredMessages.toArray(new Message[0]);
            } else {
                // 모든 메시지를 Message 배열로 변환
                msgs = new Message[receivedMessages.length];
                for (int i = 0; i < receivedMessages.length; i++) {
                    if (receivedMessages[i] instanceof MimeMessage) {
                        msgs[i] = (MimeMessage) receivedMessages[i];
                    }
                }
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
            return Collections.emptyList(); // 빈 리스트 반환
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

        return mailList;
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