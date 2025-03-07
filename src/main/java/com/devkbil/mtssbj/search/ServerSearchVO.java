package com.devkbil.mtssbj.search;

import com.devkbil.mtssbj.common.PagingVO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import org.springframework.ui.ModelMap;

import lombok.Getter;
import lombok.Setter;

/**
 * CommonSearchVO 클래스
 * - 전 테이블 공용 검색 요청 데이터를 표현하는 VO(Value Object) 클래스입니다.
 * - 페이징, 검색 조건, 기간 검색 등의 공통 요소를 포함합니다.
 */
@Schema(description = "공용 검색 데이터 모델")
@XmlRootElement(name = "CommonSearchVO")
@XmlType(propOrder = {"searchKeyword", "searchType", "searchTypeArr", "searchExt1", "startDate", "endDate", "rowStart", "displayRowCount", "userno"})
@Getter
@Setter
public class ServerSearchVO extends PagingVO {

    @Schema(description = "하드웨어 ID", example = "1")
    private String hwId;

    @Schema(description = "소프트웨어 ID", example = "1")
    private String swId;

    @Schema(description = "서비스 ID", example = "1")
    private String svcId;

    @Schema(description = "접속 정보 ID", example = "1")
    private String connId;

    @Schema(description = "기타 정보 ID", example = "1")
    private String etcId;

    @Schema(description = "검색 키워드", example = "검색어")
    private String searchKeyword = ""; // 검색 키워드

    @Schema(description = "검색 필드 (제목, 내용)", example = "제목,내용")
    private String searchType = ""; // 검색 필드(검색 항목 지정)

    @Schema(description = "검색 필드를 배열로 변환", example = "[\"제목\", \"내용\"]")
    private String[] searchTypeArr; // 검색 필드를 배열 형태로 변환

    @Schema(description = "확장 검색 필드 1", example = "EXT001")
    private String searchExt1 = ""; // 추가적인 검색 확장 필드

    @Schema(description = "검색 시작일 (YYYY-MM-DD)", example = "2023-01-01")
    private String startDate = ""; // 검색 기간 시작일

    @Schema(description = "검색 종료일 (YYYY-MM-DD)", example = "2023-12-31")
    private String endDate = ""; // 검색 기간 종료일

    @Schema(description = "페이징 처리 시 시작 행 번호", example = "1")
    private Integer rowStart = 0; // 페이징 시작 번호

    @Schema(description = "한 페이지당 표시할 데이터 수", example = "10")
    private Integer displayRowCount = 10; // 한 페이지에 보여줄 데이터 수

    @Schema(description = "사용자 번호", example = "12345")
    private String userno; // 사용자 번호

    /**
     * 검색 타입 문자열(`searchType`)을 ',' 구분자로 배열 형태로 변환하여 반환합니다.
     *
     * @return 검색 타입 배열
     */
    public String[] getSearchTypeArr() {
        return searchType != null ? searchType.split(",") : new String[]{}; // Null 안전 처리
    }

    public void setSearchParamsFromModelMap(ModelMap modelMap) {
        // ModelMap 에서 값을 ServerSearchVO 필드로 매핑
        this.searchKeyword = (String) modelMap.getOrDefault("searchKeyword", "");
        this.searchType = (String) modelMap.getOrDefault("searchType", "");
        this.searchExt1 = (String) modelMap.getOrDefault("searchExt1", "");
        this.startDate = (String) modelMap.getOrDefault("startDate", "");
        this.endDate = (String) modelMap.getOrDefault("endDate", "");
        this.rowStart = (Integer) modelMap.getOrDefault("rowStart", 0);
        this.displayRowCount = (Integer) modelMap.getOrDefault("displayRowCount", 10);
        this.userno = (String) modelMap.getOrDefault("userno", null);

        // 검색 타입 배열 변환
        this.searchTypeArr = getSearchTypeArr();
    }
}
