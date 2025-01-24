package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * CalendarVO
 * - 달력(View)에 표시되는 정보를 담는 VO 클래스.
 */
@Schema(description = "달력 : CalendarVO")
@XmlRootElement(name = "CalendarVO")
@XmlType(propOrder = {"cddate", "cddd", "cddayofweek", "shcolor", "list"})
@Getter
@Setter
public class CalendarVO {

    @Schema(description = "날짜")
    private String cddate; // 날짜 (yyyy-MM-dd 형식)

    @Schema(description = "일")
    private Integer cddd; // 해당 날짜의 일(day) 값

    @Schema(description = "주별일자")
    private Integer cddayofweek; // 요일 (1: 일요일, 7: 토요일)

    @Schema(description = "일자컬러")
    private String shcolor; // 해당 날짜의 컬러 코드

    @Schema(description = "일정 목록")
    private List<?> list; // 해당 날짜에 포함된 일정들 (SchDetailVO 리스트)

}
