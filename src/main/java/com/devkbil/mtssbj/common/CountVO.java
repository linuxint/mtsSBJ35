package com.devkbil.mtssbj.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * CountVO는 조회 수를 나타내는 데 사용되는 Value Object 클래스입니다.
 * 조회되는 필드와 관련된 조회 수를 캡슐화하여
 * 데이터를 간결하고 구조화된 형태로 제공합니다.
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
