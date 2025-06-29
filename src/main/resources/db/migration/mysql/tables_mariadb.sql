CREATE TABLE PERSISTENT_LOGINS (
    USERNAME VARCHAR(64) NOT NULL,
    SERIES VARCHAR(64) PRIMARY KEY,
    TOKEN VARCHAR(64) NOT NULL,
    LAST_USED TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) COMMENT = 'remember-me 토큰 저장소';

CREATE TABLE PRJ_PROJECT
(
    PRNO        INT          NOT NULL AUTO_INCREMENT COMMENT '프로젝트 번호',
    PRSTARTDATE VARCHAR(10)  NULL                    COMMENT '시작일자',
    PRENDDATE   VARCHAR(10)  NULL                    COMMENT '종료일자',
    PRTITLE     VARCHAR(100) NULL                    COMMENT '프로젝트 제목',
    REGDATE      DATETIME     NULL                    COMMENT '등록일자',
    USERNO      INT          NULL                    COMMENT '사용자번호',
    PRSTATUS    CHAR(1)      NULL                    COMMENT '상태',
    DELETEFLAG  CHAR(1)      NULL                    COMMENT '삭제',
    PRIMARY KEY (PRNO)
) COMMENT ='프로젝트';

CREATE TABLE PRJ_TASK
(
    PRNO        INT           NULL                    COMMENT '프로젝트 번호',
    TSNO        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '업무번호',
    TSPARENT    BIGINT        NULL                    COMMENT '부모업무번호',
    TSSORT      MEDIUMINT(10) NULL                    COMMENT '정렬',
    TSTITLE     VARCHAR(100)  NULL                    COMMENT '업무 제목',
    TSSTARTDATE VARCHAR(10)   NULL                    COMMENT '시작일자',
    TSENDDATE   VARCHAR(10)   NULL                    COMMENT '종료일자',
    TSENDREAL   VARCHAR(10)   NULL                    COMMENT '종료일자(실제)',
    TSRATE      SMALLINT      NULL                    COMMENT '진행율',
    OLDNO       BIGINT        NULL                    COMMENT '이전업무번호',
    DELETEFLAG  CHAR(1)       NULL                    COMMENT '삭제',
    PRIMARY KEY (TSNO)
) COMMENT ='프로젝트 업무';

CREATE TABLE PRJ_TASKUSER
(
    TSNO   BIGINT NOT NULL COMMENT '업무번호',
    USERNO INT    NOT NULL COMMENT '사용자번호',
    PRIMARY KEY (TSNO, USERNO)
) COMMENT ='업무할당';

CREATE TABLE PRJ_TASKFILE
(
    TSNO     BIGINT       NULL COMMENT '업무번호',
    FILENO   INT(10)      NOT NULL AUTO_INCREMENT COMMENT '파일번호',
    FILENAME VARCHAR(100) NULL COMMENT '파일명',
    REALNAME VARCHAR(30)  NULL COMMENT '실제파일명',
    FILESIZE INT(10)      NULL COMMENT '파일크기',
    DELETEFLAG CHAR(1) DEFAULT 'N',
    PRIMARY KEY (FILENO)
) COMMENT ='첨부파일';


-- ------------------------- Project 9 ---------------------------
-- DROP TABLE COM_CODE;

CREATE TABLE COM_CODE
(
    CLASSNO INT(11)     COMMENT '대분류',
    CODECD  VARCHAR(10) COMMENT '코드',
    CODENM  VARCHAR(30) COMMENT '코드명',
    PRIMARY KEY (CLASSNO, CODECD)
) COMMENT = '공통코드';

