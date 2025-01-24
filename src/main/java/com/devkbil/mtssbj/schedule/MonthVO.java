package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * MonthVO
 * - 연도 및 월 정보를 담는 VO 클래스.
 */
@Schema(description = "년/월 정보 : MonthVO")
@XmlRootElement(name = "MonthVO")
@XmlType(propOrder = {"year", "month"})
@Getter
@Setter
public class MonthVO {

    @Schema(description = "연도")
    private String year; // 연도 (yyyy 형식)

    @Schema(description = "월")
    private String month; // 월 (MM 형식, 1 ~ 12)
}
