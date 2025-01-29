package com.devkbil.mtssbj.admin.code;

import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 공통 코드 관리 컨트롤러
 * - 공통 코드 조회, 저장, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "CodeController", description = "공통 코드 관리 API")
public class CodeController {

    private final CodeService codeService;

    /**
     * 공통 코드 리스트를 조회하고 화면에 렌더링합니다.
     *
     * @param searchVO 공통 코드 리스트 필터링을 위한 검색 조건 객체
     * @param modelMap UI 레이어에 전달할 데이터를 저장하는 객체
     * @return 공통 코드 리스트 뷰 (admin/code/CodeList)
     */
    @RequestMapping("/adCodeList")
    @Operation(summary = "공통 코드 리스트", description = "모든 공통 코드의 리스트를 조회하고 화면에 렌더링합니다.")
    public String codeList(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        searchVO.pageCalculate(codeService.selectCodeCount(searchVO)); // startRow, endRow 계산
        List<?> listview = codeService.selectCodeList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "admin/code/CodeList";
    }

    /**
     * 공통 코드를 등록하거나, 수정할 수 있는 폼 화면으로 이동합니다.
     *
     * @param codeInfo 공통 코드 정보 객체 (수정 시)
     * @param modelMap UI 레이어에 전달할 데이터를 저장하는 객체
     * @return 공통 코드 등록/수정 화면 뷰 (admin/code/CodeForm)
     */
    @GetMapping("/adCodeForm")
    @Operation(summary = "공통 코드 등록/수정 폼", description = "공통 코드 정보를 등록하거나 수정할 폼 화면을 반환합니다.")
    public String codeForm(@ModelAttribute @Valid CodeVO codeInfo, ModelMap modelMap) {
        Optional.ofNullable(codeInfo.getClassno())
                .map(classno -> codeService.selectCodeOne(codeInfo))
                .ifPresent(selectedCodeInfo -> {
                    modelMap.addAttribute("codeInfo", selectedCodeInfo);
                    modelMap.addAttribute("codeFormType", "U");
                    modelMap.addAttribute("readonly", "readonly");
                });

        return "admin/code/CodeForm";
    }

    /**
     * 공통 코드를 저장하거나 업데이트합니다.
     *
     * @param codeFormType 코드 폼의 타입 (신규 등록 "C" 또는 업데이트 "U")
     * @param codeInfo     저장할 공통 코드 정보 객체
     * @param modelMap     UI 레이어에 전달할 데이터를 저장하는 객체
     * @return 저장 후 공통 코드 리스트 화면으로 리다이렉트 (또는 에러 메시지 출력)
     */
    @PostMapping("/adCodeSave")
    @Operation(summary = "공통 코드 저장/수정", description = "공통 코드를 저장하거나 업데이트합니다.")
    public String codeSave(@RequestParam(name = "codeFormType") String codeFormType,
                           @ModelAttribute @Valid CodeVO codeInfo,
                           ModelMap modelMap) {
        try {
            if (!"U".equals(codeFormType) &&
                    Optional.ofNullable(codeService.selectCodeOne(codeInfo)).isPresent()) {
                modelMap.addAttribute("msg", "이미 사용 중인 코드입니다.");
                return "common/message";
            }

            codeService.insertCode(codeFormType, codeInfo);

            return "redirect:/adCodeList";
        } catch (Exception e) {
            log.error("Error while saving code", e);
            modelMap.addAttribute("msg", "코드 저장 중 오류가 발생했습니다.");
            return "common/message";
        }
    }

    /**
     * 특정 공통 코드의 상세 정보를 조회합니다.
     *
     * @param codeVO   조회할 공통 코드의 식별 정보를 포함한 객체
     * @param modelMap UI 레이어에 전달할 데이터를 저장하는 객체
     * @return 공통 코드 상세 정보 화면 뷰 (admin/code/CodeRead)
     */
    @GetMapping("/adCodeRead")
    @Operation(summary = "공통 코드 상세 조회", description = "지정된 공통 코드의 상세 정보를 반환합니다.")
    public String codeRead(@ModelAttribute @Valid CodeVO codeVO, ModelMap modelMap) {
        Optional.ofNullable(codeService.selectCodeOne(codeVO))
                .ifPresent(codeInfo -> modelMap.addAttribute("codeInfo", codeInfo));

        return "admin/code/CodeRead";
    }

    /**
     * 지정된 공통 코드를 삭제합니다.
     *
     * @param codeVO 삭제할 공통 코드의 식별 정보를 포함한 객체
     * @return 공통 코드 리스트 화면으로 리다이렉트
     */
    @PostMapping("/adCodeDelete")
    @Operation(summary = "공통 코드 삭제", description = "지정된 공통 코드를 삭제합니다.")
    public String codeDelete(@ModelAttribute @Valid CodeVO codeVO) {
        codeService.deleteCodeOne(codeVO);
        return "redirect:/adCodeList";
    }

    /**
     * 공통 코드 리스트를 API 형태로 제공하며, 결과 데이터를 JSON 형식으로 반환합니다.
     *
     * @param searchVO 공통 코드 검색 조건 객체
     * @return ResponseEntity<List < ?>> : 조회된 공통 코드 데이터 및 HTTP 상태 코드
     */
    @PostMapping("/codeList")
    @Operation(summary = "공통 코드 리스트 조회 (API)",
            description = "공통 코드 데이터를 JSON 형태로 반환합니다.")
    public ResponseEntity<List<?>> codeList(@ModelAttribute @Valid SearchVO searchVO) {
        try {
            log.debug("코드 리스트를 조회합니다.");
            searchVO.pageCalculate(codeService.selectCodeCount(searchVO));

            List<?> resultList = codeService.selectCodeList(searchVO);
            return ResponseEntity.ok(resultList);
        } catch (Exception e) {
            log.error("Error fetching code list", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}