-- CODE TABLE V2
DROP TABLE COM_CODE_V2 CASCADE CONSTRAINTS;
CREATE TABLE COM_CODE_V2
(
    CODECD     VARCHAR(5),         -- 코드
    PCODECD    VARCHAR(5),         -- 코드시컨스
    CODENM     VARCHAR(40),        -- 코드명
    CODEDESC   VARCHAR(100),       -- 코드설명
    DELETEFLAG CHAR(1) DEFAULT 'N', -- 사용여부
    REGDATE    DATETIME, -- 등록일자
    REGUSERNO  INT, -- 등록사용자
    CHGDATE    DATETIME, -- 변경일자
    CHGUSERNO  INT, -- 변경사용자
    CONSTRAINT COM_CODE_V2_PK PRIMARY KEY (CODECD),
    CONSTRAINT COM_CODE_V2_UK UNIQUE      (CODECD, PCODECD),
    CONSTRAINT COM_CODE_V2_FK FOREIGN KEY (PCODECD) REFERENCES COM_CODE_V2(CODECD)

) COMMENT = '곧통코드';
COMMENT ON TABLE COM_CODE_V2 IS '공통코드';
COMMENT ON COLUMN COM_CODE_V2.CODECD     IS '코드';
COMMENT ON COLUMN COM_CODE_V2.PCODECD    IS '상위코드';
COMMENT ON COLUMN COM_CODE_V2.CODENM     IS '코드명';
COMMENT ON COLUMN COM_CODE_V2.CODEDESC   IS '코드설명';
COMMENT ON COLUMN COM_CODE_V2.DELETEFLAG IS '사용여부';
COMMENT ON COLUMN COM_CODE_V2.REGDATE IS '등록일자';
COMMENT ON COLUMN COM_CODE_V2.REGUSERNO IS '등록사용자';
COMMENT ON COLUMN COM_CODE_V2.CHGDATE IS '변경일자';
COMMENT ON COLUMN COM_CODE_V2.CHGUSERNO IS '변경사용자';

-- DROP TABLE COM_DEPT;

CREATE TABLE COM_DEPT
(
    DEPTNO     INT(11) NOT NULL AUTO_INCREMENT,     -- 부서 번호
    DEPTNM     VARCHAR(20), -- 부서명
    PARENTNO   INT(11) default 0,     -- 부모 부서
    DELETEFLAG CHAR(1),     -- 삭제 여부
    PRIMARY KEY (DEPTNO)
) COMMENT = '그룹 부서';


-- DROP TABLE COM_USER;

CREATE TABLE COM_USER
(
    USERNO     INT(11) NOT NULL AUTO_INCREMENT, -- 사용자 번호
    USERID     VARCHAR(20),                     -- ID
    USERNM     VARCHAR(20),                     -- 이름
    USERPW     VARCHAR(100),                    -- 비밀번호
    USERROLE   CHAR(1),                         -- 권한
    PHOTO      VARCHAR(50),                     -- 사진
    DEPTNO     INT,
    DELETEFLAG CHAR(1),                         -- 삭제 여부
    USERPOS    varchar(2) COMMENT '직위',
    PRIMARY KEY (USERNO)
);

/*------------------------------------------*/

-- DROP TABLE TBL_BOARD;

CREATE TABLE TBL_BOARD
(
    BGNO          INT(11),                         -- 게시판 그룹번호
    BRDNO         INT(11) NOT NULL AUTO_INCREMENT, -- 글 번호
    BRDTITLE      VARCHAR(255),                    -- 글 제목
    USERNO        INT,                             -- 작성자
    BRDMEMO       MEDIUMTEXT,                      -- 글 내용
    REGDATE       DATETIME,                        -- 작성일자
    BRDNOTICE     VARCHAR(1),                      -- 공지사항여부
    CHGDATE       DATETIME,
    CHGUSERNO     INT,
    BRDLIKE       INT default 0,                   -- 좋아요
    DELETEFLAG CHAR(1),                         -- 삭제 여부
    ETC1          VARCHAR(200),
    ETC2          VARCHAR(200),
    ETC3          VARCHAR(200),
    ETC4          VARCHAR(200),
    ETC5          VARCHAR(200),
    PRIMARY KEY (BRDNO)
);

-- DROP TABLE TBL_BOARDFILE;

