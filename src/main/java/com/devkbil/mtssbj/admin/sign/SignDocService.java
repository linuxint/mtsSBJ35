package com.devkbil.mtssbj.admin.sign;

import com.devkbil.mtssbj.search.SearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * SignDocService 클래스
 * - 문서 유형 관리 로직을 처리하는 서비스 클래스입니다.
 * - 문서 유형의 조회, 등록, 수정, 삭제 기능을 제공합니다.
 * - 데이터베이스 연동은 MyBatis(SqlSessionTemplate)를 사용합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SignDocService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 문서 유형 총 개수 조회
     * - 특정 조건(SearchVO)에 맞는 문서 유형의 총 개수를 조회합니다.
     *
     * @param param 검색 조건을 담은 객체 (SearchVO)
     * @return 문서 유형의 총 개수 (Integer)
     */
    public Integer selectSignDocTypeCount(SearchVO param) {
        return sqlSession.selectOne("selectSignDocTypeCount", param);
    }

    /**
     * 문서 유형 리스트 조회
     * - 특정 조건(SearchVO)에 맞는 문서 유형 리스트를 반환합니다.
     *
     * @param param 검색 조건을 담은 객체 (SearchVO)
     * @return 문서 유형 리스트 (List<?>)
     */
    public List<?> selectSignDocTypeList(SearchVO param) {
        return sqlSession.selectList("selectSignDocTypeList", param);
    }

    /**
     * 문서 유형 삽입 및 업데이트
     * - 문서 유형 번호가 없으면 신규 문서 유형을 삽입합니다.
     * - 문서 유형 번호가 있으면 기존 문서 유형을 업데이트합니다.
     *
     * @param param 삽입 또는 수정할 문서 유형 정보 (SignDocTypeVO)
     * @return 처리된 행 수 (int)
     */
    @Transactional
    public int insertSignDocType(SignDocTypeVO param) {
        if (!StringUtils.hasText(param.getDtno())) {
            // 신규 삽입
            return sqlSession.insert("insertSignDocType", param);
        } else {
            // 기존 데이터 업데이트
            return sqlSession.update("updateSignDocType", param);
        }
    }

    /**
     * 단일 문서 유형 조회
     * - 문서 유형 번호를 기반으로 특정 문서 유형 정보를 조회합니다.
     *
     * @param param 조회할 문서 유형 번호 (String)
     * @return 조회된 문서 유형 정보 (SignDocTypeVO)
     */
    public SignDocTypeVO selectSignDocTypeOne(String param) {
        return sqlSession.selectOne("selectSignDocTypeOne", param);
    }

    /**
     * 문서 유형 논리 삭제
     * - 문서 유형을 삭제 상태로 변경합니다. (논리 삭제 처리)
     *
     * @param param 삭제할 문서 유형 정보 (SignDocTypeVO)
     * @return 처리된 행 수 (int)
     */
    public int deleteSignDocType(SignDocTypeVO param) {
        return sqlSession.update("deleteSignDocType", param);
    }

}
