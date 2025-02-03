package com.devkbil.mtssbj.sign;

import com.devkbil.mtssbj.admin.sign.SignDocService;
import com.devkbil.mtssbj.admin.sign.SignDocTypeVO;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 결재 관련 요청을 처리하는 컨트롤러 클래스입니다.
 * <p>
 * 이 컨트롤러는 결재 문서의 리스트 조회, 생성, 읽기, 삭제, 결재, 회수 등의 작업을 수행합니다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "SignController", description = "결재 문서와 관련된 작업을 처리하는 컨트롤러")
public class SignController {

    private final SignService signService;
    private final SignDocService signDocService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 결재 받을 문서 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @param modelMap View에 데이터를 전달하기 위한 객체
     * @return 결재 받을 문서 리스트 페이지 ("sign/SignDocListTobe")
     */
    @RequestMapping("/signListTobe")
    @Operation(summary = "결재 받을 문서 리스트 조회", description = "사용자가 결재받아야 하는 문서들의 리스트를 조회합니다.")
    public String signListTobe(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        //
        searchVO.setUserno(userno);
        searchVO.pageCalculate(signService.selectSignDocTobeCount(searchVO)); // startRow, endRow
        List<?> listview = signService.selectSignDocTobeList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "sign/SignDocListTobe";
    }

    /**
     * 결재 할 문서 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @param modelMap View에 데이터를 전달하기 위한 객체
     * @return 결재 할 문서 리스트 페이지 ("sign/SignDocList")
     */
    @RequestMapping("/signListTo")
    @Operation(summary = "결재 할 문서 리스트 조회", description = "사용자가 결재를 진행해야 하는 문서들의 리스트를 조회합니다.")
    public String signListTo(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        //
        if (!StringUtils.hasText(searchVO.getSearchExt1())) {
            searchVO.setSearchExt1("sign");
        }
        searchVO.setUserno(userno);
        searchVO.pageCalculate(signService.selectSignDocCount(searchVO)); // startRow, endRow
        List<?> listview = signService.selectSignDocList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "sign/SignDocList";
    }

    /**
     * 기안 가능한 문서 타입 리스트를 조회합니다.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @param modelMap View에 데이터를 전달하기 위한 객체
     * @return 기안 문서 타입 리스트 페이지 ("sign/SignDocTypeList")
     */
    @GetMapping("/signDocTypeList")
    @Operation(summary = "기안 문서 타입 리스트 조회", description = "사용자가 선택할 수 있는 기안 문서 타입 리스트를 조회합니다.")
    public String signDocTypeList(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        List<?> listview = signDocService.selectSignDocTypeList(searchVO);

        modelMap.addAttribute("listview", listview);

        return "sign/SignDocTypeList";
    }

    /**
     * 결재 문서 작성 또는 수정 폼을 반환합니다.
     *
     * @param signDocInfo 결재 문서 정보를 담은 객체
     * @param modelMap    View에 데이터를 전달하기 위한 객체
     * @return 결재 문서 작성/수정 폼 페이지 ("sign/SignDocForm")
     */
    @GetMapping("/signDocForm")
    @Operation(summary = "결재 문서 작성/수정", description = "사용자가 결재 문서를 작성하거나 수정할 수 있는 화면을 반환합니다.")
    public String signDocForm(@ModelAttribute @Valid SignDocVO signDocInfo, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // 개별 작업
        List<?> signlist;
        if (signDocInfo.getDocno() == null) {    // 신규
            signDocInfo.setDocstatus("1");
            SignDocTypeVO docType = signDocService.selectSignDocTypeOne(signDocInfo.getDtno());
            signDocInfo.setDtno(docType.getDtno());
            signDocInfo.setDoccontents(docType.getDtcontents());
            signDocInfo.setUserno(userno);
            // 사번, 이름, 기안/합의/결제, 직책
            signlist = signService.selectSignLast(signDocInfo);
            String signPath = "";
            for (Object obj : signlist) {
                SignVO svo = (SignVO) obj;
                signPath += svo.getUserno() + "," + svo.getUsernm() + "," + svo.getSstype() + "," + svo.getUserpos() + "||";
            }
            signDocInfo.setDocsignpath(signPath);
        } else {                                // 수정
            signDocInfo = signService.selectSignDocOne(signDocInfo);
            signlist = signService.selectSign(signDocInfo.getDocno());
        }
        modelMap.addAttribute("signDocInfo", signDocInfo);
        modelMap.addAttribute("signlist", signlist);

        return "sign/SignDocForm";
    }

