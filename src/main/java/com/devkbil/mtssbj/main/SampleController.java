package com.devkbil.mtssbj.main;

import com.devkbil.common.util.DateUtil;
import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.board.BoardService;
import com.devkbil.mtssbj.common.ExcelConstant;
import com.devkbil.mtssbj.common.MakeExcel;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@Tag(name = "SampleController", description = "샘플 페이지와 관련된 컨트롤러")
public class SampleController {

    private final SampleService sampleService;
    private final EtcService etcService;
    private final BoardService boardService;
    private final AuthService authService;

    /**
     * 조직도/사용자 선택 샘플 페이지를 반환합니다.
     *
     * @param modelMap 모델 맵 객체
     * @return 조직도/사용자 선택 샘플 페이지(html 파일 이름)
     */
    @Operation(summary = "조직도/사용자 선택 샘플", description = "조직도 및 사용자 선택 UI를 테스트합니다.")
    @GetMapping("/sample1")
    public String sample1(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        return "main/sample1";
    }

    /**
     * 날짜 선택 샘플 페이지를 반환합니다.
     *
     * @param modelMap 모델 맵 객체
     * @return 날짜 선택 샘플 페이지(html 파일 이름)
     */
    @Operation(summary = "날짜 선택 샘플", description = "날짜 선택 UI를 테스트합니다.")
    @GetMapping("/sample2")
    public String sample2(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);
        // -----------------------------------------

        String today = DateUtil.date2Str(DateUtil.getToday());

        modelMap.addAttribute("today", today);
        return "main/sample2";
    }

    /**
     * 차트 사용 샘플 페이지를 반환합니다.
     *
     * @param modelMap 모델 맵 객체
     * @return 차트 사용 샘플 페이지(html 파일 이름)
     */
    @Operation(summary = "차트 표시 샘플", description = "차트를 테스트하고 데이터를 확인합니다.")
    @GetMapping("/sample3")
    public String sample3(ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);
        // -----------------------------------------

        List<?> listview = sampleService.selectBoardGroupCount4Statistic();
        modelMap.addAttribute("listview", listview);

        return "main/sample3";
    }

    /**
     * 목록  엑셀 사용 샘플 페이지를 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 모델 맵 객체
     * @return 목록  엑셀 사용 샘플 페이지(html 파일 이름)
     */
    @Operation(summary = "목록 & 엑셀 샘플", description = "목록 표시와 엑셀 다운로드 기능을 테스트합니다.")
    @GetMapping("/sample4")
    public String sample4(@ModelAttribute @Valid BoardSearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        etcService.setCommonAttribute(userno, modelMap);
        // -----------------------------------------

        searchVO.pageCalculate(boardService.selectBoardCount(searchVO)); // startRow, endRow
        List<?> listview = boardService.selectBoardList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);

        return "main/sample4";
    }

    /**
     * 엑셀 파일을 생성하고 다운로드합니다.
     *
     * @param request  HttpServletRequest 객체 (사용자 세션 정보를 가져오기 위해 사용)
     * @param response HttpServletResponse 객체 (엑셀 파일 다운로드를 위한 응답 객체)
     * @param searchVO 검색 조건 객체
     */
    @Operation(summary = "엑셀 다운로드 샘플", description = "목록 데이터를 Excel로 다운로드합니다.")
    @PostMapping("/sample4Excel")
    public void sample4Excel(HttpServletRequest request, HttpServletResponse response, @ModelAttribute @Valid BoardSearchVO searchVO) {

        // 엑셀 출력 헤더 정의
        String[] cellHeader = {"No", "그룹no", "그룹명", "글No", "글제목", "작성자명", "내용", "작성일",
            "작성시간", "조회수", "삭제여부", "파일수", "댓글수", "사용자번호",
            "사용자명", "공지여부", "좋아요"
        };

        // 게시판은 페이징 처리를 하지만 엑셀은 모든 데이터를 다운로드
        List<?> listview = boardService.selectBoardList(searchVO);
        Map<String, Object> beans = new HashMap<String, Object>();

        // 엑셀 데이터 설정
        beans.put(ExcelConstant.DATA_KEY_NAME, listview);
        beans.put(ExcelConstant.SHEET_KEY_NAME, "data");
        beans.put(ExcelConstant.HEADER_KEY_NAME, cellHeader);

        MakeExcel me = new MakeExcel();
        me.download(request, response, beans, me.get_Filename("mts"), "board2.xlsx"); // formatData download

    }
}