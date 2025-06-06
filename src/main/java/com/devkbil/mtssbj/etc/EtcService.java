package com.devkbil.mtssbj.etc;

import com.devkbil.mtssbj.board.BoardSearchVO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 서비스, Alert 카운트, 사용자 데이터 조회 및 관련 비즈니스 로직을 관리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Tag(name = "EtcService", description = "공통 서비스와 Alert 및 사용자 조회 관련 비즈니스 로직 처리")
public class EtcService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 공통 속성을 설정합니다 (Alert 카운트를 포함).
     *
     * @param param    사용자 ID 또는 기타 파라미터
     * @param modelMap 데이터 저장을 위한 모델맵 객체
     */
    @Operation(summary = "공통 속성 설정", description = "Alert 카운트를 포함한 공통 속성을 설정합니다.")
    public void setCommonAttribute(String param, ModelMap modelMap) {
        // 캐시된 메서드를 호출하여 알림 카운트를 가져옴
        Integer alertcount = selectAlertCount(param);
        log.debug("사용자 [{}]의 알림 카운트: {}", param, alertcount);
        modelMap.addAttribute("alertcount", alertcount);
    }

    /**
     * Alert 카운트를 조회합니다.
     * 결과는 'alertCount' 캐시에 저장되며, 키는 사용자 ID입니다.
     *
     * @param param 사용자 ID
     * @return Alert 카운트
     */
    @Cacheable(value = "alertCount", key = "#param")
    @Operation(summary = "Alert 카운트 조회", description = "사용자 ID를 기준으로 Alert 카운트를 조회합니다.")
    public Integer selectAlertCount(String param) {
        log.debug("캐시에서 찾을 수 없어 DB에서 알림 카운트 조회: 사용자 [{}]", param);
        return sqlSession.selectOne("selectAlertCount", param);
    }

    /**
     * 특정 사용자의 알림 카운트 캐시를 갱신합니다.
     * 이 메서드는 알림 데이터가 변경될 때 호출되어야 합니다.
     *
     * @param param 사용자 ID
     */
    @CacheEvict(value = "alertCount", key = "#param")
    @Operation(summary = "알림 카운트 캐시 갱신", description = "사용자 ID에 대한 알림 카운트 캐시를 갱신합니다.")
    public void refreshAlertCount(String param) {
        log.debug("사용자 [{}]의 알림 카운트 캐시 갱신", param);
    }

    /**
     * 사용자에 대한 상위 5개의 Alert 메시지를 조회합니다.
     *
     * @param param 사용자 ID
     * @return Top 5 Alert 메시지 리스트
     */
    @Operation(summary = "사용자 Alert 메시지 Top 5 조회", description = "사용자 ID를 기반으로 상위 5개의 Alert 메시지를 조회합니다.")
    public List<?> selectAlertList4Ajax(String param) {
        return sqlSession.selectList("selectAlertList4Ajax", param);
    }

    /**
     * 사용자에 대한 전체 Alert 메시지 리스트를 조회합니다.
     *
     * @param param 사용자 ID
     * @return Alert 메시지 전체 리스트
     */
    @Operation(summary = "사용자 Alert 메시지 리스트 조회", description = "사용자 ID를 기반으로 전체 Alert 메시지를 조회합니다.")
    public List<?> selectAlertList(String param) {
        return sqlSession.selectList("selectAlertList", param);
    }

    /**
     * 특정 조건의 사용자 리스트 카운트를 조회합니다.
     *
     * @param param 검색 조건 객체
     * @return 사용자 리스트 카운트
     */
    @Operation(summary = "사용자 리스트 카운트 조회", description = "검색 조건을 기준으로 사용자 리스트 카운트를 조회합니다.")
    public Integer selectList4UserCount(BoardSearchVO param) {
        return sqlSession.selectOne("selectList4UserCount", param);
    }

    /**
     * 특정 조건의 사용자 리스트를 조회합니다.
     *
     * @param param 검색 조건 객체
     * @return 사용자 리스트
     */
    @Operation(summary = "사용자 리스트 조회", description = "검색 조건을 기준으로 사용자 리스트를 조회합니다.")
    public List<?> selectList4User(BoardSearchVO param) {
        return sqlSession.selectList("selectList4User", param);
    }

    /**
     * 코드(class code) 데이터를 조회합니다.
     *
     * @param param 조회할 코드 조건
     * @return 코드 리스트
     */
    @Operation(summary = "코드 조회", description = "조건에 따라 코드(class code) 데이터를 조회합니다.")
    public List<?> selectClassCode(String param) {
        return sqlSession.selectList("selectClassCode", param);
    }

}