package com.devkbil.mtssbj.common.masking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 사용자 정보 마스킹 처리를 위한 예제 컨트롤러입니다.
 * 이 컨트롤러는 개인정보 보호를 위해 사용자 정보에 마스킹을 적용하여 반환합니다.
 * 단일 사용자 정보 조회와 사용자 목록 조회 기능을 제공합니다.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService service;

    /**
     * 단일 사용자의 마스킹된 정보를 조회합니다.
     *
     * @param request 사용자 정보 조회 요청 DTO
     * @return 마스킹 처리된 사용자 정보를 포함한 ResponseEntity
     */
    @GetMapping("/userinfo")
    public ResponseEntity<Object> userInfo(UserInfoRequestDto request) {
        UserInfoResponseDto userInfoResponseDto = service.getUserInfo(request);
        log.debug(userInfoResponseDto.toString());
        return ResponseEntity.ok(userInfoResponseDto);
    }

    /**
     * 여러 사용자의 마스킹된 정보 목록을 조회합니다.
     *
     * @param request 사용자 목록 조회 요청 DTO
     * @return 마스킹 처리된 사용자 정보 목록을 포함한 ResponseEntity
     */
    @GetMapping("/userlist")
    public ResponseEntity<Object> userList(UserListRequestDto request) {
        List<UserInfoResponseDto> userInfoResponseDtoList = service.getUserInfoList(request);
        log.debug(userInfoResponseDtoList.toString());
        return ResponseEntity.ok(userInfoResponseDtoList);
    }
}
