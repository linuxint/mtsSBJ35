package com.devkbil.mtssbj.board;

import com.devkbil.mtssbj.common.util.UtilEtc;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * BoardReplyVO 클래스
 * - 게시글의 댓글 데이터를 표현하는 VO(Value Object) 클래스입니다.
 * - 댓글 정보에는 게시물 번호, 댓글 번호, 작성자, 댓글 내용, 작성일자 등이 포함됩니다.
 * - Swagger API 문서 및 XML 변환을 위한 어노테이션이 포함되어 있습니다.
 */
@Schema(description = "게시글 댓글 : Board Reply") // Swagger API의 데이터 모델 설명
@XmlRootElement(name = "boardreplyvo")
@XmlType(propOrder = {"brdno", "reno", "rewriter", "rememo", "regdate", "reparent", "redepth", "reorder", "userno",
    "usernm", "photo"}) // XML 정렬 순서
@Getter
@Setter
public class BoardReplyVO {

    @Schema(description = "게시물 번호") // Swagger API 필드 설명
    private String brdno;

    @Schema(description = "댓글 번호")
    private String reno;

    @Schema(description = "댓글 작성자")
    private String rewriter;

    @Schema(description = "댓글 내용")
    private String rememo;

    @Schema(description = "작성일자")
    private String regdate;

    @Schema(description = "부모댓글")
    private String reparent;

    @Schema(description = "깊이")
    private String redepth;

    @Schema(description = "순서")
    private Integer reorder;

    @Schema(description = "작성자")
    private String userno;

    @Schema(description = "작성자명")
    private String usernm;

    @Schema(description = "사용자사진")
    private String photo;

    @Schema(description = "댓글내용 HTML")
    public String getRememoByHTML() {
        return UtilEtc.text2Html(rememo);
    }

}
