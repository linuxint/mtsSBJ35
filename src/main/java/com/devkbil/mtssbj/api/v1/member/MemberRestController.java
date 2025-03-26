package com.devkbil.mtssbj.api.v1.member;

import com.devkbil.mtssbj.admin.organ.UserService;
import com.devkbil.mtssbj.member.MemberService;
import com.devkbil.mtssbj.member.UserVO;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 회원 관리를 담당하는 REST API 컨트롤러.
 * 회원과 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/member")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관리 API")
public class MemberRestController {

    private final UserService userService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    /**
     * 현재 사용자 정보 조회 API.
     *
     * @return 사용자 정보를 담은 ResponseEntity
     */
    @Operation(summary = "현재 사용자 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/profile")
    public ResponseEntity<UserVO> getUserProfile() {
        String userno = authService.getAuthUserNo();
        
        UserVO userInfo = userService.selectUserOne(userno);
        if (userInfo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 사용자 정보 업데이트 API.
     *
     * @param userInfo 업데이트할 사용자 정보
     * @return 업데이트된 사용자 정보를 담은 ResponseEntity
     */
    @Operation(summary = "사용자 정보 업데이트", description = "현재 로그인한 사용자의 정보를 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 정보 업데이트 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/profile")
    public ResponseEntity<UserVO> updateUserProfile(@RequestBody @Valid UserVO userInfo) {
        String userno = authService.getAuthUserNo();
        
        userInfo.setUserno(userno);
        
        // 프로필 이미지 처리는 별도 API로 구현하거나 추후 구현
        
        userService.updateUserByMe(userInfo);
        
        UserVO updatedUser = userService.selectUserOne(userno);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 비밀번호 변경 API.
     *
     * @param passwordInfo 비밀번호 정보를 담은 맵
     * @return 변경 결과를 담은 ResponseEntity
     */
    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> passwordInfo) {
        String userno = authService.getAuthUserNo();
        
        if (!StringUtils.hasText(passwordInfo.get("password"))) {
            return ResponseEntity.badRequest().build();
        }
        
        UserVO userInfo = new UserVO();
        userInfo.setUserno(userno);
        userInfo.setUserpw(passwordEncoder.encode(passwordInfo.get("password")));
        
        userService.updateUserPassword(userInfo);
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "비밀번호가 성공적으로 변경되었습니다.");
        
        return ResponseEntity.ok(result);
    }

    /**
     * 회원 검색 API.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 검색 결과를 담은 ResponseEntity
     */
    @Operation(summary = "회원 검색", description = "검색 조건을 기반으로 회원 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원 검색 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchMembers(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();
        
        if (!StringUtils.hasText(searchVO.getSearchKeyword())) {
            result.put("message", "검색어를 입력해주세요.");
            return ResponseEntity.ok(result);
        }
        
        searchVO.pageCalculate(memberService.selectSearchMemberCount(searchVO)); // startRow, endRow
        List<?> listview = memberService.selectSearchMemberList(searchVO);
        
        result.put("searchVO", searchVO);
        result.put("list", listview);
        
        return ResponseEntity.ok(result);
    }
}