CREATE TABLE TBL_BOARDFILE
(
    FILENO   INT(10) NOT NULL AUTO_INCREMENT, -- 파일 번호
    BRDNO    INT(11),                         -- 글번호
    FILENAME VARCHAR(100),                    -- 파일명
    REALNAME VARCHAR(30),                     -- 실제파일명
    FILESIZE INT,                             -- 파일 크기
    DELETEFLAG CHAR(1)         default 'N',      -- 삭제 여부

        PRIMARY KEY (FILENO)
);

CREATE TABLE TBL_BOARDLIKE
(
    BLNO   INT(11) NOT NULL AUTO_INCREMENT, -- 번호
    BRDNO  INT(11),                         -- 글번호
    USERNO INT,                             -- 작성자
    REGDATE DATETIME,                        -- 등록일자
    PRIMARY KEY (BLNO)
);

-- DROP TABLE TBL_BOARDREPLY;

CREATE TABLE TBL_BOARDREPLY
(
    BRDNO        INT(11)    NOT NULL,       -- 게시물 번호
    RENO         INT(11)    NOT NULL AUTO_INCREMENT,       -- 댓글 번호
    USERNO       INT,                       -- 작성자
    REMEMO       VARCHAR(500) DEFAULT NULL, -- 댓글내용
    REPARENT     INT(11),                   -- 부모 댓글
    REDEPTH      INT,                       -- 깊이
    REORDER      INT,                       -- 순서
    REGDATE       DATETIME     DEFAULT NULL, -- 작성일자
    REDELETEFLAG VARCHAR(1) NOT NULL,       -- 삭제여부
    CHGDATE     DATETIME,
    CHGUSERNO   INT,
    REDELDATE   DATETIME,
    DELDATE     DATETIME,
    PRIMARY KEY (RENO)
) COMMENT = "게시판 댓글";

-- ALTER TABLE TBL_BOARDREPLY ADD REDELDATE DATETIME;
-- ALTER TABLE TBL_BOARD ADD DELDATE DATETIME;

-- DROP TABLE TBL_BOARDREAD;

CREATE TABLE TBL_BOARDREAD
(
    BRDNO    INT(11) NOT NULL, -- 게시물 번호
    USERNO   INT,              -- 작성자
    REGDATE  DATETIME,         -- 작성일자
    PRIMARY KEY (BRDNO, USERNO)
);


-- DROP TABLE TBL_BOARDGROUP;

CREATE TABLE TBL_BOARDGROUP
(
    BGNO         INT(11) NOT NULL AUTO_INCREMENT, -- 게시판 그룹번호
    BGNAME       VARCHAR(50),                     -- 게시판 그룹명
    BGPARENT     INT(11),                         -- 게시판 그룹 부모
    DELETEFLAG CHAR(1),                         -- 삭제 여부
    BGUSED       CHAR(1),                         -- 사용 여부
    BGREPLY      CHAR(1),                         -- 댓글 사용여부
    BGREADONLY   CHAR(1),                         -- 글쓰기 가능 여부
    BGNOTICE     CHAR(1),                         -- 공지 쓰기  가능 여부
    REGDATE      DATETIME,                        -- 등록일자
    CHGDATE      DATETIME,                        -- 변경일자
    PRIMARY KEY (BGNO)
);

-- DROP TABLE COM_LOGINOUT;

CREATE TABLE COM_LOGINOUT
(
    LNO    INT(11) NOT NULL AUTO_INCREMENT, -- 순번
    USERNO INT,                             -- 로그인 사용자
    LTYPE  CHAR(1),                         -- in / out
    REGDATE  DATETIME,                        -- 발생일자
    PRIMARY KEY (LNO)
);

CREATE TABLE TBL_CRUD
(
    CRNO         INT NOT NULL AUTO_INCREMENT, -- 번호
    CRTITLE      VARCHAR(255),                -- 제목
    USERNO       INT,                         -- 작성자
    CRMEMO       MEDIUMTEXT,                  -- 내용
    REGDATE       DATETIME,                    -- 작성일자
    DELETEFLAG CHAR(1),                     -- 삭제 여부
    PRIMARY KEY (CRNO)
);


