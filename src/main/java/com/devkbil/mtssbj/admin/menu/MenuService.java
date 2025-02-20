package com.devkbil.mtssbj.admin.menu;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 메뉴 서비스 클래스
 * - 메뉴 데이터 관리와 관련된 비즈니스 로직을 처리합니다.
 * - MyBatis 및 트랜잭션 관리를 활용합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 메뉴 리스트 조회
     * - 모든 메뉴 데이터를 조회합니다.
     *
     * @return List 메뉴 리스트
     */
    public List<?> selectMenu() {
        return sqlSession.selectList("selectMenu");
    }

    /**
     * 메뉴 삽입 및 업데이트
     * - 신규 메뉴를 삽입하거나 기존 메뉴를 업데이트합니다.
     *
     * @param menuVO 메뉴 정보 객체
     * @return boolean 작업 성공 여부 (true: 성공, false: 실패)
     */
    @Transactional
    public int insertMenu(MenuVO menuVO) {
        try {
            // 부모 메뉴 값 검증 (값이 없을 경우 null로 설정)
            validateMenuParent(menuVO);

            int affectedRows;

            if (!StringUtils.hasText(menuVO.getMnuNo())) {
                affectedRows = sqlSession.insert("insertMenu", menuVO); // 신규 메뉴 삽입
                log.info("insertMenu: 메뉴가 성공적으로 추가되었습니다: {}", menuVO);
            } else {
                affectedRows = sqlSession.update("updateMenu", menuVO); // 기존 메뉴 업데이트
                log.info("updateMenu: 메뉴가 성공적으로 업데이트되었습니다: {}", menuVO);
            }
            return affectedRows;
        } catch (DataAccessException e) {
            // 오류 발생 시 로그 출력 및 예외 처리
            log.error("메뉴 저장 실패: {}", menuVO, e);
            throw new RuntimeException("메뉴 저장 중 오류 발생", e);
        }
    }

    /**
     * 단일 메뉴 조회
     * - 특정 메뉴 번호에 해당하는 메뉴를 조회합니다.
     *
     * @param mnuNo 메뉴 번호
     * @return MenuVO 메뉴 정보 객체
     * @throws IllegalArgumentException 메뉴가 존재하지 않을 경우 예외를 발생시킵니다.
     */
    public MenuVO selectMenuOne(String mnuNo) {
        try {
            MenuVO menu = sqlSession.selectOne("selectMenuOne", mnuNo);
            if (ObjectUtils.isEmpty(menu)) {
                throw new IllegalArgumentException("selectMenuOne: 해당 번호로 메뉴를 찾을 수 없습니다: " + mnuNo);
            }
            return menu;
        } catch (DataAccessException e) {
            log.error("메뉴 조회 실패: {}", mnuNo, e);
            throw new RuntimeException("메뉴 조회 중 오류 발생", e);
        }
    }

    /**
     * 메뉴 삭제
     * - 특정 메뉴 번호에 해당하는 메뉴를 삭제합니다.
     *
     * @param mnuNo 메뉴 번호
     * @return boolean 삭제 성공 여부 (true: 삭제 성공, false: 삭제 실패)
     */
    @Transactional
    public boolean deleteMenu(String mnuNo) {
        try {
            int rowsDeleted = sqlSession.delete("deleteMenu", mnuNo);
            if (rowsDeleted > 0) {
                log.info("deleteMenu: 메뉴가 성공적으로 삭제되었습니다: {}", mnuNo);
                return true;
            } else {
                log.warn("deleteMenu: 메뉴를 찾을 수 없어 삭제에 실패하였습니다: {}", mnuNo);
                return false;
            }
        } catch (DataAccessException e) {
            log.error("메뉴 삭제 실패: {}", mnuNo, e);
            throw new RuntimeException("메뉴 삭제 중 오류 발생", e);
        }
    }

    /**
     * 부모 메뉴 값 검증
     * - 부모 메뉴 정보가 없을 경우 null로 설정합니다.
     *
     * @param menuVO 메뉴 정보 객체
     */
    private void validateMenuParent(MenuVO menuVO) {
        if (!StringUtils.hasText(menuVO.getMnuParent())) {
            menuVO.setMnuParent(null);
        }
    }
}