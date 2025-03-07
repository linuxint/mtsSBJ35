package com.devkbil.mtssbj.admin.organ;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

/**
 * DepartmentServiceImpl
 * 부서 관리와 관련된 비즈니스 로직을 구현하는 클래스입니다.
 * 인터페이스(DepartmentService)를 구현하여 데이터 저장, 조회, 수정 및 삭제 작업을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    // 부서 데이터 접근 레포지토리
    private final DepartmentRepository departmentRepository;

    /**
     * 새로운 부서를 저장합니다.
     *
     * @param department 저장할 부서 정보를 담고 있는 객체
     * @return 저장된 부서 정보
     */
    @Override
    public DeptVO saveDepartment(DeptVO department) {
        // 부서 정보를 저장하는 기본 JPA 메서드 호출
        return departmentRepository.save(department);
    }

    /**
     * 모든 부서의 목록을 반환합니다.
     *
     * @return 부서 정보 리스트
     */
    @Override
    public List<DeptVO> fetchDepartmentList() {
        // 모든 부서 정보를 조회
        return departmentRepository.findAll();
    }

    /**
     * 특정 부서의 정보를 수정합니다.
     *
     * @param department   수정할 부서 정보
     * @param departmentNo 수정 대상 부서의 고유 ID
     * @return 수정된 부서 정보
     * @throws IllegalArgumentException 만약 부서를 찾지 못한 경우
     */
    @Override
    public DeptVO updateDepartment(DeptVO department, Long departmentNo) {
        // 수정할 대상 부서를 ID로 검색
        Optional<DeptVO> optionalDep = departmentRepository.findById(departmentNo);

        if (optionalDep.isEmpty()) {
            throw new IllegalArgumentException("부서를 찾을 수 없습니다. ID: " + departmentNo);
        }

        DeptVO depDB = optionalDep.get();

        // 입력 값이 유효한 경우에만 해당 필드를 업데이트
        if (StringUtils.hasText(department.getDeptnm())) {
            depDB.setDeptnm(department.getDeptnm());
        }

        if (StringUtils.hasText(department.getDeptno())) {
            depDB.setDeptno(department.getDeptno());
        }

        if (StringUtils.hasText(department.getDeleteflag())) {
            depDB.setDeleteflag(department.getDeleteflag());
        }

        // 변경된 객체를 저장 후 반환
        return departmentRepository.save(depDB);
    }

    /**
     * 특정 부서를 삭제합니다.
     *
     * @param departmentNo 삭제할 부서의 고유 ID
     * @throws IllegalArgumentException 부서를 찾지 못한 경우
     */
    @Override
    public void deleteDepartmentById(Long departmentNo) {
        if (!departmentRepository.existsById(departmentNo)) {
            throw new IllegalArgumentException("삭제하려는 부서를 찾을 수 없습니다. ID: " + departmentNo);
        }

        // JPA의 기본 삭제 메서드 호출
        departmentRepository.deleteById(departmentNo);
    }
}