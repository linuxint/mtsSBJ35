package com.devkbil.mtssbj.api.v1.project;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import com.devkbil.mtssbj.project.ProjectService;
import com.devkbil.mtssbj.project.ProjectVO;
import com.devkbil.mtssbj.project.TaskService;
import com.devkbil.mtssbj.project.TaskVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 태스크 관리를 담당하는 REST API 컨트롤러.
 * 태스크와 관련된 CRUD 및 기타 기능을 REST API로 제공합니다.
 */
@RestController
@RequestMapping("/api/v1/task")
@Slf4j
@RequiredArgsConstructor
@Tag(name = "Task API", description = "태스크 관리 API")
public class TaskRestController {

    private final TaskService taskService;
    private final ProjectService projectService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * 프로젝트별 태스크 목록 조회 API.
     *
     * @param prno 프로젝트 번호
     * @return 태스크 목록을 담은 ResponseEntity
     */
    @Operation(summary = "프로젝트별 태스크 목록 조회", description = "프로젝트 번호로 해당 프로젝트의 태스크 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/list/{prno}")
    public ResponseEntity<Map<String, Object>> getTaskList(@PathVariable String prno) {

        // 프로젝트 정보 조회
        ProjectVO projectInfo = Optional.ofNullable(projectService.selectProjectOne(prno))
            .filter(p -> p.getPrno() != null)
            .orElse(null);

        if (projectInfo == null) {
            return ResponseEntity.notFound().build();
        }

        // 태스크 목록 조회
        List<?> taskList = taskService.selectTaskList(prno);

        Map<String, Object> response = Map.of(
            "projectInfo", projectInfo,
            "taskList", taskList
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 태스크 캘린더 조회 API.
     *
     * @param prno 프로젝트 번호
     * @return 캘린더 형식의 태스크 목록을 담은 ResponseEntity
     */
    @Operation(summary = "태스크 캘린더 조회", description = "프로젝트 번호로 해당 프로젝트의 태스크를 캘린더 형식으로 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 캘린더 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/calendar/{prno}")
    public ResponseEntity<Map<String, Object>> getTaskCalendar(@PathVariable String prno) {

        // 프로젝트 정보 조회
        ProjectVO projectInfo = projectService.selectProjectOne(prno);
        if (projectInfo == null || projectInfo.getPrno() == null) {
            return ResponseEntity.notFound().build();
        }

        // 태스크 목록 조회 (캘린더 형식으로 표시할 데이터)
        List<?> calendarList = taskService.selectTaskList(prno);

        Map<String, Object> response = Map.of(
            "projectInfo", projectInfo,
            "calendarList", calendarList
        );

        return ResponseEntity.ok(response);
    }

    /**
     * 태스크 담당자 목록 조회 API.
     *
     * @param prno 프로젝트 번호
     * @return 태스크 담당자 목록을 담은 ResponseEntity
     */
    @Operation(summary = "태스크 담당자 목록 조회", description = "프로젝트 번호로 해당 프로젝트의 태스크 담당자 목록을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 담당자 목록 조회 성공"),
        @ApiResponse(responseCode = "404", description = "프로젝트를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/workers/{prno}")
    public ResponseEntity<Map<String, Object>> getTaskWorkers(@PathVariable String prno) {
        Map<String, Object> result = new HashMap<>();

        // 프로젝트 정보 조회
        ProjectVO projectInfo = projectService.selectProjectOne(prno);
        if (projectInfo == null || projectInfo.getPrno() == null) {
            return ResponseEntity.notFound().build();
        }

        // 태스크 담당자 목록 조회
        List<?> workerList = taskService.selectTaskWorkerList(prno);

        result.put("projectInfo", projectInfo);
        result.put("workerList", workerList);

        return ResponseEntity.ok(result);
    }

    /**
     * 태스크 상세 조회 API.
     *
     * @param tsno 태스크 번호
     * @return 태스크 상세 정보를 담은 ResponseEntity
     */
    @Operation(summary = "태스크 상세 조회", description = "태스크 번호로 태스크 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 상세 조회 성공"),
        @ApiResponse(responseCode = "404", description = "태스크를 찾을 수 없음"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/{tsno}")
    public ResponseEntity<Map<String, Object>> getTask(@PathVariable String tsno) {
        Map<String, Object> result = new HashMap<>();

        // 태스크 정보 조회
        TaskVO taskInfo = taskService.selectTaskOne(tsno);
        if (taskInfo == null || taskInfo.getTsno() == null) {
            return ResponseEntity.notFound().build();
        }

        result.put("taskInfo", taskInfo);

        return ResponseEntity.ok(result);
    }

    /**
     * 태스크 저장 API.
     *
     * @param taskInfo 저장하려는 태스크 정보
     * @return 저장된 태스크 정보를 담은 ResponseEntity
     */
    @Operation(summary = "태스크 저장", description = "신규 또는 수정된 태스크 정보를 저장합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 저장 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping
    public ResponseEntity<TaskVO> saveTask(@RequestBody @Valid TaskVO taskInfo) {
        String userno = authService.getAuthUserNo();
        taskInfo.setUserno(userno);

        // 태스크 저장
        taskService.insertTask(taskInfo);

        // 저장 후 최신 정보 조회
        TaskVO savedTask = taskService.selectTaskOne(taskInfo.getTsno());
        return ResponseEntity.ok(savedTask);
    }

    /**
     * 태스크 삭제 API.
     *
     * @param tsno 삭제할 태스크의 고유 번호
     * @return 삭제 결과를 담은 ResponseEntity
     */
    @Operation(summary = "태스크 삭제", description = "지정된 태스크를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "태스크 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @DeleteMapping("/{tsno}")
    public ResponseEntity<Void> deleteTask(@PathVariable String tsno) {
        // 태스크 번호가 비어 있으면 예외 처리
        if (!StringUtils.hasText(tsno)) {
            return ResponseEntity.badRequest().build();
        }

        taskService.deleteTaskOne(tsno);

        return ResponseEntity.noContent().build();
    }

    /**
     * 태스크 복사 API.
     *
     * @param prno 프로젝트 번호
     * @param srcno 복사할 태스크 번호
     * @return 복사 결과를 담은 ResponseEntity
     */
    @Operation(summary = "태스크 복사", description = "기존 태스크를 복사하여 새로운 태스크를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태스크 복사 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/copy")
    public ResponseEntity<Map<String, Object>> copyTask(
            @RequestParam String prno,
            @RequestParam String srcno) {

        // 프로젝트 번호나 원본 태스크 번호가 비어 있으면 예외 처리
        if (!StringUtils.hasText(prno) || !StringUtils.hasText(srcno)) {
            return ResponseEntity.badRequest().build();
        }

        // 태스크 복사
        taskService.taskCopy(new ExtFieldVO(srcno, prno, null));

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "태스크가 성공적으로 복사되었습니다.");

        return ResponseEntity.ok(result);
    }
}
