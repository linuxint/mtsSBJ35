package com.devkbil.mtssbj.main;

import com.devkbil.mtssbj.common.ExtFieldVO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 메인 페이지와 관련된 서비스 클래스.
 * 데이터베이스와의 연동을 통해 메인 화면에서 사용되는 데이터들을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class IndexService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 최신 뉴스 목록을 조회합니다.
     *
     * @return 최신 뉴스 데이터 목록
     */
    public List<?> selectRecentNews() {
        return sqlSession.selectList("selectRecentNews");
    }

    /**
     * 메인 화면에 표시할 타임라인 데이터를 조회합니다.
     *
     * @return 타임라인 데이터 목록
     */
    public List<?> selectTimeLine() {
        return sqlSession.selectList("selectTimeLine");
    }

    /**
     * 메인 화면에 표시할 상위 5개의 공지사항 목록을 조회합니다.
     *
     * @return 공지사항 데이터 목록 (최대 5개)
     */
    public List<?> selectNoticeListTop5() {
        return sqlSession.selectList("selectNoticeListTop5");
    }

    /**
     * 캘린더에 표시할 일정 목록을 조회합니다.
     *
     * @param param 캘린더 필터를 설정하기 위한 매개변수 (ExtFieldVO)
     * @return 일정 데이터 목록
     */
    public List<?> selectSchList4Calen(ExtFieldVO param) {
        return sqlSession.selectList("selectSchList4Calen", param);
    }

}
