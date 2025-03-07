package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * SWService is responsible for managing server software related operations
 * including querying, registering, updating, and logically deleting software records.
 */
@Service
@RequiredArgsConstructor
public class SWService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 서버 소프트웨어 리스트 조회
     *
     * @param searchVO 검색 조건
     * @return 소프트웨어 목록
     */
    public List<SWVO> getSWList(ServerSearchVO searchVO) {
        return sqlSession.selectList("ad_srv_sw.selectSrvSwList", searchVO);
    }

    /**
     * 서버 소프트웨어 상세 정보 조회
     *
     * @param swId 소프트웨어 ID
     * @return SWVO 객체
     */
    public SWVO getSWDetail(String swId) {
        return sqlSession.selectOne("ad_srv_sw.selectSrvSwOne", swId);
    }

    /**
     * 서버 소프트웨어 등록
     *
     * @param swVO 등록할 소프트웨어 정보
     * @return 삽입된 데이터 수
     */
    @Transactional
    public int insertSW(SWVO swVO) {
        return sqlSession.insert("ad_srv_sw.insertSrvSw", swVO);
    }

    /**
     * 서버 소프트웨어 수정
     *
     * @param swVO 수정할 소프트웨어 정보
     * @return 업데이트된 데이터 수
     */
    @Transactional
    public int updateSW(SWVO swVO) {
        return sqlSession.update("ad_srv_sw.updateSrvSw", swVO);
    }

    /**
     * 서버 소프트웨어 삭제 (논리 삭제)
     *
     * @param swId 삭제할 소프트웨어 ID
     * @return 삭제된 데이터 수
     */
    @Transactional
    public int deleteSW(String swId) {
        return sqlSession.update("ad_srv_sw.deleteSrvSw", swId);
    }
}