-- 날짜
CREATE TABLE COM_DATE
(
    CDNO          bigint NOT NULL AUTO_INCREMENT COMMENT '번호',
    CDDATE        varchar(10) COMMENT '날짜',
    CDYEAR        smallint COMMENT '년도',
    CDMM          smallint COMMENT '월',
    CDDD          smallint COMMENT '일',
    CDWEEKOFYEAR  smallint COMMENT 'WEEKOFYEAR',
    CDWEEKOFMONTH smallint COMMENT 'WEEKOFMONTH',
    CDWEEK        smallint COMMENT 'WEEK',
    CDDAYOFWEEK   smallint COMMENT 'DAYOFWEEK',
    CDLUNARYEAR   smallint COMMENT '음력년도',
    CDLUNARMONTH  smallint COMMENT '음력월',
    CDLUNARDAY    smallint COMMENT '음력일',
    CDLUNARLEAP   char(1) DEFAULT 'N' COMMENT '음력윤달여부',
    PRIMARY KEY (CDNO),
    UNIQUE (CDNO)
) COMMENT = '날짜';

-- 메일주소
CREATE TABLE EML_ADDRESS
(
    EMNO      INT(10) NOT NULL COMMENT '메일번호',
    EASEQ     INT     NOT NULL COMMENT '순번',
    EATYPE    CHAR(1) NOT NULL COMMENT '주소종류',
    EAADDRESS VARCHAR(150) COMMENT '메일주소',
    PRIMARY KEY (EMNO, EASEQ, EATYPE)
) COMMENT = '메일주소' DEFAULT CHARACTER SET utf8
                   COLLATE utf8_general_ci;


-- 메일
CREATE TABLE EML_MAIL
(
    EMNO       INT(10) NOT NULL AUTO_INCREMENT COMMENT '메일번호',
    EMTYPE     CHAR(1) COMMENT '메일종류',
    EMSUBJECT  VARCHAR(150) COMMENT '제목',
    EMFROM     VARCHAR(150) COMMENT '보낸사람',
    EMCONTENTS MEDIUMTEXT COMMENT '내용',
    REGDATE  DATETIME COMMENT '등록일자',
    USERNO     INT(10) NOT NULL COMMENT '사용자번호',
    EMINO      INT(10) NOT NULL COMMENT '메일정보번호',
    DELETEFLAG CHAR(1) COMMENT '삭제',
    PRIMARY KEY (EMNO),
    UNIQUE (EMNO)
) COMMENT = '메일';


-- 첨부파일
CREATE TABLE EML_MAILFILE
(
    FILENO   INT(10) NOT NULL AUTO_INCREMENT COMMENT '파일번호',
    FILENAME VARCHAR(100) COMMENT '파일명',
    REALNAME VARCHAR(30) COMMENT '실제파일명',
    FILESIZE INT(10) COMMENT '파일크기',
    EMNO     INT(10) NOT NULL COMMENT '메일번호',
    PRIMARY KEY (FILENO),
    UNIQUE (FILENO)
) COMMENT = '첨부파일';


-- 메일정보
CREATE TABLE EML_MAILINFO
(
    EMINO       INT(10) NOT NULL AUTO_INCREMENT COMMENT '메일정보번호',
    EMIIMAP     VARCHAR(30) COMMENT 'IMAP서버주소',
    EMIIMAPPORT VARCHAR(5) COMMENT 'IMAP서버포트',
    EMISMTP     VARCHAR(30) COMMENT 'SMTP 서버주소',
    EMISMTPPORT VARCHAR(5) COMMENT 'SMTP 서버포트',
    EMIUSER     VARCHAR(50) COMMENT '계정',
    EMIPW       VARCHAR(50) COMMENT '비밀번호',
    USERNO      INT(10) NOT NULL COMMENT '사용자번호',
    REGDATE     DATE COMMENT '등록일자',
    DELETEFLAG  CHAR(1) COMMENT '삭제여부',
    PRIMARY KEY (EMINO),
    UNIQUE (EMINO)
) COMMENT = '메일정보';


