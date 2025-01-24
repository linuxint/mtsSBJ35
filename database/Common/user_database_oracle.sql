--docker exec -it oracle /bin/bash
--sqlplus sys as sysdba
--sqlplus /nolog
--conn sys/oracle as sysdba

create tablespace mts datafile '/u01/app/oracle/oradata/XE/mts01.dbf'
    size 300 m reuse autoextend on next 1024 k maxsize unlimited;

select tablespace_name, bytes/1024/1024 MB, file_name from dba_data_files;


create user mts identified by mts
    default tablespace mts
    TEMPORARY TABLESPACE TEMP;

select username, default_tablespace from dba_users;

-- grant connect, resource, sysdba to mts;
grant dba to mts with admin option;

grant create view to mts;
grant create table to mts;
grant create any table to mts;
grant create trigger to mts;
grant create any trigger to mts;
grant alter any trigger to mts;

select extract (year from TO_DATE('2020-12-31')) from dual; -- year month day

select TO_CHAR(TO_DATE('2020-12-31','YYYY-MM-DD'),'IW') from dual; -- WEEKOFYEAR

SELECT extract(day FROM to_date( '2022-05-03')) FROM dual;

SELECT WeekDay(('2022-05-03'))  FROM dual;

SELECT  TO_CHAR( TO_DATE('2022-05-03'), 'D' )           D   FROM dual;
SELECT TO_CHAR(TO_DATE('2022-05-03'), 'IYYY') FROM dual;

SELECT  FLOOR((to_char(TO_DATE('2019-05-15')+1,'dd') - 1) / 7) + 1 DAYOFMONTH
             , to_char(TO_DATE('2019-05-15')+1,'IW') WEEK
             , to_char(TO_DATE('2019-05-15')+1,'d') DAYOFWEEK FROM DUAL;

SELECT * FROM COM_USER;

SELECT * FROM tbl_board;

COMMIT;

