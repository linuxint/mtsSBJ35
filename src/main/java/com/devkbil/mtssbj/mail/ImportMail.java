package com.devkbil.mtssbj.mail;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.servlet.http.HttpSession;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 메일 수신 작업을 위한 ImportMail 클래스.
 * 외부 메일 서버와 연결하여 메일을 수신하는 Runnable 구현체입니다.
 */
@Schema(description = "메일 수신")
@XmlRootElement(name = "ImportMail")
@XmlType(propOrder = {"mailService", "userno", "session"})
@Getter
@Setter
@Slf4j
public class ImportMail implements Runnable {

    @Schema(description = "메일 서비스")
    private final MailService mailService;

    @Schema(description = "사용자 번호")
    private final String userno;

    @Schema(description = "HTTP 세션")
    private final HttpSession session;
    private static final int BATCH_SIZE = 100; // 유동적 변경 가능
    private static final ExecutorService executor = Executors.newFixedThreadPool(4); // 병렬 처리

    public ImportMail(MailService mailService, String userno, HttpSession session) {
        this.mailService = mailService;
        this.userno = userno;
        this.session = session;
    }

    /**
     * 메일 수신 작업 실행 메서드.
     * 외부 서버와 연결하여 메일을 수신하고 데이터를 저장합니다.
     */
    @Override
    public void run() {
        List<?> list = mailService.selectMailInfoList(userno);
        if (list == null || list.isEmpty()) {
            log.info("메일 정보가 없습니다: 사용자={}", userno);
            session.removeAttribute("mail");
            return;
        }

        try {
            list.forEach(obj -> executor.submit(() -> processMailAccount((MailInfoVO) obj)));
        } catch (Exception e) {
            log.error("메일 수신 작업 중 오류 발생: 사용자={}", userno, e);
        } finally {
            session.removeAttribute("mail");
        }
    }

    private void processMailAccount(MailInfoVO mivo) {
        String chgdate = mailService.selectLastMail(mivo.getEmino());
        Imap mail = new Imap();

        try {
            mail.connect(mivo.getEmiimap(), mivo.getEmiuser(), mivo.getEmipw());
            int total = mail.patchMessage(chgdate);

            int cnt = 0;
            while (cnt < total) {
                ArrayList<MailVO> msgList = (ArrayList<MailVO>) mail.getMail(cnt, BATCH_SIZE);
                if (msgList != null && !msgList.isEmpty()) {
                    mailService.insertMails(msgList, userno, mivo.getEmino());
                    cnt += msgList.size();
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            log.error("메일 처리 실패: 사용자={}, 계정={}", userno, mivo.getEmiuser(), e);
        } finally {
            mail.disconnect();
        }
    }
}