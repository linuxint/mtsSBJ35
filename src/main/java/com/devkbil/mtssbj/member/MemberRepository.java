package com.devkbil.mtssbj.member;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 정보 관리를 위한 Repository 인터페이스.
 * Spring Data JPA를 사용하여 데이터베이스와의 연동을 처리합니다.
 */
@Repository
public interface MemberRepository extends JpaRepository<UserVO, Long> {

    /**
     * 사용자 ID를 기반으로 회원 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional로 감싼 UserVO 객체 (회원 정보)
     */
    Optional<UserVO> findByUserid(String userId);
}
