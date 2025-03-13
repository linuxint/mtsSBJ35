package com.devkbil.mtssbj.sign;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 결재문서를 나타내는 Value Object 클래스입니다.
 * <p>
 * 이 클래스는 결재 문서의 식별자, 제목, 문서 내용, 결재 상태 등
 * 결재 프로세스에 필요한 주요 정보를 담고 있습니다.
 * Swagger 및 XML 직렬화를 지원하기 위해 필요한 어노테이션이 포함되어 있습니다.
 */
@Schema(description = "결재문서를 나타내는 VO 객체 : SignDocVO")
@XmlRootElement(name = "SignDocVO") // XML의 루트 엘리먼트 정의
@XmlType(propOrder = {"docno", "doctitle", "doccontents", "docstatus", "docstep", "dtno", "dttitle", "userno", "usernm", "deptnm", "chgdate", "docsignpath"}) // XML 직렬화 순서 지정
@Getter
@Setter
public class SignDocVO {

    @Schema(description = "문서번호", example = "DOC2023001")
    private String docno;        // 문서번호

    @Schema(description = "제목", example = "결재 요청: 신규 프로젝트 승인")
    private String doctitle;        // 제목

    @Schema(description = "문서내용", example = "프로젝트 내용 및 세부 정보 설명")
    private String doccontents;        // 문서내용

    @Schema(description = "문서상태", example = "진행중", allowableValues = {"대기", "진행중", "완료", "반려"})
    private String docstatus;        // 문서상태

    @Schema(description = "결재단계", example = "2")
    private String docstep;        // 결재단계

    @Schema(description = "양식번호", example = "FORM2023001")
    private String dtno;            // 양식번호

    @Schema(description = "양식명", example = "프로젝트 결재 양식")
    private String dttitle;        // 양식명

    @Schema(description = "사용자번호", example = "USER001")
    private String userno;        // 사용자번호

    @Schema(description = "사용자명", example = "홍길동")
    private String usernm;        // 사용자명

    @Schema(description = "부서명", example = "기획팀")
    private String deptnm;        // 부서명

    @Schema(description = "수정일자", example = "2023-10-01")
    private String chgdate;    // 수정일자

    @Schema(description = "결재경로문자열", example = "USER001,홍길동,기안,과장||USER002,김철수,심사,부장")
    private String docsignpath;        // 결재경로문자열

}