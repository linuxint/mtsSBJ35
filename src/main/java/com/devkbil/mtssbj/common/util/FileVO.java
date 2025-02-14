package com.devkbil.mtssbj.common.util;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "첨부파일 : FileVO")
@XmlRootElement(name = "첨부파일")
@XmlType(propOrder = {"fileno", "parentPK", "filename", "realname", "filesize"})
@Getter
@Setter
public class FileVO {
    @Schema(description = "파일번호")
    private Integer fileno;
    @Schema(description = "파일 상위키 (게시판,프로젝트)")
    private String parentPK;
    @Schema(description = "서버 파일명")
    private String filename;
    @Schema(description = "업로드 파일명")
    private String realname;
    @Schema(description = "파일사이즈")
    private long filesize;
    @Schema(description = "삭제여부")
    private String deleteflag;

    @Schema(description = "파일fullpath")
    private String uri;
    @Schema(description = "파일경로")
    private String filepath;
    @Schema(description = "파일드라이브")
    private String fileroot;

    /**
     * 파일 크기를 정형화하기.
     */
    public String size2String() {
        Integer unit = 1024;
        if (filesize < unit) {
            return String.format("(%d B)", filesize);
        }
        int exp = (int) (Math.log(filesize) / Math.log(unit));

        return String.format("(%.0f %s)", filesize / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }

}
