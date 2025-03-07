package com.devkbil.mtssbj.member;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 인증을 위한 `UserDetailsService` 구현 클래스.
 * Spring Security에서 사용자 정보를 가져오기 위해 사용됩니다.
 */
@Component
@RequiredArgsConstructor
public class MyUserDetailService implements UserDetailsService {

    private final MemberService memberService;

    /**
     * 사용자 ID를 기반으로 사용자 정보를 조회하여 UserDetails 객체를 반환합니다.
     *
     * @param insertedUserId 사용자 ID
     * @return UserDetails 객체 (사용자 정보)
     * @throws UsernameNotFoundException 사용자 정보를 찾을 수 없을 때 발생
     */
    @Override
    public UserVO loadUserByUsername(String insertedUserId) throws UsernameNotFoundException {
        // 사용자 정보를 데이터베이스에서 조회
        UserVO userVO = Objects.requireNonNull(memberService.findOne(insertedUserId), () -> {
            throw new UsernameNotFoundException("User not found with ID: " + insertedUserId);
        });

//        return User.builder()
//                .username(userVO.getUserid())
//                .password(userVO.getUserpw())
//                .roles(userVO.getUserrole())
//                .build();
        // UserVO는 UserDetails를 구현하고 있으므로 직접 반환
        return userVO;
    }
}
