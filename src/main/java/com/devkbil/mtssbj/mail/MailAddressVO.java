package com.devkbil.mtssbj.mail;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * 메일 주소 정보 클래스.
 * 각 메일 주소와 관련된 정보를 저장하는 VO(Value Object)입니다.
 * 이메일 주소, 주소의 역할(TO, CC, BCC), 그리고 순번 정보 등을 포함합니다.
 */
@Schema(description = "메일주소 : MailAddressVO")
@XmlRootElement(name = "MailAddressVO")
@XmlType(propOrder = {"emno", "eatype", "eaaddress", "easeq"})
@Getter
@Setter
public class MailAddressVO {

    @Schema(description = "메일 번호", example = "MAIL12345")
    private String emno; // 메일번호

    @Schema(description = "주소 유형 (TO: 수신자, CC: 참조자, BCC: 숨은 참조자)", example = "TO")
    private String eatype; // 주소 유형

    @Schema(description = "이메일 주소", example = "example@domain.com")
    private String eaaddress; // 이메일 주소

    @Schema(description = "주소 순번", example = "1")
    private Integer easeq; // 주소 순번

} 
