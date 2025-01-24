package com.devkbil.mtssbj.common.masking;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ExampleService {

    private HashMap<String, UserInfoResponseDto> users;

    @PostConstruct
    public void init() {
        users = new HashMap<>();
        users.put("1", new UserInfoResponseDto("1", "", "010-1234-5678", "honggildong@email.com"));
        users.put("2", new UserInfoResponseDto("2", "이영희", "010-9999-8765", "yhlee@email.com"));
        users.put("3", new UserInfoResponseDto("3", "김철수", "010-5678-4321", "ironwater@email.com"));
    }

    @ApplyMasking(typeValue = UserInfoResponseDto.class) // UserInfoResponseDto 에 마스킹 적용
    public UserInfoResponseDto getUserInfo(UserInfoRequestDto request) {
        UserInfoResponseDto userInfo = users.get(request.getId());
        return userInfo;
    }

    @ApplyMasking(typeValue = List.class, genericTypeValue = UserInfoResponseDto.class)
    public List<UserInfoResponseDto> getUserInfoList(UserListRequestDto request) {
        return new ArrayList<>(users.values());
    }
}