-- 일정상세
CREATE TABLE SCH_DETAIL
(
    SSNO     INT(10)  NOT NULL COMMENT '일정번호',
    SDSEQ    SMALLINT NOT NULL COMMENT '순번',
    SDDATE   DATE COMMENT '날짜',
    SDHOUR   VARCHAR(2) COMMENT '시간',
    SDMINUTE VARCHAR(2) COMMENT '분',
    PRIMARY KEY (SSNO, SDSEQ),
    UNIQUE (SSNO, SDSEQ)
) COMMENT = '일정상세';


-- 공휴일
DROP TABLE IF EXISTS SCH_HOLIDAY;
CREATE TABLE SCH_HOLIDAY
(
    SHNO       SMALLINT NOT NULL AUTO_INCREMENT COMMENT '번호',
    SHTITLE    VARCHAR(20) COMMENT '공휴일명',
    SHYEAR     SMALLINT COMMENT '년(년도 한정 공휴일/임시공휴일)',
    SHMONTH    SMALLINT COMMENT '월',
    SHDAY      SMALLINT COMMENT '일',
    SHLUNAR_YN CHAR(1) DEFAULT 'N' COMMENT '음력여부',
    SHALT_YN   CHAR(1) DEFAULT 'N' COMMENT '대체휴무여부',
    SHTYPE     CHAR(1) COMMENT '휴무유형(1-당일휴무,3-당일포함 3일휴무)',
    SHCOLOR    VARCHAR(10) COMMENT '색상',
    DELETEFLAG CHAR(1) DEFAULT 'N' COMMENT '삭제',
    PRIMARY KEY (SHNO),
    UNIQUE (SHNO)
) COMMENT = '공휴일';


-- 일정
CREATE TABLE SCH_SCHEDULE
(
    SSNO           INT(10) NOT NULL AUTO_INCREMENT COMMENT '일정번호',
    SSTITLE        VARCHAR(50) COMMENT '일정명',
    SSTYPE         CHAR(1) COMMENT '구분',
    SSSTARTDATE    VARCHAR(10) COMMENT '시작일',
    SSSTARTHOUR    VARCHAR(2) COMMENT '시작일-시간',
    SSSTARTMINUTE  VARCHAR(2) COMMENT '시작일-분',
    SSENDDATE      VARCHAR(10) COMMENT '종료일',
    SSENDHOUR      VARCHAR(2) COMMENT '종료일-시간',
    SSENDMINUTE    VARCHAR(2) COMMENT '종료일-분',
    SSREPEATTYPE   CHAR(1) COMMENT '반복',
    SSREPEATOPTION VARCHAR(2) COMMENT '반복옵션',
    SSREPEATEND    VARCHAR(10) COMMENT '반복종료',
    SSCONTENTS     TEXT COMMENT '내용',
    SSISOPEN       CHAR(1) COMMENT '공개여부',
    CHGDATE     DATETIME COMMENT '수정일자',
    REGDATE     DATETIME COMMENT '작성일자',
    USERNO         INT(10) NOT NULL COMMENT '사용자번호',
    DELETEFLAG     CHAR(1) COMMENT '삭제',
    PRIMARY KEY (SSNO),
    UNIQUE (SSNO)
) COMMENT = '일정';


-- 결재문서
CREATE TABLE SGN_DOC
(
    DOCNO       BIGINT  NOT NULL AUTO_INCREMENT COMMENT '문서번호',
    DOCTITLE    VARCHAR(50) COMMENT '제목',
    DOCCONTENTS TEXT COMMENT '문서내용',
    DELETEFLAG  CHAR(1) COMMENT '삭제여부',
    DOCSTATUS   CHAR(1) COMMENT '문서상태',
    DOCSTEP     SMALLINT COMMENT '결재단계',
    DTNO        INT     NOT NULL COMMENT '문서양식번호',
    CHGDATE     DATETIME COMMENT '수정일자',
    REGDATE     DATETIME COMMENT '작성일자',
    USERNO      INT(10) NOT NULL COMMENT '사용자번호',
    DEPTNM      VARCHAR(20) COMMENT '부서명',
    DOCSIGNPATH VARCHAR(200) COMMENT '결재경로문자열',
    PRIMARY KEY (DOCNO),
    UNIQUE (DOCNO)
) COMMENT = '결재문서';


