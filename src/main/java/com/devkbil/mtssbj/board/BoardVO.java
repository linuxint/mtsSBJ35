package com.devkbil.mtssbj.board;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * 게시글 정보 VO (Value Object)
 * - 게시글의 기본 정보를 담고 있는 데이터 객체입니다.
 * - 게시판 기본 정보, 작성자 정보, 첨부파일 정보, 추가 필드 등을 포함합니다.
 *
 * Swagger와 XML 마샬링을 통해 API 문서 생성 및 데이터 처리가 가능하도록 설계되었습니다.
 */
@Schema(description = "게시글 정보 : Board")
@XmlRootElement(name = "BoardVO")
@XmlType(propOrder = {"bgno", "bgname", "brdno", "brdtitle", "brdwriter", "brdmemo", "regdate", "regtime", "brdhit", "deleteflag", "filecnt", "replycnt", "userno", "usernm", "brdnotice", "brdlike", "brdlikechk", "extfield1", "etc1", "etc2", "etc3", "etc4", "etc5", "uploadfile"})
@Getter
@Setter
public class BoardVO {

    @Schema(description = "게시글 그룹번호", example = "BG001")
    private String bgno;

    @Schema(description = "게시글 그룹명", example = "공지사항")
    private String bgname;

    @Schema(description = "게시글 번호", example = "BRD001")
    private String brdno;

    @Schema(description = "게시글 제목", example = "게시글 제목입니다.")
    private String brdtitle;

    @Schema(description = "게시글 작성자 ID", example = "USR001")
    private String brdwriter;

    @Schema(description = "게시글 내용", example = "게시글 내용입니다.")
    private String brdmemo;

    @Schema(description = "게시글 작성일", example = "2023-11-01")
    private String regdate;

    @Schema(description = "게시글 작성시간", example = "14:25:00")
    private String regtime;

    @Schema(description = "게시글 조회수", example = "123")
    private String brdhit;

    @Schema(description = "삭제 여부 (0: 사용, 1: 삭제)", example = "0")
    private String deleteflag;

    @Schema(description = "첨부파일 개수", example = "3")
    private String filecnt;

    @Schema(description = "댓글 개수", example = "5")
    private String replycnt;

    @Schema(description = "게시글 작성자 번호")
    private String userno;

    @Schema(description = "게시글 작성자명")
    private String usernm;

    @Schema(description = "게시글 공지사항 여부 (0: 일반, 1: 공지사항)", example = "1")
    private String brdnotice;

    @Schema(description = "좋아요 수", example = "25")
    private String brdlike;

    @Schema(description = "게시글 좋아요 여부 (0: 아님, 1: 좋아요)", example = "1")
    private String brdlikechk;

    @Schema(description = "게시글 확장 필드1", example = "추가 데이터1")
    private String extfield1;

    @Schema(description = "게시글 임시 필드1", example = "기타 데이터1")
    private String etc1;

    @Schema(description = "게시글 임시 필드2", example = "기타 데이터2")
    private String etc2;

    @Schema(description = "게시글 임시 필드3", example = "기타 데이터3")
    private String etc3;

    @Schema(description = "게시글 임시 필드4", example = "기타 데이터4")
    private String etc4;

    @Schema(description = "게시글 임시 필드5", example = "기타 데이터5")
    private String etc5;

    /* 첨부파일 */
    @Schema(description = "게시글 업로드 파일 목록")
    private List<MultipartFile> uploadfile;

    /**
     * `brdmemo` 필드의 값을 가져온 뒤 XSS 취약점을 방지하기 위해 필요한 세척을 수행합니다.
     * 필드 값이 비어있거나 null인 경우 빈 문자열을 반환합니다.
     *
     * @return 세척된 `brdmemo` 값 또는 값이 null이거나 비어있을 경우 빈 문자열
     */
    public String getBrdmemo() {
        if (!StringUtils.hasText(brdmemo)) {
            return "";
        }
        return brdmemo.replaceAll("(?i)<script", "&lt;script"); // XSS 태그 치환
    }
}
