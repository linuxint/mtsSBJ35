package com.devkbil.mtssbj.board;

import com.devkbil.mtssbj.admin.board.BoardGroupService;
import com.devkbil.mtssbj.admin.board.BoardGroupVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.tree.TreeMaker;
import com.devkbil.mtssbj.common.util.FileUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.config.security.Role;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.AuthenticationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * BoardController는 게시판 관련 작업을 처리하는 API 컨트롤러입니다.
 * 게시판 리스트 조회, 게시물 작성, 저장, 읽기 등의 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "BoardController", description = "게시판 관리 API") // 컨트롤러 Swagger 태그
public class BoardController {

    private final BoardService boardService;
    private final BoardGroupService boardGroupService;
    private final EtcService etcService;
    private final AuthenticationService authenticationService;

    /**
     * 게시판 리스트 조회 및 렌더링을 처리합니다.
     * 특정 게시판 그룹이나 키워드를 기준으로 검색 결과를 제공하며,
     * 결과에 대응되는 뷰를 반환합니다.
     *
     * @param searchVO 게시판 리스트 필터링을 위한 검색 매개변수를 포함하는 객체.
     * @param modelMap 뷰 레이어에 데이터를 전달하기 위해 사용되는 ModelMap 객체.
     * @return 게시판 리스트를 렌더링하기 위한 뷰 이름을 나타내는 문자열.
     */
    @RequestMapping("/boardList")
    @Operation(summary = "게시판 리스트 조회", description = "게시판 리스트와 관련 데이터를 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시판 리스트 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardList(@RequestParam(value = "globalKeyword", required = false) String globalKeyword
            , @RequestBody @Valid BoardSearchVO searchVO, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        if (StringUtils.hasText(globalKeyword)) {
            searchVO.setSearchKeyword(globalKeyword);
        }
        etcService.setCommonAttribute(userno, modelMap);

        if (StringUtils.hasText(searchVO.getBgno())) {
            BoardGroupVO bgInfo = boardService.selectBoardGroupOne4Used(searchVO.getBgno());
            if (bgInfo == null) {
                return "board/BoardGroupFail";
            }
            modelMap.addAttribute("bgInfo", bgInfo);
        }

        List<?> noticelist = boardService.selectNoticeList(searchVO);

        searchVO.pageCalculate(boardService.selectBoardCount(searchVO)); // startRow, endRow
        List<?> listview = boardService.selectBoardList(searchVO);

        modelMap.addAttribute("searchVO", searchVO);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("noticelist", noticelist);

        if (!StringUtils.hasText(searchVO.getBgno())) {
            return "board/BoardListAll";
        }
        return "board/BoardList";
    }

    /**
     * 게시물 작성 페이지를 로드합니다.
     * 기존 게시물 데이터를 수정하거나 새 게시물을 작성할 수 있는 폼 화면을 제공합니다.
     *
     * @param modelMap ModelMap 객체로 게시판 작성 폼 뷰를 렌더링하기 위한 속성을 저장하는 데 사용됩니다.
     * @return 반환할 뷰 이름 문자열. 성공 시 "board/BoardForm"을 반환하며,
     * 게시판 그룹 정보를 사용할 수 없는 경우 "board/BoardGroupFail"을 반환합니다.
     */
    @GetMapping("/boardForm")
    @Operation(summary = "게시물 작성 폼 조회", description = "게시물 작성 화면을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 작성 폼 반환 성공"),
            @ApiResponse(responseCode = "404", description = "게시판 그룹 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardForm(@RequestParam(value = "bgno", required = false) String bgno
            , @RequestParam(value = "brdno", required = false) String brdno
            , ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        if (brdno != null) {
            BoardVO boardInfo = boardService.selectBoardOne(new ExtFieldVO(brdno, userno, null));
            List<?> listview = boardService.selectBoardFileList(brdno);

            modelMap.addAttribute("boardInfo", boardInfo);
            modelMap.addAttribute("listview", listview);
            bgno = boardInfo.getBgno();
        }
        BoardGroupVO bgInfo = boardService.selectBoardGroupOne4Used(bgno);
        if (bgInfo == null) {
            return "board/BoardGroupFail";
        }

        modelMap.addAttribute("bgno", bgno);
        modelMap.addAttribute("bgInfo", bgInfo);

        return "board/BoardForm";
    }

