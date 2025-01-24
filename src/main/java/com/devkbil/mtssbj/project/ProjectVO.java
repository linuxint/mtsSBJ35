package com.devkbil.mtssbj.project;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * 프로젝트 정보를 담고 있는 VO 클래스.
 */
@Schema(description = "프로젝트 정보 : ProjectVO")
@XmlRootElement(name = "ProjectVO")
@XmlType(propOrder = {"prno", "prstartdate", "prenddate", "prtitle", "regdate", "userno", "usernm", "prstatus", "deleteflag"})
@Getter
@Setter
public class ProjectVO {

    @Schema(description = "프로젝트 번호", example = "P12345")
    private String prno;        //프로젝트 번호

    @Schema(description = "프로젝트 시작일자", example = "2023-10-01")
    private String prstartdate;    //시작일자

    @Schema(description = "프로젝트 종료일자", example = "2023-10-10")
    private String prenddate;    //종료일자

    @Schema(description = "프로젝트 제목", example = "인터널 프로젝트")
    private String prtitle;        //프로젝트 제목

    @Schema(description = "작성일자", example = "2023-09-30")
    private String regdate;        //작성일자

    @Schema(description = "사용자 번호", example = "1234")
    private String userno;        //사용자번호

    @Schema(description = "사용자 이름", example = "Jane Lee")
    private String usernm;        //사용자명

    @Schema(description = "프로젝트 상태코드", example = "1")
    private String prstatus;    //상태

    @Schema(description = "삭제 여부 (Y: 삭제, N: 정상)", example = "N")
    private String deleteflag;    //삭제

}
