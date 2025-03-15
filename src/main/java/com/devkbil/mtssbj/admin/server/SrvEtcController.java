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
 * 서비스 기타 정보 관리 컨트롤러
 * - 기타 정보 데이터의 조회, 저장, 읽기, 삭제 기능 제공.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "SrvEtcController", description = "서비스 기타 정보 관리 API")
public class SrvEtcController {

    private final SrvEtcService srvEtcService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 기타 정보 목록 조회
     *
     * @param modelMap 데이터 전달을 위한 모델 객체
     * @return 요청 처리 결과를 렌더링할 뷰 이름
     */
    @GetMapping("/adServerEtcList")
    @Operation(summary = "기타 정보 목록 조회", description = "모든 서비스 기타 정보의 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public String serverEtcList(ModelMap modelMap) {

        // 현재 사용자 정보 가져오기
        String userno = authService.getAuthUserNo();

        // 공통 속성 설정
        etcService.setCommonAttribute(userno, modelMap);

        ServerSearchVO searchVO = new ServerSearchVO();

        List<SrvEtcVO> list = srvEtcService.getServiceEtcList(searchVO);
        modelMap.addAttribute("listview", list);
        log.debug("serverEtcList modelMap {}", modelMap);

        return "admin/server/ServerEtcList";
    }

    /**
     * 기타 정보 저장/수정
     *
     * @param response     작업 결과를 반환하는 HTTP 응답 객체
     * @param serviceSrvEtcVO 저장 또는 수정할 데이터 객체
     */
    @PostMapping("/adServerEtcSave")
    @Operation(summary = "기타 정보 저장", description = "새로운 정보를 저장하거나 기존 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverEtcSave(HttpServletResponse response, SrvEtcVO serviceSrvEtcVO) {
        int affectedRows = !StringUtils.hasText(serviceSrvEtcVO.getEtcId())
                ? srvEtcService.insertServiceEtc(serviceSrvEtcVO)
                : srvEtcService.updateServiceEtc(serviceSrvEtcVO);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }

    /**
     * 특정 기타 정보 상세 조회
     *
     * @param etcId 읽고자 하는 기타 정보의 식별자(ID)
     * @param response 검색된 데이터를 JSON 형식으로 반환하는 HTTP 응답 객체
     */
    @GetMapping("/adServerEtcRead")
    @Operation(summary = "기타 정보 조회", description = "특정 기타 정보의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회 성공"),
        @ApiResponse(responseCode = "404", description = "요청 데이터 없음"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverEtcRead(@RequestParam(value = "etcId") String etcId, HttpServletResponse response) {
        if (!StringUtils.hasText(etcId)) {
            throw new IllegalArgumentException("etcId 값이 필요합니다.");
        }

        SrvEtcVO serviceSrvEtcVO = srvEtcService.getServiceEtcDetail(etcId);
        UtilEtc.responseJsonValue(response, serviceSrvEtcVO);
    }

    /**
     * 특정 기타 정보 삭제
     *
     * @param etcId 삭제하려는 기타 정보의 식별자(ID)
     * @param response 삭제 결과를 JSON 형식으로 반환하는 HTTP 응답 객체
     */
    @GetMapping("/adServerEtcDelete")
    @Operation(summary = "기타 정보 삭제", description = "특정 기타 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제 대상 정보 없음"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverEtcDelete(@RequestParam(value = "etcId") String etcId, HttpServletResponse response) {
        if (!StringUtils.hasText(etcId)) {
            throw new IllegalArgumentException("etcId 값이 필요합니다.");
        }

        int affectedRows = srvEtcService.deleteServiceEtc(etcId);
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }
}