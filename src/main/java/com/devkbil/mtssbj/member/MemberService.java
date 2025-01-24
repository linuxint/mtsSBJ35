package com.devkbil.mtssbj.member;

import com.devkbil.mtssbj.search.SearchVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 사용자 관리와 관련된 서비스 클래스.
 * 사용자 로그인, 로그아웃, 검색 및 기타 사용자 관련 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
public class MemberService {

    private final SqlSessionTemplate sqlSession;
    private final MemberRepository repository;

    /**
     * 검색 조건에 따른 회원 수 조회.
     *
     * @param param 검색 조건 객체
     * @return 검색된 회원 수
     */
    public Integer selectSearchMemberCount(SearchVO param) {
        return sqlSession.selectOne("selectSearchMemberCount", param);
    }

    /**
     * 검색 조건에 따른 회원 목록 조회.
     *
     * @param param 검색 조건 객체
     * @return 검색된 회원 목록
     */
    public List<?> selectSearchMemberList(SearchVO param) {
        return sqlSession.selectList("selectSearchMemberList", param);
    }

    /**
     * 로그인 조건에 따라 회원 정보 조회 (로그인 처리에 사용).
     *
     * @param param 회원 정보 요청 객체
     * @return 조회된 사용자 정보 VO
     * @throws Exception SQL 문제가 발생할 경우 예외 발생
     */
    @SneakyThrows
    public UserVO selectMember4Login(UserVO param) {
        return sqlSession.selectOne("selectMember4Login", param);
    }

    /**
     * 로그인 로그 기록을 삽입합니다.
     *
     * @param param 사용자 ID 또는 정보
     */
    public void insertLogIn(String param) {
        sqlSession.insert("insertLogIn", param);
    }

    /**
     * 로그아웃 로그 기록을 삽입합니다.
     *
     * @param param 사용자 ID 또는 정보
     */
    public void insertLogOut(String param) {
        sqlSession.insert("insertLogOut", param);
    }

    /**
     * 사용자 ID를 기반으로 회원 정보를 조회합니다.
     *
     * @param userId 사용자 ID
     * @return Optional 형태의 사용자 VO 객체
     */
    public Optional<UserVO> findOne(String userId) {
        return repository.findByUserid(userId);
    }
}