-- 첨부파일
CREATE TABLE SGN_DOCFILE
(
    FILENO   INT(10) NOT NULL AUTO_INCREMENT COMMENT '파일번호',
    FILENAME VARCHAR(100) COMMENT '파일명',
    REALNAME VARCHAR(30) COMMENT '실제파일명',
    FILESIZE INT(10) COMMENT '파일크기',
    DOCNO    BIGINT  NOT NULL COMMENT '문서번호',
    PRIMARY KEY (FILENO),
    UNIQUE (FILENO)
) COMMENT = '첨부파일';


-- 문서양식종류
CREATE TABLE SGN_DOCTYPE
(
    DTNO       INT NOT NULL AUTO_INCREMENT COMMENT '문서양식번호',
    DTTITLE    VARCHAR(30) COMMENT '문서양식명',
    DTCONTENTS TEXT COMMENT '문서양식내용',
    DELETEFLAG CHAR(1) COMMENT '삭제',
    PRIMARY KEY (DTNO),
    UNIQUE (DTNO)
) COMMENT = '문서양식종류';


-- 결재경로
CREATE TABLE SGN_PATH
(
    SPNO       INT     NOT NULL AUTO_INCREMENT COMMENT '결재경로번호',
    SPTITLE    VARCHAR(30) COMMENT '경로명',
    REGDATE    DATE COMMENT '등록일자',
    USERNO     INT(10) NOT NULL COMMENT '사용자번호',
    SPSIGNPATH VARCHAR(200) COMMENT '결재경로문자열',
    PRIMARY KEY (SPNO),
    UNIQUE (SPNO)
) COMMENT = '결재경로';


-- 결재경로상세-결재자
CREATE TABLE SGN_PATHUSER
(
    SPNO   INT     NOT NULL COMMENT '결재경로번호',
    SPUSEQ INT     NOT NULL COMMENT '경로순번',
    USERNO INT(10) NOT NULL COMMENT '사용자번호',
    SSTYPE CHAR(1) COMMENT '결재종류',
    PRIMARY KEY (SPNO, SPUSEQ),
    UNIQUE (SPNO)
) COMMENT = '결재경로상세-결재자';


-- 결재
CREATE TABLE SGN_SIGN
(
    SSNO        INT(10)     NOT NULL AUTO_INCREMENT COMMENT '결재번호',
    DOCNO       BIGINT      NOT NULL    COMMENT '문서번호',
    SSSTEP      SMALLINT                COMMENT '결재단계',
    SSTYPE      CHAR(1)                 COMMENT '결재종류',
    SSRESULT    CHAR(1)                 COMMENT '결재결과',
    SSCOMMENT   VARCHAR(1000)           COMMENT '코멘트',
    RECEIVEDATE DATETIME                COMMENT '받은일자',
    SIGNDATE    DATETIME                COMMENT '결재일자',
    USERNO      INT(10)     NOT NULL    COMMENT '사용자번호',
    USERPOS     VARCHAR(10)             COMMENT '직위',
    PRIMARY KEY (SSNO, DOCNO),
    UNIQUE (SSNO, DOCNO)
) COMMENT = '결재';

