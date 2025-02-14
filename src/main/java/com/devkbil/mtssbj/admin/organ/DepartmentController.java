package com.devkbil.mtssbj.admin.organ;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * DepartmentController
 * 부서 관리와 관련된 CRUD 작업을 처리하는 REST 컨트롤러입니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Tag(name = "DepartmentController", description = "부서 관리 API") // Swagger API 태그
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * 부서를 생성하고 저장합니다.
     *
     * @param department 저장할 부서 정보를 포함하는 객체
     * @return 저장된 부서 정보
     */
    @PostMapping("/departments")
    @Operation(summary = "부서 생성", description = "새로운 부서를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서가 성공적으로 생성되었습니다."),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생하였습니다.")
    })
    public DeptVO saveDepartment(@ModelAttribute @Valid DeptVO department) {
        return departmentService.saveDepartment(department);
    }

    /**
     * 모든 부서를 조회합니다.
     *
     * @return 부서 목록
     */
    @GetMapping("/departments")
    @Operation(summary = "부서 목록 조회", description = "모든 부서의 리스트를 반환합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청이 성공적으로 수행되었습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생하였습니다.")
    })
    public List<DeptVO> fetchDepartmentList() {
        return departmentService.fetchDepartmentList();
    }

    /**
     * 특정 부서의 정보를 수정합니다.
     *
     * @param department   수정할 부서 정보
     * @param departmentNo 수정 대상 부서를 식별하는 부서 번호
     * @return 업데이트된 부서 정보
     */
    @PutMapping("/departments/{id}")
    @Operation(summary = "부서 수정", description = "주어진 ID의 부서 정보를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서가 성공적으로 수정되었습니다."),
        @ApiResponse(responseCode = "404", description = "해당 ID의 부서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생하였습니다.")
    })
    public DeptVO updateDepartment(@ModelAttribute DeptVO department, @PathVariable("id") Long departmentNo) {
        return departmentService.updateDepartment(department, departmentNo);
    }

    /**
     * 특정 부서를 삭제합니다.
     *
     * @param departmentNo 삭제할 부서의 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/departments/{id}")
    @Operation(summary = "부서 삭제", description = "주어진 ID의 부서를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "부서가 성공적으로 삭제되었습니다."),
        @ApiResponse(responseCode = "404", description = "해당 ID의 부서를 찾을 수 없습니다."),
        @ApiResponse(responseCode = "500", description = "서버에 오류가 발생하였습니다.")
    })
    public String deleteDepartmentById(@PathVariable("id") Long departmentNo) {
        departmentService.deleteDepartmentById(departmentNo);

        return "Deleted Successfully";
    }
}