package com.devkbil.mtssbj.admin.organ;

import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.AdminAuthorize;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.member.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 사용자 관리와 관련된 API를 제공합니다.
 * - 사용자 관리 화면, 리스트 조회, 저장, 삭제, ID 중복 확인 등의 기능 포함.
 */
@Slf4j
@Controller
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "UserController", description = "사용자 관리 API")
public class UserController {

    private final DeptService deptService; // 부서 관련 서비스
    private final UserService userService; // 사용자 관련 서비스
    private final EtcService etcService; // 기타 공통 서비스
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화 도구
    private final TreeMaker treeMaker = new TreeMaker(); // 부서 트리 생성 도구
    private final AuthenticationService authenticationService;

    /**
     * 사용자 관리 메인 화면을 반환하는 메서드입니다.
     * - 부서 트리 데이터를 포함하여 반환합니다.
     *
     * @param modelMap 뷰 렌더링 시 전달될 데이터 모델 객체
     * @return 사용자 관리 화면의 뷰 이름 ("admin/organ/User")
     */
    @GetMapping("/adUser")
    @Operation(summary = "사용자 관리 화면 조회", description = "사용자 관리 메인 화면을 반환합니다.")
    @ApiResponse(responseCode = "200", description = "요청 성공, 사용자 관리 화면 반환")
    public String user(ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // 부서 데이터를 트리 형태로 전달
        List<?> deptList = deptService.selectDept();
        String treeStr = treeMaker.makeTreeByHierarchy(deptList);

        modelMap.addAttribute("treeStr", treeStr);
        return "admin/organ/User";
    }

    /**
     * 부서별 사용자 리스트 조회
     * - 특정 부서에 속한 사용자 목록을 반환합니다.
     * - 부서 번호가 없을 경우 전체 데이터를 반환할 수 있습니다.
     *
     * @param deptno   부서 번호(선택 사항). 제공되지 않을 경우 부서로 필터링하지 않고 조회합니다.
     * @param modelMap 뷰 렌더링을 위한 데이터 모델 객체.
     * @return 공통 사용자 리스트 조회 프로세스의 출력값을 나타내는 문자열.
     */
    @PostMapping("/adUserList")
    @Operation(summary = "부서별 사용자 리스트 조회", description = "특정 부서에 속한 사용자들의 리스트를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공, 사용자 리스트 반환"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터")
    })
    public String userList(@RequestParam(value = "deptno", required = false) String deptno, ModelMap modelMap) {
        return commonUserList(modelMap, deptno);
    }

    /**
     * 공통 사용자 리스트 처리 메소드
     * - 부서 번호에 따른 사용자 데이터를 조회하여 화면에 전달합니다.
     *
     * @param modelMap 사용자 리스트가 추가될 모델 객체.
     * @param deptno   사용자 리스트를 조회할 부서의 고유 번호.
     * @return 렌더링할 뷰의 이름, 특히 "admin/organ/UserList".
     */
    private String commonUserList(ModelMap modelMap, String deptno) {
        List<?> userList = userService.selectUserList(deptno);
        modelMap.addAttribute("listview", userList);
        return "admin/organ/UserList";
    }

    /**
     * 사용자를 생성하거나 기존 사용자를 업데이트합니다.
     * 새로운 사용자에 대해서는 사용자 ID 중복 검사를 수행하고,
     * 비밀번호는 암호화한 뒤 저장합니다.
     *
     * @param userInfo 저장할 사용자 정보(ID, 비밀번호 및 기타 세부 정보 포함)
     * @param modelMap 뷰 렌더링에 사용할 모델 맵
     * @return 작업 완료 후 반환할 뷰 이름 또는 경로
     */
    @PostMapping("/adUserSave")
    @Operation(summary = "사용자 저장", description = "사용자 정보를 신규 저장하거나 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 저장 완료"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "409", description = "아이디 중복 오류")
    })
    public String saveUser(@ModelAttribute @Valid UserVO userInfo, ModelMap modelMap) {
        // 신규 사용자 ID 중복 검사
        if (!StringUtils.hasText(userInfo.getUserno())) {
            if (userService.selectUserID(userInfo.getUserid()) != null) {
                modelMap.addAttribute("msg", "이미 존재하는 아이디입니다.");
                return "common/blank";
            }
        }

        // 비밀번호 암호화 후 저장
        userInfo.setUserpw(passwordEncoder.encode(userInfo.getUserpw()));
        userService.insertUser(userInfo);

        return commonUserList(modelMap, userInfo.getDeptno());
    }

    /**
     * 사용자 ID 중복 확인.
     * 입력된 사용자 ID가 중복되었는지 여부를 확인합니다.
     *
     * @param userid   확인할 사용자 ID
     * @param response 중복 결과를 JSON 형식으로 응답
     */
    @PostMapping("/chkUserid")
    @Operation(summary = "중복된 사용자 ID 확인", description = "특정 사용자 ID가 중복되었는지 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "중복되지 않은 ID 사용 가능"),
            @ApiResponse(responseCode = "409", description = "중복된 사용자 ID")
    })
    public void chkUserid(@RequestParam(value = "userid") String userid, HttpServletResponse response) {
        String existingUserID = userService.selectUserID(userid);
        UtilEtc.responseJsonValue(response, existingUserID); // JSON 형식으로 ID 확인 결과 반환
    }

    /**
     * 특정 사용자 번호(userno)에 해당하는 사용자 데이터를 조회하여 반환합니다.
     *
     * @param response HttpServletResponse 객체로 조회된 사용자 데이터를 JSON 형식으로 반환합니다.
     */
    @PostMapping(value = "/adUserRead")
    @Operation(summary = "사용자 상세 조회", description = "특정 사용자 번호(userno)에 해당하는 사용자 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "요청 성공, 사용자 데이터 반환"),
            @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없음")
    })
    public void userRead(HttpServletResponse response) {

        String userno = authenticationService.getAuthenticatedUserNo();

        // 사용자 정보 조회 후 JSON 형태로 반환
        UserVO userInfo = userService.selectUserOne(userno);
        UtilEtc.responseJsonValue(response, userInfo);
    }

    /**
     * 사용자 삭제
     * - 특정 사용자 ID(userno)에 대한 계정을 삭제합니다.
     *
     * @param userInfo 삭제할 사용자 번호(userno)를 포함한 사용자 정보
     * @param modelMap 요청 속성을 저장하는 데 사용되는 모델 맵
     * @return 삭제된 사용자와 동일한 부서의 업데이트된 사용자 목록을 나타내는 문자열
     */
    @PostMapping("/adUserDelete")
    @Operation(summary = "사용자 삭제", description = "특정 사용자 번호(userno)에 해당하는 사용자를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 삭제 완료"),
            @ApiResponse(responseCode = "404", description = "삭제 대상 사용자 없음")
    })
    public String deleteUser(@ModelAttribute @Valid UserVO userInfo, ModelMap modelMap) {
        userService.deleteUser(userInfo.getUserno());
        return commonUserList(modelMap, userInfo.getDeptno());
    }
}