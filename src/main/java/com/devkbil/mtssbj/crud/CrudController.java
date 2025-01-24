package com.devkbil.mtssbj.crud;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * CRUDController: CRUD API와 View를 처리하는 컨트롤러
 * - 리스트, 읽기, 쓰기, 저장, 삭제 등의 기능 제공
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "CRUD Controller", description = "CRUD 데이터를 조회/처리하는 API")
public class CrudController {

    private final CrudService crudService;
    private final EtcService etcService;
    private final AuthenticationService authenticationService;

    /**
     * CRUD 리스트 페이지
     * - 검색 조건(SearchVO)에 따라 CRUD 데이터를 조회합니다.
     *
     * @param searchVO an object containing search criteria such as page number,
     *                 search keyword, and filters which determine the data to be retrieved.
     * @param modelMap an instance of ModelMap used to add attributes that will
     *                 be rendered in the view.
     * @return the name of the view page where the CRUD list is displayed.
     */
    @Operation(summary = "CRUD 리스트 조회", description = "CRUD 데이터를 조건에 따라 조회하여 리스트로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 데이터를 반환합니다.")
    @GetMapping("/crudList")
    public String crudList(@RequestBody @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련
        searchVO.pageCalculate(crudService.selectCrudCount(searchVO)); // startRow, endRow
        List<?> listview = crudService.selectCrudList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "crud/CrudList";
    }

    /**
     * CRUD 입력/수정 페이지
     * - 특정 데이터를 수정하거나 새로 입력합니다.
     */
    @Operation(summary = "CRUD 입력/수정", description = "CRUD 데이터를 입력하거나 수정합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 입력/수정 페이지를 반환합니다.")
    @GetMapping("/crudForm")
    public String crudForm(@RequestBody @Valid CrudVO crudInfo, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련
        if (crudInfo.getCrno() != null) {
            crudInfo = crudService.selectCrudOne(crudInfo);

            modelMap.addAttribute("crudInfo", crudInfo);
        }

        return "crud/CrudForm";
    }

    /**
     * CRUD 저장 처리
     * - 입력된 데이터를 저장(삽입/수정)합니다.
     */
    @Operation(summary = "CRUD 저장", description = "CRUD 데이터를 저장(삽입/수정) 처리합니다.")
    @ApiResponse(responseCode = "302", description = "정상적으로 데이터를 저장하고 리스트 페이지로 리다이렉트합니다.")
    @PostMapping("/crudSave")
    public String crudSave(@RequestBody @Valid CrudVO crudInfo) {

        String userno = authenticationService.getAuthenticatedUserNo();

        crudInfo.setUserno(userno);

        crudService.insertCrud(crudInfo);

        return "redirect:/crudList";
    }

    /**
     * CRUD 상세 보기 페이지
     * - 특정 CRUD 데이터를 조회합니다.
     */
    @Operation(summary = "CRUD 상세 보기", description = "CRUD 데이터를 상세 조회하여 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 상세 페이지를 반환합니다.")
    @GetMapping("/crudRead")
    public String crudRead(@RequestBody @Valid CrudVO crudVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련

        CrudVO crudInfo = crudService.selectCrudOne(crudVO);

        modelMap.addAttribute("crudInfo", crudInfo);

        return "crud/CrudRead";
    }

    /**
     * CRUD 데이터 삭제 처리
     * - 특정 CRUD 데이터를 삭제(논리 삭제)합니다.
     *
     * @param crudVO An object containing the data of the CRUD to be deleted.
     * @return A redirection string to the CRUD list page.
     */
    @Operation(summary = "CRUD 삭제", description = "CRUD 데이터를 삭제 처리합니다.")
    @ApiResponse(responseCode = "302", description = "정상적으로 데이터를 삭제하고 리스트 페이지로 리다이렉트합니다.")
    @GetMapping("/crudDelete")
    public String crudDelete(@RequestBody @Valid CrudVO crudVO) {

        crudService.deleteCrud(crudVO);

        return "redirect:/crudList";
    }

}
