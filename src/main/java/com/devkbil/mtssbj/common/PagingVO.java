package com.devkbil.mtssbj.common;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * PagingVO는 데이터 응답의 페이징 정보를 처리하는 VO (Value Object) 클래스입니다.
 * 총 행 수, 페이지 수, 행 범위와 같은 특성을 캡슐화하여 효율적인 데이터를 계산합니다.
 * 이 클래스는 총 페이지 수와 행 인덱스를 계산하는 메서드를 제공합니다.
 */
@Schema(description = "페이지수 : PageVO")
@XmlRootElement(name = "PageVO")
@XmlType(propOrder = {"displayRowCount", "rowStart", "rowEnd", "totPage", "totRow", "page", "pageStart", "pageEnd"})
@Getter
@Setter
public class PagingVO {

    @Schema(description = "출력할 데이터 개수")
    private Integer displayRowCount = 10;           // 출력할 데이터 개수
    @Schema(description = "시작행번호")
    private Integer rowStart;                       // 시작행번호
    @Schema(description = "종료행 번호")
    private Integer rowEnd;                         // 종료행 번호
    @Schema(description = "전체 페이수")
    private Integer totPage;                        // 전체 페이수
    @Schema(description = "전체 데이터 수")
    private Integer totRow = 0;                     // 전체 데이터 수
    @Schema(description = "현재 페이지")
    private Integer page;                           // 현재 페이지
    @Schema(description = "시작페이지")
    private Integer pageStart;                      // 시작페이지
    @Schema(description = "종료페이지")
    private Integer pageEnd;                        // 종료페이지

    /**
     * 현재 페이지 번호를 가져옵니다. 페이지 번호가 설정되지 않았거나 0으로 설정된 경우,
     * 유효한 페이지네이션을 보장하기 위해 기본값인 1로 설정됩니다.
     *
     * @return 설정되지 않았거나 0으로 설정된 경우 기본값 1로 대체된 현재 페이지 번호
     */
    public Integer getPage() {
        if (page == null || page == 0) {
            page = 1;
        }

        return page;
    }

    /**
     * 제공된 총 행 수를 기반으로 현재 페이지에 대한 페이징 세부정보를 계산합니다.
     * 이 메서드는 총 행 수, 총 페이지, 시작 페이지, 종료 페이지,
     * 시작 행 인덱스 및 종료 행 인덱스와 같은 속성을 업데이트합니다.
     *
     * @param total 페이징해야 하는 총 행 수
     */
    public void pageCalculate(Integer total) {
        getPage();
        totRow = total;
        totPage = total / displayRowCount;

        if (total % displayRowCount > 0) {
            totPage++;
        }

        pageStart = (page - (page - 1) % 10);
        pageEnd = pageStart + 9;
        if (pageEnd > totPage) {
            pageEnd = totPage;
        }

        rowStart = ((page - 1) * displayRowCount) + 1;
        rowEnd = rowStart + displayRowCount - 1;
    }

}


