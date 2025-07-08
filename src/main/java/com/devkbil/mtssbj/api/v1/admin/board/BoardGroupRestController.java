package com.devkbil.mtssbj.api.v1.admin.board;

import com.devkbil.mtssbj.admin.board.BoardGroupService;
import com.devkbil.mtssbj.admin.board.BoardGroupVO;
import com.devkbil.mtssbj.common.TreeMaker;
import com.devkbil.mtssbj.config.security.AdminAuthorize;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 그룹 관리 REST API 컨트롤러
 * - 게시판 그룹 데이터의 조회, 저장, 읽기, 삭제 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/admin/board-group")
@Slf4j
@AdminAuthorize
@RequiredArgsConstructor
@Tag(name = "Board Group API", description = "게시판 그룹 관리 API")
public class BoardGroupRestController {

    private final BoardGroupService boardGroupService;

    /**
     * 모든 게시판 그룹의 리스트를 조회합니다.
     *
     * @return 게시판 그룹 리스트와 트리 구조를 담은 ResponseEntity
     */
    @Operation(summary = "게시판 그룹 리스트 조회", description = "모든 게시판 그룹의 계층 트리를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBoardGroupList() {
        Map<String, Object> result = new HashMap<>();

        List<?> listview = boardGroupService.selectBoardGroupList();

        TreeMaker tm = new TreeMaker();
        String treeStr = tm.makeTreeByHierarchy(listview);

        result.put("list", listview);
        result.put("treeStr", treeStr);

        return ResponseEntity.ok(result);
    }

    /**
     * 게시판 그룹 데이터를 저장하거나 업데이트합니다.
     *
     * @param bgInfo 저장 또는 업데이트할 게시판 그룹 데이터를 포함하는 객체
     * @return 저장된 게시판 그룹 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시판 그룹 저장", description = "새로운 게시판 그룹을 저장하거나 기존 그룹을 업데이트합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @PostMapping
    public ResponseEntity<Object> saveBoardGroup(@RequestBody BoardGroupVO bgInfo) {
        int affectedRows = boardGroupService.insertBoard(bgInfo);

        if (affectedRows > 0) {
            return ResponseEntity.ok(bgInfo);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("저장에 실패했습니다.");
        }
    }

    /**
     * 특정 게시판 그룹의 상세 정보를 조회합니다.
     *
     * @param bgno 읽을 게시판 그룹을 식별하는 bgno
     * @return 게시판 그룹 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시판 그룹 상세 조회", description = "게시판 그룹 번호(bgno)에 해당하는 정보를 읽어옵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청 성공"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @GetMapping("/{bgno}")
    public ResponseEntity<BoardGroupVO> getBoardGroup(@PathVariable String bgno) {
        if (!StringUtils.hasText(bgno)) {
            return ResponseEntity.badRequest().build();
        }

        BoardGroupVO bgInfo = boardGroupService.selectBoardGroupOne(bgno);
        
        if (bgInfo == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(bgInfo);
    }

    /**
     * 특정 게시판 그룹 데이터를 삭제합니다.
     *
     * @param bgno 삭제할 게시판 그룹을 식별하는 bgno
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "게시판 그룹 삭제", description = "게시판 그룹 번호(bgno)에 해당하는 정보를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생했습니다.")
    })
    @DeleteMapping("/{bgno}")
    public ResponseEntity<Void> deleteBoardGroup(@PathVariable String bgno) {
        if (!StringUtils.hasText(bgno)) {
            return ResponseEntity.badRequest().build();
        }

        int affectedRows = boardGroupService.deleteBoardGroup(bgno);
        
        if (affectedRows > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}