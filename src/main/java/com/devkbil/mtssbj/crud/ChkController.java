package com.devkbil.mtssbj.crud;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * CRUD 작업을 위해 사용되는 ChkController
 * - 리스트 확인 및 선택된 행의 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "ChkController", description = "CRUD 관련 API를 제공합니다.")
public class ChkController {

    private final CrudService crudService;
    private final EtcService etcService;
    private final AuthenticationService authenticationService;

    /**
     * 체크 리스트 조회
     * - CRUD 데이터 리스트를 페이징 조건에 맞게 반환합니다.
     *
     * @param searchVO 검색 조건을 담고 있는 객체
     * @param modelMap 조회된 데이터를 페이지에 전달하기 위한 객체
     * @return JSP 페이지 경로 : "crud/ChkList"
     */
    @Operation(
            summary = "체크 리스트 조회",
            description = "CRUD 데이터 리스트를 반환합니다. 페이징 관련 계산과 리스트 데이터를 제공하며, 사용자 세션 정보를 처리합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "정상적으로 리스트 페이지 반환",
                            content = @Content(schema = @Schema(implementation = String.class))
                    )
            }
    )
    @GetMapping("/chkList")
    public String chkList(@RequestBody @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        // 공통 속성 설정 (예: 사용자 정보)
        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련 작업: 페이징 계산 및 리스트 생성
        searchVO.pageCalculate(crudService.selectCrudCount(searchVO)); // startRow, endRow 계산
        List<?> listview = crudService.selectCrudList(searchVO);

        // ModelMap에 데이터 추가
        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        // 결과 페이지로 이동
        return "crud/ChkList";
    }

    /**
     * 선택된 행 삭제
     * - 사용자가 선택한 행을 삭제 처리합니다.
     *
     * @param checkRow 삭제할 행(row)의 아이디 배열
     * @return "redirect:/chkList" 삭제 후 리스트 페이지로 리다이렉션
     */
    @Operation(
            summary = "선택된 행 삭제",
            description = "사용자가 선택한 행을 삭제한 후, 리스트 페이지로 리다이렉션합니다.",
            responses = {
                    @ApiResponse(responseCode = "302", description = "체크 리스트 페이지로 리다이렉션"),
                    @ApiResponse(responseCode = "400", description = "삭제할 데이터가 없거나 유효하지 않은 경우 예외 발생")
            }
    )
    @PostMapping("/chkDelete")
    public String chkDelete(@RequestParam(value = "checkRow", required = false) String[] checkRow) {

        if (!ObjectUtils.isEmpty(checkRow)) {
            // 체크된 행 삭제
            crudService.deleteChk(checkRow);
        }

        // 삭제 완료 후 리스트 페이지로 리다이렉트
        return "redirect:/chkList";
    }

}
