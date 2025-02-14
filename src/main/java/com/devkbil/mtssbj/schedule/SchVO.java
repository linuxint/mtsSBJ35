package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * SchVO
 * - 특정 일정의 정보를 담는 VO 클래스.
 */
@Schema(description = "일정 : SchVO")
@XmlRootElement(name = "SchVO")
@XmlType(propOrder = {"ssno", "sstitle", "sstype", "ssstartdate", "ssstarthour", "ssstartminute", "ssenddate", "ssendhour", "ssendminute", "ssrepeattype", "ssrepeattypenm", "ssrepeatoption", "ssrepeatend", "sscontents", "ssisopen", "userno", "usernm"})
@Getter
@Setter
public class SchVO {

    @Schema(description = "일정 번호")
    private String ssno; // 일정 식별 번호

    @Schema(description = "일정 제목")
    private String sstitle; // 일정 제목

    @Schema(description = "일정 구분")
    private String sstype; // 일정 구분(ex: 개인, 공용)

    @Schema(description = "시작일")
    private String ssstartdate; // 일정 시작 날짜 (yyyy-MM-dd)

    @Schema(description = "시작 시간")
    private String ssstarthour; // 일정 시작 시각 (HH)

    @Schema(description = "시작 분")
    private String ssstartminute; // 일정 시작 분 (mm)

    @Schema(description = "종료일")
    private String ssenddate; // 일정 종료 날짜 (yyyy-MM-dd)

    @Schema(description = "종료 시간")
    private String ssendhour; // 일정 종료 시각 (HH)

    @Schema(description = "종료 분")
    private String ssendminute; // 일정 종료 분 (mm)

    @Schema(description = "반복 유형")
    private String ssrepeattype; // 반복 유형 (1: 없음, 2: 주간, 3: 월간)

    @Schema(description = "반복 유형 이름")
    private String ssrepeattypenm; // 반복 유형 명칭

    @Schema(description = "반복 옵션")
    private String ssrepeatoption; // 반복 설정값 (주: 요일, 월: 날짜)

    @Schema(description = "반복 종료일")
    private String ssrepeatend; // 반복 일정 종료 날짜

    @Schema(description = "일정 상세 내용")
    private String sscontents; // 일정 세부 내용

    @Schema(description = "공개 여부")
    private String ssisopen; // 공개 여부 ("Y" / "N")

    @Schema(description = "사용자 번호")
    private String userno; // 사용자 ID

    @Schema(description = "사용자 이름")
    private String usernm; // 사용자 이름
}
