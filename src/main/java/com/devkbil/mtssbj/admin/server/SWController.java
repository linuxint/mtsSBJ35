package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.ServerSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 서버 소프트웨어 관리 컨트롤러
 * - 서버 소프트웨어 데이터의 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "SWController", description = "서버 소프트웨어 관리 API")
public class SWController {

    private final SWService swService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 모든 서버 소프트웨어 리스트를 조회합니다.
     *
     * @param modelMap 구성된 데이터를 저장할 ModelMap 객체.
     * @return 요청 처리 결과를 나타내는 문자열(뷰 이름).
     */
    @GetMapping("/adServerSWList")
    @Operation(summary = "서버 소프트웨어 리스트 조회", description = "모든 서버 소프트웨어의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public String serverSWList(ModelMap modelMap) {

        // 현재 사용자 정보 가져오기
        String userno = authService.getAuthUserNo();

        // 공통 속성 설정
        etcService.setCommonAttribute(userno, modelMap);

        ServerSearchVO searchVO = new ServerSearchVO();
        searchVO.setSearchParamsFromModelMap(modelMap);
        List<SWVO> listview = swService.getSWList(searchVO);

        modelMap.addAttribute("listview", listview);
        log.debug("serverSWList modelMap {}", modelMap);

        return "thymeleaf/admin/server/ServerSWList";  // 렌더링할 뷰 이름
    }

    /**
     * 서버 소프트웨어 데이터를 저장하거나 업데이트합니다.
     *
     * @param response 작업 결과를 전달할 HttpServletResponse 객체
     * @param swVO     저장 또는 업데이트할 서버 소프트웨어 데이터 객체
     */
    @PostMapping("/adServerSWSave")
    @Operation(summary = "서버 소프트웨어 저장", description = "새로운 서버 소프트웨어를 저장하거나 기존 데이터를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverSWSave(HttpServletResponse response, SWVO swVO) {

        int affectedRows = !StringUtils.hasText(swVO.getSwId())
                ? swService.insertSW(swVO)
                : swService.updateSW(swVO);

        log.debug("serverSWSave affectedRows {}", affectedRows);
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }

    /**
     * 특정 서버 소프트웨어의 상세 정보를 조회합니다.
     *
     * @param swId     읽을 서버 소프트웨어를 식별하는 swId
     * @param response 검색된 데이터를 JSON 형식으로 반환
     */
    @GetMapping("/adServerSWRead")
    @Operation(summary = "서버 소프트웨어 상세 조회", description = "서버 소프트웨어 ID(swId)에 해당하는 정보를 읽어옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "소프트웨어를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverSWRead(@RequestParam(value = "swId") String swId, HttpServletResponse response) {

        if (!StringUtils.hasText(swId)) {
            throw new IllegalArgumentException("swId 값이 필요합니다.");
        }

        SWVO swVO = swService.getSWDetail(swId);

        UtilEtc.responseJsonValue(response, swVO);
    }

    /**
     * 특정 서버 소프트웨어 데이터를 삭제합니다.
     *
     * @param swId     삭제할 소프트웨어를 식별하는 swId
     * @param response 작업 결과를 JSON 형식으로 반환
     */
    @GetMapping("/adServerSWDelete")
    @Operation(summary = "서버 소프트웨어 삭제", description = "서버 소프트웨어 ID(swId)에 해당하는 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "소프트웨어를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverSWDelete(@RequestParam(value = "swId") String swId, HttpServletResponse response) {

        if (!StringUtils.hasText(swId)) {
            throw new IllegalArgumentException("swId 값이 필요합니다.");
        }

        int affectedRows = swService.deleteSW(swId);
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }
}