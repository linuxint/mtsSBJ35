package com.devkbil.mtssbj.admin.server;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "서버 하드웨어 VO")
@XmlRootElement(name = "HWVO")
@XmlType(propOrder = {"hwId", "hwName", "hwIp", "adminUserNo", "adminEmail", "osInfo", "cpuInfo", "memoryInfo", "diskInfo", "etcInfo", "actYn", "deleteFlag", "regDate", "chgDate"})
public class HWVO {
    @Schema(description = "하드웨어 ID", example = "1")
    private String hwId;

    @Schema(description = "하드웨어명", example = "서버1")
    private String hwName;

    @Schema(description = "IP 주소", example = "192.168.0.1")
    private String hwIp;

    @Schema(description = "관리자 번호", example = "123")
    private String adminUserNo;

    @Schema(description = "관리자 이메일", example = "admin@example.com")
    private String adminEmail;

    @Schema(description = "OS 정보", example = "Ubuntu 20.04")
    private String osInfo;

    @Schema(description = "CPU 정보", example = "Intel i7")
    private String cpuInfo;

    @Schema(description = "메모리 정보", example = "16GB")
    private String memoryInfo;

    @Schema(description = "디스크 정보", example = "1TB SSD")
    private String diskInfo;

    @Schema(description = "기타 정보", example = "추가 세부 사항")
    private String etcInfo;

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