package com.devkbil.mtssbj.crud;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "CRUD 데이터 모델: CrudVO")
@XmlRootElement(name = "CrudVO")
@XmlType(propOrder = {"crno", "crtitle", "userno", "crmemo", "regdate", "usernm"})
@Getter
@Setter
public class CrudVO {

    @Schema(description = "CRUD 번호", example = "1001")
    private String crno;        // 번호

    @Schema(description = "CRUD 제목", example = "샘플 제목")
    private String crtitle;    // 제목

    @Schema(description = "작성자 번호", example = "USER001")
    private String userno;        // 작성자

    @Schema(description = "CRUD 내용", example = "CRUD의 내용이 들어갑니다.")
    private String crmemo;        // 내용

    @Schema(description = "작성일자", example = "2023-11-01")
    private String regdate;        // 작성일자

    @Schema(description = "작성자 이름", example = "홍길동")
    private String usernm;        // 작성자 이름

}
