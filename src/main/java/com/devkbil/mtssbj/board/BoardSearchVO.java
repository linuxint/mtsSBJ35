package com.devkbil.mtssbj.board;

import com.devkbil.mtssbj.common.PagingVO;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

/**
 * BoardSearchVO 클래스
 * - 게시판 검색 요청 데이터를 표현하는 VO(Value Object) 클래스입니다.
 * - PagingVO를 상속받아 페이징 관련 코드와 함께 사용됩니다.
 * - 검색 키워드, 검색 필드 등의 정보를 포함하고 있습니다.
 */
@Schema(description = "게시글 검색 데이터 모델") // Swagger에서 모델 설명 정의
@XmlRootElement(name = "BoardSearchVO") // XML 루트 태그 이름 설정
@XmlType(propOrder = {"bgno", "searchKeyword", "searchType", "searchTypeArr", "searchExt1"})
@Getter
@Setter
public class BoardSearchVO extends PagingVO {

    @Schema(description = "게시판 그룹 코드", example = "BG001")
    private String bgno;

    @Schema(description = "검색 키워드", example = "검색어")
    private String searchKeyword = "";

    @Schema(description = "검색 필드 (제목, 내용)", example = "제목,내용")
    private String searchType = "";

    @Schema(description = "검색 필드를 배열로 변환", example = "[\"제목\", \"내용\"]")
    private String[] searchTypeArr;

    @Schema(description = "검색 확장 필드", example = "EXT001")
    private String searchExt1 = "";

    public String[] getSearchTypeArr() {
        return searchType.split(",");
    }
}