    /**
     * 게시물 저장 및 업데이트를 처리합니다.
     * 게시물 데이터와 업로드된 파일을 처리한 후 저장소에 저장합니다.
     *
     * @param boardInfo 게시물 정보를 포함하는 BoardVO 객체
     * @return 리디렉션 URL 또는 권한 없음 페이지를 나타내는 문자열
     */
    @PostMapping("/boardSave")
    @Operation(summary = "게시물 저장", description = "새로운 게시물을 저장하거나 기존 게시물을 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "303", description = "게시물 저장 후 리디렉션"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardSave(@RequestParam(value = "fileno", required = false) String[] fileno
            , @RequestBody @Valid BoardVO boardInfo) {

        String userno = authenticationService.getAuthenticatedUserNo();
        String userrole = authenticationService.getAuthenticatedUserrole();

        boolean isAdmin = Role.ROLE_ADMIN == Role.getRoleByValue(userrole);
        boardInfo.setUserno(userno);

        if (StringUtils.hasText(boardInfo.getBrdno())) {    // 업데이트 권한 확인
            String chk = boardService.selectBoardAuthChk(boardInfo);
            if (!StringUtils.hasText(chk) && !isAdmin) {
                return "common/noAuth";
            }
        }

        FileUtil fs = new FileUtil();
        List<FileVO> filelist = fs.saveAllFiles(boardInfo.getUploadfile());

        boardService.insertBoard(boardInfo, filelist, fileno);

        return "redirect:/boardList?bgno=" + boardInfo.getBgno();
    }


