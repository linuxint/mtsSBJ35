package com.devkbil.mtssbj.admin.board;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 게시판 그룹 서비스 클래스
 * - 게시판 그룹과 관련된 데이터 처리를 담당하는 클래스입니다.
 * - 그룹 리스트 조회, 그룹 저장/수정/삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BoardGroupService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 게시판 그룹 리스트 조회
     * - 모든 게시판 그룹 정보를 반환합니다.
     *
     * @return List 게시판 그룹 리스트
     */
    public List<?> selectBoardGroupList() {
        return sqlSession.selectList("selectBoardGroupList");
    }

    /**
     * 게시판 그룹 저장
     * - 게시판 그룹을 추가 삽입하거나 수정 작업을 처리합니다.
     *
     * @param param 게시판 그룹 정보 객체(BoardGroupVO)
     * @throws IllegalArgumentException BoardGroupVO가 null일 경우 예외 발생
     */
    @Transactional
    public int insertBoard(BoardGroupVO param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new IllegalArgumentException("BoardGroupVO는 null일 수 없습니다.");
        }

        // 부모 그룹 ID 검증
        param.setBgparent(validateBgparent(param.getBgparent()));

        if (!StringUtils.hasText(param.getBgno())) {
            // 신규 그룹 삽입
            return sqlSession.insert("insertBoardGroup", param);
        } else {
            // 기존 그룹 수정
            return sqlSession.update("updateBoardGroup", param);
        }
    }

    /**
     * 단일 게시판 그룹 조회
     * - 특정 그룹 ID에 해당하는 게시판 그룹 정보를 반환합니다.
     *
     * @param bgno 게시판 그룹 ID
     * @return BoardGroupVO 조회된 게시판 그룹 정보
     * @throws BoardGroupNotFoundException 그룹을 찾지 못한 경우 발생
     */
    public BoardGroupVO selectBoardGroupOne(String bgno) {

        BoardGroupVO boardGroupVO = sqlSession.selectOne("selectBoardGroupOne", bgno);
        if (ObjectUtils.isEmpty(boardGroupVO)) {
            throw new BoardGroupNotFoundException("bgno " + bgno + "에 해당하는 게시판 그룹을 찾을 수 없습니다.");
        }
        return boardGroupVO;
    }

    /**
     * 게시판 그룹 삭제
     * - 특정 그룹 ID에 해당하는 게시판 그룹을 삭제합니다.
     *
     * @param bgno 삭제할 게시판 그룹 ID
     * @return int 삭제된 행(row) 수
     * @throws BoardGroupNotFoundException 그룹 삭제 실패 시 발생
     */
    @Transactional
    public int deleteBoardGroup(String bgno) {

        int affectedRows = sqlSession.delete("deleteBoardGroup", bgno);
        if (affectedRows == 0) {
            throw new BoardGroupNotFoundException("bgno " + bgno + "에 해당하는 게시판 그룹 삭제 실패");
        }
        return affectedRows;
    }

    /**
     * 부모 그룹 ID 검증
     * - 부모 그룹 ID가 비어있거나 유효하지 않은 경우 null로 반환합니다.
     *
     * @param bgparent 부모 그룹 ID
     * @return String 검증된 부모 그룹 ID 또는 null
     */
    private String validateBgparent(String bgparent) {
        return (!StringUtils.hasText(bgparent)) ? null : bgparent;
    }

    /**
     * 게시판 그룹 조회/삭제 실패 시 예외 처리 클래스
     * - 호출한 게시판 그룹 데이터가 존재하지 않을 경우 사용됩니다.
     */
    public class BoardGroupNotFoundException extends RuntimeException {
        public BoardGroupNotFoundException(String message) {
            super(message);
        }
    }
}