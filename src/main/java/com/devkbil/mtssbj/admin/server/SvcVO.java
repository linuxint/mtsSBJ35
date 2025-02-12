package com.devkbil.mtssbj.admin.server;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "서버 서비스 VO")
@XmlRootElement(name = "SvcVO")
@XmlType(propOrder = {"svcId", "hwId", "hwName", "swId", "swName", "svcName", "svcPort", "actYn", "deleteFlag", "regDate", "regUserNo", "chgDate", "chgUserNo"})
public class SvcVO extends SWVO {

    @Schema(description = "서비스 ID", example = "1")
    private String svcId;

    @Schema(description = "하드웨어 ID", example = "1001")
    private String hwId;

    @Schema(description = "하드웨어명", example = "서버1")
    private String hwName; // 하드웨어 이름(조회 조인 시 포함 가능)

    @Schema(description = "소프트웨어 ID", example = "2001")
    private String swId;

    @Schema(description = "소프트웨어명", example = "Apache")
    private String swName; // 소프트웨어 이름(조회 조인 시 포함 가능)

    @Schema(description = "서비스명", example = "WAS서버")
    private String svcName;

    @Schema(description = "서비스 포트", example = "8080")
    private String svcPort;

    @Schema(description = "활성 여부", example = "Y")
    private String actYn;

    @Schema(description = "삭제 여부", example = "N")
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