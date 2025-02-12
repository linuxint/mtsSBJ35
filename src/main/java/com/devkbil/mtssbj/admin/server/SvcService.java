package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SvcService {

    private final SqlSessionTemplate sqlSession;

    /**
     * Service 목록 조회
     *
     * @param searchVO 검색 조건
     * @return Service 목록
     */
    public List<SvcVO> getServiceList(ServerSearchVO searchVO) {
        return sqlSession.selectList("ad_srv_svc.selectSrvSvcList", searchVO);
    }

    /**
     * 특정 Service 상세 조회
     *
     * @param svcId 서비스 ID
     * @return SvcVO
     */
    public SvcVO getServiceDetail(String svcId) {
        return sqlSession.selectOne("ad_srv_svc.selectSrvSvcOne", svcId);
    }

    /**
     * Service 등록
     *
     * @param svcVO 등록할 서비스 정보
     * @return 등록된 행 개수
     */
    @Transactional
    public int insertService(SvcVO svcVO) {
        return sqlSession.insert("ad_srv_svc.insertSrvSvc", svcVO);
    }

    /**
     * Service 수정
     *
     * @param svcVO 수정할 서비스 정보
     * @return 수정된 행 개수
     */
    @Transactional
    public int updateService(SvcVO svcVO) {
        return sqlSession.update("ad_srv_svc.updateSrvSvc", svcVO);
    }

    /**
     * Service 삭제
     *
     * @param svcId 삭제할 서비스 ID
     * @return 삭제된 행 개수
     */
    @Transactional
    public int deleteService(String svcId) {
        return sqlSession.update("ad_srv_svc.deleteSrvSvc", svcId);
    }
}