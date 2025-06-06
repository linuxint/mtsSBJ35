package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 데이터베이스 도구 컨트롤러
 * - 테이블 레이아웃 정보 조회 및 화면 연결을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "DB Tool Controller", description = "데이터베이스 관련 테이블 레이아웃 API를 처리하는 컨트롤러")
public class DbtoolController {

    private final DbtoolService dbtoolService;

    /**
     * 주어진 {@code tableName}에 따라 테이블 데이터와 레이아웃 정보를 검색하고,
     * 뷰에 결과를 표시하기 위해 모델에 데이터를 추가합니다.
     * <p>
     * 빈 값 또는 잘못된 테이블 이름, 데이터가 없는 테이블, 데이터 검색 중 오류가 발생하는
     * 시나리오를 처리합니다.
     *
     * @param tableName 검색할 테이블 이름
     * @param model 속성을 뷰에 전달하기 위한 {@code Model} 객체
     * @return 렌더링할 Thymeleaf 템플릿 이름
     */
    @GetMapping("/table/view/{tableName}")
    public String viewTableData(@PathVariable("tableName") String tableName, Model model) {
        log.info("Request received for table: {}", tableName);

        if (!StringUtils.hasText(tableName)) {
            log.error("Invalid tableName provided.");
            model.addAttribute("errorMessage", "Invalid or empty table name.");
            return "error";
        }

        try {
            // 1. 테이블 데이터 가져오기
            List<?> tableData = dbtoolService.getTableData(tableName);

            // 2. 테이블 레이아웃 정보 가져오기 (컬럼 순서 포함)
            DbtoolVO param = new DbtoolVO();
            param.setTableName(tableName);
            List<DbtoolVO> dbColumns = dbtoolService.selectTabeLayout(param);

            // 데이터가 없는 경우 처리
            if (tableData == null || tableData.isEmpty()) {
                log.warn("No data found for table: {}", tableName);
                model.addAttribute("tableName", tableName);
                model.addAttribute("message", "데이터가 없습니다.");
                model.addAttribute("hasData", false);
                model.addAttribute("columns", dbColumns); // 컬럼 정보를 전달
                return "thymeleaf/tableData";
            }

            // 데이터가 있는 경우 처리
            model.addAttribute("tableName", tableName);
            model.addAttribute("list", tableData);
            model.addAttribute("columns", dbColumns); // 컬럼 순서 포함
            model.addAttribute("hasData", true);
            return "thymeleaf/tableData";
        } catch (Exception e) {
            log.error("Error while retrieving data for table {}: {}", tableName, e.getMessage());
            model.addAttribute("message", e.getMessage());
            return "error";
        }
    }

    /**
     * 특정 테이블과 컬럼의 상세 레이아웃 정보를 조회합니다.
     * 제공된 매개변수를 기반으로 테이블 레이아웃 세부 정보를 가져오고
     * 조회된 정보를 프론트엔드에 전달합니다.
     *
     * @param columnComments 컬럼에 대한 설명(선택사항).
     * @param columnName 컬럼 이름(선택사항).
     * @param tableName 테이블 이름(선택사항).
     * @param dbtoolVO 데이터베이스 툴 조회 매개변수를 포함하는 객체.
     * @param modelMap 뷰 계층에 전달할 속성을 보유하는 ModelMap 객체.
     * @return 테이블 레이아웃을 렌더링할 뷰 페이지 이름("thymeleaf/tableLayout").
     * @throws IOException 처리 중 I/O 오류가 발생한 경우.
     */
    @Operation(summary = "테이블 레이아웃 조회", description = "특정 테이블과 컬럼의 상세 레이아웃 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "레코드가 정상적으로 조회되었습니다.")
    @GetMapping("/tableLayout")
    public String tableLayout(
        @RequestParam(value = "columnComments", required = false) String columnComments,
        @RequestParam(value = "columnName", required = false) String columnName,
        @RequestParam(value = "tableName", required = false) String tableName,
        @ModelAttribute @Valid DbtoolVO dbtoolVO,
        ModelMap modelMap) throws IOException {

        DbtoolVO param = new DbtoolVO();

        param.setColumnComments(columnComments);
        param.setColumnName(columnName);
        param.setTableName(tableName);

        // 테이블 레이아웃 정보 리스트 조회
        List<DbtoolVO> dbtoolVOList = dbtoolService.selectTabeLayout(param);

        // 모델에 데이터 추가
        modelMap.addAttribute("dbtoolvo", dbtoolVO);
        modelMap.addAttribute("listview", dbtoolVOList);

        // 테이블 레이아웃 화면 호출
        return "thymeleaf/tableLayout";
    }

}