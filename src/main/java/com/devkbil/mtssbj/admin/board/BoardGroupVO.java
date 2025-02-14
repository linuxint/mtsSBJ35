package com.devkbil.mtssbj.admin.board;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시판 그룹 관리 VO
 * - 게시판 그룹 정보를 담는 데이터 객체입니다.
 * - Swagger 및 XML 마샬링을 위한 Schema 설정이 포함되어 있습니다.
 */
@Schema(description = "게시판 그룹 관리 객체 : BoardGroupVO")
@XmlRootElement(name = "BoardGroupVO")
@XmlType(propOrder = {"bgno", "bgname", "bglevel", "bgparent", "deleteflag", "bgused", "bgreply", "bgreadonly", "regdate", "chgdate", "bgnotice"})
@Getter
@Setter
public class BoardGroupVO {

    @Schema(description = "그룹 계층 레벨", example = "1")
    private String bglevel;

    @Schema(description = "게시판 그룹 번호", example = "BG001")
    private String bgno;

    @Schema(description = "게시판 그룹명", example = "공지사항")
    private String bgname;

    @Schema(description = "부모 그룹 ID", example = "G01")
    private String bgparent;

    @Schema(description = "삭제 여부 (Y/N)", example = "N")
    private String deleteflag;

    @Schema(description = "댓글 사용 여부 (Y/N)", example = "Y")
    private String bgused;

    @Schema(description = "글쓰기 가능 여부 (Y/N)", example = "Y")
    private String bgreply;

    @Schema(description = "읽기 전용 여부 (Y/N)", example = "N")
    private String bgreadonly;

    @Schema(description = "공지 쓰기 가능 여부 (Y/N)", example = "Y")
    private String bgnotice;

    @Schema(description = "생성일자 (YYYY-MM-DD)", example = "2023-01-01")
    private String regdate;

    @Schema(description = "변경일자 (YYYY-MM-DD)", example = "2023-01-15")
    private String chgdate;

}
