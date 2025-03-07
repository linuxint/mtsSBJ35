package com.devkbil.mtssbj.crud;

import com.devkbil.mtssbj.search.SearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * CRUD 비즈니스 로직을 처리하는 서비스 클래스
 * - 데이터베이스 조작(조회, 삽입, 수정, 삭제)을 처리합니다.
 * - 트랜잭션 관리 및 MyBatis 템플릿을 사용합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CrudService {

    private final SqlSessionTemplate sqlSession;

    /**
     * CRUD 리스트 개수 조회
     * - 검색 조건(SearchVO)에 따라 총 데이터 개수를 조회합니다.
     *
     * @param param 검색 조건을 담고 있는 SearchVO 객체
     * @return 조회된 전체 데이터 개수
     */
    public Integer selectCrudCount(SearchVO param) {
        return sqlSession.selectOne("selectCrudCount", param);
    }

    /**
     * CRUD 리스트 데이터 조회
     * - 페이징 조건(SearchVO)에 따라 CRUD 데이터 리스트를 조회합니다.
     *
     * @param param 검색 조건 및 페이징 데이터를 담고 있는 SearchVO 객체
     * @return 조회된 CRUD 데이터 리스트
     */
    public List<CrudVO> selectCrudList(SearchVO param) {
        return sqlSession.selectList("selectCrudList", param);
    }

    /**
     * CRUD 데이터 삽입 또는 수정
     * - `crno` 필드가 비어 있으면 데이터를 새로 삽입, 값이 있으면 데이터를 업데이트합니다.
     * - 트랜잭션을 관리하여 삽입/수정 중 오류 발생 시 롤백합니다.
     *
     * @param param 삽입 또는 수정할 CRUD 데이터 객체
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertCrud(CrudVO param) {
        try {
            if (!StringUtils.hasText(param.getCrno())) {
                // 새로운 CRUD 데이터 삽입
                sqlSession.insert("insertCrud", param);
            } else {
                // 기존 CRUD 데이터 업데이트
                sqlSession.update("updateCrud", param);
            }
        } catch (Exception ex) {
            log.error("Error in insertCrud: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * CRUD 데이터 상세 조회
     * - 특정 CRUD 데이터를 단일 조회합니다.
     *
     * @param param 조회할 데이터를 식별할 Key 값(CrudVO)
     * @return 조회된 CRUD 데이터 객체
     */
    public CrudVO selectCrudOne(CrudVO param) {
        return sqlSession.selectOne("selectCrudOne", param);
    }

    /**
     * CRUD 데이터 삭제 (논리 삭제)
     * - 특정 CRUD 데이터를 삭제 상태로 설정합니다.
     * - 트랜잭션 관리를 통해 삭제 처리에 대한 안정성을 보장합니다.
     *
     * @param param 삭제할 데이터를 식별할 Key 값(CrudVO)
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCrud(CrudVO param) {
        try {
            sqlSession.update("deleteCrud", param);
        } catch (Exception ex) {
            log.error("Error in deleteCrud: {}", ex.getMessage());
            throw ex;
        }
    }

    /**
     * 다중 데이터 삭제 (논리 삭제)
     * - 리스트로 제공된 데이터를 모두 삭제 처리합니다.
     * - 트랜잭션 관리를 통해 롤백 가능하도록 설정합니다.
     *
     * @param param 삭제할 데이터를 식별할 번호 리스트
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteChk(String[] param) {
        HashMap hm = new HashMap();
        hm.put("list", param);

        sqlSession.insert("deleteChk", hm);
    }
}
