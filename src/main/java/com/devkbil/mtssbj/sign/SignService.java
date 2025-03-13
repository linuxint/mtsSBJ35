package com.devkbil.mtssbj.sign;

import com.devkbil.mtssbj.search.SearchVO;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignService {

    private final SqlSessionTemplate sqlSession;

    /**
     * 결재 받을 문서 총 개수를 조회합니다.
     *
     * @param param 검색 조건 (SearchVO 객체)
     * @return 결재를 대기 중인 문서 수
     */
    public int selectSignDocTobeCount(SearchVO param) {
        return sqlSession.selectOne("selectSignDocTobeCount", param);
    }

    /**
     * 결재 받을 문서 리스트를 조회합니다.
     *
     * @param param 검색 조건 (SearchVO 객체)
     * @return 결재를 대기 중인 문서 목록
     */
    public List<?> selectSignDocTobeList(SearchVO param) {
        return sqlSession.selectList("selectSignDocTobeList", param);
    }

    /**
     * 결재할 문서 총 개수를 조회합니다.
     *
     * @param param 검색 조건 (SearchVO 객체)
     * @return 사용자에 의해 처리 중인 문서 수
     */
    public int selectSignDocCount(SearchVO param) {
        return sqlSession.selectOne("selectSignDocCount", param);
    }

    /**
     * 결재할 문서 리스트를 조회합니다.
     *
     * @param param 검색 조건 (SearchVO 객체)
     * @return 사용자에 의해 처리 중인 문서 리스트
     */
    public List<?> selectSignDocList(SearchVO param) {
        return sqlSession.selectList("selectSignDocList", param);
    }

    /**
     * 결재 문서를 저장하거나 업데이트합니다.
     * <p>
     * - 신규 문서 저장 시 `insertSignDoc` 호출
     * - 기존 문서 업데이트 시 `updateSignDoc`, `deleteSign` 호출
     * - 결재 경로를 기반으로 결재 데이터를 새로 생성
     *
     * @param param 저장할 문서 정보 (SignDocVO 객체)
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertSignDoc(SignDocVO param) {
        try {
            if (param.getDocno() == null || "".equals(param.getDocno())) {
                sqlSession.insert("insertSignDoc", param);
            } else {
                sqlSession.update("updateSignDoc", param);
                sqlSession.delete("deleteSign", param.getDocno());
            }

            // 결재 경로 처리
            String docsignpath = param.getDocsignpath();
            String[] users = docsignpath.split("\\|\\|");
            String[] arr;
            SignVO param2 = new SignVO();
            for (int i = 0; i < users.length; i++) {
                if ("".equals(users[i])) {
                    continue;
                }
                arr = users[i].split(","); // 사번, 이름, 기안/합의/결제, 직책
                param2.setSsstep(Integer.toString(i));
                param2.setDocno(param.getDocno());
                param2.setUserno(arr[0]);
                param2.setSstype(arr[2]);
                param2.setUserpos(arr[3]);
                if ("0".equals(arr[2])) { // 기안 단계
                    param2.setSsresult("1");
                    param2.setSigndate("SYSDATE");
                } else { // 승인 또는 다른 단계
                    param2.setSsresult("0");
                    param2.setSigndate("''");
                }

                sqlSession.insert("insertSign", param2);

                param2 = new SignVO();
            }

        } catch (TransactionException ex) {
            log.error("insertSignDoc: 오류 발생", ex);
        }
    }

    /**
     * 특정 결재 문서의 상세 정보를 조회합니다.
     *
     * @param param 문서 식별 정보 (SignDocVO 객체)
     * @return 결재 문서 정보
     */
    public SignDocVO selectSignDocOne(SignDocVO param) {
        return sqlSession.selectOne("selectSignDocOne", param);
    }

    /**
     * 현재 단계에서의 결재자를 조회합니다.
     *
     * @param param 문서 번호
     * @return 현재 결재자의 사용자 번호
     */
    public String selectCurrentSigner(String param) {
        return sqlSession.selectOne("selectCurrentSigner", param);
    }

    /**
     * 지정된 문서의 결재 경로를 조회합니다.
     *
     * @param param 문서 번호
     * @return 결재 경로의 정보 목록
     */
    public List<?> selectSign(String param) {
        return sqlSession.selectList("selectSign", param);
    }

    /**
     * 결재 경로의 최종 단계를 조회합니다.
     *
     * @param param 문서 식별 정보 (SignDocVO 객체)
     * @return 최종 결재 경로 정보 (SignVO 목록)
     */
    public List<SignVO> selectSignLast(SignDocVO param) {
        return sqlSession.selectList("selectSignLast", param);
    }

    /**
     * Deletes a sign document entry from the database based on the provided parameters.
     *
     * @param param the SignDocVO object containing the parameters for identifying the sign document to be deleted.
     * @return the number of rows affected by the delete operation.
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteSignDoc(SignDocVO param) {
        return sqlSession.delete("deleteSignDoc", param);
    }

    /**
     * 결재 처리를 수행합니다.
     * <p>
     * - 결재 상태 변경
     * - 문서의 상태를 단계별로 업데이트
     * - 다음 단계 결재자 지정 또는 결재 완료 처리
     *
     * @param param 결재 처리 정보 (SignVO 객체)
     */
    @Transactional
    public void updateSign(SignVO param) {

        try {
            sqlSession.update("updateSign", param);

            // signdoc의 상태 변경: docstatus 변수를 사용해야 하나 그냥 ssresult로 사용
            if ("2".equals(param.getSsresult())) {    // 반려 - 결재 종료
                param.setSsresult("3");
            } else {
                String chk = sqlSession.selectOne("selectChkRemainSign", param);
                if (chk != null) { // 다음 심사가 있으면 심사 단계 설정
                    param.setSsstep("1");
                    param.setSsresult("2");
                } else { // 최종 승인
                    param.setSsresult("4");
                }
            }
            sqlSession.update("updateSignDocStatus", param);

        } catch (TransactionException ex) {
            log.error("updateSign: 오류 발생", ex);
        }
    }

    /**
     * Updates the sign document status to canceled in the database.
     *
     * @param param the identifier or parameter necessary to locate and update the appropriate sign document record.
     * @return the number of records that were updated in the database.
     */
    @Transactional
    public int updateSignDocCancel(String param) {
        return sqlSession.update("updateSignDocCancel", param);
    }
}