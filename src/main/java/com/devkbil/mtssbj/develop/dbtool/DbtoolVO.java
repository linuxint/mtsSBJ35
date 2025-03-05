package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * 데이터베이스 테이블과 해당 컬럼에 관련된 메타데이터 정보를 나타냅니다.
 * 이 클래스는 테이블 이름, 컬럼 정보, 기본 키, 외래 키, 그리고 기타 컬럼별 속성과 같은
 * 세부사항을 캡슐화합니다. 주로 스키마 설명이나 테이블 설계 정보가 필요한
 * 컨텍스트에서 사용됩니다.
 */
@Getter
@Setter
@Schema(description = "테이블 및 컬럼 정보 모델")
public class DbtoolVO {

    @Schema(description = "테이블 이름", example = "USER_TABLE")
    private String tableName;             // 테이블 이름

    @Schema(description = "테이블에 대한 설명/주석", example = "사용자 정보 테이블")
    private String tableComments;         // 테이블 주석

    @Schema(description = "컬럼에 대한 설명/주석", example = "사용자 식별자")
    private String columnComments;        // 컬럼 주석

    @Schema(description = "컬럼 이름", example = "USER_ID")
    private String columnName;            // 컬럼 이름

    @Schema(description = "PK 여부", example = "Y")
    private String pkFlag;                // 기본 키 여부

    @Schema(description = "FK 여부", example = "N")
    private String fkFlag;                // 외래 키 여부

    @Schema(description = "NULL 허용 여부", example = "N")
    private String nullFlag;              // NULL 허용 여부

    @Schema(description = "데이터 타입", example = "VARCHAR2")
    private String dataType;              // 데이터 타입

    @Schema(description = "데이터 길이", example = "20")
    private String dataLength;            // 데이터 길이
}