create table COM_MENU
(
    MNU_NO           INT(10) NOT NULL AUTO_INCREMENT COMMENT '메뉴ID',
    MNU_PARENT       INT(10) COMMENT '상위메뉴ID',
    MNU_TYPE         VARCHAR(10) COMMENT '메뉴업무구분코드',
    MNU_NM           VARCHAR(100) COMMENT '메뉴명',
    MNU_MSG_CD       VARCHAR(100) COMMENT '메뉴 다국어 코드',
    MNU_DESC         VARCHAR(400) COMMENT '설명',
    MNU_TARGET       VARCHAR(100) COMMENT '메뉴링크',
    MNU_FILENM       VARCHAR(100) COMMENT '파일명',
    MNU_IMGPATH      VARCHAR(100) COMMENT '이미지경로',
    MNU_CUSTOM       VARCHAR(400) COMMENT '커스텀태그',
    MNU_DESKTOP      CHAR(1) DEFAULT 'N' COMMENT '데스크탑버전 사용여부',
    MNU_MOBILE       CHAR(1) DEFAULT 'N' COMMENT '모바일버전 사용여부',
    MNU_ORDER        INT(10) COMMENT '정렬순서',
    MNU_CERT_TYPE    VARCHAR(10) COMMENT '인증구분코드',
    MNU_EXTN_CONN_YN CHAR(1) DEFAULT 'N' COMMENT '외부연결여부',
    MNU_START_HOUR   VARCHAR(5) COMMENT '사용시작시',
    MNU_END_HOUR     VARCHAR(5) COMMENT '사용종료시',
    REGDATE          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '등록일자',
    REGUSERNO        INT(10) COMMENT '등록자',
    CHGDATE          DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '수정일자',
    CHGUSERNO        INT(10) COMMENT '수정자',
    DELETEFLAG       CHAR(1) COMMENT '삭제 여부',
    PRIMARY KEY (MNU_NO)
) COMMENT = '메뉴정보';

CREATE INDEX EML_MAIL_INX01 ON EML_MAIL (EMTYPE ASC, USERNO ASC, EMINO ASC);

CREATE TABLE TBL_BUCKET (
    BKNO BIGINT NOT NULL PRIMARY KEY,
    STATE BLOB   NULL
);

-- DROP FUNCTION uf_datetime2string;

DELIMITER $$

CREATE FUNCTION `uf_datetime2string`(dt_ Datetime) RETURNS varchar(10) CHARSET utf8
BEGIN
    DECLARE ti INTEGER;
    DECLARE ret_ VARCHAR(10);

    SET ti := TIMESTAMPDIFF(MINUTE, dt_, NOW());

    IF ti < 1 THEN
        SET ret_ := '방금';
    ELSEIF ti < 60 THEN
        SET ret_ := CONCAT(TRUNCATE(ti, 0), '분전');
    ELSEIF ti < 60 * 24 THEN
        IF (DATEDIFF(NOW(), dt_) = 1) THEN
            SET ret_ := '어제';
        ELSE
            SET ret_ := CONCAT(TRUNCATE(ti / 60, 0), '시간전');
        END IF;
    ELSEIF ti < 60 * 24 * 7 THEN
        SET ret_ := CONCAT(TRUNCATE(ti / 60 / 24, 0), '일전');
    ELSEIF ti < 60 * 24 * 7 * 4 THEN
        SET ret_ := CONCAT(TRUNCATE(ti / 60 / 24 / 7, 0), '주전');
    ELSEIF (TIMESTAMPDIFF(MONTH, dt_, NOW()) = 1) THEN
        SET ret_ := '지난달';
    ELSEIF (TIMESTAMPDIFF(MONTH, dt_, NOW()) < 13) THEN
        IF (TIMESTAMPDIFF(YEAR, dt_, NOW()) = 1) THEN
            SET ret_ := '작년';
        ELSE
            SET ret_ := CONCAT(TRUNCATE(TIMESTAMPDIFF(MONTH, dt_, NOW()), 0), '달전');
        END IF;
    ELSE
        SET ret_ := CONCAT(TIMESTAMPDIFF(YEAR, dt_, NOW()), '년전');
    END IF;

    RETURN ret_;
END;

DELIMITER $$

