package com.devkbil.mtssbj.common.code;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 코드 캐싱 관련 작업을 처리하기 위한 Data Access Object (DAO) 클래스.
 * 데이터베이스와 상호작용하여 코드 그룹 및 상세 코드 정보를 조회하는 메서드를 제공합니다.
 */
@Repository("commonCodeDAO")
@RequiredArgsConstructor
public class CodeCacheDAO { // extends EgovComAbstractDAO

    private final SqlSessionTemplate sqlSession;

    /**
     * 공통코드그룹 List 조회
     *
     * @param
     * @return
     */
    public List<?> selectListCodeGroup() {
        return sqlSession.selectList("selectListCodeGroup");
    }

    /**
     * 공통코드상세 List 조회
     *
     * @param
     * @return
     */
    public List<?> selectListCode() {
        return sqlSession.selectList("selectListCode");
    }
}
