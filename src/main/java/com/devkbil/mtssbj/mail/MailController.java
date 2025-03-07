package com.devkbil.mtssbj.mail;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Mail", description = "메일 송수신 관리 API") // Controller의 Swagger Tag
public class MailController {

    private final MailService mailService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 수신 메일 리스트 조회
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 뷰에 전달할 데이터
     * @return 수신 메일 리스트 화면
     */
    @Operation(summary = "수신 메일 리스트 조회", description = "사용자의 수신 메일 리스트를 조회합니다.")
    @GetMapping("/receiveMails")
    public String receiveMails(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return "mail/MailInfoGuide";
        }

        // 페이지 공통: alert
        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        //
        searchVO.setSearchExt1("R");
        searchVO.pageCalculate(mailService.selectReceiveMailCount(searchVO)); // startRow, endRow
        List<?> listview = mailService.selectReceiveMailList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "mail/ReceiveMails";
    }

    /**
     * 발신 메일 리스트 조회
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 뷰에 전달할 데이터
     * @return 발신 메일 리스트 화면
     */
    @Operation(summary = "발신 메일 리스트 조회", description = "사용자의 발신 메일 리스트를 조회합니다.")
    @GetMapping("/sendMails")
    public String sendMails(@ModelAttribute @Valid SearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return "mail/MailInfoGuide";
        }

        // 페이지 공통: alert
        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        //
        searchVO.setSearchExt1("S");
        searchVO.pageCalculate(mailService.selectReceiveMailCount(searchVO)); // startRow, endRow
        List<?> listview = mailService.selectReceiveMailList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "mail/SendMails";
    }

    /**
     * 메일 작성 화면 또는 수정 화면 조회
     *
     * @param mailInfo 메일 정보 객체
     * @param modelMap 뷰에 전달할 데이터
     * @return 메일 작성 및 수정 화면
     */
    @Operation(summary = "메일 작성 폼", description = "메일 작성 또는 수정 화면을 반환합니다.")
    @GetMapping("/mailForm")
    public String mailForm(@ModelAttribute @Valid MailVO mailInfo, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return "mail/MailInfoGuide";
        }

        // 페이지 공통: alert
        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        //
        modelMap.addAttribute("mailInfoList", mailInfoList);

        if (mailInfo.getEmno() != null) {
            mailInfo = mailService.selectReceiveMailOne(mailInfo);

            modelMap.addAttribute("mailInfo", mailInfo);
        }

        return "mail/MailForm";
    }

    /**
     * 메일 저장
     *
     * @param mailInfo 저장할 메일 정보
     * @return 발신 메일 리스트 화면으로 리다이렉트
     */
    @Operation(summary = "메일 저장", description = "사용자가 작성한 메일 데이터를 저장합니다.")
    @PostMapping("/mailSave")
    public String mailSave(@ModelAttribute @Valid MailVO mailInfo) {

        String userno = authService.getAuthUserNo();

        mailInfo.setUserno(userno);
        mailInfo.setEmtype("S");

        mailService.insertMail(mailInfo);

        return "redirect:/sendMails";
    }

    /**
     * 수신 메일 상세 조회
     *
     * @param mailVO   조회할 메일 정보
     * @param modelMap 뷰에 전달할 데이터
     * @return 수신 메일 상세 화면
     */
    @Operation(summary = "수신 메일 상세 조회", description = "특정 수신 메일의 상세 정보를 조회합니다.")
    @GetMapping("/receiveMailRead")
    public String receiveMailRead(@ModelAttribute @Valid MailVO mailVO, ModelMap modelMap) {

        mailRead(mailVO, modelMap);

        return "mail/ReceiveMailRead";
    }

    /**
     * 발신 메일 상세 조회
     *
     * @param mailVO   조회할 메일 정보
     * @param modelMap 뷰에 전달할 데이터
     * @return 발신 메일 상세 화면
     */
    @Operation(summary = "발신 메일 상세 조회", description = "특정 발신 메일의 상세 정보를 조회합니다.")
    @GetMapping("/sendMailRead")
    public String sendMailRead(@ModelAttribute @Valid MailVO mailVO, ModelMap modelMap) {

        mailRead(mailVO, modelMap);

        return "mail/SendMailRead";
    }

    /**
     * 메일 상세 조회 공통 처리 로직
     *
     * @param mailVO   메일 정보 객체
     * @param modelMap 뷰에 전달할 데이터
     */
    private void mailRead(@ModelAttribute @Valid MailVO mailVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        //
        MailVO mailInfo = mailService.selectReceiveMailOne(mailVO);

        modelMap.addAttribute("mailInfo", mailInfo);
    }

    /**
     * 단일 수신 메일 삭제
     *
     * @param mailVO 삭제할 메일 정보
     * @return 수신 메일 리스트 화면으로 리다이렉트
     */
    @Operation(summary = "수신 메일 삭제", description = "단일 수신 메일을 삭제합니다.")
    @PostMapping("/receiveMailDelete")
    public String receiveMailDelete(@ModelAttribute @Valid MailVO mailVO) {

        mailService.deleteMail(mailVO);

        return "redirect:/receiveMails";
    }

    /**
     * 다중 수신 메일 삭제
     *
     * @param checkRow 삭제할 메일 ID 배열
     * @return 수신 메일 리스트 화면으로 리다이렉트
     */
    @Operation(summary = "다중 수신 메일 삭제", description = "여러 수신 메일을 동시에 삭제합니다.")
    @PostMapping("/receiveMailsDelete")
    public String receiveMailsDelete(@RequestParam(value = "checkRow") String[] checkRow) {

        mailService.deleteMails(checkRow);

        return "redirect:/receiveMails";
    }

    /**
     * 외부 서버로부터 메일 가져오기
     *
     * @param request  HttpServletRequest 객체
     * @param modelMap 뷰에 전달할 데이터
     * @return 수신 메일 리스트 화면
     */
    @Operation(summary = "메일 가져오기 작업", description = "외부 서버에서 메일을 가져오는 작업을 수행합니다.")
    @PostMapping("/getReceiveMail")
    public String importMail(HttpServletRequest request, ModelMap modelMap) {
        HttpSession session = request.getSession();

        if (session.getAttribute("mail") != null) {
            return "mail/MailInfoGuide";
        }

        session.setAttribute("mail", "ing"); // 작업 진행 상태 설정

        String userno = authService.getAuthUserNo();

        Thread thread = new Thread(new ImportMail(mailService, userno, session));
        thread.start();

        return "mail/ReceiveMails";
    }

}