CREATE PROCEDURE `makeCalendar`(startdate VARCHAR(10),enddate VARCHAR(10))
BEGIN
    DECLARE sdate date DEFAULT STR_TO_DATE(startdate, '%Y-%m-%d');
    DECLARE edate date DEFAULT STR_TO_DATE(enddate, '%Y-%m-%d');

    SELECT CONCAT('sdate=' , sdate);
    SELECT CONCAT('edate=' , edate);

    IF sdate IS NULL THEN
        SELECT DATE_ADD(MAX(DATE_FORMAT(CDDATE,'%Y-%m-%d')), INTERVAL 1 DAY),
               DATE_ADD(MAX(DATE_FORMAT(CDDATE,'%Y-%m-%d')), INTERVAL 300 DAY)
        INTO sdate, edate
        FROM COM_DATE;
    END IF;

    SELECT CONCAT('sdate=', DATE_FORMAT(sdate,'%Y-%m-%d'));
    SELECT CONCAT('edate=', DATE_FORMAT(edate,'%Y-%m-%d'));

    IF edate <= sdate THEN
        SELECT 'edate <= sdate RET';
        RETURN;
    END IF;

    WHILE sdate <= edate
        DO
          SELECT CONCAT('LOOP ', DATE_FORMAT(sdate,'%Y-%m-%d'));

          INSERT INTO COM_DATE (CDDATE, CDYEAR, CDMM, CDDD, CDWEEKOFYEAR, CDWEEKOFMONTH, CDWEEK, CDDAYOFWEEK)
            SELECT SDATE,
                   YEAR(SDATE),
                   MONTH(SDATE),
                   day(SDATE),
                   WEEKOFYEAR(SDATE),
                   FLOOR((DAYOFMONTH(SDATE) - 1) / 7) + 1,
                   WEEK(SDATE),
                   DAYOFWEEK(SDATE);
            -- SELECT SDATE, YEAR(SDATE), DATE_FORMAT(SDATE, '%m'), DATE_FORMAT(SDATE, '%d'),  WEEKOFYEAR(SDATE), FLOOR((DAYOFMONTH(SDATE) - 1) / 7) + 1 , WEEK(SDATE);

            SET sdate = DATE_ADD(sdate, INTERVAL 1 DAY);
        END WHILE;
        SELECT CONCAT('LOOP END');
END;

DELIMITER $$

CREATE FUNCTION `getColor4Alert`(tsstartdate_ VARCHAR(10), tsenddate_ VARCHAR(10), tsendreal_ VARCHAR(10),
                                 tsrate_ Integer) RETURNS varchar(10) CHARSET utf8
BEGIN
    DECLARE bgcolor_ VARCHAR(10);
    DECLARE today_ Datetime;
/*
  오늘 날짜가 시작일자보다 작으면 회색
  오늘 날짜가 종료일자를 지났으면 빨강
  오늘 날짜가 주어진 기간의 50% 미만이면 녹색
  오늘 날짜가 주어진 기간의 50% 이상이면 노랑
  진행율이 기간내에 100이 되었으면 녹색, 지난뒤에 100이면 검정색
*/
    SET today_ := now();
    SET bgcolor_ := 'gray'; -- tsstartdate < today

    IF tsrate_ < 100 THEN
        IF tsstartdate_ > today_ THEN
            SET bgcolor_ := 'gray';
        ELSEIF tsenddate_ < today_ THEN
            SET bgcolor_ := 'red';
        ELSEIF TIMESTAMPDIFF(DAY, tsstartdate_, today_) < TIMESTAMPDIFF(DAY, today_, tsenddate_) THEN
            SET bgcolor_ := 'green';
        ELSEIF TIMESTAMPDIFF(DAY, tsstartdate_, today_) >= TIMESTAMPDIFF(DAY, today_, tsenddate_) THEN
            SET bgcolor_ := 'yellow';
        END IF;
    ELSE
        IF tsendreal_ != "" AND tsendreal_ <= tsenddate_ THEN
            SET bgcolor_ := 'green';
        ELSE
            SET bgcolor_ := 'orange';
        END IF;
    END IF;

    RETURN bgcolor_;
END
