package com.devkbil.mtssbj.main;

import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 샘플 서비스 클래스.
 * 데이터베이스와 연동하여 통계 및 샘플 데이터를 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class SampleService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 게시판 그룹별 통계 데이터를 조회합니다.
     *
     * @return 게시판 그룹별 통계 데이터 목록
     */
    public List<?> selectBoardGroupCount4Statistic() {
        return sqlSession.selectList("selectBoardGroupCount4Statistic");
    }
}
