package com.devkbil.mtssbj.project;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.util.UtilEtc;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Task 관련 기능의 컨트롤러 - 작업, 일정, 복사 등 각종 Task 데이터를 처리합니다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
@Tag(name = "TaskController", description = "프로젝트 작업(Task) 관련 API")
public class TaskController {

    private final ProjectService projectService;
    private final TaskService taskService;
    private final EtcService etcService;
    private final AuthService authService;

    /**
     * Task의 기본 화면.
     * - 특정 프로젝트에 대한 작업 중심 화면을 호출합니다.
     *
     * @param prno     프로젝트 번호, 없을 경우 null
     * @param request  HttpServletRequest 객체
     * @param modelMap Spring UI 데이터를 포함하는 모델 객체
     * @return Task 화면 View
     */
    @Operation(summary = "작업 중심 화면", description = "프로젝트의 작업 중심 목록 화면을 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 호출"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/task")
    public String task(@RequestParam(value = "prno", required = false) String prno, HttpServletRequest request, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        return task_do(userno, prno, modelMap);
    }

    /**
     * 일정 중심 화면.
     * - 프로젝트의 작업을 일정(Calendar) 중심으로 표시합니다.
     *
     * @param prno     프로젝트 번호
     * @param modelMap Spring UI 데이터를 포함하는 모델 객체
     * @return Task Calendar 화면 View
     */
    @Operation(summary = "일정 중심 화면", description = "프로젝트의 작업을 일정(calendar) 중심으로 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 호출"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/taskCalendar")
    public String taskCalendar(@RequestParam(value = "prno", required = false) String prno, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        String ret = task_do(userno, prno, modelMap);

        if (ret.indexOf("Task") > -1) {
            ret += "Calendar";        // return "project/TaskCalendar"
        }
        return ret;
    }

    private String task_do(String userno, String prno, ModelMap modelMap) {

        ProjectVO projectInfo = projectService.selectProjectOne(prno);

        if (ObjectUtils.isEmpty(projectInfo)) {
            return "common/noAuth";
        }

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        List<?> listview = taskService.selectTaskList(prno);

        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("prno", prno);
        modelMap.addAttribute("projectInfo", projectInfo);

        return "project/Task";
    }

    /**
     * 작업자 중심 화면.
     * - 작업자를 기준으로 작업 데이터를 확인합니다.
     */
    @Operation(summary = "작업자 중심 화면", description = "작업자를 기준으로 작업 데이터를 반환합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 호출"),
            @ApiResponse(responseCode = "403", description = "권한 없음")
    })
    @GetMapping("/taskWorker")
    public String taskWorker(@RequestParam(value = "prno", required = false) String prno, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        ProjectVO projectInfo = projectService.selectProjectOne(prno);
        if (ObjectUtils.isEmpty(projectInfo)) {
            return "common/noAuth";
        }

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        List<?> listview = taskService.selectTaskWorkerList(prno);

        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("prno", prno);
        modelMap.addAttribute("projectInfo", projectInfo);

        return "project/TaskWorker";
    }

    /**
     * 작업(Task) 저장.
     * - 작업 데이터를 추가/수정 요청 처리.
     */
    @Operation(summary = "작업 저장", description = "작업 데이터를 저장하거나 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 저장 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터 오류")
    })
    @PostMapping("/taskSave")
    public void taskSave(HttpServletResponse response, @ModelAttribute @Valid TaskVO taskInfo) {

        taskService.insertTask(taskInfo);

        UtilEtc.responseJsonValue(response, taskInfo.getTsno());
    }

    /**
     * 작업(Task) 삭제.
     * - 작업 번호(tsno)에 맞는 데이터를 삭제합니다.
     */
    @Operation(summary = "작업 삭제", description = "작업 번호(tsno)에 해당하는 작업 데이터를 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "작업을 찾을 수 없음")
    })
    @PostMapping("/taskDelete")
    public void taskDelete(@RequestParam(value = "tsno", required = false) String tsno, HttpServletResponse response) {

        taskService.deleteTaskOne(tsno);

        UtilEtc.responseJsonValue(response, "OK");
    }

    /**
     * 일정(Task) 클릭 시 호출되는 팝업.
     */
    @Operation(summary = "일정 팝업", description = "일정을 클릭했을 때 세부 정보를 반환하는 팝업 화면입니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상 처리"),
            @ApiResponse(responseCode = "404", description = "일정을 찾을 수 없음")
    })
    @PostMapping("/taskCalenPopup")
    public String taskCalenPopup(@RequestParam(value = "tsno", required = false) String tsno, ModelMap modelMap) {

        TaskVO taskInfo = taskService.selectTaskOne(tsno);

        modelMap.addAttribute("taskInfo", taskInfo);

        return "project/TaskCalenPopup";
    }

    /**
     * 작업 복사.
     * - 특정 Task 데이터를 다른 프로젝트로 복사합니다.
     */
    @Operation(summary = "작업 복사", description = "특정 작업(Task)을 다른 프로젝트로 복사합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업 복사 성공"),
            @ApiResponse(responseCode = "400", description = "입력 데이터 오류")
    })
    @GetMapping("/taskCopy")
    public String taskCopy(@RequestParam(value = "prno", required = false) String prno, @RequestParam(value = "srcno", required = false) String srcno) {

        taskService.taskCopy(new ExtFieldVO(srcno, prno, null));

        return "redirect:/task?prno=" + prno;
    }

}