    /**
     * 결재 문서를 저장합니다.
     *
     * @param signDocInfo 저장할 결재 문서 정보를 담은 객체
     * @return 결재 받을 문서 리스트 페이지로 리다이렉트
     */
    @PostMapping("/signDocSave")
    @Operation(summary = "결재 문서 저장", description = "작성하거나 수정된 결재 문서를 저장합니다.")
    public String signDocSave(@ModelAttribute @Valid SignDocVO signDocInfo) {

        String userno = authService.getAuthUserNo();

        signDocInfo.setUserno(userno);

        signService.insertSignDoc(signDocInfo);

        return "redirect:/signListTobe";
    }

    /**
     * 결재 문서를 읽습니다.
     *
     * @param signDocVO 읽을 결재 문서의 정보를 담은 객체
     * @param modelMap  View에 데이터를 전달하기 위한 객체
     * @return 결재 문서 읽기 페이지 ("sign/SignDocRead")
     */
    @GetMapping("/signDocRead")
    @Operation(summary = "결재 문서 읽기", description = "결재 문서를 읽는 세부 화면을 반환합니다.")
    public String signDocRead(@ModelAttribute @Valid SignDocVO signDocVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // 개별 작업
        SignDocVO signDocInfo = signService.selectSignDocOne(signDocVO);
        List<?> signlist = signService.selectSign(signDocInfo.getDocno());
        String signer = signService.selectCurrentSigner(signDocVO.getDocno());

        modelMap.addAttribute("signDocInfo", signDocInfo);
        modelMap.addAttribute("signlist", signlist);
        modelMap.addAttribute("signer", signer);

        return "sign/SignDocRead";
    }

    /**
     * 결재 문서를 삭제합니다.
     *
     * @param signDocVO 삭제할 결재 문서의 정보를 담은 객체
     * @return 결재 받을 문서 리스트 페이지로 리다이렉트
     */
    @GetMapping("/signDocDelete")
    @Operation(summary = "결재 문서 삭제", description = "특정 결재 문서를 삭제합니다.")
    public String signDocDelete(@ModelAttribute @Valid SignDocVO signDocVO) {

        signService.deleteSignDoc(signDocVO);

        return "redirect:/signListTobe";
    }

    /**
     * 결재를 진행합니다.
     *
     * @param signInfo 결재 정보를 담은 객체
     * @return 결재 할 문서 리스트 페이지로 리다이렉트
     */
    @PostMapping("/signSave")
    @Operation(summary = "결재 진행", description = "사용자가 문서를 결재합니다.")
    public String signSave(@ModelAttribute @Valid SignVO signInfo) {

        signService.updateSign(signInfo);

        return "redirect:/signListTo";
    }

    /**
     * 결재 문서를 회수합니다.
     *
     * @param docno 회수할 문서 번호
     * @return 결재 받을 문서 리스트 페이지로 리다이렉트
     */
    @GetMapping("/signDocCancel")
    @Operation(summary = "결재 문서 회수", description = "이미 제출된 결재 문서를 회수 처리합니다.")
    public String signDocCancel(@RequestParam(value = "docno", required = false) String docno) {

        signService.updateSignDocCancel(docno);

        return "redirect:/signListTobe";
    }
}
