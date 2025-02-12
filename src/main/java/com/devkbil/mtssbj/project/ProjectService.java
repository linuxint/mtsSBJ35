package com.devkbil.mtssbj.project;

import com.devkbil.mtssbj.search.SearchVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 프로젝트 관련 비즈니스 로직 제공 서비스 클래스.
 * 데이터베이스 접근 및 처리 로직을 담당합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProjectService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 프로젝트 개수를 조회합니다.
     *
     * @param param 검색 조건을 담은 객체
     * @return 검색 조건에 해당하는 프로젝트의 총 개수
     */
    public Integer selectProjectCount(SearchVO param) {
        return sqlSession.selectOne("selectProjectCount", param);
    }

    /**
     * 프로젝트 목록을 조회합니다.
     *
     * @param param 검색 조건을 담은 객체
     * @return 조건에 해당하는 프로젝트 목록
     */
    public List<?> selectProjectList(SearchVO param) {
        return sqlSession.selectList("selectProjectList", param);
    }

    /**
     * 프로젝트를 저장합니다.
     * - 신규 프로젝트의 경우 INSERT 수행.
     * - 기존 프로젝트는 UPDATE 수행.
     *
     * @param param 저장할 프로젝트 데이터를 담은 객체
     */
    public int insertProject(ProjectVO param) {

        if (!StringUtils.hasText(param.getPrno())) { // 프로젝트 번호가 없으면 새 프로젝트로 간주
            return sqlSession.insert("insertProject", param);
        } else { // 프로젝트 번호가 있으면 업데이트
            return sqlSession.update("updateProject", param);
        }
    }

    /**
     * 특정 프로젝트의 상세 정보를 조회합니다.
     *
     * @param param 프로젝트 번호
     * @return 프로젝트 상세 정보
     */
    public ProjectVO selectProjectOne(String param) {

        ProjectVO projectVO;

        if(StringUtils.hasText(param)) {
            projectVO = sqlSession.selectOne("selectProjectOne", param);
        } else {
            log.warn("프로젝트 번호 [{}]에 대한 조회 결과가 없습니다. 기본값을 초기화합니다.", param);
            String today = LocalDate.now().toString(); // 현재 날짜
            projectVO = new ProjectVO();
            projectVO.setPrstartdate(today);
            projectVO.setPrenddate(today);
            projectVO.setPrstatus("0"); // 기본 상태
        }

        return projectVO;

    }

    /**
     * 프로젝트 수정/삭제 권한을 확인합니다.
     *
     * @param param 체크할 권한 정보를 담은 프로젝트 객체
     * @return 권한 체크 결과 (NULL일 경우 권한 없음)
     */
    public String selectProjectAuthChk(ProjectVO param) {
        return sqlSession.selectOne("selectProjectAuthChk", param);
    }

    /**
     * 특정 프로젝트를 삭제합니다.
     *
     * @param param 삭제할 프로젝트 번호
     */
    public int deleteProjectOne(String param) {
        return sqlSession.delete("deleteProjectOne", param);
    }

}
