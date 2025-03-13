package com.devkbil.mtssbj.search;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * 통합 검색을 위한 데이터 VO (Value Object) 클래스입니다.
 * <p>
 * 이 클래스는 특정 검색 조건(작성자, 제목, 내용, 검색 기간 등)을 설정하고,
 * 기본 검색 정보는 `SearchVO`를 상속받아 활용합니다.
 */
@Schema(description = "통합 검색 모델 : FullTextSearchVO") // OpenAPI 설명 추가
@XmlRootElement(name = "FullTextSearchVO") // XML 루트 엘리먼트 정의
@XmlType(propOrder = {"searchRange", "searchTerm", "searchTerm1", "searchTerm2", "userno"}) // 속성 순서 정의
@Getter
@Setter
public class FullTextSearchVO extends SearchVO {

    @Schema(description = "검색 대상 필드 (예: 작성자, 제목, 내용)") // Swagger 설명
    private String searchRange = ""; // 검색 필드 지정 (ex: 작성자, 제목, 내용)

    @Schema(description = "기간 조회 사용 여부 (Y/N)")
    private String searchTerm = ""; // 기간 조회 사용 여부 플래그 (Y/N)

    @Schema(description = "검색 시작일 (포맷: yyyy-MM-dd)", example = "2023-01-01")
    private String searchTerm1 = ""; // 검색 시작일 (예: 2023-01-01)

    @Schema(description = "검색 종료일 (포맷: yyyy-MM-dd)", example = "2023-12-31")
    private String searchTerm2 = ""; // 검색 종료일 (예: 2023-12-31)

    @Schema(description = "사용자 번호 (권한 테스트용)")
    private String userno = ""; // 사용자 번호 (권한 설정 확인을 위해 사용)
}