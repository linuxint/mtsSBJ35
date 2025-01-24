package com.devkbil.mtssbj.etc;

import com.devkbil.mtssbj.admin.organ.DeptService;
import com.devkbil.mtssbj.admin.organ.UserService;
import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 부서 및 사용자 데이터를 팝업 UI를 통해 상호작용할 수 있는 API를 제공하는 컨트롤러입니다.
 * 계층형 부서 구조 표시, 부서 내 사용자 목록, 그리고 다양한 컨텍스트에서
 * 사용자 관련 뷰를 처리하는 작업을 담당합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "PopUserController", description = "부서 및 사용자 관련 팝업 API")
public class PopUserController {

    private final DeptService deptService;
    private final UserService userService;

    /**
     * 부서 리스트를 계층 구조로 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return 부서 트리 정보가 포함된 뷰 경로
     */
    @Operation(summary = "부서 리스트", description = "전체 부서 정보를 계층 구조로 반환합니다.")
    @PostMapping("/popupDept")
    public String popupDept(ModelMap modelMap) {
        // 부서 리스트 가져오기
        List<?> listview = deptService.selectDept();

        // 계층 형태의 트리 데이터 구성
        TreeMaker tm = new TreeMaker();
        String treeStr = tm.makeTreeByHierarchy(listview);

        modelMap.addAttribute("treeStr", treeStr);

        return "etc/popupDept";
    }

    /**
     * 사용자를 위한 부서 리스트를 계층 구조로 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return 사용자용 부서 트리 뷰 경로
     */
    @Operation(summary = "사용자를 위한 부서 리스트", description = "사용자를 위한 부서 정보를 계층 구조로 반환합니다.")
    @PostMapping("/popupUser")
    public String popupUser(ModelMap modelMap) {

        List<?> listview = deptService.selectDept();

        TreeMaker tm = new TreeMaker();
        String treeStr = tm.makeTreeByHierarchy(listview);

        modelMap.addAttribute("treeStr", treeStr);

        return "etc/popupUser";
    }

    /**
     * 부서 ID로 해당 부서의 사용자 리스트를 반환합니다.
     *
     * @param deptno   선택된 부서의 번호
     * @param searchVO 검색 조건 객체
     * @param modelMap 모델맵 객체, 사용자 리스트를 포함하도록 설정
     * @return 사용자 정보 리스트를 포함한 뷰 경로
     */
    @Operation(summary = "선택된 부서의 사용자 리스트", description = "부서 ID로 해당 부서의 사용자 리스트를 반환합니다.")
    @PostMapping("/popupUsersByDept")
    public String popupUsersByDept(
            @RequestParam(name = "deptno") String deptno
            , @RequestBody @Valid SearchVO searchVO
            , ModelMap modelMap
    ) {
        searchVO.setSearchExt1(deptno);

        List<?> listview = userService.selectUserListWithDept(searchVO);

        modelMap.addAttribute("listview", listview);

        return "etc/popupUsersByDept";
    }

    /**
     * 사용자들의 부서 리스트를 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return 사용자들용 부서 트리 뷰 경로
     */
    @Operation(summary = "사용자들 부서 리스트", description = "사용자들의 부서 리스트 조회 후 반환합니다.")
    @PostMapping("/popupUsers")
    public String popupUsers(ModelMap modelMap) {

        popupUser(modelMap);

        return "etc/popupUsers";
    }

    /**
     * 결재 경로 지정을 위한 사용자 부서 리스트를 반환합니다.
     *
     * @param modelMap 모델맵 객체
     * @return 결재 경로용 부서 트리 뷰 경로
     */
    @Operation(summary = "결재 경로 지정용 사용자 부서 리스트", description = "결재 경로 지정을 위해 부서 리스트를 제공합니다.")
    @PostMapping("/popupUsers4SignPath")
    public String popupUsers4SignPath(ModelMap modelMap) {

        popupUser(modelMap);

        return "etc/popupUsers4SignPath";
    }

    /**
     * 사용자의 부서 정보를 바탕으로 사용자들의 리스트를 조회하여 반환합니다.
     *
     * @param deptno   부서 번호
     * @param request  요청 객체
     * @param searchVO 검색 조건 객체
     * @param modelMap 모델맵 객체
     * @return 사용자 정보 리스트를 포함한 뷰 경로
     */
    @Operation(summary = "사용자들 리스트", description = "사용자들의 정보를 부서별로 조회 후 반환합니다.")
    @PostMapping("/popupUsers4Users")
    public String popupUsers4Users(
            @RequestParam(name = "deptno") String deptno
            , HttpServletRequest request
            , SearchVO searchVO
            , ModelMap modelMap) {

        popupUsersByDept(deptno, searchVO, modelMap);

        return "etc/popupUsers4Users";
    }
}
