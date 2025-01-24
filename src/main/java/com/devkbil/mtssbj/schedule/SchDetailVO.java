package com.devkbil.mtssbj.schedule;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * SchDetailVO
 * - 일정 상세 정보를 담는 VO 클래스.
 */
@Schema(description = "일정 상세 정보 : SchDetailVO")
@XmlRootElement(name = "SchDetailVO")
@XmlType(propOrder = {"ssno", "sddate", "sdhour", "sdminute", "userno", "sstitle", "fontcolor", "sdseq"})
@Getter
@Setter
public class SchDetailVO {

    @Schema(description = "일정 번호")
    private String ssno; // 일정 번호

    @Schema(description = "날짜")
    private String sddate; // 일정이 속한 날짜

    @Schema(description = "시간")
    private String sdhour; // 일정의 시간 (HH 형식)

    @Schema(description = "분")
    private String sdminute; // 일정의 분 (mm 형식)

    @Schema(description = "사용자 번호")
    private String userno; // 사용자 번호

    @Schema(description = "일정 제목")
    private String sstitle; // 일정 제목

    @Schema(description = "폰트 색상")
    private String fontcolor; // 글자 표시 색상

    @Schema(description = "일정 순번")
    private Integer sdseq; // 상세 일정에서의 순번
}
