ALTER TABLE TBL_BOARD ADD FOREIGN KEY (BGNO) REFERENCES TBL_BOARDGROUP(BGNO);
ALTER TABLE TBL_BOARDREAD ADD FOREIGN KEY (BRDNO) REFERENCES TBL_BOARD(BRDNO);
ALTER TABLE TBL_BOARDREPLY ADD FOREIGN KEY (BRDNO) REFERENCES TBL_BOARD(BRDNO);
ALTER TABLE TBL_BOARDLIKE ADD FOREIGN KEY (BRDNO) REFERENCES TBL_BOARD(BRDNO);
ALTER TABLE TBL_BOARDFILE ADD FOREIGN KEY (BRDNO) REFERENCES TBL_BOARD(BRDNO);
ALTER TABLE COM_USER ADD FOREIGN KEY (DEPTNO) REFERENCES COM_DEPT(DEPTNO);
ALTER TABLE SGN_SIGN ADD FOREIGN KEY (DOCNO) REFERENCES SGN_DOC(DOCNO);
ALTER TABLE SGN_DOCFILE ADD FOREIGN KEY (DOCNO) REFERENCES SGN_DOC(DOCNO);
ALTER TABLE SGN_DOC ADD FOREIGN KEY (DTNO) REFERENCES SGN_DOCTYPE(DTNO);
ALTER TABLE EML_MAIL ADD FOREIGN KEY (EMINO) REFERENCES EML_MAILINFO(EMINO);
ALTER TABLE EML_MAILFILE ADD FOREIGN KEY (EMNO) REFERENCES EML_MAIL(EMNO);
ALTER TABLE EML_ADDRESS ADD FOREIGN KEY (EMNO) REFERENCES EML_MAIL(EMNO);
ALTER TABLE PRJ_TASK ADD FOREIGN KEY (PRNO) REFERENCES PRJ_PROJECT(PRNO);
ALTER TABLE SGN_PATHUSER ADD FOREIGN KEY (SPNO) REFERENCES SGN_PATH(SPNO);
ALTER TABLE SCH_DETAIL ADD FOREIGN KEY (SSNO) REFERENCES SCH_SCHEDULE(SSNO);
ALTER TABLE PRJ_TASKUSER ADD FOREIGN KEY (TSNO) REFERENCES PRJ_TASK(TSNO);
ALTER TABLE PRJ_TASKFILE ADD FOREIGN KEY (TSNO) REFERENCES PRJ_TASK(TSNO);
ALTER TABLE EML_MAIL ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE SCH_SCHEDULE ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE SGN_DOC ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE EML_MAILINFO ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE SGN_PATHUSER ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE SGN_SIGN ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE SGN_PATH ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARDREAD ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE PRJ_TASKUSER ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_CRUD ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE COM_LOGINOUT ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARDLIKE ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARD ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE PRJ_PROJECT ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARDREPLY ADD FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE COM_CODE_V2 ADD FOREIGN KEY (CHGUSERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARD ADD FOREIGN KEY (CHGUSERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE TBL_BOARDREPLY ADD FOREIGN KEY (CHGUSERNO) REFERENCES COM_USER(USERNO);

ALTER TABLE COM_MENU ADD FOREIGN KEY (REGUSERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE COM_MENU ADD FOREIGN KEY (CHGUSERNO) REFERENCES COM_USER(USERNO);
ALTER TABLE COM_MENU ADD FOREIGN KEY (MNU_PARENT) REFERENCES COM_MENU(MNU_NO);

ALTER TABLE COM_CODE_V2 DROP CONSTRAINT IF EXISTS FK_COM_CODE_V2_CHGUSERNO; -- 제약 조건 삭제 시 IF EXISTS 추가