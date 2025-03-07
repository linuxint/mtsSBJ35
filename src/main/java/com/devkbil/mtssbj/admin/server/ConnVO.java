package com.devkbil.mtssbj.admin.server;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 서버 서비스 접속 정보를 나타내는 데이터 전송 객체.
 * 이 클래스는 {@code SvcVO} 클래스를 상속하여 서비스, 하드웨어, 소프트웨어에 관련된 속성을 상속받음.
 * <p>
 * 접속 ID, 접속 이름, 사용자 인증정보, 상태 및 수정 추적을 위한 메타데이터 등
 * 접속 관련 데이터를 관리하기 위한 추가 속성을 포함함.
 * <p>
 * {@code ConnVO} 클래스는
 * 시스템의 다양한 계층 간에 서비스 접속 정보를 처리하고 교환하는 데 사용됨.
 * <p>
 * 스키마 설명, XML 바인딩 및 속성 정렬을 위해 애노테이션이 적용됨.
 */
@Getter
@Setter
@Schema(description = "서버 서비스 접속 정보 VO")
@XmlRootElement(name = "ServiceConnVO")
@XmlType(propOrder = {"connId", "connName", "svcId", "svcName", "hwId", "hwName", "swId", "swName", "connUserName", "connUserPw", "actYn", "deleteFlag", "regDate", "regUserNo", "chgDate", "chgUserNo"})
public class ConnVO extends SvcVO {

    @Schema(description = "접속 정보 ID", example = "1")
    private String connId;

    @Schema(description = "접속 정보명", example = "접속정보명")
    private String connName;

    //    @Schema(description = "서비스 ID", example = "1001")
    //    private String svcId;

    //    @Schema(description = "서비스명", example = "Apache Service")
    //    private String svcName; // 서비스 이름(조회 조인 시 포함 가능)

    //    @Schema(description = "하드웨어 ID", example = "2001")
    //    private String hwId;

    //    @Schema(description = "하드웨어명", example = "서버1")
    //    private String hwName; // 하드웨어 이름(조회 조인 시 포함 가능)

    //    @Schema(description = "소프트웨어 ID", example = "3001")
    //    private String swId;

    //    @Schema(description = "소프트웨어명", example = "Apache")
    //    private String swName; // 소프트웨어 이름(조회 조인 시 포함 가능)

    @Schema(description = "접속 사용자 이름", example = "admin")
    private String userName;

    @Schema(description = "접속 사용자 비밀번호 (암호화 필요)", example = "password1234")
    private String userPw;

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
