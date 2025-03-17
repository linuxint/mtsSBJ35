package com.devkbil.mtssbj.member;

import com.devkbil.common.util.FileUpload;
import com.devkbil.common.util.UtilEtc;
import com.devkbil.mtssbj.admin.organ.UserService;
import com.devkbil.mtssbj.common.FileVO;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * 멤버와 관련된 작업을 관리하는 컨트롤러입니다.
 * 사용자 프로필 관리, 비밀번호 변경, 멤버 검색 기능의 요청을 처리합니다.
 */
@Controller
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    /**
     * 사용자의 개인 정보를 조회하여 표시하는 페이지를 반환합니다.
     * 현재 로그인한 사용자의 정보를 조회하고, 필요한 경우 저장 완료 메시지를 표시합니다.
     *
     * @param save 저장 완료 여부를 나타내는 파라미터 ("OK"인 경우 저장 완료 메시지 표시)
     * @param modelMap 뷰에 전달할 데이터를 담는 모델 객체
     * @return "member/memberForm" 뷰 페이지 경로
     */
    @GetMapping("/memberForm")
    public String memberForm(@RequestParam(value = "save", required = false) String save, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        UserVO userInfo = userService.selectUserOne(userno);

        modelMap.addAttribute("userInfo", userInfo);
        modelMap.addAttribute("save", save);

        return "member/memberForm";
    }

    /**
     * 사용자 정보를 저장합니다.
     * 프로필 사진이 업로드된 경우 이미지를 저장하고, 사용자 정보를 업데이트합니다.
     *
     * @param userInfo 저장할 사용자 정보 ({@link UserVO})
     * @return 저장 완료 후 리다이렉트할 URL
     */
    @PostMapping("/userSave")
    public String userSave(@ModelAttribute @Valid UserVO userInfo) {

        String userno = authService.getAuthUserNo();

        userInfo.setUserno(userno);

        FileUpload fs = new FileUpload();
        FileVO fileInfo = fs.saveImage(userInfo.getPhotofile());
        if (!ObjectUtils.isEmpty(fileInfo)) {
            userInfo.setPhoto(fileInfo.getRealname());
        }
        userService.updateUserByMe(userInfo);

        return "redirect:/memberForm?save=OK";
    }

    /**
     * 사용자의 비밀번호를 변경합니다.
     * 입력받은 새 비밀번호를 암호화하여 저장하고, 결과를 JSON 형식으로 반환합니다.
     *
     * @param response HTTP 응답 객체
     * @param userInfo 변경할 비밀번호 정보가 포함된 사용자 정보 ({@link UserVO})
     */
    @PostMapping("/changePWSave")
    public void changePWSave(HttpServletResponse response, @ModelAttribute @Valid UserVO userInfo) {

        String userno = authService.getAuthUserNo();

        userInfo.setUserno(userno);
        userInfo.setUserpw(passwordEncoder.encode(userInfo.getUserpw()));
        userService.updateUserPassword(userInfo);

        UtilEtc.responseJsonValue(response, "OK");
    }

    /**
     * 직원 목록을 검색하여 결과를 표시하는 페이지를 반환합니다.
     * 검색어가 입력된 경우에만 검색을 수행하며, 페이징 처리된 결과를 반환합니다.
     *
     * @param searchVO 검색 조건 및 페이징 정보 ({@link SearchVO})
     * @param modelMap 뷰에 전달할 데이터를 담는 모델 객체
     * @return "member/searchMember" 뷰 페이지 경로
     */
    @RequestMapping("/searchMember")
    public String searchMember(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        if (StringUtils.hasText(searchVO.getSearchKeyword())) {

            searchVO.pageCalculate(memberService.selectSearchMemberCount(searchVO)); // startRow, endRow

            List<?> listview = memberService.selectSearchMemberList(searchVO);

            modelMap.addAttribute("listview", listview);
        }
        modelMap.addAttribute("searchVO", searchVO);
        return "member/searchMember";
    }
}