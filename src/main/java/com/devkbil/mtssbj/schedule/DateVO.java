package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * DateVO
 * - 특정 날짜에 대한 세부 정보를 담는 VO 클래스.
 * - ComDateVO를 대체하기 위해 확장됨.
 */
@Schema(description = "날짜 정보 : DateVO")
@XmlRootElement(name = "DateVO")
@XmlType(propOrder = {"cdno", "year", "month", "day", "date", "week", "weekOfYear", "weekOfMonth", "dayOfWeek", "lunarYear", "lunarMonth", "lunarDay", "lunarLeap", "istoday", "list"})
@Getter
@Setter
public class DateVO {

    @Schema(description = "번호")
    private Long cdno; // 날짜 레코드의 고유 식별자

    @Schema(description = "연도")
    private int year;

    @Schema(description = "월")
    private int month;

    @Schema(description = "일")
    private int day;

    @Schema(description = "날짜")
    private String date;

    @Schema(description = "주")
    private Integer week; // 주차 혹은 요일 정보

    @Schema(description = "연별주차")
    private Integer weekOfYear; // 연도 내 주차

    @Schema(description = "월별주차")
    private Integer weekOfMonth; // 월 내 주차

    @Schema(description = "주별일자")
    private Integer dayOfWeek; // 요일 (1: 일요일, 7: 토요일)

    @Schema(description = "음력 연도")
    private int lunarYear;

    @Schema(description = "음력 월")
    private int lunarMonth;

    @Schema(description = "음력 일")
    private int lunarDay;

    @Schema(description = "음력 윤달 여부")
    private String lunarLeap = "N";

    @Schema(description = "오늘 여부")
    private boolean istoday = false; // 해당 날짜가 오늘인지 여부

    @Schema(description = "해당 날짜의 일정 목록")
    private List<?> list; // 해당 날짜에 포함된 일정들

}
