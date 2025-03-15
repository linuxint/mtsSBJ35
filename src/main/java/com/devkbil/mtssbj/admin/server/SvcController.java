package com.devkbil.mtssbj.admin.server;

import com.devkbil.common.util.UtilEtc;
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
 * 서비스 관리 컨트롤러
 * - 서비스 데이터의 조회, 저장, 읽기, 삭제 기능 제공.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "SvcController", description = "서비스 관리 API")
public class SvcController {

    private final SvcService svcService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 서비스 목록 조회
     *
     * @param modelMap 데이터 전송용 모델
     * @return 뷰 이름
     */
    @GetMapping("/adServerSvcList")
    @Operation(summary = "서비스 목록 조회", description = "모든 서비스의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public String serverSvcList(ModelMap modelMap) {

        ServerSearchVO searchVO = new ServerSearchVO();
        List<SvcVO> serviceList = svcService.getServiceList(searchVO);

        modelMap.addAttribute("listview", serviceList);
        log.debug("getServerServiceList modelMap {}", modelMap);

        return "admin/server/ServerSvcList";
    }

    /**
     * 서비스 저장/수정
     *
     * @param response  응답 객체
     * @param svcVO 저장할 데이터 객체
     */
    @PostMapping("/adServerSvcSave")
    @Operation(summary = "서비스 저장", description = "서비스 데이터를 저장하거나 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public void serverSvcSave(HttpServletResponse response, SvcVO svcVO) {
        int affectedRows = !StringUtils.hasText(svcVO.getSvcId())
                ? svcService.insertService(svcVO)
                : svcService.updateService(svcVO);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }

    /**
     * 특정 서비스 상세 조회
     *
     * @param svcId    서비스 식별자
     * @param response JSON 응답을 위한 HTTP 객체
     */
    @GetMapping("/adServerSvcRead")
    @Operation(summary = "서비스 조회", description = "특정 서비스 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "찾을 수 없는 데이터"),
        @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public void serverSvcRead(@RequestParam(value = "svcId") String svcId, HttpServletResponse response) {
        if (!StringUtils.hasText(svcId)) {
            throw new IllegalArgumentException("svcId 값이 필요합니다.");
        }

        SvcVO svcVO = svcService.getServiceDetail(svcId);
        UtilEtc.responseJsonValue(response, svcVO);
    }

    /**
     * 서비스 삭제
     *
     * @param svcId    삭제할 서비스의 ID
     * @param response 결과를 JSON으로 반환할 객체
     */
    @GetMapping("/adServerSvcDelete")
    @Operation(summary = "서비스 삭제", description = "특정 서비스 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제 대상이 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러 발생")
    })
    public void serverSvcDelete(@RequestParam(value = "svcId") String svcId, HttpServletResponse response) {
        if (!StringUtils.hasText(svcId)) {
            throw new IllegalArgumentException("svcId 값이 필요합니다.");
        }

        int affectedRows = svcService.deleteService(svcId);
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }
}