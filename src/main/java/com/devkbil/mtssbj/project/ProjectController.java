package com.devkbil.mtssbj.project;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 프로젝트 관리를 담당하는 컨트롤러.
 * 프로젝트와 관련된 CRUD 및 기타 기능을 제공합니다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 프로젝트 목록 조회.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @param modelMap 결과 데이터를 담을 모델맵 객체
     * @return 프로젝트 목록 페이지 경로
     */
    @Operation(summary = "프로젝트 목록 조회", description = "검색 조건을 기반으로 프로젝트 목록을 조회합니다.")
    @GetMapping("/projectList")
    public String projectList(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        searchVO.pageCalculate(projectService.selectProjectCount(searchVO)); // startRow, endRow
        List<?> listview = projectService.selectProjectList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "project/ProjectList";
    }

    /**
     * 프로젝트 목록을 Ajax를 통해 조회.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @param modelMap 결과 데이터를 담을 모델맵 객체
     * @return Ajax를 위한 프로젝트 목록 페이지
     */
    @Operation(summary = "프로젝트 목록 Ajax 조회", description = "Ajax 방식으로 프로젝트 정보를 제공합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ajax 기반 프로젝트 목록 반환",
            content = @Content(mediaType = "text/html")),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/projectList4Ajax")
    public String projectList4Ajax(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        searchVO.pageCalculate(projectService.selectProjectCount(searchVO)); // startRow, endRow
        List<?> listview = projectService.selectProjectList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "project/ProjectList4Ajax";
    }

    /**
     * 프로젝트 작성 폼 조회.
     *
     * @param modelMap 결과 데이터를 담을 모델맵 객체
     * @return 프로젝트 작성 페이지 경로
     */
    @Operation(summary = "프로젝트 작성 폼 조회", description = "신규 또는 수정 중인 프로젝트 정보를 표시하는 양식 페이지를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 작성 페이지 반환",
            content = @Content(mediaType = "text/html")),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/projectForm")
    public String projectForm(@RequestParam(value = "prno", required = false) String prno, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        ProjectVO projectInfo = projectService.selectProjectOne(prno); // 프로젝트 정보 조회 (조회 실패 시 초기값 자동 처리)
        modelMap.addAttribute("projectInfo", projectInfo);
        modelMap.addAttribute("prno", prno);

        return "project/ProjectForm";
    }

    /**
     * 프로젝트 저장.
     *
     * @param projectInfo 저장하려는 프로젝트 정보
     * @return 프로젝트 목록 페이지로 리다이렉트
     */
    @Operation(summary = "프로젝트 저장", description = "신규 또는 수정된 프로젝트 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "저장 후 프로젝트 목록 페이지로 리다이렉트",
            content = @Content(mediaType = "text/html")),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/projectSave")
    public String projectSave(@ModelAttribute @Valid ProjectVO projectInfo, BindingResult result, ModelMap modelMap) {

        // 유효성 검사 오류가 있을 경우, 에러 정보를 모델에 추가하고 작성 폼 페이지로 다시 이동
        if (result.hasErrors()) {
            modelMap.addAttribute("validationErrors", result.getAllErrors());
            return "project/ProjectForm"; // 혹은 에러 페이지
        }

        String userno = authService.getAuthUserNo(); // 현재 인증된 사용자의 번호를 가져옴

        projectInfo.setUserno(userno); // 프로젝트 정보에 사용자 번호를 설정

        // 프로젝트 번호가 존재할 경우(수정 모드), 권한 검사를 진행
        if (StringUtils.hasText(projectInfo.getPrno())) { // null과 빈 문자열 동시 체크
            String chk = projectService.selectProjectAuthChk(projectInfo);
            if (chk == null) { // 권한이 없을 경우 권한 없음 페이지로 이동
                return "common/noAuth";
            }
        }

        projectService.insertProject(projectInfo); // 프로젝트 정보를 저장 (신규 또는 수정)

        return "redirect:projectList"; // 저장 완료 후 프로젝트 목록 페이지로 리다이렉트
    }

    /**
     * 프로젝트 삭제.
     *
     * @return 프로젝트 목록 페이지로 리다이렉트
     */
    @Operation(summary = "프로젝트 삭제", description = "지정된 프로젝트를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "302", description = "삭제 후 프로젝트 목록 페이지로 리다이렉트",
            content = @Content(mediaType = "text/html")),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/projectDelete")
    public String projectDelete(@RequestParam(value = "prno", required = false) String prno) {

        // 프로젝트 번호가 비어 있으면 예외 처리
        if (!StringUtils.hasText(prno)) {
            throw new IllegalArgumentException("프로젝트 번호가 잘못되었습니다.");
        }

        String userno = authService.getAuthUserNo();

        ProjectVO projectInfo = new ProjectVO();        // check auth for delete
        projectInfo.setPrno(prno);
        projectInfo.setUserno(userno);

        String chk = projectService.selectProjectAuthChk(projectInfo);
        if (!StringUtils.hasText(chk)) {
            return "common/noAuth";
        }

        projectService.deleteProjectOne(prno);

        return "redirect:/projectList";
    }

}
