package com.devkbil.mtssbj.api.v1.mail;

import com.devkbil.mtssbj.mail.Imap;
import com.devkbil.mtssbj.mail.ImportMail;
import com.devkbil.mtssbj.mail.MailInfoVO;
import com.devkbil.mtssbj.mail.MailService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메일 설정 관리를 담당하는 REST API 컨트롤러.
 * 메일 설정과 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/mail/info")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Mail Info API", description = "메일 설정 관리 API")
public class MailInfoRestController {

    private final MailService mailService;
    private final AuthService authService;

    /**
     * 메일 설정 리스트 조회 API.
     *
     * @param searchVO 검색 조건 객체
     * @return 메일 설정 리스트 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "메일 설정 리스트 조회", description = "사용자 메일 설정 리스트를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 설정 리스트 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getMailInfoList(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();

        List<?> listview = mailService.selectMailInfoList(userno);

        result.put("searchVO", searchVO);
        result.put("listview", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 메일 설정 상세 조회 API.
     *
     * @param emino 조회할 메일 설정 ID
     * @return 메일 설정 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "메일 설정 상세 조회", description = "특정 메일 설정의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 설정 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메일 설정을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{emino}")
    public ResponseEntity<MailInfoVO> getMailInfo(@PathVariable String emino) {
        MailInfoVO mailInfoVO = new MailInfoVO();
        mailInfoVO.setEmino(emino);
        
        MailInfoVO mailInfoInfo = mailService.selectMailInfoOne(mailInfoVO);
        if (mailInfoInfo == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(mailInfoInfo);
    }

    /**
     * 메일 설정 저장 API.
     *
     * @param mailInfoInfo 저장할 메일 설정 정보
     * @param request HttpServletRequest 객체
     * @return 저장된 메일 설정 정보 또는 오류 메시지를 담은 ResponseEntity
     */
    @Operation(summary = "메일 설정 저장", description = "메일 서버 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 설정 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 또는 서버 접속 실패"),
        @ApiResponse(responseCode = "409", description = "이미 메일 가져오기 작업이 진행 중"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<?> saveMailInfo(@RequestBody @Valid MailInfoVO mailInfoInfo, HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("mail") != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "이전에 등록한 메일 서버에서 메일을 가져오는 중입니다. 잠시 후 다시 등록해 주세요."));
        }

        String userno = authService.getAuthUserNo();
        mailInfoInfo.setUserno(userno);

        try {
            Imap mail = new Imap();
            mail.connect(mailInfoInfo.getEmiimap(), mailInfoInfo.getEmiuser(), mailInfoInfo.getEmipw());
            mail.disconnect();
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "서버에 접속할 수 없습니다."));
        }

        mailService.insertMailInfo(mailInfoInfo);

        Thread thread = new Thread(new ImportMail(mailService, userno, session));
        thread.start();

        return ResponseEntity.ok(mailInfoInfo);
    }

    /**
     * 메일 설정 삭제 API.
     *
     * @param emino 삭제할 메일 설정 ID
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "메일 설정 삭제", description = "선택한 메일 설정을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "메일 설정 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{emino}")
    public ResponseEntity<Void> deleteMailInfo(@PathVariable String emino) {
        MailInfoVO mailInfoVO = new MailInfoVO();
        mailInfoVO.setEmino(emino);
        
        mailService.deleteMailInfo(mailInfoVO);

        return ResponseEntity.noContent().build();
    }
}