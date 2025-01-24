package com.devkbil.mtssbj.admin.menu;

import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 메뉴 관리 컨트롤러
 * - 메뉴의 조회, 저장, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "MenuController", description = "메뉴 관리 API")
public class MenuController {

    private final MenuService menuService;
    private final EtcService etcService;
    private final AuthenticationService authenticationService;

    /**
     * 메뉴 리스트를 조회하여 트리 형태로 렌더링합니다.
     *
     * @param modelMap 뷰에 전달할 데이터를 저장하는 객체
     * @return 메뉴 리스트 화면을 렌더링할 뷰 이름 (admin/menu/MenuList)
     */
    @GetMapping("/adMenuList")
    @Operation(summary = "메뉴 리스트 조회", description = "모든 메뉴 항목의 리스트를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 리스트 리턴 성공"),
            @ApiResponse(responseCode = "500", description = "서버 에러: 메뉴 리스트를 반환하는 동안 문제가 발생했습니다.")
    })
    public String menuList(ModelMap modelMap) {
        setCommonAttributes(modelMap);
        List<?> menuList = menuService.selectMenu();
        String treeStr = new TreeMaker().makeTreeByHierarchy(menuList);

        modelMap.addAttribute("treeStr", treeStr);
        return "admin/menu/MenuList";
    }

    /**
     * 메뉴 데이터를 저장하거나 기존 데이터를 업데이트합니다.
     *
     * @param response 저장 결과를 JSON 형식으로 반환할 응답 객체
     * @param menuInfo 저장 또는 업데이트할 메뉴 데이터를 담은 객체
     */
    @PostMapping("/adMenuSave")
    @Operation(summary = "메뉴 저장", description = "새로운 메뉴 정보를 저장하거나 기존 메뉴를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 저장 성공"),
            @ApiResponse(responseCode = "400", description = "클라이언트 요청 오류: 유효하지 않은 데이터"),
            @ApiResponse(responseCode = "500", description = "서버 에러: 메뉴 저장 처리 중 오류가 발생했습니다.")
    })
    public void menuSave(HttpServletResponse response, MenuVO menuInfo) {
        handleJsonResponse(response, () -> {

            String userno = authenticationService.getAuthenticatedUserNo();

            menuInfo.setReguserno(userno);
            menuService.insertMenu(menuInfo);
            return menuInfo;
        });
    }

    /**
     * 특정 메뉴 정보를 조회합니다.
     *
     * @param mnuNo    조회할 메뉴의 번호
     * @param response 조회 결과를 JSON 형식으로 반환할 응답 객체
     */
    @GetMapping("/adMenuRead")
    @Operation(summary = "단일 메뉴 조회", description = "메뉴 번호(mnuNo)에 해당하는 메뉴 정보를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 데이터 리턴 성공"),
            @ApiResponse(responseCode = "404", description = "해당 메뉴를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러: 메뉴 정보를 불러오는 동안 문제가 발생했습니다.")
    })
    public void menuRead(@RequestParam(value = "mnuNo") String mnuNo, HttpServletResponse response) {
        handleJsonResponse(response, () -> menuService.selectMenuOne(mnuNo));
    }

    /**
     * 특정 메뉴 데이터를 삭제합니다.
     *
     * @param mnuNo    삭제할 메뉴 번호
     * @param response 삭제 결과를 JSON 형식으로 반환할 응답 객체
     */
    @PostMapping("/adMenuDelete")
    @Operation(summary = "메뉴 삭제", description = "메뉴 번호(mnuNo)에 해당하는 메뉴 항목을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메뉴 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "삭제할 메뉴를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 에러: 메뉴 삭제 처리 중 오류가 발생했습니다.")
    })
    public void menuDelete(@RequestParam(value = "mnuNo") String mnuNo, HttpServletResponse response) {
        handleJsonResponse(response, () -> {
            menuService.deleteMenu(mnuNo);
            return "OK";
        });
    }

    /**
     * 요청 처리 공통 메서드로 사용자 번호를 가져오고, UI 공통 데이터를 설정합니다.
     *
     * @param modelMap UI에 전달할 공통 데이터를 저장할 ModelMap 객체
     */
    private void setCommonAttributes(ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

    }

    /**
     * JSON 형식으로 응답을 처리하는 공통 메서드. 응답 데이터를 반환하거나 예외를 처리합니다.
     *
     * @param response         응답 객체
     * @param responseSupplier 처리 결과를 생성하는 함수형 인터페이스
     */
    private void handleJsonResponse(HttpServletResponse response, ResponseSupplier<?> responseSupplier) {
        try {
            Object result = responseSupplier.get();
            UtilEtc.responseJsonValue(response, result);
        } catch (Exception e) {
            log.error("Error processing request", e);
            UtilEtc.responseJsonValue(response, "FATAL_ERROR");
        }
    }

    /**
     * 응답 데이터를 생성하는 함수형 인터페이스.
     *
     * @param <T> 생성할 응답 데이터 타입
     */
    @FunctionalInterface
    private interface ResponseSupplier<T> {
        T get() throws Exception;
    }
}