package com.devkbil.mtssbj.common.masking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ExampleController {
    private final ExampleService service;

    @GetMapping("/userinfo")
    public ResponseEntity<Object> userInfo(UserInfoRequestDto request) {
        UserInfoResponseDto userInfoResponseDto = service.getUserInfo(request);
        log.debug(userInfoResponseDto.toString());
        return ResponseEntity.ok(userInfoResponseDto);
    }

    @GetMapping("/userlist")
    public ResponseEntity<Object> userList(UserListRequestDto request) {
        List<UserInfoResponseDto> userInfoResponseDtoList = service.getUserInfoList(request);
        log.debug(userInfoResponseDtoList.toString());
        return ResponseEntity.ok(userInfoResponseDtoList);
    }
}