package com.devkbil.mtssbj.admin.organ;

import com.devkbil.mtssbj.member.UserVO;
import com.devkbil.mtssbj.search.SearchVO;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 사용자 관련 서비스 (UserService)
 * - 사용자 데이터를 조회, 등록, 수정, 삭제하는 기능을 제공합니다.
 * - 데이터베이스와의 연동은 MyBatis(SqlSessionTemplate)를 사용합니다.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final SqlSessionTemplate sqlSession; // MyBatis 연동을 위한 SqlSessionTemplate 사용

    /**
     * 사용자 리스트 조회
     * - 부서 번호 또는 특정 조건에 따른 사용자 데이터를 조회합니다.
     *
     * @param param 부서 번호 또는 기타 조건 (String)
     * @return 사용자 리스트 (List<?>)
     */
    public List<?> selectUserList(String param) {
        return sqlSession.selectList("selectUserList", param);
    }

    /**
     * 부서 정보 포함 사용자 리스트 조회
     * - 검색 조건(SearchVO)을 사용하여 사용자와 부서를 포함한 데이터를 조회합니다.
     *
     * @param param 검색 조건을 담은 객체 (SearchVO)
     * @return 사용자 리스트 (부서 정보 포함) (List<?>)
     */
    public List<?> selectUserListWithDept(SearchVO param) {
        return sqlSession.selectList("selectUserListWithDept", param);
    }

    /**
     * 사용자 등록 및 업데이트
     * - 사용자 정보를 저장(신규)하거나 업데이트(기존)합니다.
     * - userno(사용자 번호)가 없는 경우 신규 저장, 있는 경우 업데이트 로직 수행.
     *
     * @param param 저장할 사용자 정보 (UserVO)
     * @return 처리된 결과 (int, 영향받은 행 수)
     */
    public int insertUser(UserVO param) {
        if (!StringUtils.hasText(param.getUserno())) {
            // 신규 사용자 등록
            return sqlSession.insert("insertUser", param);
        } else {
            // 기존 사용자 정보 업데이트
            return sqlSession.insert("updateUser", param);
        }
    }

    /**
     * 사용자 ID 조회
     * - ID의 중복 여부에서 사용되거나, 특정 사용자의 ID를 확인할 때 사용됩니다.
     *
     * @param param 사용자 ID (String)
     * @return 해당 ID가 존재하면 ID 반환, 없으면 null 반환 (String)
     */
    public String selectUserID(String param) {
        return sqlSession.selectOne("selectUserID", param);
    }

    /**
     * 특정 사용자 정보 조회
     * - 사용자 번호(userno)를 사용하여 사용자 상세 정보를 조회합니다.
     *
     * @param param 사용자 번호 (String)
     * @return 사용자 정보 객체 (UserVO)
     */
    public UserVO selectUserOne(String param) {
        return sqlSession.selectOne("selectUserOne", param);
    }

    /**
     * 사용자 삭제
     * - 사용자 번호(userno)를 사용하여 해당 사용자를 삭제합니다.
     *
     * @param param 사용자 번호 (String)
     * @return 처리된 결과 (삭제된 행 수, int)
     */
    public int deleteUser(String param) {
        return sqlSession.delete("deleteUser", param);
    }

    /**
     * 사용자 자기 정보 수정
     * - 현재 로그인된 사용자가 자신의 정보를 수정할 때 사용됩니다.
     *
     * @param param 수정할 사용자 정보 (UserVO)
     * @return 처리된 결과 (수정된 행 수, int)
     */
    public int updateUserByMe(UserVO param) {
        return sqlSession.delete("updateUserByMe", param);
    }

    /**
     * 사용자 비밀번호 변경
     * - 사용자 비밀번호를 특정 조건에 따라 업데이트합니다.
     * - `@SneakyThrows`를 사용하여 예외 처리를 간소화합니다.
     *
     * @param param 비밀번호 변경 정보를 담은 객체 (UserVO)
     * @return 처리된 결과 (변경된 행 수, int)
     */
    @SneakyThrows
    public int updateUserPassword(UserVO param) {
        return sqlSession.delete("updateUserPassword", param);
    }
}