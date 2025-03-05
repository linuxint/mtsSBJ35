package com.devkbil.mtssbj.crud;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

/**
 * 이 컨트롤러는 CRUD 작업(목록, 조회, 생성, 수정, 삭제)을 관리합니다.
 * CRUD 데이터와 관련된 요청을 처리하고 관련 서비스(CrudService, EtcService, AuthService)와
 * 상호 작용하여 비즈니스 로직을 실행합니다. 응답은 각 작업에 대한 뷰로 렌더링됩니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "CRUD Controller", description = "CRUD 데이터를 조회/처리하는 API")
public class CrudController {

    private final CrudService crudService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * CRUD 리스트 페이지
     * - 검색 조건(SearchVO)에 따라 CRUD 데이터를 조회합니다.
     *
     * @param searchVO 페이지 번호, 검색 키워드, 필터 등 데이터 조회에 필요한
     *                검색 조건을 포함하는 객체입니다.
     * @param modelMap 뷰에서 렌더링될 속성들을 추가하는데 사용되는
     *                ModelMap 인스턴스입니다.
     * @return CRUD 리스트가 표시되는 뷰 페이지의 이름입니다.
     */
    @Operation(summary = "CRUD 리스트 조회", description = "CRUD 데이터를 조건에 따라 조회하여 리스트로 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 데이터를 반환합니다.")
    @GetMapping("/crudList")
    public String crudList(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련
        searchVO.pageCalculate(crudService.selectCrudCount(searchVO)); // startRow, endRow
        List<?> listview = crudService.selectCrudList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "crud/CrudList";
    }

    /**
     * CRUD 데이터 입력 또는 수정을 위한 폼을 표시하는 요청을 처리합니다.
     * 제공된 CRUD 데이터를 처리하고 렌더링을 위한 모델을 준비합니다.
     *
     * @param crudInfo 생성 또는 수정을 위한 CRUD 정보로, 검증되고 모델에 바인딩됩니다
     * @param modelMap 뷰에 속성을 전달하는 데 사용되는 모델 맵입니다
     * @return CRUD 폼을 렌더링할 뷰의 이름입니다
     */
    @Operation(summary = "CRUD 입력/수정", description = "CRUD 데이터를 입력하거나 수정합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 입력/수정 페이지를 반환합니다.")
    @GetMapping("/crudForm")
    public String crudForm(@ModelAttribute @Valid CrudVO crudInfo, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련
        if (crudInfo.getCrno() != null) {
            crudInfo = crudService.selectCrudOne(crudInfo);

            modelMap.addAttribute("crudInfo", crudInfo);
        }

        return "crud/CrudForm";
    }

    /**
     * 제공된 정보를 기반으로 CRUD 데이터를 저장하거나 업데이트합니다.
     *
     * @param crudInfo 저장 또는 업데이트할 CRUD 정보를 담고 있는 데이터 객체입니다.
     *                 유효하고 적절히 채워져 있어야 합니다.
     * @return 작업 성공 시 CRUD 리스트 페이지로의 리다이렉트 URL입니다.
     */
    @Operation(summary = "CRUD 저장", description = "CRUD 데이터를 저장(삽입/수정) 처리합니다.")
    @ApiResponse(responseCode = "302", description = "정상적으로 데이터를 저장하고 리스트 페이지로 리다이렉트합니다.")
    @PostMapping("/crudSave")
    public String crudSave(@ModelAttribute @Valid CrudVO crudInfo) {

        String userno = authService.getAuthUserNo();

        crudInfo.setUserno(userno);

        crudService.insertCrud(crudInfo);

        return "redirect:/crudList";
    }

    /**
     * 특정 CRUD 데이터 객체의 상세 정보를 조회하고 CRUD 상세 페이지의 뷰를 반환합니다.
     *
     * @param crudVO 조회할 CRUD 데이터를 식별하기 위한 정보가 포함된 요청 객체입니다.
     *              `CrudVO`의 유효한 인스턴스여야 합니다.
     * @param modelMap 뷰에서 사용할 속성들이 추가되는 모델 맵입니다.
     * @return CRUD 상세 정보를 표시하기 위해 렌더링될 뷰의 이름입니다.
     */
    @Operation(summary = "CRUD 상세 보기", description = "CRUD 데이터를 상세 조회하여 반환합니다.")
    @ApiResponse(responseCode = "200", description = "정상적으로 상세 페이지를 반환합니다.")
    @GetMapping("/crudRead")
    public String crudRead(@ModelAttribute @Valid CrudVO crudVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // CRUD 관련

        CrudVO crudInfo = crudService.selectCrudOne(crudVO);

        modelMap.addAttribute("crudInfo", crudInfo);

        return "crud/CrudRead";
    }

    /**
     * 지정된 CRUD 데이터를 삭제하고 목록 페이지로 리다이렉트합니다.
     *
     * @param crudVO 삭제할 CRUD 데이터로, 유효한 모델 속성으로 제공됩니다
     * @return CRUD 목록 페이지로의 리다이렉션 문자열입니다
     */
    @Operation(summary = "CRUD 삭제", description = "CRUD 데이터를 삭제 처리합니다.")
    @ApiResponse(responseCode = "302", description = "정상적으로 데이터를 삭제하고 리스트 페이지로 리다이렉트합니다.")
    @GetMapping("/crudDelete")
    public String crudDelete(@ModelAttribute @Valid CrudVO crudVO) {

        crudService.deleteCrud(crudVO);

        return "redirect:/crudList";
    }

}
