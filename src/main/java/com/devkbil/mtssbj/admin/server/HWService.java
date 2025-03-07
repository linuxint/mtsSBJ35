package com.devkbil.mtssbj.admin.server;

import com.devkbil.mtssbj.search.ServerSearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HWService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 서버 하드웨어 목록 조회
     *
     * @param searchVO 검색 조건
     * @return 하드웨어 목록
     */
    public List<HWVO> getHWList(ServerSearchVO searchVO) {
        return sqlSession.selectList("ad_srv_hw.selectSrvHwList", searchVO);
    }

    /**
     * 서버 하드웨어 상세 정보 조회
     *
     * @param hwId 하드웨어 ID
     * @return HWVO 객체
     */
    public HWVO getHWDetail(String hwId) {
        return sqlSession.selectOne("ad_srv_hw.selectSrvHwOne", hwId);
    }

    /**
     * 서버 하드웨어 등록
     *
     * @param hwVO 등록할 하드웨어 정보
     * @return 데이터베이스에 성공적으로 등록된 하드웨어 레코드의 수. 성공 시 1, 실패 시 0
     */
    @Transactional
    public int insertHW(HWVO hwVO) {
        return sqlSession.insert("ad_srv_hw.insertSrvHw", hwVO);
    }

    /**
     * 서버 하드웨어 정보 수정
     *
     * @param hwVO 수정할 하드웨어 정보
     * @return 데이터베이스에서 성공적으로 수정된 하드웨어 레코드의 수. 성공 시 1, 실패 시 0
     */
    @Transactional
    public int updateHW(HWVO hwVO) {
        return sqlSession.update("ad_srv_hw.updateSrvHw", hwVO);
    }

    /**
     * 서버 하드웨어 삭제 (논리 삭제)
     *
     * @param hwId 삭제할 하드웨어 ID
     * @return 논리적으로 삭제 처리된 하드웨어 레코드의 수. 성공 시 1, 실패 시 0.
     *         실제로 레코드가 삭제되지 않고 삭제 표시만 업데이트됨
     */
    @Transactional
    public int deleteHW(String hwId) {
        return sqlSession.update("ad_srv_hw.deleteSrvHw", hwId);
    }
}
