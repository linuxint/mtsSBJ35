package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;

import lombok.RequiredArgsConstructor;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConnService {

    private final SqlSessionTemplate sqlSession;

    /**
     * ServiceConn 목록 조회
     *
     * @param searchVO 검색 조건
     * @return 접속 정보 목록
     */
    public List<ConnVO> getServiceConnList(ServerSearchVO searchVO) {
        return sqlSession.selectList("ad_srv_conn.selectSrvConnList", searchVO);
    }

    /**
     * ServiceConn 상세 조회
     *
     * @param connId 접속 정보 ID
     * @return ServiceConnVO 객체
     */
    public ConnVO getServiceConnDetail(String connId) {
        return sqlSession.selectOne("ad_srv_conn.selectSrvConnOne", connId);
    }

    /**
     * ServiceConn 등록
     *
     * @param serviceConnVO 등록할 접속 정보
     */
    @Transactional
    public int insertServiceConn(ConnVO serviceConnVO) {
        return sqlSession.insert("ad_srv_conn.insertSrvConn", serviceConnVO);
    }

    /**
     * ServiceConn 수정
     *
     * @param serviceConnVO 수정할 접속 정보
     */
    @Transactional
    public int updateServiceConn(ConnVO serviceConnVO) {
        return sqlSession.update("ad_srv_conn.updateSrvConn", serviceConnVO);
    }

    /**
     * ServiceConn 삭제
     *
     * @param connId 삭제할 접속 정보 ID
     */
    @Transactional
    public int deleteServiceConn(String connId) {
        return sqlSession.update("ad_srv_conn.deleteSrvConn", connId);
    }
}