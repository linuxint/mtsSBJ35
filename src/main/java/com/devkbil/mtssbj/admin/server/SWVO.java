package com.devkbil.mtssbj.admin.server;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Server Software Value Object (SWVO).
 * This class extends HWVO and contains properties specific to software information,
 * including software ID, name, type, activation and deletion flags, and audit information.
 *
 * It is used as a data transfer object for managing software-related details,
 * particularly in relation to a server environment.
 *
 * The properties include:
 * - Software details such as ID, name, and type.
 * - Activation and deletion status.
 * - Audit-related information such as registration and modification details.
 *
 * The class is annotated for both JAXB and OpenAPI integration, allowing it to be used
 * for XML serialization and API schema documentation purposes.
 */
@Getter
@Setter
@Schema(description = "서버 소프트웨어 VO")
@XmlRootElement(name = "SWVO")
@XmlType(propOrder = {"swId", "swName", "swTypeCd", "actYn", "deleteFlag", "regDate", "regUserNo", "chgDate", "chgUserNo"})
public class SWVO extends HWVO {

    @Schema(description = "소프트웨어 ID", example = "1")
    private String swId;

    @Schema(description = "소프트웨어명", example = "Apache")
    private String swName;

    @Schema(description = "소프트웨어 타입 코드", example = "SW001")
    private String swTypeCd;

    @Schema(description = "소프트웨어 타입명", example = "WAS")
    private String swTypeNm;

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
