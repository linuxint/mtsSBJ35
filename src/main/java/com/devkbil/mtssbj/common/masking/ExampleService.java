package com.devkbil.mtssbj.common.masking;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 데이터 마스킹과 함께 사용자 정보를 검색하는 기능을 제공하는 서비스 클래스.
 * <p>
 * 이 클래스는 사용자 데이터를 초기화하고 사용자 ID를 기반으로 사용자 정보 또는
 * 사용자 리스트를 검색하여 반환하는 메서드를 제공합니다. 반환 값에는 데이터 마스킹이
 * 적용되어 민감한 사용자 정보를 보호합니다.
 * <p>
 * 책임:
 * - HashMap에 사용자 데이터를 애플리케이션 시작 시 초기화.
 * - 사용자 ID를 기반으로 사용자 정보를 검색 및 반환.
 * - 모든 사용자의 리스트 검색 및 반환.
 * <p>
 * 데이터 마스킹:
 * - {@code ApplyMasking} 애노테이션을 사용하여 민감한 데이터 필드가 마스킹되도록 설정.
 * 마스킹 설정은 {@code UserInfoResponseDto}에서 지정.
 */
@Service
public class ExampleService {

    private HashMap<String, UserInfoResponseDto> users;

    /**
     * 애플리케이션 시작 시 HashMap에 사용자 데이터를 초기화.
     * 이 메서드는 Spring 프레임워크에 의해 클래스의 모든 의존성이 주입된 이후
     * 실행되도록 {@code @PostConstruct} 애노테이션이 적용되어 있습니다.
     * <p>
     * 초기화된 데이터는 다음과 같은 필드로 구성된 사전 정의된 사용자 정보를 제공합니다:
     * - userId: 사용자의 고유 식별자.
     * - userName: 사용자의 이름. 일부 경우는 빈 문자열일 수 있습니다.
     * - phoneNumber: 사용자의 연락처.
     * - email: 사용자의 이메일 주소.
     * <p>
     * 초기화된 데이터는 클래스 내부에서 사용자 관련 작업에 사용됩니다.
     */
    @PostConstruct
    public void init() {
        users = new HashMap<>();
        users.put("1", new UserInfoResponseDto("1", "", "010-1234-5678", "honggildong@email.com"));
        users.put("2", new UserInfoResponseDto("2", "이영희", "010-9999-8765", "yhlee@email.com"));
        users.put("3", new UserInfoResponseDto("3", "김철수", "010-5678-4321", "ironwater@email.com"));
    }

    /**
     * 요청에서 제공된 정보를 기반으로 사용자 정보를 검색.
     * 반환된 사용자 정보에 {@code ApplyMasking} 애노테이션을 사용하여 데이터 마스킹 적용.
     *
     * @param request {@code UserInfoRequestDto} 객체로, 사용자 식별 정보를 포함.
     * @return {@code UserInfoResponseDto} 객체로, 마스킹된 사용자 정보를 포함.
     * 요청한 ID에 사용자가 없을 경우 {@code null} 반환.
     */
    @ApplyMasking(typeValue = UserInfoResponseDto.class) // UserInfoResponseDto 에 마스킹 적용
    public UserInfoResponseDto getUserInfo(UserInfoRequestDto request) {
        UserInfoResponseDto userInfo = users.get(request.getId());
        return userInfo;
    }

    /**
     * 사용자 정보 리스트를 검색하며, 데이터 마스킹을 적용.
     * 반환된 리스트는 {@code UserInfoResponseDto} 객체로 표현된 사용자 정보를 포함.
     * 데이터 마스킹은 사용자 정보의 민감한 필드를 보호함.
     *
     * @param request {@code UserListRequestDto} 객체로, 사용자 리스트 검색에 필요한 파라미터 포함.
     * @return {@code UserInfoResponseDto} 객체의 리스트로, 각 객체는 마스킹된 사용자 정보를 포함.
     * 사용자 데이터가 없을 경우 비어 있는 리스트를 반환할 수 있음.
     */
    @ApplyMasking(typeValue = List.class, genericTypeValue = UserInfoResponseDto.class)
    public List<UserInfoResponseDto> getUserInfoList(UserListRequestDto request) {
        return new ArrayList<>(users.values());
    }
}