SELECT SSNO, SDSEQ, SSTITLE, USERNO, SDHOUR, SDMINUTE, FONTCOLOR
FROM (
         SELECT NULL SSNO, NULL SDSEQ, SHTITLE SSTITLE, NULL USERNO, NULL SDHOUR, NULL SDMINUTE, SHCOLOR FONTCOLOR
         FROM SCH_HOLIDAY SH
         WHERE SHMONTH=extract (month from TO_DATE(#{field2})) AND SHDATE=extract (day from TO_DATE((#{field2})) AND SH.DELETEFLAG='N'
                                                                                           UNION ALL
                                                                                           SELECT SD.SSNO, SDSEQ, SSTITLE, USERNO, SD.SDHOUR, SD.SDMINUTE, NULL FONTCOLOR
                                                                                           FROM SCH_DETAIL SD
                                                                                           LEFT OUTER JOIN SCH_SCHEDULE SS ON SS.SSNO=SD.SSNO
                                                                                           WHERE SDDATE=#{field2} AND SS.DELETEFLAG='N' AND SS.SSISOPEN='Y'
                 UNION ALL
                SELECT SD.SSNO, SDSEQ, SSTITLE, USERNO, SD.SDHOUR, SD.SDMINUTE, NULL FONTCOLOR
                  FROM SCH_DETAIL SD
                  LEFT OUTER JOIN SCH_SCHEDULE SS ON SS.SSNO=SD.SSNO
                 WHERE SDDATE=#{field2} AND SS.DELETEFLAG='N' AND SS.SSISOPEN='N' AND USERNO=#{field1}
         )DS
                                ORDER BY SDHOUR, SDMINUTE, SSNO;

select extract (day from TO_DATE('2022-05-04')) from dual;


SELECT BGNAME FIELD1, CNT CNT1
FROM (
         SELECT BGNO, COUNT(*) CNT
         FROM TBL_BOARD TB
         WHERE TB.DELETEFLAG='N'
         GROUP BY BGNO
     ) DS
         INNER JOIN TBL_BOARDGROUP TBG
WHERE TBG.BGNO=DS.BGNO AND TBG.DELETEFLAG='N'

SELECT BGNAME FIELD1, CNT CNT1
FROM (
         SELECT BGNO, COUNT(*) CNT
         FROM TBL_BOARD TB
         WHERE TB.DELETEFLAG='N'
         GROUP BY BGNO
     ) DS
         INNER JOIN TBL_BOARDGROUP TBG
                    ON TBG.BGNO=DS.BGNO AND TBG.DELETEFLAG='N';

SELECT USERNO_SEQ.currval FROM DUAL;

SELECT MAX(USERNO) FROM COM_USER;

DROP SEQUENCE USERNO_SEQ;
CREATE SEQUENCE USERNO_SEQ
    INCREMENT BY 1
    START WITH 81
    NOMAXVALUE
    NOCYCLE
    CACHE 10;


COMMIT;

select max(userno) from com_user;

INSERT INTO COM_USER(USERNO, USERID, USERNM, USERPW, USERROLE, PHOTO, DEPTNO, DELETEFLAG)
VALUES (USERNO_SEQ.NEXTVAL, 'kbil75', '1', '1111', 'U', null, '2', 'N');

select USERNO_SEQ.currval from dual;

select * from tbl_board ;

UPDATE  /*+ BYPASS_UJVC  */
(
    SELECT TBR1.REORDER REORDER_1
         , TBR1.REORDER-1 REORDER_2
    FROM TBL_BOARDREPLY TBR1
       , TBL_BOARDREPLY TBR2
    WHERE TBR2.BRDNO=TBR1.BRDNO
      AND TBR1.REORDER > TBR2.REORDER
      AND TBR1.REDELETEFLAG='N'
      AND TBR2.RENO=1
)
SET REORDER_1 = REORDER_2;

UPDATE TBL_BOARDREPLY TBR1
    INNER JOIN TBL_BOARDREPLY TBR2 ON TBR2.BRDNO=TBR1.BRDNO AND TBR1.REORDER >TBR2.REORDER AND TBR1.REDELETEFLAG='N'
SET TBR1.REORDER = TBR1.REORDER - 1
    WHERE TBR2.RENO=#{reno}

UPDATE
    TBL_BOARDREPLY TBR1
SET
    TBR1.REORDER = (SELECT B.ROLE_CD FROM ARTIST_GRP B WHERE A.ARTIST_ID = B.MEMBER_ID)
WHERE EXISTS (
              SELECT
                  0
              FROM
                  TBL_BOARDREPLY TBR2
              WHERE
                      A.ARTIST_ID = B.MEMBER_ID
          )
;


UPDATE TBL_BOARDREPLY TBR1
SET TBR1.REORDER = (
    SELECT  TBR1.REORDER - 1
    FROM TBL_BOARDREPLY TBR2
    WHERE TBR2.RENO='1' AND TBR2.BRDNO=TBR1.BRDNO AND TBR1.REORDER > TBR2.REORDER AND TBR1.REDELETEFLAG='N'
);

COMMIT;

SELECT * FROM  TBL_BOARDREPLY TBR1
    INNER JOIN TBL_BOARDREPLY TBR2 ON TBR2.BRDNO=TBR1.BRDNO AND TBR1.REORDER > TBR2.REORDER
    AND TBR2.RENO='1';

SELECT * FROM TBL_BOARDREPLY TBR1, TBL_BOARDREPLY TBR2 WHERE TBR2.RENO='1' AND TBR2.BRDNO=TBR1.BRDNO AND TBR1.REORDER > TBR2.REORDER;

UPDATE  /*+ BYPASS_UJVC  */
(
    SELECT TBR1.REORDER REORDER_1
         , TBR1.REORDER-1 REORDER_2
    FROM TBL_BOARDREPLY TBR1
       , TBL_BOARDREPLY TBR2
    WHERE TBR2.BRDNO=TBR1.BRDNO
      AND TBR1.REORDER > TBR2.REORDER
      AND TBR1.REDELETEFLAG='N'
      AND TBR2.RENO=1
)
SET REORDER_1 = REORDER_2;

COMMIT;

SELECT BRDNO, REDEPTH+1 REDEPTH , NVL((SELECT MAX(REORDER) FROM TBL_BOARDREPLY WHERE REPARENT=TB.RENO), TB.REORDER) REORDER FROM TBL_BOARDREPLY TB WHERE RENO=;

SELECT BRDNO, RENO, USERNM, TBR.USERNO, REMEMO, REGDATE
FROM TBL_BOARDREPLY TBR
         INNER JOIN COM_USER CU ON CU.USERNO = TBR.USERNO AND RENO > 1 AND BRDNO <= 38
WHERE REDELETEFLAG = 'N'
ORDER BY RENO;

select * from COM_DATE where cdno < 100;



drop TABLE COM_LOGINOUT cascade constraints ;
CREATE TABLE COM_LOGINOUT
(
    LNO                  NUMBER(10),
    USERNO               NUMBER(10),
    LTYPE                CHAR(1 BYTE),
    REGDATE                TIMESTAMP(6) DEFAULT  CURRENT_TIMESTAMP  NULL
);


---- UK DML
CREATE UNIQUE INDEX UK_COM_LOGINOUT ON COM_LOGINOUT
    (LNO   ASC);
ALTER TABLE COM_LOGINOUT
    ADD CONSTRAINT  PK_COM_LOGINOUT PRIMARY KEY (LNO);


SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME
FROM USER_CONS_COLUMNS
WHERE TABLE_NAME='COM_LOGINOUT';
