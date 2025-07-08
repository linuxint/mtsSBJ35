package com.devkbil.mtssbj.api.v1.board;

import com.devkbil.mtssbj.board.BoardService;
import com.devkbil.mtssbj.board.BoardVO;
import com.devkbil.mtssbj.board.BoardReplyVO;
import com.devkbil.mtssbj.board.BoardSearchVO;
import com.devkbil.mtssbj.admin.board.BoardGroupVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.FileVO;
import com.devkbil.mtssbj.member.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 관리를 담당하는 REST API 컨트롤러.
 * 게시판과 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/board")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Board API", description = "게시판 관리 API")
public class BoardRestController {

    private final BoardService boardService;
    private final AuthService authService;

    /**
     * 게시판 목록 조회 API.
     *
     * @param globalKeyword 전역 검색 키워드 (선택적)
     * @param searchVO 검색 조건을 담은 객체
     * @return 게시판 목록 및 관련 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시판 목록 조회", description = "검색 조건을 기반으로 게시판 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시판 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBoardList(
            @RequestParam(value = "globalKeyword", required = false) String globalKeyword,
            @ModelAttribute @Valid BoardSearchVO searchVO) {

        Map<String, Object> result = new HashMap<>();

        if (StringUtils.hasText(globalKeyword)) {
            searchVO.setSearchKeyword(globalKeyword);
        }

        if (StringUtils.hasText(searchVO.getBgno())) {
            BoardGroupVO bgInfo = boardService.selectBoardGroupOne4Used(searchVO.getBgno());
            if (bgInfo == null) {
                return ResponseEntity.notFound().build();
            }
            result.put("bgInfo", bgInfo);
        }

        List<?> noticelist = boardService.selectNoticeList(searchVO);

        searchVO.pageCalculate(boardService.selectBoardCount(searchVO)); // startRow, endRow
        List<?> listview = boardService.selectBoardList(searchVO);

        result.put("searchVO", searchVO);
        result.put("listview", listview);
        result.put("noticelist", noticelist);

        return ResponseEntity.ok(result);
    }

    /**
     * 게시물 상세 조회 API.
     *
     * @param brdno 조회할 게시물 번호
     * @return 게시물 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 상세 조회", description = "게시물 번호로 게시물 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시물 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시물을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{brdno}")
    public ResponseEntity<Map<String, Object>> getBoard(@PathVariable String brdno) {
        String userno = authService.getAuthUserNo();

        BoardVO boardInfo = boardService.selectBoardOne(new ExtFieldVO(brdno, userno, null));
        if (boardInfo == null || boardInfo.getBrdno() == null) {
            return ResponseEntity.notFound().build();
        }

        List<?> fileList = boardService.selectBoardFileList(brdno);
        List<?> replyList = boardService.selectBoardReplyList(brdno);

        Map<String, Object> result = new HashMap<>();
        result.put("boardInfo", boardInfo);
        result.put("fileList", fileList);
        result.put("replyList", replyList);

        return ResponseEntity.ok(result);
    }

    /**
     * 게시물 저장 API.
     *
     * @param boardInfo 저장하려는 게시물 정보
     * @param fileno 파일 번호 배열 (선택적)
     * @return 저장된 게시물 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 저장", description = "신규 또는 수정된 게시물 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시물 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "게시판 그룹을 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<BoardVO> saveBoard(
            @RequestBody @Valid BoardVO boardInfo,
            @RequestParam(value = "fileno", required = false) String[] fileno) {

        String userno = authService.getAuthUserNo();
        boardInfo.setUserno(userno);

        if (!StringUtils.hasText(boardInfo.getBgno())) {
            return ResponseEntity.badRequest().build();
        }

        BoardGroupVO bgInfo = boardService.selectBoardGroupOne4Used(boardInfo.getBgno());
        if (bgInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 파일 목록은 REST API에서는 별도로 처리하거나 추후 구현
        List<FileVO> filelist = new ArrayList<>();

        // insertBoard 메서드는 신규 및 수정 모두 처리함
        boardService.insertBoard(boardInfo, filelist, fileno);

        // 저장 후 최신 정보 조회
        BoardVO savedBoard = boardService.selectBoardOne(new ExtFieldVO(boardInfo.getBrdno(), userno, null));
        return ResponseEntity.ok(savedBoard);
    }

    /**
     * 게시물 삭제 API.
     *
     * @param brdno 삭제할 게시물의 고유 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 삭제", description = "지정된 게시물을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "게시물 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{brdno}")
    public ResponseEntity<Void> deleteBoard(@PathVariable String brdno) {
        if (!StringUtils.hasText(brdno)) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();

        BoardVO boardInfo = new BoardVO();
        boardInfo.setBrdno(brdno);
        boardInfo.setUserno(userno);

        String chk = boardService.selectBoardAuthChk(boardInfo);
        if (!StringUtils.hasText(chk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boardService.deleteBoardOne(brdno);

        return ResponseEntity.noContent().build();
    }

    /**
     * 게시물 좋아요 추가 API.
     *
     * @param brdno 좋아요를 추가할 게시물 번호
     * @return 업데이트된 좋아요 수를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 좋아요 추가", description = "게시물에 좋아요를 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좋아요 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/{brdno}/like")
    public ResponseEntity<Map<String, Object>> addBoardLike(@PathVariable String brdno) {
        if (!StringUtils.hasText(brdno)) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();

        ExtFieldVO param = new ExtFieldVO(brdno, userno, null);
        boardService.insertBoardLike(param);

        // 좋아요 수를 반환하기 위한 로직 추가 필요
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "좋아요가 추가되었습니다.");

        return ResponseEntity.ok(result);
    }

    /**
     * 게시물 댓글 저장 API.
     *
     * @param boardReplyInfo 저장하려는 댓글 정보
     * @return 저장된 댓글 정보를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 댓글 저장", description = "게시물에 댓글을 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/reply")
    public ResponseEntity<BoardReplyVO> saveBoardReply(@RequestBody @Valid BoardReplyVO boardReplyInfo) {
        if (!StringUtils.hasText(boardReplyInfo.getBrdno())) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();
        boardReplyInfo.setUserno(userno);

        BoardReplyVO savedReply = boardService.insertBoardReply(boardReplyInfo);

        return ResponseEntity.ok(savedReply);
    }

    /**
     * 게시물 댓글 삭제 API.
     *
     * @param reno 삭제할 댓글 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "게시물 댓글 삭제", description = "게시물의 댓글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/reply/{reno}")
    public ResponseEntity<Void> deleteBoardReply(@PathVariable String reno) {
        if (!StringUtils.hasText(reno)) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();

        BoardReplyVO boardReplyInfo = new BoardReplyVO();
        boardReplyInfo.setReno(reno);
        boardReplyInfo.setUserno(userno);

        String chk = boardService.selectBoardReplyAuthChk(boardReplyInfo);
        if (!StringUtils.hasText(chk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean result = boardService.deleteBoardReply(boardReplyInfo);

        if (result) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
