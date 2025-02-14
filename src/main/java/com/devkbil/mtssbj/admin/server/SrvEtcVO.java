package com.devkbil.mtssbj.admin.server;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "서버 서비스 기타 정보 VO")
@XmlRootElement(name = "ServiceEtcVO")
@XmlType(propOrder = {"etcId", "connId", "userName", "etcName", "etcData", "actYn", "deleteFlag", "regDate", "regUserNo", "chgDate", "chgUserNo"})
public class SrvEtcVO extends ConnVO {

    @Schema(description = "기타 정보 ID", example = "1")
    private String etcId;

//    @Schema(description = "접속 정보 ID", example = "2001")
//    private String connId;

    @Schema(description = "기타 정보명", example = "Configuration")
    private String etcName;

    @Schema(description = "기타 정보 데이터", example = "Some configuration details")
    private String etcData;

    @Schema(description = "활성 여부 (Y/N)", example = "Y")
    private String actYn;

    @Schema(description = "삭제 여부 (Y/N)", example = "N")
    private String deleteFlag;

    @Schema(description = "등록일", example = "2023-01-01")
    private String regDate;

    @Schema(description = "등록자 번호", example = "101")
    private String regUserNo;

    @Schema(description = "수정일", example = "2023-01-10")
    private String chgDate;

    @Schema(description = "수정자 번호", example = "102")
    private String chgUserNo;
}