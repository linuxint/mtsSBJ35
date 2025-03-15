package com.devkbil.mtssbj.board;

import com.devkbil.mtssbj.admin.board.BoardGroupVO;
import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.FileVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 게시판 서비스 클래스
 * - 게시판 관련 데이터의 비즈니스 로직을 관리합니다.
 * - MyBatis를 사용하여 데이터베이스와 상호작용하고, 트랜잭션 처리를 포함합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BoardService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 게시판 그룹 정보 조회
     * - 특정 ID에 해당하는 사용 가능한 게시판 그룹 정보를 조회합니다.
     *
     * @param param 게시판 그룹 ID
     * @return BoardGroupVO 게시판 그룹 정보
     */
    public BoardGroupVO selectBoardGroupOne4Used(String param) {
        return sqlSession.selectOne("selectBoardGroupOne4Used", param);
    }

    /**
     * 게시글 수 조회
     * - 검색 조건에 맞는 게시판의 게시글 총 개수를 반환합니다.
     *
     * @param param 검색 조건 VO
     * @return Integer 게시글 수
     */
    public Integer selectBoardCount(BoardSearchVO param) {
        return sqlSession.selectOne("selectBoardCount", param);
    }

    /**
     * 게시글 목록 조회
     * - 검색 조건에 따라 게시판의 게시글 목록을 반환합니다.
     *
     * @param param 검색 조건 VO
     * @return List 게시글 목록
     */
    @Cacheable(value = "boardList", key = "#param.bgno + '_' + #param.searchKeyword + '_' + #param.rowStart", condition = "#param.rowStart != null")
    public List<?> selectBoardList(BoardSearchVO param) {
        return sqlSession.selectList("selectBoardList", param);
    }

    /**
     * 공지사항 목록 조회
     * - 공지 타입의 게시글만 조회합니다.
     *
     * @param param 검색 조건 VO
     * @return List 공지사항 목록
     */
    @Cacheable(value = "boardNotices", key = "#param.bgno != null ? #param.bgno : 'all'")
    public List<?> selectNoticeList(BoardSearchVO param) {
        return sqlSession.selectList("selectNoticeList", param);
    }

    /**
     * 게시글 저장
     * - 신규 게시글을 저장하거나 기존 게시글을 업데이트합니다.
     * - 파일 관련 데이터 또한 관리합니다.
     *
     * @param param    게시글 VO
     * @param filelist 첨부 파일 목록
     * @param fileno   삭제할 파일 번호 목록
     */
    @Transactional(rollbackFor = Exception.class)
    @Caching(evict = {
        @CacheEvict(value = "boardList", allEntries = true),
        @CacheEvict(value = "boardNotices", allEntries = true)
    })
    public void insertBoard(BoardVO param, List<FileVO> filelist, String[] fileno) {
        try {
            if (!StringUtils.hasText(param.getBrdno())) {
                sqlSession.insert("insertBoard", param); // 신규 등록
            } else {
                sqlSession.update("updateBoard", param); // 업데이트
            }

            // 첨부 파일 관리
            if (!ObjectUtils.isEmpty(fileno)) {
                HashMap<String, String[]> fparam = new HashMap<>();
                fparam.put("fileno", fileno);
                sqlSession.insert("updateBoardFile", fparam);
            }

            // 신규 첨부 파일 삽입
            for (FileVO f : filelist) {
                f.setParentPK(param.getBrdno());
                sqlSession.insert("insertBoardFile", f);
            }
        } catch (TransactionException ex) {
            log.error("insertBoard: 게시글 저장 중 오류 발생", ex);
        }
    }

    /**
     * 게시글 상세 조회
     * - 지정된 ID에 해당하는 게시글 데이터를 반환합니다.
     *
     * @param param 검색 조건 VO
     * @return BoardVO 게시글 상세 정보
     */
    @Cacheable(value = "boardDetail", key = "#param.field1 + '_' + #param.field2", unless = "#result == null")
    public BoardVO selectBoardOne(ExtFieldVO param) {
        return sqlSession.selectOne("selectBoardOne", param);
    }

    /**
     * 게시글 권한 확인
     * - 게시글 수정/삭제 권한이 있는지 확인합니다.
     *
     * @param param 게시글 VO
     * @return String 권한 확인 결과 ("Y" 또는 "N")
     */
    public String selectBoardAuthChk(BoardVO param) {
        return sqlSession.selectOne("selectBoardAuthChk", param);
    }

    /**
     * 게시글 조회수 증가
     * - 특정 게시글의 조회수를 1 증가시킵니다.
     *
     * @param param 검색 조건 VO
     * @return int 업데이트된 행(row) 개수
     */
    public int updateBoardRead(ExtFieldVO param) {
        return sqlSession.insert("updateBoardRead", param);
    }

    /**
     * 게시글 삭제
     * - 특정 게시글을 삭제 처리합니다.
     *
     * @param param 게시글 ID
     * @return int 삭제된 행(row) 개수
     */
    @Caching(evict = {
        @CacheEvict(value = "boardList", allEntries = true),
        @CacheEvict(value = "boardNotices", allEntries = true)
    })
    public int deleteBoardOne(String param) {
        return sqlSession.delete("deleteBoardOne", param);
    }

    /**
     * 좋아요 저장
     * - 게시글에 대한 좋아요 정보를 저장하고 관련 데이터를 갱신합니다.
     *
     * @param param 확장 필드 VO
     */
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "boardList", allEntries = true)
    public void insertBoardLike(ExtFieldVO param) {
        try {
            sqlSession.insert("insertBoardLike", param);
            sqlSession.update("updateBoard4Like", param);
        } catch (TransactionException ex) {
            log.error("insertBoardLike: 좋아요 저장 중 오류 발생", ex);
        }
    }

    /**
     * 게시글 파일 목록 조회
     * - 게시글에 첨부된 파일 목록을 반환합니다.
     *
     * @param param 게시글 ID
     * @return List 첨부 파일 목록
     */
    public List<?> selectBoardFileList(String param) {
        return sqlSession.selectList("selectBoardFileList", param);
    }

    /**
     * 게시판 댓글 조회
     * - 특정 게시글 ID에 연결된 댓글 리스트를 반환합니다.
     *
     * @param param 게시글 ID
     * @return List 댓글 목록
     */
    public List<?> selectBoardReplyList(String param) {
        return sqlSession.selectList("selectBoardReplyList", param);
    }

    /**
     * 댓글 저장
     * - 댓글을 삽입하거나 업데이트합니다.
     * - 부모 댓글 정보에 따라 깊이(depth)와 순서(order)를 조정합니다.
     *
     * @param param 댓글 VO
     * @return BoardReplyVO 저장 또는 업데이트된 댓글 정보
     */
    @Transactional(rollbackFor = Exception.class)
    public BoardReplyVO insertBoardReply(BoardReplyVO param) {
        if (!StringUtils.hasText(param.getReno())) {
            // 신규 댓글 추가
            if (StringUtils.hasText(param.getReparent())) {
                BoardReplyVO parentReply = sqlSession.selectOne("selectBoardReplyParent", param.getReparent());
                param.setRedepth(parentReply.getRedepth());
                param.setReorder(parentReply.getReorder() + 1);
                sqlSession.selectOne("updateBoardReplyOrder", parentReply);
            } else {
                Integer reorder = sqlSession.selectOne("selectBoardReplyMaxOrder", param.getBrdno());
                param.setReorder(reorder);
            }
            sqlSession.insert("insertBoardReply", param);
        } else {
            // 댓글 수정
            sqlSession.update("updateBoardReply", param);
        }
        return sqlSession.selectOne("selectBoardReplyOne", param.getReno());
    }

    /**
     * 댓글 권한 확인
     * - 댓글 수정/삭제 권한을 확인합니다.
     *
     * @param param 댓글 VO
     * @return String 권한 확인 결과 ("Y" 또는 "N")
     */
    public String selectBoardReplyAuthChk(BoardReplyVO param) {
        return sqlSession.selectOne("selectBoardReplyAuthChk", param);
    }

    /**
     * 댓글 삭제
     * - 댓글을 삭제합니다. 단, 자식 댓글이 있는 경우 삭제되지 않습니다.
     *
     * @param boardReplyInfo 삭제할 댓글 정보
     * @return boolean 삭제 성공 여부 (true: 성공, false: 실패)
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBoardReply(BoardReplyVO boardReplyInfo) {
        Integer childCount = sqlSession.selectOne("selectBoardReplyChild", boardReplyInfo);

        if (childCount > 0) {
            return false; // 자식 댓글이 있는 경우 삭제 불가
        }

        sqlSession.update("updateBoardReplyOrder4Delete", boardReplyInfo);
        sqlSession.delete("deleteBoardReply", boardReplyInfo);

        return true;
    }

    /**
     * 댓글 전체 삭제
     * - 특정 게시글의 모든 댓글을 삭제합니다.
     *
     * @param map 삭제 조건
     * @return boolean 삭제 성공 여부
     */
    public int deleteBoardReplyAll(Map map) {
        return sqlSession.delete("deleteBoardReplyAll", map);
    }

    /**
     * 색인용 데이터 추출
     * - 마지막 색인 이후 추가된 게시글 데이터를 반환합니다.
     *
     * @param brdno 검색 조건
     * @return List 색인 데이터
     */
    public List<?> selectBoards4Indexing(String brdno) {
        return sqlSession.selectList("selectBoards4Indexing", brdno);
    }

    /**
     * 색인용 댓글 데이터 조회
     *
     * @param extFieldVO 검색 조건
     * @return List 색인 데이터
     */
    public List<?> selectBoardReply4Indexing(ExtFieldVO extFieldVO) {
        return sqlSession.selectList("selectBoardReply4Indexing", extFieldVO);
    }

    /**
     * 첨부파일 데이터 조회
     *
     * @param extFieldVO 검색 조건
     * @return List 색인 데이터
     */
    public List<?> selectBoardFiles4Indexing(ExtFieldVO extFieldVO) {
        return sqlSession.selectList("selectBoardFiles4Indexing", extFieldVO);
    }
}