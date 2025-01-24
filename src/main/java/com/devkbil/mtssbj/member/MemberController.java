package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.admin.organ.UserService;
import com.devkbil.mtssbj.common.util.FileUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.search.SearchVO;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final UserService userService;
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationService authenticationService;

    /**
     * 내정보.
     */
    @GetMapping("/memberForm")
    public String memberForm(@RequestParam(value = "save", required = false) String save, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        UserVO userInfo = userService.selectUserOne(userno);

        modelMap.addAttribute("userInfo", userInfo);
        modelMap.addAttribute("save", save);

        return "member/memberForm";
    }

    /**
     * 사용자 저장.
     */
    @PostMapping("/userSave")
    public String userSave(@RequestBody @Valid UserVO userInfo) {

        String userno = authenticationService.getAuthenticatedUserNo();

        userInfo.setUserno(userno);

        FileUtil fs = new FileUtil();
        FileVO fileInfo = fs.saveImage(userInfo.getPhotofile());
        if (!ObjectUtils.isEmpty(fileInfo)) {
            userInfo.setPhoto(fileInfo.getRealname());
        }
        userService.updateUserByMe(userInfo);

        return "redirect:/memberForm?save=OK";
    }

    /**
     * 비밀번호 변경.
     */
    @PostMapping("/changePWSave")
    public void changePWSave(HttpServletResponse response, @RequestBody @Valid UserVO userInfo) {

        String userno = authenticationService.getAuthenticatedUserNo();

        userInfo.setUserno(userno);
        userInfo.setUserpw(passwordEncoder.encode(userInfo.getUserpw()));
        userService.updateUserPassword(userInfo);

        UtilEtc.responseJsonValue(response, "OK");
    }

    /**
     * 직원조회.
     */
    @GetMapping("/searchMember")
    public String searchMember(@RequestBody @Valid SearchVO searchVO, ModelMap modelMap) {

        if (StringUtils.hasText(searchVO.getSearchKeyword())) {

            searchVO.pageCalculate(memberService.selectSearchMemberCount(searchVO)); // startRow, endRow

            List<?> listview = memberService.selectSearchMemberList(searchVO);

            modelMap.addAttribute("listview", listview);
        }
        modelMap.addAttribute("searchVO", searchVO);
        return "member/searchMember";
    }
}