    /**
     * 특정 게시물의 상세 내역을 조회합니다.
     * 댓글, 파일 첨부, 게시판 그룹 등 관련 데이터를 함께 반환합니다.
     *
     * @param modelMap 데이터를 렌더링하기 위해 속성을 포함하는 ModelMap 객체
     * @return 렌더링할 뷰를 지정하는 문자열, 일반적으로 "board/BoardRead" 뷰
     */
    @GetMapping("/boardRead")
    @Operation(summary = "게시물 읽기", description = "특정 게시물의 상세 내역을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시물 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "게시물 정보 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardRead(@RequestParam(value = "bgno", required = false) String bgno
            , @RequestParam(value = "brdno", required = false) String brdno, ModelMap modelMap) {// 세션에서 사용자 번호 가져오기

        String userno = authenticationService.getAuthenticatedUserNo();

        etcService.setCommonAttribute(userno, modelMap);

        ExtFieldVO f3 = new ExtFieldVO(brdno, userno, null);

        boardService.updateBoardRead(f3);
        BoardVO boardInfo = boardService.selectBoardOne(f3);
        List<?> listview = boardService.selectBoardFileList(brdno);
        List<?> replylist = boardService.selectBoardReplyList(brdno);

        BoardGroupVO bgInfo = boardService.selectBoardGroupOne4Used(boardInfo.getBgno());
        if (ObjectUtils.isEmpty(bgInfo)) {
            return "board/BoardGroupFail";
        }

        modelMap.addAttribute("boardInfo", boardInfo);
        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("replylist", replylist);
        modelMap.addAttribute("bgno", bgno);
        modelMap.addAttribute("bgInfo", bgInfo);

        return "board/BoardRead";
    }

    /**
     * 게시글과 관련된 댓글을 삭제한 후 게시판 목록 페이지로 리디렉션합니다.
     *
     * @param brdno 삭제할 게시글의 게시판 번호.
     * @param bgno  게시판 목록으로 리디렉션하기 위한 그룹 번호.
     * @return 게시판 목록 페이지로의 리디렉션 URL 또는 사용자가 게시글을 삭제할 권한이 없는 경우 권한 없음 페이지.
     */
    @GetMapping("/boardDelete")
    @Operation(summary = "게시글 삭제", description = "게시글 삭제 반환.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시판 삭제 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardDelete(@RequestParam(value = "brdno") String brdno, @RequestParam(value = "bgno") String bgno) {

        String userno = authenticationService.getAuthenticatedUserNo();

        BoardVO boardInfo = new BoardVO();        // 삭제 권한 확인
        boardInfo.setBrdno(brdno);
        boardInfo.setUserno(userno);
        String chk = boardService.selectBoardAuthChk(boardInfo);
        if (!StringUtils.hasText(chk)) {
            return "common/noAuth";
        }

        {
            // 댓글 전체 삭제
            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(boardInfo, Map.class);
            boardService.deleteBoardReplyAll(map);
        }
        boardService.deleteBoardOne(brdno);

        return "redirect:/boardList?bgno=" + bgno;
    }

    /**
     * 게시판 그룹 데이터를 Ajax 방식으로 제공합니다.
     * 트리 구조로 데이터를 변환한 뒤 JSON으로 반환합니다.
     *
     * @param response 결과 데이터를 포함한 `HttpServletResponse` 객체
     */
    @PostMapping("/boardListByAjax")
    @Operation(summary = "게시판 그룹 트리", description = "게시판 그룹 트리 반환.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시판 그룹 트리 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public void boardListByAjax(HttpServletResponse response) {
        List<?> listview = boardGroupService.selectBoardGroupList();

        TreeMaker tm = new TreeMaker();
        String treeStr = tm.makeTreeByHierarchy(listview);

        response.setContentType("application/json;charset=UTF-8");
        try {
            response.getWriter().print(treeStr);
        } catch (IOException ex) {
            log.error("boardListByAjax");
        }

    }

    /*===================================================================== */

    /**
     * 게시물에 새로운 좋아요 데이터를 추가합니다.
     *
     * @param request  요청 정보를 포함하는 `HttpServletRequest` 객체
     * @param response 작업 결과를 JSON 형식으로 반환하기 위해 사용되는 객체
     */
    /**
     * 게시물에 새로운 좋아요 데이터를 추가합니다.
     *
     * @param brdno    The unique identifier of the board post to be liked. This parameter is optional.
     * @param response 작업 결과를 JSON 형식으로 반환하기 위해 사용되는 객체
     */
    @PostMapping("/addBoardLike")
    @Operation(summary = "게시글 좋아요 저장", description = "게시글 좋아요 저장 반환.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 저장 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public void addBoardLike(@RequestParam(value = "brdno", required = false) String brdno, HttpServletResponse response) {

        String userno = authenticationService.getAuthenticatedUserNo();

        boardService.insertBoardLike(new ExtFieldVO(brdno, userno, null));

        UtilEtc.responseJsonValue(response, "OK");
    }

    /*===================================================================== */

    /**
     * 게시물에 새 댓글을 추가하거나 기존 댓글을 수정합니다.
     *
     * @param response       작업 결과를 반환하기 위해 사용되는 `HttpServletResponse` 객체
     * @param boardReplyInfo 댓글 정보를 포함하는 객체
     * @param modelMap       뷰 데이터 전달에 사용되는 모델 객체
     * @return Ajax 요청 결과 또는 렌더링할 뷰 이름
     */
    @PostMapping("/boardReplySave")
    @Operation(summary = "게시글 댓글 저장", description = "게시글 댓글 저장 반환.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 댓글 저장 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public String boardReplySave(HttpServletResponse response, @RequestBody @Valid BoardReplyVO boardReplyInfo, ModelMap modelMap) {

        String userno = authenticationService.getAuthenticatedUserNo();

        boardReplyInfo.setUserno(userno);

        if (StringUtils.hasText(boardReplyInfo.getReno())) {    // check auth for update
            String chk = boardService.selectBoardReplyAuthChk(boardReplyInfo);
            if (!StringUtils.hasText(chk)) {
                UtilEtc.responseJsonValue(response, "");
                return null;
            }
        }

        boardReplyInfo = boardService.insertBoardReply(boardReplyInfo);
        //boardReplyInfo.setRewriter(request.getSession().getAttribute("usernm").toString());

        modelMap.addAttribute("replyInfo", boardReplyInfo);

        return "board/BoardReadAjax4Reply";
    }

    /**
     * 게시물에 작성된 댓글을 삭제합니다.
     *
     * @param response       결과를 JSON 형식으로 반환하기 위해 사용하는 `HttpServletResponse` 객체
     * @param boardReplyInfo 삭제할 댓글 데이터를 포함한 객체
     */
    @PostMapping("/boardReplyDelete")
    @Operation(summary = "게시글 댓글 삭제", description = "게시글 댓글 삭제 반환.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 댓글 삭제 반환 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 파라미터"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public void boardReplyDelete(HttpServletResponse response, @RequestBody @Valid BoardReplyVO boardReplyInfo) {

        String userno = authenticationService.getAuthenticatedUserNo();

        boardReplyInfo.setUserno(userno);

        if (StringUtils.hasText(boardReplyInfo.getReno())) {    // check auth for update
            String chk = boardService.selectBoardReplyAuthChk(boardReplyInfo);
            if (!StringUtils.hasText(chk)) {
                UtilEtc.responseJsonValue(response, "FailAuth");
                return;
            }
        }

        if (!boardService.deleteBoardReply(boardReplyInfo)) {
            UtilEtc.responseJsonValue(response, "Fail");
        } else {
            UtilEtc.responseJsonValue(response, "OK");
        }
    }

}
