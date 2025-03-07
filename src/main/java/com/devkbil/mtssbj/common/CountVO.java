package com.devkbil.mtssbj.common;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * CountVO는 특정 항목에 대해 조회된 데이터의 상세 정보를 나타내는 클래스입니다.
 * 조회 대상 필드 명과 해당 조회 건수를 표현하며, 조회 통계와 관련된 데이터 전송을 수행합니다.
 * 이 클래스는 VO (Value Object)로 사용됩니다.
 */
@Schema(description = "조회수 : CountVO")
@XmlRootElement(name = "CountVO")
@XmlType(propOrder = {"field1", "cnt1"})
@Getter
@Setter
public class CountVO {
    @Schema(description = "조회필드")
    private String field1;
    @Schema(description = "조회수")
    private Integer cnt1;

}
