package com.devkbil.mtssbj.admin.menu;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * MenuVO 클래스
 * - 메뉴 데이터를 표현하는 데이터 모델 클래스입니다.
 * - 메뉴 ID, 이름, 상위 메뉴, 파일 경로 등과 같은 정보를 포함합니다.
 * - Swagger API 문서화 및 XML 변환을 위한 어노테이션이 포함되어 있습니다.
 */
@Schema(description = "메뉴 데이터 모델 : MenuVO") // Swagger에서 모델 설명 정의
@XmlRootElement(name = "MenuVO") // XML 루트 태그 이름 설정
@XmlType(propOrder = {
        "mnuNo", "mnuNm", "mnuParent", "mnuType", "mnuDesc",
        "mnuTarget", "mnuFilenm", "mnuImgpath", "mnuCustom",
        "mnuDesktop", "mnuMobile", "mnuOrder", "mnuCertType",
        "mnuExtnConnYn", "mnuStartHour", "mnuEndHour",
        "regdate", "reguserno", "chgdate", "chguserno", "deleteflag"
}) // XML 및 Swagger에서 필드 출력 순서 정의
@Getter
@Setter
public class MenuVO {

    @Schema(description = "메뉴ID", example = "MENU001")
    private String mnuNo;

    @Schema(description = "상위메뉴ID", example = "PARENT001")
    private String mnuParent;

    @Schema(description = "메뉴업무구분코드", example = "TYPE001")
    private String mnuType;

    @Schema(description = "메뉴명", example = "대시보드")
    private String mnuNm;

    @Schema(description = "설명", example = "메뉴에 대한 설명")
    private String mnuDesc;

    @Schema(description = "메뉴링크", example = "/dashboard")
    private String mnuTarget;

    @Schema(description = "파일명", example = "dashboard.html")
    private String mnuFilenm;

    @Schema(description = "이미지경로", example = "/images/menu.png")
    private String mnuImgpath;

    @Schema(description = "커스텀태그", example = "<custom>tag</custom>")
    private String mnuCustom;

    @Schema(description = "데스크탑버전 사용여부(0: 아니오, 1: 예)", example = "1")
    private String mnuDesktop;

    @Schema(description = "모바일버전 사용여부(0: 아니오, 1: 예)", example = "1")
    private String mnuMobile;

    @Schema(description = "정렬순서", example = "1")
    private String mnuOrder;

    @Schema(description = "인증구분코드", example = "CERT001")
    private String mnuCertType;

    @Schema(description = "외부연결여부 (0: 아니오, 1: 예)", example = "1")
    private String mnuExtnConnYn;

    @Schema(description = "사용 시작 시간", example = "09:00")
    private String mnuStartHour;

    @Schema(description = "사용 종료 시간", example = "18:00")
    private String mnuEndHour;

    @Schema(description = "등록일자", example = "2023-10-01")
    private String regdate;

    @Schema(description = "등록자", example = "admin001")
    private String reguserno;

    @Schema(description = "수정일자", example = "2023-10-15")
    private String chgdate;

    @Schema(description = "수정자", example = "admin002")
    private String chguserno;

    @Schema(description = "삭제여부 (0: 사용, 1: 삭제)", example = "0")
    private String deleteflag;

}
