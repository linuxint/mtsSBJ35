package com.devkbil.mtssbj.api.v1.menu;

import com.devkbil.mtssbj.admin.menu.MenuService;
import com.devkbil.mtssbj.admin.menu.MenuVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 메뉴 데이터를 제공하는 REST 컨트롤러.
 * 프론트엔드 애플리케이션에서 사용할 메뉴 데이터를 조회하는 API를 제공합니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/menu")
@RequiredArgsConstructor
@Tag(name = "MenuRestController", description = "메뉴 데이터 API")
public class MenuRestController {

    private final MenuService menuService;

    /**
     * 메뉴 목록 조회
     * 프론트엔드 네비게이션에서 사용할 메뉴 데이터를 계층 구조로 반환합니다.
     *
     * @return 메뉴 목록을 포함하는 ResponseEntity
     */
    @GetMapping("/list")
    @Operation(summary = "메뉴 목록 조회", description = "프론트엔드 네비게이션에서 사용할 메뉴 데이터를 계층 구조로 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "메뉴 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 내부 오류 발생")
    })
    public ResponseEntity<List<?>> getMenuList() {
        try {
            log.debug("메뉴 리스트 조회 요청");
            
            // 메뉴 리스트 조회
            List<?> menuList = menuService.selectMenu();
            
            // 삭제된 메뉴 필터링 (deleteflag가 'N'인 메뉴만 포함)
            List<?> filteredMenuList = menuList.stream()
                .filter(menu -> {
                    if (menu instanceof MenuVO) {
                        MenuVO menuVO = (MenuVO) menu;
                        return "N".equals(menuVO.getDeleteflag());
                    }
                    return true;
                })
                .collect(Collectors.toList());
            
            // 200 상태와 함께 메뉴 리스트 반환
            return ResponseEntity.ok(filteredMenuList);
        } catch (Exception e) {
            log.error("API 호출 중 오류 발생: 메뉴 리스트 조회 실패", e);
            
            // 500 상태 반환 (내부 서버 오류)
            return ResponseEntity.status(org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}