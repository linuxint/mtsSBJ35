package com.devkbil.mtssbj.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

/**
 * 메일 데이터 요청 객체 (VO).
 * 발신인, 수신인, 첨부파일 등의 메일 요소들을 포함합니다.
 */
@Schema(description = "메일 요청 데이터")
@XmlRootElement(name = "ImportMailVO")
@XmlType(propOrder = {"from", "to", "cc", "bcc", "file", "subject", "contents", "regdate"})
@Getter
@Setter
public class ImportMailVO {

    @Schema(description = "발신인 이메일", example = "sender@example.com")
    @NotEmpty(message = "발신인 이메일은 필수 입력 값입니다.")
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String from;  // 발신인

    @Schema(description = "수신인 목록", example = "[\"receiver1@example.com\", \"receiver2@example.com\"]")
    private ArrayList<String> to = new ArrayList<String>();  // 수신인

    @Schema(description = "참조자 목록", example = "[\"cc1@example.com\", \"cc2@example.com\"]")
    private ArrayList<String> cc = new ArrayList<String>();  // 참조자

    @Schema(description = "숨은 참조자 목록", example = "[\"bcc1@example.com\", \"bcc2@example.com\"]")
    private ArrayList<String> bcc = new ArrayList<String>();  // 숨은 참조자

    @Schema(description = "첨부 파일 목록", example = "[\"file1.pdf\", \"file2.png\"]")
    private ArrayList<String> file = new ArrayList<String>(); // 첨부파일

    @Schema(description = "메일 제목", example = "회의 일정 안내")
    private String subject; // 제목

    @Schema(description = "메일 내용", example = "안녕하세요, 아래는 회의 일정입니다.")
    private String conents; // 내용

    @Schema(description = "메일 작성일 (ISO 8601 형식: YYYY-MM-DD)", example = "2023-11-15")
    private String regdate; // 작성일

}
