package com.devkbil.mtssbj.api.v1.mail;

import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.mail.ImportMail;
import com.devkbil.mtssbj.mail.MailService;
import com.devkbil.mtssbj.mail.MailVO;
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
 * 메일 관리를 담당하는 REST API 컨트롤러.
 * 메일과 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/mail")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Mail API", description = "메일 송수신 관리 API")
public class MailRestController {

    private final MailService mailService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 수신 메일 리스트 조회 API.
     *
     * @param searchVO 검색 조건 객체
     * @return 수신 메일 리스트 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "수신 메일 리스트 조회", description = "사용자의 수신 메일 리스트를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "수신 메일 리스트 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메일 정보가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/receive")
    public ResponseEntity<Map<String, Object>> getReceiveMails(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "메일 정보가 없습니다."));
        }

        Integer alertcount = etcService.selectAlertCount(userno);

        searchVO.setSearchExt1("R");
        searchVO.pageCalculate(mailService.selectReceiveMailCount(searchVO)); // startRow, endRow
        List<?> listview = mailService.selectReceiveMailList(searchVO);

        result.put("alertcount", alertcount);
        result.put("searchVO", searchVO);
        result.put("listview", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 발신 메일 리스트 조회 API.
     *
     * @param searchVO 검색 조건 객체
     * @return 발신 메일 리스트 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "발신 메일 리스트 조회", description = "사용자의 발신 메일 리스트를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "발신 메일 리스트 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메일 정보가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/send")
    public ResponseEntity<Map<String, Object>> getSendMails(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "메일 정보가 없습니다."));
        }

        Integer alertcount = etcService.selectAlertCount(userno);

        searchVO.setSearchExt1("S");
        searchVO.pageCalculate(mailService.selectReceiveMailCount(searchVO)); // startRow, endRow
        List<?> listview = mailService.selectReceiveMailList(searchVO);

        result.put("alertcount", alertcount);
        result.put("searchVO", searchVO);
        result.put("listview", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 메일 정보 조회 API.
     *
     * @param mailInfo 메일 정보 객체
     * @return 메일 정보 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "메일 정보 조회", description = "메일 작성 또는 수정을 위한 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 정보 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메일 정보가 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/form")
    public ResponseEntity<Map<String, Object>> getMailForm(@ModelAttribute @Valid MailVO mailInfo) {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();

        List<?> mailInfoList = mailService.selectMailInfoList(userno);
        if (mailInfoList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "메일 정보가 없습니다."));
        }

        Integer alertcount = etcService.selectAlertCount(userno);

        result.put("alertcount", alertcount);
        result.put("mailInfoList", mailInfoList);

        if (mailInfo.getEmno() != null) {
            mailInfo = mailService.selectReceiveMailOne(mailInfo);
            result.put("mailInfo", mailInfo);
        }

        return ResponseEntity.ok(result);
    }

    /**
     * 메일 저장 API.
     *
     * @param mailInfo 저장할 메일 정보
     * @return 저장된 메일 정보를 담은 ResponseEntity
     */
    @Operation(summary = "메일 저장", description = "사용자가 작성한 메일 데이터를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<MailVO> saveMail(@RequestBody @Valid MailVO mailInfo) {
        String userno = authService.getAuthUserNo();

        mailInfo.setUserno(userno);
        mailInfo.setEmtype("S");

        mailService.insertMail(mailInfo);

        return ResponseEntity.ok(mailInfo);
    }

    /**
     * 메일 상세 조회 API.
     *
     * @param emno 조회할 메일 ID
     * @return 메일 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "메일 상세 조회", description = "특정 메일의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메일 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "메일을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{emno}")
    public ResponseEntity<Map<String, Object>> getMail(@PathVariable String emno) {
        Map<String, Object> result = new HashMap<>();

        String userno = authService.getAuthUserNo();

        Integer alertcount = etcService.selectAlertCount(userno);
        result.put("alertcount", alertcount);

        MailVO mailVO = new MailVO();
        mailVO.setEmno(emno);

        MailVO mailInfo = mailService.selectReceiveMailOne(mailVO);
        if (mailInfo == null) {
            return ResponseEntity.notFound().build();
        }

        result.put("mailInfo", mailInfo);

        return ResponseEntity.ok(result);
    }

    /**
     * 메일 삭제 API.
     *
     * @param emno 삭제할 메일 ID
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "메일 삭제", description = "특정 메일을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "메일 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{emno}")
    public ResponseEntity<Void> deleteMail(@PathVariable String emno) {
        MailVO mailVO = new MailVO();
        mailVO.setEmno(emno);

        mailService.deleteMail(mailVO);

        return ResponseEntity.noContent().build();
    }

    /**
     * 다중 메일 삭제 API.
     *
     * @param emnoList 삭제할 메일 ID 목록
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "다중 메일 삭제", description = "여러 메일을 동시에 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "메일 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteMails(@RequestBody List<String> emnoList) {
        String[] checkRow = emnoList.toArray(new String[0]);
        mailService.deleteMails(checkRow);

        return ResponseEntity.noContent().build();
    }

    /**
     * 외부 서버로부터 메일 가져오기 API.
     *
     * @param request HttpServletRequest 객체
     * @return 작업 상태를 담은 ResponseEntity
     */
    @Operation(summary = "메일 가져오기 작업", description = "외부 서버에서 메일을 가져오는 작업을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "202", description = "메일 가져오기 작업 시작됨"),
        @ApiResponse(responseCode = "409", description = "이미 작업이 진행 중"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/import")
    public ResponseEntity<Map<String, String>> importMail(HttpServletRequest request) {
        HttpSession session = request.getSession();

        if (session.getAttribute("mail") != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "이미 메일 가져오기 작업이 진행 중입니다."));
        }

        session.setAttribute("mail", "ing");

        String userno = authService.getAuthUserNo();

        Thread thread = new Thread(new ImportMail(mailService, userno, session));
        thread.start();

        return ResponseEntity.status(HttpStatus.ACCEPTED)
            .body(Map.of("message", "메일 가져오기 작업이 시작되었습니다."));
    }
}
