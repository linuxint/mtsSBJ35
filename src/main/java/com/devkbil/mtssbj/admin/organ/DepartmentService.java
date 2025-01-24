package com.devkbil.mtssbj.admin.organ;

import java.util.List;

/**
 * DepartmentService
 * 부서 관리와 관련된 비즈니스 로직을 정의하는 인터페이스입니다.
 * 주요 작업에는 저장, 조회, 갱신, 삭제가 포함됩니다.
 */
public interface DepartmentService {

    /**
     * 새로운 부서를 저장합니다.
     *
     * @param department 저장할 부서 정보를 포함하는 객체
     * @return 저장된 부서 정보
     */
    DeptVO saveDepartment(DeptVO department);

    /**
     * 모든 부서의 목록을 조회합니다.
     *
     * @return 부서 목록
     */
    List<DeptVO> fetchDepartmentList();

    /**
     * 특정 부서를 수정합니다.
     *
     * @param department   수정할 부서 정보
     * @param departmentNo 수정 대상이 되는 부서의 고유 ID
     * @return 수정된 부서 정보
     */
    DeptVO updateDepartment(DeptVO department, Long departmentNo);

    /**
     * 특정 부서를 삭제합니다.
     *
     * @param departmentNo 삭제할 부서의 고유 ID
     */
    void deleteDepartmentById(Long departmentNo);
}