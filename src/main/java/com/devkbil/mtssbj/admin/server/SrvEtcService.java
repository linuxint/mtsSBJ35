package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

/**
 * 서비스 기타 정보 관리 서비스
 * - 기타 정보 데이터의 조회, 등록, 수정, 삭제 기능 제공.
 */
@Service
@RequiredArgsConstructor
public class SrvEtcService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 제공된 검색 조건에 따라 기타 서비스 정보를 조회합니다.
     *
     * @param searchVO 검색 조건이 포함된 ServerSearchVO 객체
     * @return 기타 서비스 정보를 나타내는 SrvEtcVO 객체 리스트
     */
    public List<SrvEtcVO> getServiceEtcList(ServerSearchVO searchVO) {
        return sqlSession.selectList("ad_srv_etc.selectSrvEtcList", searchVO);
    }

    /**
     * 기타 정보 상세 조회
     *
     * @param etcId 조회하고자 하는 기타 정보의 식별자(ID)
     * @return 조회된 기타 정보 데이터 객체 (ServiceEtcVO)
     */
    public SrvEtcVO getServiceEtcDetail(String etcId) {
        return sqlSession.selectOne("ad_srv_etc.selectSrvEtcOne", etcId);
    }

    /**
     * 기타 정보 등록
     *
     * @param serviceSrvEtcVO 새로 등록할 데이터 객체
     * @return 영향을 받은 레코드의 수
     */
    @Transactional
    public int insertServiceEtc(SrvEtcVO serviceSrvEtcVO) {
        return sqlSession.insert("ad_srv_etc.insertSrvEtc", serviceSrvEtcVO);
    }

    /**
     * 기타 정보 수정
     *
     * @param serviceSrvEtcVO 수정할 데이터 객체
     * @return 영향을 받은 레코드의 수
     */
    @Transactional
    public int updateServiceEtc(SrvEtcVO serviceSrvEtcVO) {
        return sqlSession.update("ad_srv_etc.updateSrvEtc", serviceSrvEtcVO);
    }

    /**
     * 기타 정보 삭제
     *
     * @param etcId 삭제할 기타 정보의 식별자(ID)
     * @return 삭제된 레코드의 수
     */
    @Transactional
    public int deleteServiceEtc(String etcId) {
        return sqlSession.update("ad_srv_etc.deleteSrvEtc", etcId);
    }
}