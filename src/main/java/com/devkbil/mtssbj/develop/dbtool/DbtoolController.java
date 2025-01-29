package com.devkbil.mtssbj.develop.dbtool;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
