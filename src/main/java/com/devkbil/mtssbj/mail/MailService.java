package com.devkbil.mtssbj.mail;

import com.devkbil.mtssbj.common.util.FileVO;
import com.devkbil.mtssbj.search.SearchVO;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * MailService
 * 메일 데이터 처리와 관련된 비즈니스 로직을 담당하는 서비스 클래스.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final SqlSessionTemplate sqlSession;

    // ================================ 메일 수신 관련 메서드 ================================

    /**
     * 수신 메일 수 카운트 조회.
     *
     * @param param 검색 조건(SearchVO)
     * @return 수신 메일 수(Integer)
     */
    public Integer selectReceiveMailCount(SearchVO param) {
        return sqlSession.selectOne("selectReceiveMailCount", param);
    }

    /**
     * 수신 메일 리스트 조회.
     *
     * @param param 검색 조건(SearchVO)
     * @return 수신 메일 리스트(List<?>)
     */
    public List<?> selectReceiveMailList(SearchVO param) {
        return sqlSession.selectList("selectReceiveMailList", param);
    }

    /**
     * 상세 메일 조회.
     *
     * @param param 메일 키 정보를 포함한 MailVO 객체
     * @return 상세 메일 정보(MailVO)
     */
    public MailVO selectReceiveMailOne(MailVO param) {
        MailVO mail = sqlSession.selectOne("selectReceiveMailOne", param);
        if (mail != null) {

            // 메일 주소 설정
            MailAddressVO addressVO = new MailAddressVO();
            addressVO.setEmno(param.getEmno());

            // 수신인(To) 추가
            addressVO.setEatype("t");
            List<String> addressList = sqlSession.selectList("selectMailAddressList", addressVO);
            mail.setEmto((ArrayList<String>) addressList);

            // 참조인(Cc) 추가
            addressVO.setEatype("c");
            addressList = sqlSession.selectList("selectMailAddressList", addressVO);
            mail.setEmcc((ArrayList<String>) addressList);

            // 숨은 참조인(Bcc) 추가
            addressVO.setEatype("b");
            addressList = sqlSession.selectList("selectMailAddressList", addressVO);
            mail.setEmbcc((ArrayList<String>) addressList);

            // 첨부파일 추가
            List<FileVO> files = sqlSession.selectList("selectMailFileList", addressVO);
            mail.setFiles((ArrayList<FileVO>) files);

        }
        return mail;
    }

    // ================================ 메일 삭제 관련 메서드 ================================

    /**
     * 단일 메일 삭제.
     *
     * @param param 삭제할 메일(MailVO)
     */
    public void deleteMail(MailVO param) {
        sqlSession.update("deleteMail", param);
    }

    /**
     * 다중 메일 삭제.
     *
     * @param param 삭제할 메일 ID 배열
     */
    public void deleteMails(String[] param) {
        HashMap<String, String[]> hm = new HashMap<>();
        hm.put("list", param);

        sqlSession.insert("deleteMails", hm);
    }

    // ================================ 메일 전송 및 삽입 관련 메서드 ================================

    /**
     * 다중 메일 삽입 처리.
     *
     * @param param  삽입할 메일 리스트(ArrayList<MailVO>)
     * @param userno 사용자 번호
     * @param emino  메일정보 번호
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMails(ArrayList<MailVO> param, String userno, String emino) {
        try {
            for (MailVO mail : param) {
                mail.setUserno(userno);
                mail.setEmtype("R");
                mail.setEmino(emino);
                insertMailOne(mail);
            }
        } catch (TransactionException ex) {
            log.error("Error during insertMails", ex);
            throw ex;
        }
    }

    /**
     * 메일 삽입 및 SMTP 전송.
     * 메일 주소(To, Cc, Bcc)를 처리하고, 메일 내용을 전송합니다.
     *
     * @param param 메일 데이터(MailVO)
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMail(MailVO param) {
        String[] to = param.getStrTo().split(";");
        String[] cc = StringUtils.hasText(param.getStrCc()) ? param.getStrCc().split(";") : new String[]{};
        String[] bcc = StringUtils.hasText(param.getStrBcc()) ? param.getStrBcc().split(";") : new String[]{};

        // 주소 필드 설정
        param.setEmto(new ArrayList<>(Arrays.asList(to)));
        param.setEmcc(new ArrayList<>(Arrays.asList(cc)));
        param.setEmbcc(new ArrayList<>(Arrays.asList(bcc)));

        try {
            // 발신자 정보 조회
            MailInfoVO fromVO = sqlSession.selectOne("selectMailInfoOne", param.getEmfrom());
            param.setEmino(param.getEmfrom());
            param.setEmfrom(fromVO.getEmiuser());

            // 메일 DB에 삽입
            insertMailOne(param);

            // SMTP 전송
            SendMail mailSender = new SendMail(
                fromVO.getEmismtp(),
                fromVO.getEmismtpport(),
                fromVO.getEmiuser(),
                fromVO.getUsernm(),
                fromVO.getEmipw()
            );
            mailSender.send(true, to, cc, bcc, param.getEmsubject(), param.getEmcontents());

        } catch (TransactionException | MessagingException ex) {
            log.error("Error during insertMail", ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * 단일 메일 데이터 삽입.
     *
     * @param mail 메일 데이터(MailVO)
     */
    public void insertMailOne(MailVO mail) {
        MailAddressVO addressVO = new MailAddressVO();
        sqlSession.insert("insertMail", mail);

        addressVO.setEmno(mail.getEmno());

        // 수신인(To) 주소 삽입
        addressVO.setEatype("t");
        insertMailAddress(mail.getEmto(), addressVO);

        // 참조인(Cc) 주소 삽입
        addressVO.setEatype("c");
        insertMailAddress(mail.getEmcc(), addressVO);

        // 숨은 참조인(Bcc) 주소 삽입
        addressVO.setEatype("b");
        insertMailAddress(mail.getEmbcc(), addressVO);

        // 첨부파일 삽입
        ArrayList<FileVO> files = mail.getFiles();
        for (FileVO file : files) {
            file.setParentPK(mail.getEmno());
            sqlSession.insert("insertMailFile", file);
        }
    }

    /**
     * 메일 주소 삽입 처리.
     *
     * @param addressesList 주소 데이터 리스트(ArrayList<String>)
     * @param addressVO     주소 VO(MailAddressVO)
     */
    public void insertMailAddress(ArrayList<String> addressesList, MailAddressVO addressVO) {
        String email;
        for (int j = 0; j < addressesList.size(); j++) {
            email = addressesList.get(j);
            if (!StringUtils.hasText(email)) {
                continue;
            }
            addressVO.setEaseq(j);
            addressVO.setEaaddress(email);
            sqlSession.insert("insertMailAddress", addressVO);
        }
    }

    public String selectLastMail(String param) {
        return sqlSession.selectOne("selectLastMail", param);
    }

    // ================================ 메일 정보 관련 메서드 ================================

    /**
     * 메일 정보 리스트 조회.
     *
     * @param param 검색 조건(SearchVO)
     * @return 메일 정보 리스트(List<?>)
     */
    public Integer selectMailInfoCount(SearchVO param) {
        return sqlSession.selectOne("selectMailInfoCount", param);
    }

    /**
     * 메일 정보 리스트 조회.
     *
     * @param param 검색 조건(SearchVO)
     * @return 메일 정보 리스트(List<?>)
     */
    public List<?> selectMailInfoList(String param) {
        return sqlSession.selectList("selectMailInfoList", param);
    }

    /**
     * 메일 정보 저장.
     *
     * @param param 저장할 메일 정보(MailInfoVO)
     */
    @Transactional(rollbackFor = Exception.class)
    public void insertMailInfo(MailInfoVO param) {
        try {
            if (!StringUtils.hasText(param.getEmino())) {  // 신규 삽입
                sqlSession.insert("insertMailInfo", param);
            } else {  // 기존 데이터 업데이트
                sqlSession.update("updateMailInfo", param);
            }
        } catch (TransactionException ex) {
            log.error("Error during insertMailInfo", ex);
            throw ex;
        }
    }

    /**
     * 메일 정보 단일 데이터 조회.
     *
     * @param param MailInfoVO 객체
     * @return 메일 정보(MailInfoVO)
     */
    public MailInfoVO selectMailInfoOne(MailInfoVO param) {
        return sqlSession.selectOne("selectMailInfoOne", param);
    }

    /**
     * 메일 정보 삭제.
     *
     * @param param 삭제할 메일 정보(MailInfoVO)
     */
    public void deleteMailInfo(MailInfoVO param) {
        sqlSession.update("deleteMailInfo", param);
    }
}