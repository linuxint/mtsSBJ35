package com.devkbil.mtssbj.admin.organ;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * DepartmentRepository
 * 부서 엔티티(DeptVO)에 대한 데이터 접근 계층을 관리합니다.
 * Spring Data JPA를 활용하여 기본적인 CRUD 작업을 제공합니다.
 */
@Repository
public interface DepartmentRepository extends JpaRepository<DeptVO, Long> {
    // JpaRepository를 상속받아 기본적인 CRUD 작업과 페이징, 정렬 기능을 활용할 수 있습니다.
}