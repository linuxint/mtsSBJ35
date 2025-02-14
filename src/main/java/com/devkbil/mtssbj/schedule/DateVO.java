package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * DateVO
 * - 특정 날짜에 대한 세부 정보를 담는 VO 클래스.
 */
@Schema(description = "날짜 정보 : DateVO")
@XmlRootElement(name = "DateVO")
@XmlType(propOrder = {"year", "month", "day", "date", "week", "istoday", "list"})
@Getter
@Setter
public class DateVO {

    @Schema(description = "연도")
    private int year;

    @Schema(description = "월")
    private int month;

    @Schema(description = "일")
    private int day;

    @Schema(description = "날짜")
    private String date;

    @Schema(description = "주")
    private String week; // 주차 혹은 요일 정보

    @Schema(description = "오늘 여부")
    private boolean istoday = false; // 해당 날짜가 오늘인지 여부

    @Schema(description = "해당 날짜의 일정 목록")
    private List<?> list; // 해당 날짜에 포함된 일정들

}
