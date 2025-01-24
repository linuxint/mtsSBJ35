package com.devkbil.mtssbj.admin.code;

import com.devkbil.mtssbj.search.SearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.List;

/**
 * 공통 코드 서비스 클래스
 * - 공통 코드의 조회, 저장, 삭제 등의 작업을 담당합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 공통 코드 개수 조회
     * - 특정 검색 조건에 따라 코드의 총 개수를 반환합니다.
     *
     * @param param 검색 조건 객체 (SearchVO)
     * @return Integer 공통 코드 개수
     */
    public Integer selectCodeCount(SearchVO param) {
        return sqlSession.selectOne("selectCodeCount", param);
    }

    /**
     * 공통 코드 리스트 조회
     * - 특정 검색 조건에 따라 공통 코드 리스트를 반환합니다.
     *
     * @param param 검색 조건 객체 (SearchVO)
     * @return List 공통 코드 리스트
     */
    public List<?> selectCodeList(SearchVO param) {
        return sqlSession.selectList("selectCodeList", param);
    }

    /**
     * 공통 코드 저장
     * - 새 공통 코드를 추가하거나 기존 코드를 업데이트합니다.
     *
     * @param codeFormType 작업 유형 (U: 업데이트, 기타: 삽입)
     * @param param        저장할 공통 코드 정보 (CodeVO)
     * @throws IllegalArgumentException CodeVO가 null이거나 이미 존재하는 코드일 경우 발생
     * @throws RuntimeException         코드 저장 중 오류가 발생할 경우 발생
     */
    @Transactional
    public void insertCode(String codeFormType, CodeVO param) {
        if (ObjectUtils.isEmpty(param)) {
            throw new IllegalArgumentException("CodeVO 객체는 null일 수 없습니다.");
        }
        try {
            if ("U".equals(codeFormType)) {
                // 코드 업데이트
                sqlSession.update("updateCode", param);
            } else {
                // 코드 중복 확인 후 코드 삽입
                if (sqlSession.selectOne("selectCodeOne", param) != null) {
                    throw new IllegalArgumentException("이미 존재하는 코드입니다.");
                }
                sqlSession.insert("insertCode", param);
            }
        } catch (Exception e) {
            log.error("코드 저장 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("코드 추가 작업 중 오류 발생", e);
        }
    }

    /**
     * 공통 코드 단건 조회
     * - 특정 코드의 상세 정보를 조회합니다.
     *
     * @param param 조회할 코드 정보 (CodeVO)
     * @return CodeVO 조회된 공통 코드 정보
     */
    public CodeVO selectCodeOne(CodeVO param) {
        return sqlSession.selectOne("selectCodeOne", param);
    }

    /**
     * 공통 코드 삭제
     * - 특정 코드를 삭제합니다.
     *
     * @param param 삭제할 공통 코드 정보 (CodeVO)
     * @throws IllegalArgumentException 삭제할 코드가 존재하지 않을 경우 발생
     */
    @Transactional
    public void deleteCodeOne(CodeVO param) {
        if (sqlSession.delete("deleteCodeOne", param) == 0) {
            throw new IllegalArgumentException("삭제할 코드가 존재하지 않습니다.");
        }
    }
}