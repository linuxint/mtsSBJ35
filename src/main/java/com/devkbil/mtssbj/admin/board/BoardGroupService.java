package com.devkbil.mtssbj.admin.board;

import com.devkbil.mtssbj.error.BusinessExceptionHandler;
import com.devkbil.mtssbj.error.ErrorCode;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
     * 제공된 BoardGroupVO 상세 정보에 따라 게시판 그룹을 삽입하거나 업데이트합니다.
     * 매개변수의 bgno 속성이 설정되지 않은 경우 새로운 게시판 그룹이 삽입됩니다.
     * 설정된 경우 기존 게시판 그룹이 업데이트됩니다.
     *
     * @param param 삽입 또는 업데이트할 게시판 그룹의 상세 정보를 포함하는 BoardGroupVO 객체
     * @return int 데이터베이스 작업에 영향을 받은 행의 수
     * @throws IllegalArgumentException 제공된 BoardGroupVO가 null인 경우 발생
     */
    @Transactional
    public int insertBoard(BoardGroupVO param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new IllegalArgumentException("BoardGroupVO는 null일 수 없습니다.");
        }

        param.setBgparent(validateBgparent(param.getBgparent())); // 부모 그룹 ID 검증

        if (!StringUtils.hasText(param.getBgno())) {
            return sqlSession.insert("insertBoardGroup", param); // 신규 그룹 삽입
        } else {
            return sqlSession.update("updateBoardGroup", param); // 기존 그룹 수정
        }
    }

    /**
     * 단일 게시판 그룹 조회
     * - 특정 그룹 ID에 해당하는 게시판 그룹 정보를 반환합니다.
     *
     * @param bgno 게시판 그룹 ID
     * @return BoardGroupVO 조회된 게시판 그룹 정보
     * @throws BusinessExceptionHandler 그룹을 찾지 못한 경우 발생
     */
    public BoardGroupVO selectBoardGroupOne(String bgno) {

        BoardGroupVO boardGroupVO = sqlSession.selectOne("selectBoardGroupOne", bgno);

        return boardGroupVO;
    }

    /**
     * 게시판 그룹 삭제
     * - 특정 그룹 ID에 해당하는 게시판 그룹을 삭제합니다.
     *
     * @param bgno 삭제할 게시판 그룹 ID
     * @return int 삭제된 행(row) 수
     * @throws BusinessExceptionHandler 그룹 삭제 실패 시 발생
     */
    @Transactional
    public int deleteBoardGroup(String bgno) {

        int affectedRows = sqlSession.delete("deleteBoardGroup", bgno);

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
        return !StringUtils.hasText(bgparent) ? null : bgparent;
    }

}