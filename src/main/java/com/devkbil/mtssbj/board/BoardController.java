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
import com.devkbil.mtssbj.member.auth.AuthService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * BoardController는 게시판 관련 작업을 처리하는 API 컨트롤러입니다.
 * 게시판 목록 보기, 게시물 작성, 저장, 읽기 및 기타 게시판 작업을 위한 기능을 제공합니다.
 */
@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "BoardController", description = "게시판 관리 API") // Controller Swagger 태그
public class BoardController {

    private final BoardService boardService;
    private final BoardGroupService boardGroupService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 게시판 리스트 조회 및 렌더링을 처리합니다.
     * 특정 게시판 그룹이나 키워드를 기준으로 검색 결과를 제공하며,
     * 결과에 대응되는 뷰를 반환합니다.
     *
     * @param globalKeyword 게시판 리스트 필터링에 전역으로 적용되는 검색 키워드, 선택적 매개변수.
     * @param searchVO 게시판 리스트에 대한 검색 기준(페이징 세부 정보를 포함)을 담고 있는 모델 객체. 유효해야 합니다.
     * @param modelMap 뷰 렌더링에 필요한 속성을 보유하기 위한 모델 맵.
     * @return 렌더링할 뷰의 이름을 나타내는 문자열. 특정 게시판 그룹이 선택되지 않으면 "board/BoardListAll"을 반환,
     *         특정 게시판 그룹이 선택되면 "board/BoardList"를 반환, 게시판 그룹 정보가 유효하지 않으면 "board/BoardGroupFail"을 반환합니다.
     */
    @RequestMapping("/boardList")
    @Operation(summary = "Get Board List", description = "Returns the board list and related data.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved board list"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardList(@RequestParam(value = "globalKeyword", required = false) String globalKeyword,
                            @ModelAttribute @Valid BoardSearchVO searchVO,
                            ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

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
     * 게시판 양식 뷰를 가져옵니다. 제공된 경우 게시판 그룹 번호(bgno)와 게시물 번호(brdno)에 따라
     * 필요한 속성을 모델에 구성한 다음, 게시물 작성 또는 편집 양식을 렌더링할 뷰 이름을 반환합니다.
     *
     * @param bgno 게시판 그룹을 식별하는 데 사용하는 게시판 그룹 번호 (선택적).
     * @param brdno 기존 게시물의 세부 정보를 검색하는 데 사용하는 게시물 번호 (선택적).
     * @param modelMap 뷰 렌더링에 필요한 속성이 추가되는 모델 맵.
     * @return 게시물 작성 또는 편집 양식을 렌더링하기 위한 뷰 이름.
     *         게시판 그룹 정보가 없으면 실패 뷰 이름("board/BoardGroupFail")을 반환합니다.
     */
    @GetMapping("/boardForm")
    @Operation(summary = "Get Post Creation Form", description = "Returns the form for creating a new post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved post creation form"),
        @ApiResponse(responseCode = "404", description = "Board group information not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardForm(@RequestParam(value = "bgno", required = false) String bgno,
                            @RequestParam(value = "brdno", required = false) String brdno,
                            ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

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
     * 새 게시물 저장 또는 기존 게시물 업데이트를 처리합니다.
     *
     * @param fileno 게시물과 연관된 파일 번호를 나타내는 선택적 배열.
     *               파일이 없으면 null 또는 비울 수 있습니다.
     * @param boardInfo 저장 또는 업데이트할 게시물 정보를 포함하는 BoardVO 객체.
     *                  이 객체는 정의된 제약 조건에 따라 유효하고 채워져야 합니다.
     * @return 게시판 그룹 번호가 쿼리 매개변수로 추가된 게시판 목록 보기로 리디렉션하는 URL.
     *         사용자가 작업을 수행할 권한이 없으면 "common/noAuth" 뷰를 반환.
     */
    @PostMapping("/boardSave")
    @Operation(summary = "Save Post", description = "Saves a new post or updates an existing one.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "303", description = "Redirect after saving post"),
        @ApiResponse(responseCode = "403", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardSave(@RequestParam(value = "fileno", required = false) String[] fileno,
                            @ModelAttribute @Valid BoardVO boardInfo) {

        String userno = authService.getAuthUserNo();
        String userrole = authService.getAuthUserrole();

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
     * 특정 게시물의 세부 정보를 읽고 표시하는 요청을 처리합니다.
     *
     * @param bgno 게시물이 속한 게시판 그룹의 ID, 선택적 매개변수.
     * @param brdno 읽을 게시판 게시물의 ID, 선택적 매개변수.
     * @param modelMap 뷰 렌더링에 필요한 속성을 저장할 모델 맵.
     * @return 게시판 게시물 세부 정보를 표시하기 위한 뷰 이름.
     */
    @GetMapping("/boardRead")
    @Operation(summary = "Read Post", description = "Returns the detailed information of a specific post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved post details"),
        @ApiResponse(responseCode = "404", description = "Post not found"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardRead(@RequestParam(value = "bgno", required = false) String bgno,
                            @RequestParam(value = "brdno", required = false) String brdno, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

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
     * 특정 게시물 번호(brdno)로 식별된 게시판 게시물의 삭제를 처리합니다.
     * 게시물과 연결된 모든 댓글도 삭제됩니다.
     * 삭제를 진행하기 전에 사용자 권한을 확인합니다.
     *
     * @param brdno 삭제할 게시물의 고유 번호입니다.
     * @param bgno 적절한 게시판 목록으로 리디렉션할 때 사용하는 게시판 그룹 번호입니다.
     * @return 성공적으로 삭제 후 리디렉션할 URL 문자열
     *         또는 권한 확인 실패 시 반환할 뷰 이름.
     */
    @GetMapping("/boardDelete")
    @Operation(summary = "Delete Post", description = "Deletes a board post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted post"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardDelete(@RequestParam(value = "brdno") String brdno, @RequestParam(value = "bgno") String bgno) {

        String userno = authService.getAuthUserNo();

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
     * HTTP POST 요청을 처리하여 게시판 그룹 트리 계층을 JSON 응답으로 반환합니다.
     *
     * @param response JSON 형식의 게시판 그룹 트리를 반환하기 위해 응답 콘텐츠 유형을 설정하는 데 사용되는 HttpServletResponse 객체입니다.
     */
    @PostMapping("/boardListByAjax")
    @Operation(summary = "Get Board Group Tree", description = "Returns the board group hierarchy tree.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved board group tree"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
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

    /**
     * 특정 게시물 번호로 식별된 게시판 게시물에 좋아요를 추가합니다.
     *
     * @param brdno 좋아요를 추가할 게시물 번호입니다. 이 매개변수는 선택 사항입니다.
     * @param response 클라이언트에 응답을 보내는 데 사용되는 HttpServletResponse 객체입니다.
     */
    @PostMapping("/addBoardLike")
    @Operation(summary = "Add Post Like", description = "Adds a like to a post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully added like to post"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public void addBoardLike(@RequestParam(value = "brdno", required = false) String brdno, HttpServletResponse response) {

        String userno = authService.getAuthUserNo();

        boardService.insertBoardLike(new ExtFieldVO(brdno, userno, null));

        UtilEtc.responseJsonValue(response, "OK");
    }

    /**
     * 게시판 댓글 저장 처리를 담당합니다. 이 메서드는 특정 게시판 게시물과 관련된 댓글 추가 또는 편집 작업을 처리합니다.
     * 입력된 댓글 데이터를 검증하고, 편집을 위한 사용자 인증을 확인한 다음 데이터베이스에 댓글 정보를 저장합니다.
     *
     * @param response  응답 세부정보를 설정하는 데 사용되는 HttpServletResponse 객체입니다.
     * @param boardReplyInfo  저장할 댓글 세부정보를 포함하는 BoardReplyVO 객체입니다.
     * @param modelMap  뷰에 데이터를 전달하기 위해 사용되는 ModelMap 객체입니다.
     * @return 렌더링할 뷰 이름을 나타내는 문자열입니다. 여기서는 "board/BoardReadAjax4Reply" 뷰입니다.
     */
    @PostMapping("/boardReplySave")
    @Operation(summary = "Save Reply", description = "Saves a reply to a post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully saved reply"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public String boardReplySave(HttpServletResponse response, @ModelAttribute @Valid BoardReplyVO boardReplyInfo, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

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
     * 특정 게시판 게시물에 있는 댓글을 삭제합니다.
     * 이 메서드는 주어진 정보에 따라 특정 게시물과 연관된 댓글의 삭제 처리를 진행합니다.
     * 삭제를 수행하기 전에 사용자 인증을 확인합니다.
     *
     * @param response 클라이언트에 대한 응답을 생성하는 데 사용되는 HttpServletResponse 객체입니다.
     * @param boardReplyInfo 삭제할 댓글 정보를 포함하는 BoardReplyVO 객체입니다.
     *                       유효하며 댓글 ID와 사용자 정보가 포함되어 있어야 합니다.
     */
    @PostMapping("/boardReplyDelete")
    @Operation(summary = "Delete Reply", description = "Deletes a reply from a post.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully deleted reply"),
        @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
        @ApiResponse(responseCode = "500", description = "Server error")
    })
    public void boardReplyDelete(HttpServletResponse response, @ModelAttribute @Valid BoardReplyVO boardReplyInfo) {

        String userno = authService.getAuthUserNo();

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
