package com.devkbil.mtssbj.admin.organ;

import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 부서 관리 컨트롤러
 * - 부서 트리 조회, 저장, 수정, 삭제와 관련된 요청을 처리합니다.
 */

/**
 * 부서 관리 작업을 처리하는 컨트롤러 클래스
 * 부서 계층 보기, 부서 정보 추가/업데이트, 특정 부서 세부 정보 조회, 부서 삭제 등의 작업을 포함합니다.
 * 부서 관련 기능에 대한 HTTP 요청을 처리하며, 관련 서비스 클래스와 상호 작용하여 작업을 실행합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "DeptController", description = "부서 관리")
public class DeptController {

    private final DeptService deptService;
    private final EtcService etcService;
    private final TreeMaker treeMaker = new TreeMaker();  // TreeMaker 재사용
    private final AuthService authService;

    /**
     * 부서 트리 구조를 조회합니다.
     *
     * @param modelMap 뷰에 전달할 데이터를 저장하는 객체
     * @return 부서 트리 뷰 렌더링 (admin/organ/Dept)
     */
    @GetMapping("/adDept")
    @Operation(summary = "부서 트리 조회", description = "부서의 계층 트리 구조를 반환합니다.")
    public String dept(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        // 공통 속성 설정
        etcService.setCommonAttribute(userno, modelMap);

        // 부서 트리 데이터 설정
        List<?> listview = deptService.selectDept();
        String treeStr = treeMaker.makeTreeByHierarchy(listview);

        modelMap.addAttribute("treeStr", treeStr);
        return "admin/organ/Dept";
    }

    /**
     * 부서 데이터를 저장하거나 기존 데이터를 업데이트합니다.
     *
     * @param deptInfo 저장 혹은 업데이트할 부서 정보를 포함하는 객체
     * @param response 저장 결과를 JSON 형태로 반환하기 위한 HttpServletResponse 객체
     */
    @PostMapping("/adDeptSave")
    @Operation(summary = "부서 저장", description = "새로운 부서를 저장하거나 기존 부서를 업데이트합니다.")
    public void saveDept(@ModelAttribute @Valid DeptVO deptInfo, HttpServletResponse response) {

        int affectedRows = deptService.insertDept(deptInfo);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? deptInfo : "Fail");

    }

    /**
     * 특정 부서 정보를 조회합니다.
     *
     * @param deptno   조회할 부서 번호
     * @param response JSON 형식으로 조회 결과를 반환하는 응답 객체
     */
    @GetMapping("/adDeptRead")
    @Operation(summary = "부서 상세 조회", description = "지정된 부서 번호의 상세 정보를 조회합니다.")
    public void readDept(@RequestParam(value = "deptno") String deptno, HttpServletResponse response) {

        DeptVO deptInfo = deptService.selectDeptOne(deptno);

        UtilEtc.responseJsonValue(response, deptInfo != null ? deptInfo : "Fail");
    }

    /**
     * 특정 부서 정보를 삭제합니다.
     *
     * @param deptno   삭제할 부서 번호
     * @param response JSON 형식으로 삭제 결과를 반환하는 응답 객체
     */
    @GetMapping("/adDeptDelete")
    @Operation(summary = "부서 삭제", description = "지정된 부서 번호(deptno)에 해당하는 부서를 삭제합니다.")
    public void deleteDept(@RequestParam(value = "deptno") String deptno, HttpServletResponse response) {

        int affectedRows = deptService.deleteDept(deptno);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");

    }
}