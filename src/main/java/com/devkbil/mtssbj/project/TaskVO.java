package com.devkbil.mtssbj.project;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 작업(Task) 정보를 담고 있는 VO 클래스.
 */
@Schema(description = "프로젝트 작업 정보 : TaskVO")
@XmlRootElement(name = "TaskVO")
@XmlType(propOrder = {"prno", "tsno", "tsparent", "tssort", "tstitle", "tsstartdate", "tsenddate", "tsendreal", "tsrate", "userno", "usernm", "statuscolor", "uploadfile"})
@Getter
@Setter
public class TaskVO {

    @Schema(description = "프로젝트 번호", example = "P12345")
    private String prno;        //프로젝트 번호

    @Schema(description = "업무번호")
    private String tsno;        //업무번호

    @Schema(description = "부모업무번호")
    private String tsparent;    //부모업무번호

    @Schema(description = "작업 정렬", example = "1")
    private String tssort;    //정렬

    @Schema(description = "업무 제목", example = "설계 작성")
    private String tstitle;    //업무 제목

    @Schema(description = "시작일자", example = "2023-10-01")
    private String tsstartdate; //시작일자

    @Schema(description = "종료일자", example = "2023-10-05")
    private String tsenddate;    //종료일자

    @Schema(description = "종료일자(실제)", example = "2023-10-04")
    private String tsendreal;    //종료일자(실제)

    @Schema(description = "진행율", example = "75")
    private String tsrate;    //진행율

    @Schema(description = "작업자번호", example = "1234")
    private String userno; // 작업자번호

    @Schema(description = "작업자명", example = "Max Kim")
    private String usernm; // 작업자명

    @Schema(description = "업무 진행 상태용 색", example = "#FF5733")
    private String statuscolor;    // 업무 진행 상태용 색

    @Schema(description = "첨부파일")
    private List<MultipartFile> uploadfile; // 첨부파일

}
