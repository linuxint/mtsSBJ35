package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Getter;
import lombok.Setter;

/**
 * DBToolVO
 * - 테이블 및 컬럼 정보를 담는 데이터 객체
 * - 주요 변수: 테이블 이름, 컬럼 정보, PK, FK, 데이터 타입 등
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
