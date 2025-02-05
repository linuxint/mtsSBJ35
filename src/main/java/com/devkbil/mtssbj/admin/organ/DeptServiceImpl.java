package com.devkbil.mtssbj.admin.organ;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * DeptServiceImpl 클래스
 * - DeptService 인터페이스의 구현체로, 부서 데이터를 관리하기 위한 비즈니스 로직을 제공합니다.
 * - 데이터베이스 연동은 DeptMapper를 통해 이루어집니다.
 * - 트랜잭션 및 조건에 따라 삽입, 수정, 삭제 등의 작업을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class DeptServiceImpl implements DeptService {

    private final DeptMapper deptMapper; // MyBatis Mapper 인터페이스

    /**
     * 부서 리스트 조회
     * - 모든 부서를 조회하여 반환합니다.
     *
     * @return 부서 리스트 (List<?>)
     */
    @Override
    public List<?> selectDept() {
        return deptMapper.selectDept();
    }

    /**
     * 부서 저장 (삽입 또는 업데이트)
     * - 부서 번호(deptno)가 없는 경우 새로운 부서를 삽입.
     * - 부서 번호(deptno)가 있는 경우 기존 부서를 업데이트.
     * - parentno(부모 부서 번호)가 빈 문자열일 경우 null로 변환 후 처리.
     *
     * @param param 삽입 또는 수정할 부서 정보 (DeptVO)
     * @return 처리된 행 수 (int, 삽입 또는 수정된 행 수)
     */
    @Override
    @Transactional
    public int insertDept(DeptVO param) {
        int affectedRows = 0;

        // 부모 번호가 빈 문자열이면 null로 처리
        if ("".equals(param.getParentno())) {
            param.setParentno(null);
        }

        // 부서 번호가 없으면 삽입, 있으면 업데이트
        if (!StringUtils.hasText(param.getDeptno())) {
            affectedRows = deptMapper.insertDept(param); // 신규 삽입
        } else {
            affectedRows = deptMapper.updateDept(param); // 기존 데이터 업데이트
        }

        return affectedRows;
    }

    /**
     * 단일 부서 조회
     * - 부서 번호(deptno)를 통해 특정 부서 정보를 조회합니다.
     *
     * @param deptno 조회할 부서 번호 (String)
     * @return 조회된 부서 정보 (DeptVO)
     */
    @Override
    public DeptVO selectDeptOne(String deptno) {
        return deptMapper.selectDeptOne(deptno);
    }

    /**
     * 부서 삭제 (논리 삭제)
     * - 부서 번호(deptno)를 사용하여 해당 데이터를 삭제 상태로 변경합니다.
     * - 물리적 삭제가 아닌 논리적 삭제로 처리.
     *
     * @param deptno 삭제할 부서 번호 (String)
     * @return 처리된 행 수 (int, 삭제된 행 수)
     */
    @Override
    @Transactional
    public int deleteDept(String deptno) {
        return deptMapper.deleteDept(deptno); // 논리 삭제 처리
    }

}