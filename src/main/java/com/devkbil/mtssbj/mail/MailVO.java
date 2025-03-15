package com.devkbil.mtssbj.mail;

import com.devkbil.mtssbj.common.FileVO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * 메일 정보를 담는 VO 클래스.
 * 메일 발신자, 수신자, 제목, 내용, 첨부파일, 작성일 등 메일 데이터 속성을 정의합니다.
 */
@Schema(description = "메일 : MailVO")
@XmlRootElement(name = "MailVO")
@XmlType(propOrder = {"emno", "emtype", "emfrom", "emsubject", "emcontents", "regdate", "userno", "usernm", "emino", "strTo", "strCc", "strBcc", "emto", "emcc", "embcc", "files"})
@Getter
@Setter
public class MailVO {

    @Schema(description = "메일서비스", example = "MAIL001")
    private String emno;

    @Schema(description = " 받은 / 보낸 메일")
    private String emtype;    // 받은 / 보낸 메일

    @Schema(description = "발신인 이메일", example = "sender@example.com")
    private String emfrom;  // 발신인

    @Schema(description = "메일 제목", example = "프로젝트 진행 상황 보고")
    private String emsubject;  // 제목

    @Schema(description = "메일 내용", example = "안녕하세요, 프로젝트 진행 상황을 공유드립니다.")
    private String emcontents;  // 내용

    @Schema(description = "작성일 (YYYY-MM-DD 형식)", example = "2023-11-15")
    private String regdate;  // 작성일

    @Schema(description = "사용자 번호", example = "USR001")
    private String userno;  // 사용자 번호

    @Schema(description = "사용자 이름", example = "John Doe")
    private String usernm;  // 사용자명

    @Schema(description = "메일 정보 번호", example = "INFO001")
    private String emino;  // 메일정보번호

    @Schema(description = "수신자 (쉼표로 구분된 이메일 목록)", example = "user1@example.com, user2@example.com")
    private String strTo;  // 수신자 (문자열 형태)

    @Schema(description = "참조자 (쉼표로 구분된 이메일 목록)", example = "user3@example.com, user4@example.com")
    private String strCc; // 참조자

    @Schema(description = "숨은 참조자 (쉼표로 구분된 이메일 목록)", example = "hidden1@example.com, hidden2@example.com")
    private String strBcc; // 숨은참조자

    @Schema(description = "수신인 목록", example = "[\"user1@example.com\", \"user2@example.com\"]")
    private ArrayList<String> emto = new ArrayList<String>();

    @Schema(description = "참조자 목록", example = "[\"user3@example.com\", \"user4@example.com\"]")
    private ArrayList<String> emcc = new ArrayList<String>();

    @Schema(description = "숨은 참조자 목록", example = "[\"hidden1@example.com\", \"hidden2@example.com\"]")
    private ArrayList<String> embcc = new ArrayList<String>();

    @Schema(description = "첨부 파일 목록")
    private ArrayList<FileVO> files = new ArrayList<FileVO>();

}