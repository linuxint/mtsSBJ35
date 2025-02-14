package com.devkbil.mtssbj.search;

import com.devkbil.mtssbj.common.PagingVO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 검색 관련 데이터를 캡슐화하는 VO (Value Object) 클래스입니다.
 * <p>
 * 이 클래스는 페이징 기능을 포함한 검색 조건 필드와 그에 대한 추가 정보를 관리합니다.
 */
@Schema(description = "검색 정보 모델 : SearchVO") // OpenAPI 문서 설명 추가
@XmlRootElement(name = "SearchVO") // XML로 데이터 변환 시 활용
@XmlType(propOrder = {"searchKeyword", "searchType", "searchTypeArr", "searchExt1", "userno"}) // 속성 순서 정의
@Getter
@Setter
public class SearchVO extends PagingVO {

    @Schema(description = "검색 키워드")
    private String searchKeyword = ""; // 검색 키워드 (기본값 빈 문자열)

    @Schema(description = "검색 필드: 가능한 값은 제목(title), 내용(content) 등")
    private String searchType = ""; // 검색 필드 값 (ex: 제목, 내용)

    @Schema(description = "검색 필드를 배열 형태로 반환")
    private String[] searchTypeArr; // 검색 필드를 배열로 변환하여 저장

    @Schema(description = "확장 검색 필드")
    private String searchExt1 = ""; // 검색 확장을 위한 추가 필드

    @Schema(description = "사용자 번호")
    private String userno; // 사용자 번호

    /**
     * `searchType` 필드의 값을 ',' 기준으로 분리하여 배열로 반환합니다.
     *
     * @return 검색 필드 값을 배열로 변환한 결과
     */
    public String[] getSearchTypeArr() {
        return searchType.split(","); // "," 구분자로 문자열을 배열로 변환
    }
}
