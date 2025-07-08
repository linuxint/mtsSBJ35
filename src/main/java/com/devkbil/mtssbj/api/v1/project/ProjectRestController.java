package com.devkbil.mtssbj.api.v1.project;

import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.project.ProjectService;
import com.devkbil.mtssbj.project.ProjectVO;
import com.devkbil.mtssbj.search.SearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 프로젝트 관리를 담당하는 REST API 컨트롤러.
 * 프로젝트와 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/project")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Project API", description = "프로젝트 관리 API")
public class ProjectRestController {

    private final ProjectService projectService;
    private final AuthService authService;

    /**
     * 프로젝트 목록 조회 API.
     *
     * @param searchVO 검색 조건을 담은 객체
     * @return 프로젝트 목록 및 페이징 정보를 담은 ResponseEntity
     */
    @Operation(summary = "프로젝트 목록 조회", description = "검색 조건을 기반으로 프로젝트 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 목록 조회 성공"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getProjectList(@ModelAttribute @Valid SearchVO searchVO) {
        Map<String, Object> result = new HashMap<>();

        searchVO.pageCalculate(projectService.selectProjectCount(searchVO)); // startRow, endRow
        List<?> listview = projectService.selectProjectList(searchVO);

        result.put("searchVO", searchVO);
        result.put("list", listview);

        return ResponseEntity.ok(result);
    }

    /**
     * 프로젝트 상세 조회 API.
     *
     * @param prno 조회할 프로젝트 번호
     * @return 프로젝트 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "프로젝트 상세 조회", description = "프로젝트 번호로 프로젝트 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{prno}")
    public ResponseEntity<ProjectVO> getProject(@PathVariable String prno) {
        ProjectVO projectInfo = projectService.selectProjectOne(prno);
        
        if (projectInfo == null || projectInfo.getPrno() == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(projectInfo);
    }

    /**
     * 프로젝트 저장 API.
     *
     * @param projectInfo 저장하려는 프로젝트 정보
     * @return 저장된 프로젝트 정보를 담은 ResponseEntity
     */
    @Operation(summary = "프로젝트 저장", description = "신규 또는 수정된 프로젝트 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "프로젝트 저장 성공"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<ProjectVO> saveProject(@RequestBody @Valid ProjectVO projectInfo) {
        String userno = authService.getAuthUserNo();
        projectInfo.setUserno(userno);

        // 프로젝트 번호가 존재할 경우(수정 모드), 권한 검사를 진행
        if (StringUtils.hasText(projectInfo.getPrno())) {
            String chk = projectService.selectProjectAuthChk(projectInfo);
            if (chk == null) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }

        projectService.insertProject(projectInfo);
        
        // 저장 후 최신 정보 조회
        ProjectVO savedProject = projectService.selectProjectOne(projectInfo.getPrno());
        return ResponseEntity.ok(savedProject);
    }

    /**
     * 프로젝트 삭제 API.
     *
     * @param prno 삭제할 프로젝트의 고유 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "프로젝트 삭제", description = "지정된 프로젝트를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "프로젝트 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "403", description = "권한 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{prno}")
    public ResponseEntity<Void> deleteProject(@PathVariable String prno) {
        // 프로젝트 번호가 비어 있으면 예외 처리
        if (!StringUtils.hasText(prno)) {
            return ResponseEntity.badRequest().build();
        }

        String userno = authService.getAuthUserNo();

        ProjectVO projectInfo = new ProjectVO();
        projectInfo.setPrno(prno);
        projectInfo.setUserno(userno);

        String chk = projectService.selectProjectAuthChk(projectInfo);
        if (!StringUtils.hasText(chk)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        projectService.deleteProjectOne(prno);

        return ResponseEntity.noContent().build();
    }
}