package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

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

    @GetMapping("/table/view/{tableName}")
    public String viewTableData(@PathVariable("tableName") String tableName, Model model) {
        log.info("Request received for table: {}", tableName);

        if (tableName == null || tableName.isBlank()) {
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
     * 테이블 레이아웃 조회
     * - 사용자가 요청한 테이블과 컬럼 정보에 대한 레이아웃을 조회합니다.
     *
     * @param dbtoolVO 테이블/컬럼 정보를 담고 있는 VO 객체
     * @param modelMap 화면에 데이터를 전달하기 위한 모델 객체
     * @return 테이블 레이아웃 페이지
     * @throws IOException I/O 처리 중 오류 발생 시 예외 처리
     */
    @Operation(summary = "테이블 레이아웃 조회", description = "특정 테이블과 컬럼의 상세 레이아웃 정보를 조회합니다.")
    @ApiResponse(responseCode = "200", description = "레코드가 정상적으로 조회되었습니다.")
    @GetMapping("/tableLayout")
    public String tableLayout(
            @RequestParam(value = "columnComments", required = false) String columnComments
            , @RequestParam(value = "columnName", required = false) String columnName
            , @RequestParam(value = "tableName", required = false) String tableName
            , @ModelAttribute @Valid DbtoolVO dbtoolVO
            , ModelMap modelMap) throws IOException {

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
