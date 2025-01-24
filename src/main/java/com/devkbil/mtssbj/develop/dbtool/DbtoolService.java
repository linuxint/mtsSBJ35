package com.devkbil.mtssbj.develop.dbtool;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 데이터베이스 도구 서비스
 * - 데이터베이스 테이블 및 컬럼 레이아웃 정보를 조회합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DbtoolService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 테이블 레이아웃 정보 조회
     * - 사용자가 요청한 테이블과 컬럼 정보를 기반으로 데이터베이스에서 레이아웃을 조회합니다.
     *
     * @param param 테이블 이름, 컬럼 이름 등 조회 조건이 포함된 객체
     * @return 테이블 및 컬럼 레이아웃 정보 리스트
     */
    public List<DbtoolVO> selectTabeLayout(DbtoolVO param) {
        return sqlSession.selectList("selectTableLayout", param);
    }

}
