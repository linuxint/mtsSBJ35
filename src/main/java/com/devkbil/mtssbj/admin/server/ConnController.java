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
 * 서버 서비스 접속 정보 관리 컨트롤러
 * - 서비스 접속 정보 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "ConnController", description = "서버 서비스 접속 정보 관리 API")
public class ConnController {

    private final ConnService connService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 모든 서비스 접속 정보의 리스트를 조회합니다.
     *
     * @param modelMap 데이터를 전달할 ModelMap 객체
     * @return 요청 처리 결과를 나타내는 문자열(뷰 이름)
     */
    @GetMapping("/adServerConnList")
    @Operation(summary = "서비스 접속 정보 목록 조회", description = "모든 서비스 접속 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public String serverConnList(ModelMap modelMap) {

        // 현재 사용자 정보 가져오기
        String userno = authService.getAuthUserNo();

        // 공통 속성 설정
        etcService.setCommonAttribute(userno, modelMap);

        // 검색 조건 설정 및 리스트 조회
        ServerSearchVO searchVO = new ServerSearchVO();
        searchVO.setSearchParamsFromModelMap(modelMap);
        List<?> listview = connService.getServiceConnList(searchVO);

        // 조회 결과를 모델에 추가
        modelMap.addAttribute("listview", listview);
        log.debug("serverConnList modelMap {}", modelMap);

        return "admin/server/ServerConnList";  // 렌더링할 뷰 이름
    }

    /**
     * 서비스 접속 정보를 저장하거나 업데이트합니다.
     *
     * @param response      작업 결과를 전달할 HttpServletResponse 객체
     * @param serviceConnVO 저장 또는 업데이트할 서비스 접속 정보 데이터 객체
     */
    @PostMapping("/adServerConnSave")
    @Operation(summary = "서비스 접속 정보 저장", description = "새로운 서비스를 저장하거나 기존 서비스 데이터를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverConnSave(HttpServletResponse response, ConnVO serviceConnVO) {

        int affectedRows = !StringUtils.hasText(serviceConnVO.getConnId())
                ? connService.insertServiceConn(serviceConnVO)
                : connService.updateServiceConn(serviceConnVO);

        // 결과를 JSON 형태로 응답
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }

    /**
     * 특정 서비스 접속 상세 정보를 조회합니다.
     *
     * @param connId   읽을 서비스 접속 정보를 식별하는 ID
     * @param response 결과 데이터를 JSON 형식으로 반환할 HttpServletResponse 객체
     */
    @GetMapping("/adServerConnRead")
    @Operation(summary = "서비스 접속 정보 조회", description = "특정 서비스 접속 정보의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "서비스 접속 정보를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverConnRead(@RequestParam(value = "connId") String connId, HttpServletResponse response) {

        // ID 유효성 체크
        if (!StringUtils.hasText(connId)) {
            throw new IllegalArgumentException("connId 값이 필요합니다.");
        }

        // 상세 정보 조회
        ConnVO serviceConnVO = connService.getServiceConnDetail(connId);

        // 결과를 JSON 형태로 응답
        UtilEtc.responseJsonValue(response, serviceConnVO);
    }

    /**
     * 특정 서비스 접속 데이터를 삭제합니다.
     *
     * @param connId   삭제할 서비스 접속 데이터를 식별하는 ID
     * @param response 결과를 JSON 형식("OK" 또는 "Fail")으로 반환할 HttpServletResponse 객체
     */
    @GetMapping("/adServerConnDelete")
    @Operation(summary = "서비스 접속 정보 삭제", description = "특정 서비스 접속 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "서비스 접속 정보를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverConnDelete(@RequestParam(value = "connId") String connId, HttpServletResponse response) {

        // ID 유효성 체크
        if (!StringUtils.hasText(connId)) {
            throw new IllegalArgumentException("connId 값이 필요합니다.");
        }

        // 삭제 작업 수행
        int affectedRows = connService.deleteServiceConn(connId);

        // 결과를 JSON 형태로 응답
        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }
}