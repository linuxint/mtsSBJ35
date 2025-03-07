package com.devkbil.mtssbj.crud;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * CRUD 데이터 모델(CrudVO)을 나타냅니다.
 * 이 클래스는 CRUD 관련 데이터를 처리하기 위해 사용되며, 식별자, 제목,
 * 작성자, 내용, 생성일자, 작성자 이름을 포함합니다.
 * <p>
 * 이 클래스는 JSON 직렬화, XML 표현, OpenAPI 스키마 정의를 위한 애노테이션을 포함합니다.
 * 관련 CRUD 작업 내에서 데이터 전송 객체로 사용됩니다.
 */
@Schema(description = "CRUD 데이터 모델: CrudVO")
@XmlRootElement(name = "CrudVO")
@XmlType(propOrder = {"crno", "crtitle", "userno", "crmemo", "regdate", "usernm"})
@Getter
@Setter
public class CrudVO {

    @Schema(description = "CRUD 번호", example = "1001")
    private String crno; // 번호

    @Schema(description = "CRUD 제목", example = "샘플 제목")
    private String crtitle; // 제목

    @Schema(description = "작성자 번호", example = "USER001")
    private String userno; // 작성자

    @Schema(description = "CRUD 내용", example = "CRUD의 내용이 들어갑니다.")
    private String crmemo; // 내용

    @Schema(description = "작성일자", example = "2023-11-01")
    private String regdate; // 작성일자

    @Schema(description = "작성자 이름", example = "홍길동")
    private String usernm; // 작성자 이름
}
