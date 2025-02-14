package com.devkbil.mtssbj.admin.board;

import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 게시판 그룹 관리 컨트롤러
 * - 게시판 그룹 데이터의 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "BoardGroupController", description = "게시판 그룹 관리 API")
public class BoardGroupController {

    private final BoardGroupService boardGroupService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 모든 게시판 그룹의 리스트를 조회합니다.
     *
     * @param modelMap 구성된 트리 구조와 같은 속성을 저장할 ModelMap 객체.
     * @return 요청 처리 결과를 나타내는 문자열(이 구현에서는 빈 문자열을 반환).
     */
    @GetMapping("/adBoardGroupList")
    @Operation(summary = "게시판 그룹 리스트 조회", description = "모든 게시판 그룹의 계층 트리를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public String boardGroupList(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        List<?> listview = boardGroupService.selectBoardGroupList();

        TreeMaker tm = new TreeMaker();
        String treeStr = tm.makeTreeByHierarchy(listview);

        modelMap.addAttribute("treeStr", treeStr);
        log.debug("boardGroupList modelMap {}", modelMap);
        return "";
        //return "admin/board/BoardGroupList";
    }

    /**
     * 게시판 그룹 데이터를 저장하거나 업데이트합니다.
     * 클라이언트로부터 전달된 게시판 그룹 데이터를 저장소에 반영합니다.
     * 작업 결과는 JSON 형식으로 응답합니다.
     *
     * @param response 작업 결과를 전달할 HttpServletResponse 객체
     * @param bgInfo   저장 또는 업데이트할 게시판 그룹 데이터를 포함하는 객체
     */
    @PostMapping("/adBoardGroupSave")
    @Operation(summary = "게시판 그룹 저장", description = "새로운 게시판 그룹을 저장하거나 기존 그룹을 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void boardGroupSave(HttpServletResponse response, BoardGroupVO bgInfo) {

        int affectedRows = boardGroupService.insertBoard(bgInfo);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? bgInfo : "Fail");

    }

    /**
     * 특정 게시판 그룹의 상세 정보를 조회합니다.
     * 게시판 그룹 번호(bgno)를 기준으로 정보를 검색하고, 결과를 JSON 형식으로 반환합니다.
     *
     * @param bgno     읽을 게시판 그룹을 식별하는 bgno 객체
     * @param response 검색된 데이터를 JSON 형식으로 작성할 HttpServletResponse 객체
     */
    @GetMapping("/adBoardGroupRead")
    @Operation(summary = "게시판 그룹 상세 조회", description = "게시판 그룹 번호(bgno)에 해당하는 정보를 읽어옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void boardGroupRead(@RequestParam(value = "bgno") String bgno, HttpServletResponse response) {

        if (!StringUtils.hasText(bgno)) {
            throw new IllegalArgumentException("bgno 값이 없습니다.");
        }

        BoardGroupVO bgInfo = boardGroupService.selectBoardGroupOne(bgno);

        UtilEtc.responseJsonValue(response, bgInfo);
    }

    /**
     * 특정 게시판 그룹 데이터를 삭제합니다.
     * 게시판 그룹 번호(bgno)를 기준으로 데이터를 삭제하고,
     * 작업 결과를 JSON 형식("OK")으로 반환합니다.
     *
     * @param bgno     삭제할 게시판 그룹을 식별하는 bgno 객체
     * @param response 작업 결과를 JSON 형식으로 반환할 HttpServletResponse 객체
     */
    @GetMapping("/adBoardGroupDelete")
    @Operation(summary = "게시판 그룹 삭제", description = "게시판 그룹 번호(bgno)에 해당하는 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void boardGroupDelete(@RequestParam(value = "bgno") String bgno, HttpServletResponse response) {

        if (!StringUtils.hasText(bgno)) {
            throw new IllegalArgumentException("bgno 값이 필요합니다.");
        }

        int affectedRows = boardGroupService.deleteBoardGroup(bgno);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");

    }

}
