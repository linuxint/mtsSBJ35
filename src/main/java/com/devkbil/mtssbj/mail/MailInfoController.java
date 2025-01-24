package com.devkbil.mtssbj.mail;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.devkbil.mtssbj.search.SearchVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "MailInfoController", description = "메일 설정 관련 API")
public class MailInfoController {

    private final MailService mailService;
    private final EtcService etcService;
    private final AuthenticationService authenticationService;

    /**
     * 메일 설정 리스트 조회
     *
     * @param searchVO 검색 조건
     * @param modelMap 뷰에 전달할 데이터
     * @return 메일 설정 리스트 화면
     */
    @Operation(summary = "메일 설정 리스트 조회", description = "사용자 메일 설정 리스트를 조회합니다.")
    @GetMapping("/mailInfoList")
    public String mailInfoList(@RequestBody @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        List<?> listview = mailService.selectMailInfoList(userno);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "mail/MailInfoList";
    }

    /**
     * 메일 설정 작성/수정 폼 조회
     *
     * @param mailInfoInfo 메일 설정 데이터
     * @param modelMap     뷰에 전달할 데이터
     * @return 메일 설정 폼 화면
     */
    @Operation(summary = "메일 설정 작성/수정 폼 조회", description = "메일 설정을 작성하거나 수정하기 위한 폼을 제공합니다.")
    @GetMapping("/mailInfoForm")
    public String mailInfoForm(@RequestBody @Valid MailInfoVO mailInfoInfo, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        // 
        if (mailInfoInfo.getEmino() != null) {
            mailInfoInfo = mailService.selectMailInfoOne(mailInfoInfo);

            modelMap.addAttribute("mailInfoInfo", mailInfoInfo);
        }

        return "mail/MailInfoForm";
    }

    /**
     * 메일 설정 저장
     *
     * @param request      HttpServletRequest 객체
     * @param mailInfoInfo 메일 설정 데이터
     * @param modelMap     뷰에 전달할 데이터
     * @return 저장 후 메일 설정 리스트 화면으로 리다이렉트
     */
    @Operation(summary = "메일 설정 저장", description = "메일 서버 정보를 저장합니다.")
    @PostMapping("/mailInfoSave")
    public String mailInfoSave(HttpServletRequest request, @RequestBody @Valid MailInfoVO mailInfoInfo, ModelMap modelMap) {

        HttpSession session = request.getSession();

        if (session.getAttribute("mail") != null) {
            modelMap.addAttribute("msg", "이전에 등록한 메일 서버에서 메일을 가져오는 중입니다. \n 잠시 후 다시 등록해 주세요.");
            return "common/message";
        }

        String userno = authenticationService.getAuthenticatedUserNo();

        mailInfoInfo.setUserno(userno);

        try {
            Imap mail = new Imap();
            mail.connect(mailInfoInfo.getEmiimap(), mailInfoInfo.getEmiuser(), mailInfoInfo.getEmipw());
            mail.disconnect();
        } catch (Exception e) {
            modelMap.addAttribute("msg", "서버에 접속할 수 없습니다.");
            return "common/message";
        }

        mailService.insertMailInfo(mailInfoInfo);

        Thread t = new Thread(new ImportMail(mailService, userno, session));
        t.start();

        return "redirect:/mailInfoList";
    }

    /**
     * 메일 설정 삭제
     *
     * @param mailInfoVO 메일 설정 데이터
     * @return 삭제 후 메일 설정 리스트 화면으로 리다이렉트
     */
    @Operation(summary = "메일 설정 삭제", description = "선택한 메일 설정을 삭제합니다.")
    @PostMapping("/mailInfoDelete")
    public String mailInfoDelete(@RequestBody @Valid MailInfoVO mailInfoVO) {

        mailService.deleteMailInfo(mailInfoVO);

        return "redirect:/mailInfoList";
    }

}
