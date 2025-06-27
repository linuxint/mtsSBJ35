package com.devkbil.mtssbj.common;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a File Value Object (VO) that encapsulates the information
 * about an attached file, including metadata such as file name, size, and path.
 * It is used to store and manage file-related information within the application.
 * <p>
 * Fields:
 * - fileno: The unique identifier for the file.
 * - parentPK: The primary key of the associated parent entity,
 * such as a board or project.
 * - filename: The name of the file stored on the server.
 * - realname: The original name of the file uploaded by the user.
 * - filesize: The size of the file in bytes.
 * - deleteflag: A flag indicating whether the file has been marked for deletion.
 * - uri: The full URI of the file.
 * - filepath: The physical path of the file in the server.
 * - fileroot: The drive or root location where the file is stored.
 * <p>
 * Methods:
 * - size2String(): Converts the file size into a human-readable string format
 * with appropriate units (e.g., bytes, kilobytes, megabytes).
 */
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
     * Converts the file size into a human-readable string format with appropriate units
     * (e.g., bytes, kilobytes, megabytes, etc.).
     *
     * @return a formatted string representing the file size with the appropriate unit
     */
    public String size2String() {
        Integer unit = 1024;
        if (filesize < unit) {
            return String.format("(%d B)", filesize);
        }
        int exp = (int) (Math.log((double) filesize) / Math.log(unit));

        return String.format("(%.0f %s)", filesize / Math.pow(unit, exp), "KMGTPE".charAt(exp - 1));
    }

}