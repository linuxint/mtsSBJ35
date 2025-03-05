package com.devkbil.mtssbj.admin.organ;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * DeptMapper 인터페이스
 * - 부서 관련 데이터베이스 작업(조회, 삽입, 수정, 삭제)을 처리합니다.
 * - MyBatis를 사용하여 SQL 매핑을 담당합니다.
 */
@Mapper
public interface DeptMapper {

    /**
     * 모든 부서 정보를 조회합니다.
     * 데이터베이스에 등록된 전체 부서 목록을 반환합니다.
     *
     * @return 부서 리스트 (List)
     */
    List<?> selectDept();

    /**
     * 부서 삽입
     * - 새로운 부서를 데이터베이스에 등록합니다.
     *
     * @param param 삽입할 부서 정보 (DeptVO)
     * @return 처리 결과 (삽입된 행의 수, int)
     */
    int insertDept(DeptVO param);

    /**
     * 부서 수정
     * - 기존 부서 정보를 업데이트합니다.
     *
     * @param param 수정할 부서 정보 (DeptVO)
     * @return 처리 결과 (수정된 행의 수, int)
     */
    int updateDept(DeptVO param);

    /**
     * 단일 부서 정보 조회
     * - 특정 부서 번호(deptno)에 해당하는 부서 정보를 반환합니다.
     *
     * @param deptno 조회할 부서 번호 (String)
     * @return 해당 부서 정보 (DeptVO)
     */
    DeptVO selectDeptOne(@Param("deptno") String deptno);

    /**
     * 부서 삭제 (논리 삭제)
     * - 특정 부서를 삭제 상태로 업데이트합니다.
     * - 데이터는 실제로 삭제되지 않으며, 삭제 여부(플래그)로 표시됩니다.
     *
     * @param deptno 삭제할 부서 번호 (String)
     * @return 처리 결과 (삭제된 행의 수, int)
     */
    int deleteDept(@Param("deptno") String deptno);
}