package com.devkbil.mtssbj.admin.sign;

import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 전자 결재 문서 관리 컨트롤러
 * - 문서 타입 조회, 저장, 삭제 등 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "SignDocController", description = "전자결재 문서 관리 API") // 컨트롤러 태그 추가
public class SignDocController {

    private final SignDocService signDocService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 문서 유형 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 뷰에 데이터를 전달하기 위한 객체
     * @return 문서 유형 리스트 화면 뷰 경로
     */
    @GetMapping("/adSignDocTypeList")
    @Operation(summary = "문서 유형 리스트 조회", description = "관리자가 문서 유형 리스트를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문서 유형 리스트 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public String signDocTypeList(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {


        // 문서 유형 리스트 및 페이징 처리
        searchVO.pageCalculate(signDocService.selectSignDocTypeCount(searchVO));
        List<?> listview = signDocService.selectSignDocTypeList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "admin/sign/SignDocTypeList";
    }

    /**
     * 문서 유형 폼을 조회하거나 생성합니다.
     *
     * @param signInfo 문서 유형 정보 객체
     * @param modelMap 뷰에 전달할 데이터를 담는 Spring의 ModelMap 객체.
     *                공통 속성과 문서 유형 정보를 담아 뷰에 전달하는데 사용됨
     * @return 문서 유형 폼 뷰의 경로
     */
    @GetMapping("/adSignDocTypeForm")
    @Operation(summary = "문서 유형 폼 조회", description = "특정 문서 유형 정보를 조회하거나, 신규 문서 유형을 위한 폼을 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "문서 유형 폼 반환"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public String signDocTypeForm(@ModelAttribute SignDocTypeVO signInfo, ModelMap modelMap) {


        // 문서 유형 정보 조회
        if (StringUtils.hasText(signInfo.getDtno())) {
            signInfo = signDocService.selectSignDocTypeOne(signInfo.getDtno());
            modelMap.addAttribute("signInfo", signInfo);
        }

        return "admin/sign/SignDocTypeForm";
    }

    /**
     * 문서 유형 정보를 저장하거나 업데이트합니다.
     *
     * @param signInfo 저장할 문서 유형 정보 객체
     * @return 저장 후 문서 유형 리스트로 리디렉션 경로
     */
    @PostMapping("/adSignDocTypeSave")
    @Operation(summary = "문서 유형 저장", description = "문서 유형 정보를 저장하거나 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "303", description = "저장 후 문서 유형 리스트로 리디렉션"),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public String signDocTypeSave(@ModelAttribute @Valid SignDocTypeVO signInfo) {
        signDocService.insertSignDocType(signInfo);
        return "redirect:/adSignDocTypeList";
    }

    /**
     * 제공된 정보를 기반으로 특정 문서 유형을 삭제하고 문서 유형 리스트로 리디렉션합니다.
     *
     * @param signVO 삭제할 문서 유형 정보를 포함하는 객체.
     * @return 문서 유형 리스트 페이지로 리디렉션하는 문자열.
     */
    @GetMapping("/adSignDocTypeDelete")
    @Operation(summary = "문서 유형 삭제", description = "특정 문서 유형 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "303", description = "삭제 후 문서 유형 리스트로 리디렉션"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 오류 발생")
    })
    public String signDocTypeDelete(@ModelAttribute @Valid SignDocTypeVO signVO) {
        signDocService.deleteSignDocType(signVO);
        return "redirect:/adSignDocTypeList";
    }
}