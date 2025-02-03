package com.devkbil.mtssbj.project;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.util.FileUtil;
import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.etc.EtcService;
import com.devkbil.mtssbj.member.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
 * 작업(TaskMine) 관리를 담당하는 컨트롤러.
 * 사용자의 개인 작업과 관련된 모든 작업을 처리합니다.
 */
@Controller
@Slf4j
@RequiredArgsConstructor
public class TaskMineController {

    private final TaskService taskService;
    private final EtcService etcService;
    private final ProjectService projectService;
    private final AuthService authService;

    /**
     * 사용자의 작업 목록 조회.
     *
     * @param modelMap 결과 데이터를 담을 모델맵 객체
     * @return 개인 작업(TaskMine) 페이지 경로
     */
    @Operation(summary = "사용자의 작업 목록 조회", description = "사용자가 담당한 작업 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업 목록 페이지 반환"),
            @ApiResponse(responseCode = "403", description = "사용 권한 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/taskMine")
    public String taskMine(@RequestParam(value = "prno", required = false) String prno, ModelMap modelMap) {

        String userno = authService.getAuthUserNo();

        ProjectVO projectInfo = projectService.selectProjectOne(prno);
        if (ObjectUtils.isEmpty(projectInfo)) {
            return "common/noAuth";
        }

        Integer alertcount = etcService.selectAlertCount(userno);
        modelMap.addAttribute("alertcount", alertcount);

        List<?> listview = taskService.selectTaskMineList(new ExtFieldVO(prno, userno, null));

        modelMap.addAttribute("listview", listview);
        modelMap.addAttribute("prno", prno);
        modelMap.addAttribute("projectInfo", projectInfo);

        return "project/TaskMine";
    }

    /**
     * 특정 작업의 세부 정보를 조회하고 수정 폼 제공.
     *
     * @param modelMap 결과 데이터를 담을 모델맵 객체
     * @return 작업 상세보기 및 수정 폼 페이지 경로
     */
    @Operation(summary = "작업 세부 정보 조회", description = "특정 작업의 세부 정보를 조회하고 수정 가능한 폼을 제공합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업 수정 폼 페이지 반환"),
            @ApiResponse(responseCode = "404", description = "작업 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @GetMapping("/taskMineForm")
    public String taskMineForm(@RequestParam(value = "tsno") String tsno, ModelMap modelMap) {

        TaskVO taskInfo = taskService.selectTaskOne(tsno);
        List<?> listview = taskService.selectTaskFileList(tsno);

        modelMap.addAttribute("taskInfo", taskInfo);
        modelMap.addAttribute("listview", listview);

        return "project/TaskMineForm";
    }

    /**
     * 개인 작업 저장 (수정 또는 생성).
     *
     * @param taskInfo 저장하려는 작업 정보
     * @return 개인 작업 목록 화면으로 리다이렉트
     */
    @Operation(summary = "작업 저장", description = "사용자가 수정하거나 새로 생성한 작업 데이터를 저장합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "저장 작업 완료 후 목록 페이지로 리다이렉트"),
            @ApiResponse(responseCode = "500", description = "서버 에러")
    })
    @PostMapping("/taskMineSave")
    public String taskMineSave(@RequestParam(value = "fileno", required = false) String[] fileno
            , @ModelAttribute @Valid TaskVO taskInfo) {

        // 요청 파일 데이터 처리
        FileUtil fs = new FileUtil();
        List<FileVO> filelist = fs.saveAllFiles(taskInfo.getUploadfile());

        taskService.insertTaskMine(taskInfo, filelist, fileno);

        return "redirect:taskMine?prno=" + taskInfo.getPrno();
    }
}
