package com.devkbil.mtssbj.etc;

import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * 사용자와 관련된 리스트, 특히 Alert를 처리하는 컨트롤러 클래스입니다.
 * 현재 사용자와 관련된 Alert 리스트를 조회하고 페이징 처리된 데이터를 제공합니다.
 */
@Controller
@RequiredArgsConstructor
@Tag(name = "List4User", description = "사용자 관련 리스트 API")
public class List4User {

    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 사용자의 Alert 리스트를 페이징 처리하여 반환합니다.
     *
     * @param searchVO 검색 조건 객체
     * @param modelMap 모델맵 객체
     * @return Alert 리스트 뷰 경로
     */
    @Operation(summary = "사용자의 Alert 리스트", description = "현재 사용자와 관련된 Alert 리스트를 페이징 처리하여 반환합니다.")
    @GetMapping("/list4User")
    public String list4User(BoardSearchVO searchVO, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        searchVO.setSearchExt1(userno);

        searchVO.pageCalculate(etcService.selectList4UserCount(searchVO)); // startRow, endRow

        List<?> listview = etcService.selectList4User(searchVO);

        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("searchVO", searchVO);

        return "etc/list4User";
    }

}
