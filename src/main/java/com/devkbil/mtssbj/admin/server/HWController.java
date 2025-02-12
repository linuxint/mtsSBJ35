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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 서버 하드웨어 관리 컨트롤러
 * - 서버 하드웨어 데이터의 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "HWController", description = "서버 하드웨어 관리 API")
public class HWController {

    private final HWService hwService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 모든 서버 하드웨어 리스트를 조회합니다.
     *
     * @param modelMap 구성된 데이터를 저장할 ModelMap 객체.
     * @return 요청 처리 결과를 나타내는 문자열(뷰 이름).
     */
    @GetMapping("/adServerHWList")
    @Operation(summary = "서버 하드웨어 리스트 조회", description = "모든 서버 하드웨어의 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public String serverHWList(ModelMap modelMap) {

        // 현재 사용자 정보 가져오기
        String userno = authService.getAuthUserNo();

        // 공통 속성 설정
        etcService.setCommonAttribute(userno, modelMap);

        ServerSearchVO searchVO = new ServerSearchVO();
        searchVO.setSearchParamsFromModelMap(modelMap);
        List<?> listview = hwService.getHWList(searchVO);

        modelMap.addAttribute("listview", listview);
        log.debug("serverHWList modelMap {}", modelMap);

        return "thymeleaf/admin/server/HWList";  // 렌더링할 뷰 이름
    }

    /**
     * 서버 하드웨어 등록/수정 폼 화면
     *
     * @param hwId     수정할 하드웨어의 ID (새로운 경우 null)
     * @param modelMap 화면에 전달할 데이터
     * @return 등록/수정 화면
     */
    @GetMapping("/adServerHWForm")
    public String serverHWForm(@RequestParam(value = "hwId", required = false) String hwId, ModelMap modelMap) {
        HWVO hwVO;
        if (hwId != null) { // 수정 요청인 경우
            hwVO = hwService.getHWDetail(hwId); // 상세 데이터 조회
        } else {
            hwVO = new HWVO();
        }
        modelMap.addAttribute("hwVO", hwVO);
        return "thymeleaf/admin/server/HWForm"; // HWForm.html 템플릿
    }

    /**
     * 서버 하드웨어 데이터를 저장하거나 업데이트합니다.
     *
     * @param response 작업 결과를 전달할 HttpServletResponse 객체
     * @param hwInfo   저장 또는 업데이트할 서버 하드웨어 데이터 객체
     */
    @PostMapping("/adServerHWSave")
    @Operation(summary = "서버 하드웨어 저장", description = "새로운 서버 하드웨어를 저장하거나 기존 데이터를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverHWSave(HttpServletResponse response, HWVO hwInfo) {

        int affectedRows = !StringUtils.hasText(hwInfo.getHwId())
                ? hwService.insertHW(hwInfo)
                : hwService.updateHW(hwInfo);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }

    /**
     * 특정 서버 하드웨어의 상세 정보를 조회합니다.
     *
     * @param hwId     읽을 서버 하드웨어를 식별하는 hwId 객체
     * @param response 검색된 데이터를 JSON 형식으로 작성할 HttpServletResponse 객체
     */
    @GetMapping("/adServerHWRead")
    @Operation(summary = "서버 하드웨어 상세 조회", description = "서버 하드웨어 ID(hwId)에 해당하는 정보를 읽어옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "404", description = "서버 하드웨어를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverHWRead(@RequestParam(value = "hwId") String hwId, HttpServletResponse response) {

        if (!StringUtils.hasText(hwId)) {
            throw new IllegalArgumentException("hwId 값이 없습니다.");
        }

        HWVO hwVO = hwService.getHWDetail(hwId);

        UtilEtc.responseJsonValue(response, hwVO);
    }

    /**
     * 특정 서버 하드웨어 데이터를 삭제합니다.
     *
     * @param hwId     삭제할 서버 하드웨어를 식별하는 hwId 객체
     * @param response 작업 결과를 JSON 형식("OK")으로 반환할 HttpServletResponse 객체
     */
    @GetMapping("/adServerHWDelete")
    @Operation(summary = "서버 하드웨어 삭제", description = "서버 하드웨어 ID(hwId)에 해당하는 정보를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공"),
            @ApiResponse(responseCode = "404", description = "서버 하드웨어를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    public void serverHWDelete(@RequestParam(value = "hwId") String hwId, HttpServletResponse response) {

        if (!StringUtils.hasText(hwId)) {
            throw new IllegalArgumentException("hwId 값이 필요합니다.");
        }

        int affectedRows = hwService.deleteHW(hwId);

        UtilEtc.responseJsonValue(response, affectedRows > 0 ? "OK" : "Fail");
    }
}