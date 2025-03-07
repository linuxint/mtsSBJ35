package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * 서버 서비스 연결 기록을 관리하는 서비스 클래스입니다.
 * MyBatis SqlSessionTemplate을 통해 데이터베이스에 연결하여
 * 서비스 연결 데이터에 대한 CRUD 작업을 제공합니다.
 */
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
     * 새로운 서비스 연결 정보를 데이터베이스에 삽입합니다.
     *
     * @param serviceConnVO 삽입할 서비스 연결 정보
     * @return 삽입된 레코드 수
     */
    @Transactional
    public int insertServiceConn(ConnVO serviceConnVO) {
        return sqlSession.insert("ad_srv_conn.insertSrvConn", serviceConnVO);
    }

    /**
     * 기존 서비스 연결 정보를 데이터베이스에서 수정합니다.
     *
     * @param serviceConnVO 수정할 서비스 연결 정보
     * @return 수정된 레코드 수
     */
    @Transactional
    public int updateServiceConn(ConnVO serviceConnVO) {
        return sqlSession.update("ad_srv_conn.updateSrvConn", serviceConnVO);
    }

    /**
     * 제공된 연결 ID를 사용하여 데이터베이스에서 서비스 연결 정보를 삭제합니다.
     *
     * @param connId 삭제할 서비스 연결의 ID
     * @return 삭제된 레코드 수
     */
    @Transactional
    public int deleteServiceConn(String connId) {
        return sqlSession.update("ad_srv_conn.deleteSrvConn", connId);
    }
}