package com.devkbil.mtssbj.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * 메일 계정 정보 VO 클래스.
 * 메일 관련 계정 및 서버 정보를 포함한 데이터 객체입니다.
 */
@Schema(description = "메일정보 : MailInfoVO")
@XmlRootElement(name = "MailInfoVO")
@XmlType(propOrder = {"emino", "emiimap", "emiimapport", "emismtp", "emismtpport", "emiuser", "emipw", "userno",
        "usernm"})
@Getter
@Setter
public class MailInfoVO {

    @Schema(description = "메일 정보 번호", example = "MAIL12345")
    private String emino;  // 메일 정보 번호

    @Schema(description = "IMAP 서버 주소", example = "imap.example.com")
    private String emiimap;  // IMAP 서버 주소

    @Schema(description = "IMAP 서버 포트", example = "993")
    private String emiimapport;  // IMAP 서버 포트

    @Schema(description = "SMTP 서버 주소", example = "smtp.example.com")
    private String emismtp;  // SMTP 서버 주소

    @Schema(description = "SMTP 서버 포트", example = "465")
    private String emismtpport;  // SMTP 서버 포트

    @Schema(description = "메일 계정", example = "user@example.com")
    private String emiuser;  // 계정

    @Schema(description = "메일 계정 비밀번호", example = "password123")
    private String emipw;  // 비밀번호

    @Schema(description = "사용자 번호", example = "USR001")
    private String userno;  // 사용자 번호

    @Schema(description = "사용자 이름", example = "John Doe")
    private String usernm;  // 사용자 이름
}
