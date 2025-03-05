package com.devkbil.mtssbj.admin.organ;

import java.util.List;

/**
 * DeptService 인터페이스
 * - 부서 정보를 관리하기 위한 서비스 로직의 인터페이스 정의.
 * - 부서 데이터의 조회, 저장, 수정, 삭제 기능 제공.
 */
public interface DeptService {

    /**
     * 부서 리스트 조회
     * - 등록된 모든 부서 정보를 반환합니다.
     *
     * @return 부서 리스트 (List)
     */
    List<?> selectDept();

    /**
     * 부서 삽입/업데이트
     * - 부서를 새롭게 생성하거나 기존 부서 데이터를 수정합니다.
     *
     * @param param 삽입하거나 수정할 부서 정보 (DeptVO)
     * @return 처리 결과 (삽입/수정된 행의 개수, int)
     */
    int insertDept(DeptVO param);

    /**
     * 단일 부서 조회
     * - 부서 번호를 사용하여 특정 부서 정보를 반환합니다.
     *
     * @param deptno 조회할 부서 번호 (String)
     * @return 해당 부서 정보 (DeptVO)
     */
    DeptVO selectDeptOne(String deptno);

    /**
     * 부서 삭제 (논리 삭제)
     * - 특정 부서를 삭제 상태로 변경합니다.
     *
     * @param deptno 삭제할 부서 번호 (String)
     * @return 처리 결과 (삭제된 행의 개수, int)
     */
    int deleteDept(String deptno);

}