package com.devkbil.mtssbj.project;

import com.devkbil.mtssbj.common.ExtFieldVO;
import com.devkbil.mtssbj.common.FileVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TaskService
 * - Task(업무) 및 TaskMine(내 업무) 관련 비즈니스 로직을 처리하는 서비스 클래스.
 * - 데이터베이스와 상호 작용하며 트랜잭션 처리를 포함합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {

    private final SqlSessionTemplate sqlSession;

    /**
     * Task 목록 조회.
     *
     * @param param 프로젝트 또는 특정 조건 (예: 프로젝트 번호)
     * @return Task 목록
     */
    public List<?> selectTaskList(String param) {
        return sqlSession.selectList("selectTaskList", param);
    }

    /**
     * Task 작업자 목록 조회.
     *
     * @param param Task 번호
     * @return Task 작업자 목록
     */
    public List<?> selectTaskWorkerList(String param) {
        return sqlSession.selectList("selectTaskWorkerList", param);
    }

    /**
     * Task 저장.
     * - 신규 Task는 INSERT.
     * - 기존 Task는 UPDATE 및 관련 작업자 데이터 초기화 후 재삽입.
     *
     * @param param Task 정보 데이터
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTask(TaskVO param) {

        try {
            // 신규 Task인지 확인
            if (!StringUtils.hasText(param.getTsno())) { // Task 번호(TSNO)가 없으면 INSERT
                if ("".equals(param.getTsparent())) {
                    param.setTsparent(null); // 부모 Task가 없을 경우 null 처리
                }
                sqlSession.insert("insertTask", param);
            } else { // Task 번호가 있으면 UPDATE
                sqlSession.update("updateTask", param);
                sqlSession.delete("deleteTaskUser", param.getTsno()); // 기존 작업자 데이터 삭제
            }

            // 작업자 데이터 저장
            String userno = param.getUserno();
            if (StringUtils.hasText(userno)) {
                ExtFieldVO fld = new ExtFieldVO(param.getTsno(), null, null);
                // Note: split(",") may not handle spaces around commas. Consider split("\\s*,\\s*") for robustness.
                String[] usernos = userno.split("\\s*,\\s*");

                for (String uno : usernos) {
                    if (StringUtils.hasText(uno)) {
                        fld.setField2(uno); // 각 작업자의 ID 설정
                        sqlSession.update("insertTaskUser", fld);
                    }
                }
            }
        } catch (TransactionException ex) {
            log.error("insertTask 트랜잭션 오류 발생: {}", ex.getMessage());
            throw ex; // 트랜잭션 롤백
        }
    }

    /**
     * Task 상세 조회.
     *
     * @param param Task 번호
     * @return Task 상세 정보
     */
    public TaskVO selectTaskOne(String param) {
        return sqlSession.selectOne("selectTaskOne", param);
    }

    /**
     * Task 관련 파일 목록 조회.
     *
     * @param param Task 번호
     * @return Task 파일 목록
     */
    public List<?> selectTaskFileList(String param) {
        return sqlSession.selectList("selectTaskFileList", param);
    }

    /**
     * 특정 Task를 삭제합니다.
     *
     * @param param 삭제할 Task의 식별자
     * @return 삭제된 행 수
     */
    public int deleteTaskOne(String param) {
        return sqlSession.delete("deleteTaskOne", param);
    }

    /**
     * TaskMine 목록 조회.
     * - 개인별로 할당된 Task 목록 가져오기.
     *
     * @param param 사용자와 관련된 확장 필드 정보
     * @return TaskMine 목록
     */
    public List<?> selectTaskMineList(ExtFieldVO param) {
        return sqlSession.selectList("selectTaskMineList", param);
    }

    /**
     * TaskMine 저장.
     * - 개인 Task 데이터를 업데이트하고, 관련 파일 저장 (첨부 파일 포함).
     *
     * @param param    Task 정보
     * @param filelist 첨부 파일 목록
     * @param fileno   업데이트할 파일 번호 배열
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertTaskMine(TaskVO param, List<FileVO> filelist, String[] fileno) {

        try {
            // TaskMine 업데이트
            sqlSession.update("updateTaskMine", param);

            // 기존 파일 업데이트
            if (!ObjectUtils.isEmpty(fileno)) {
                HashMap<String, String[]> fparam = new HashMap<String, String[]>();
                fparam.put("fileno", fileno);
                sqlSession.update("updateTaskFile", fparam);
            }

            // 새로운 파일 저장
            for (FileVO file : filelist) {
                file.setParentPK(param.getTsno()); // Task 번호 설정
                sqlSession.insert("insertTaskFile", file);
            }
        } catch (TransactionException ex) {
            log.error("insertTaskMine 트랜잭션 오류 발생: {}", ex.getMessage());
            throw ex; // 트랜잭션 롤백
        }
    }

    /**
     * TaskMine 복사.
     * - 기존 Task 데이터를 복사하여 새 TaskMine 생성.
     *
     * @param param 복사할 정보를 담고 있는 확장 필드
     */
    @Transactional(rollbackFor = Exception.class)
    public void taskCopy(ExtFieldVO param) {

        try {
            sqlSession.insert("taskCopy_step1", param); // 복사 단계 1
            sqlSession.update("taskCopy_step2", param.getField2()); // 복사 단계 2 (예: 프로젝트 번호)
        } catch (TransactionException ex) {
            log.error("taskCopy 트랜잭션 오류 발생: {}", ex.getMessage());
            throw ex; // 트랜잭션 롤백
        }
    }

}