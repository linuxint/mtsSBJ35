package com.devkbil.mtssbj.common.code;

import lombok.RequiredArgsConstructor;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * CodeCacheDAO는 데이터베이스와 상호작용하여 코드 그룹 및 상세 코드와 관련된 데이터를
 * 관리하는 역할을 하는 Data Access Object입니다.
 * MyBatis {@link SqlSessionTemplate}를 사용하여 SQL 쿼리를 실행합니다.
 *
 * 이 클래스는 코드 그룹 및 상세 코드의 목록을 조회하는 메서드를 제공합니다.
 */
@Repository("commonCodeDAO")
@RequiredArgsConstructor
public class CodeCacheDAO { // extends EgovComAbstractDAO

    private final SqlSessionTemplate sqlSession;

    /**
     * 공통코드그룹 List 조회
     *
     * @return 공통코드 그룹 리스트
     */
    public List<?> selectListCodeGroup() {
        return sqlSession.selectList("selectListCodeGroup");
    }

    /**
     * 공통코드상세 List 조회
     *
     * @return 공통코드 리스트
     */
    public List<?> selectListCode() {
        return sqlSession.selectList("selectListCode");
    }
}
