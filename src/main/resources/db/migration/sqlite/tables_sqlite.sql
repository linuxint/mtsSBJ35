-- SQLite 테이블 스키마 (H2, MySQL, Oracle 기준 동기화)
-- SQLite 문법에 맞게 변환

CREATE TABLE PERSISTENT_LOGINS (
    USERNAME TEXT NOT NULL,
    SERIES TEXT PRIMARY KEY,
    TOKEN TEXT NOT NULL,
    LAST_USED DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE PRJ_PROJECT (
    PRNO INTEGER PRIMARY KEY AUTOINCREMENT,
    PRSTARTDATE TEXT,
    PRENDDATE TEXT,
    PRTITLE TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    USERNO INTEGER,
    PRSTATUS TEXT,
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE PRJ_TASK (
    PRNO INTEGER,
    TSNO INTEGER PRIMARY KEY AUTOINCREMENT,
    TSPARENT INTEGER,
    TSSORT INTEGER,
    TSTITLE TEXT,
    TSSTARTDATE TEXT,
    TSENDDATE TEXT,
    TSENDREAL TEXT,
    TSRATE INTEGER,
    OLDNO INTEGER,
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE PRJ_TASKUSER (
    TSNO INTEGER NOT NULL,
    USERNO INTEGER NOT NULL,
    PRIMARY KEY (TSNO, USERNO)
);

CREATE TABLE PRJ_TASKFILE (
    TSNO INTEGER,
    FILENO INTEGER PRIMARY KEY AUTOINCREMENT,
    FILENAME TEXT,
    REALNAME TEXT,
    FILESIZE INTEGER,
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE COM_CODE (
    CLASSNO INTEGER NOT NULL,
    CODECD TEXT NOT NULL,
    CODENM TEXT,
    PRIMARY KEY (CLASSNO, CODECD)
);

CREATE TABLE COM_CODE_V2 (
    CODECD TEXT NOT NULL PRIMARY KEY,
    PCODECD TEXT,
    CODENM TEXT,
    CODEDESC TEXT,
    DELETEFLAG TEXT DEFAULT 'N',
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    REGUSERNO INTEGER,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGUSERNO INTEGER,
    FOREIGN KEY (PCODECD) REFERENCES COM_CODE_V2(CODECD)
);

CREATE TABLE COM_DEPT (
    DEPTNO INTEGER PRIMARY KEY AUTOINCREMENT,
    DEPTNM TEXT,
    PARENTNO INTEGER DEFAULT 0,
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE COM_USER (
    USERNO INTEGER PRIMARY KEY AUTOINCREMENT,
    USERID TEXT,
    USERNM TEXT,
    USERPW TEXT,
    USERROLE TEXT,
    PHOTO TEXT,
    DEPTNO INTEGER,
    DELETEFLAG TEXT DEFAULT 'N',
    USERPOS TEXT
);

CREATE TABLE TBL_BOARD (
    BGNO INTEGER DEFAULT 0,
    BRDNO INTEGER PRIMARY KEY AUTOINCREMENT,
    BRDTITLE TEXT,
    USERNO INTEGER,
    BRDMEMO TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    BRDNOTICE TEXT,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGUSERNO INTEGER,
    BRDLIKE INTEGER DEFAULT 0,
    DELETEFLAG TEXT DEFAULT 'N',
    ETC1 TEXT,
    ETC2 TEXT,
    ETC3 TEXT,
    ETC4 TEXT,
    ETC5 TEXT,
    FOREIGN KEY (USERNO) REFERENCES COM_USER(USERNO)
);

CREATE TABLE TBL_BOARDFILE (
    FILENO INTEGER PRIMARY KEY AUTOINCREMENT,
    BRDNO INTEGER DEFAULT 0,
    FILENAME TEXT,
    REALNAME TEXT,
    FILESIZE INTEGER,
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE TBL_BOARDLIKE (
    BLNO INTEGER PRIMARY KEY AUTOINCREMENT,
    BRDNO INTEGER DEFAULT 0,
    USERNO INTEGER,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_BOARDREPLY (
    BRDNO INTEGER NOT NULL,
    RENO INTEGER PRIMARY KEY AUTOINCREMENT,
    USERNO INTEGER,
    REMEMO TEXT DEFAULT NULL,
    REPARENT INTEGER DEFAULT 0,
    REDEPTH INTEGER,
    REORDER INTEGER,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    REDELETEFLAG TEXT DEFAULT 'N',
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGUSERNO INTEGER,
    REDELDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    DELDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (BRDNO) REFERENCES TBL_BOARD(BRDNO)
);

CREATE TABLE TBL_BOARDREAD (
    BRDNO INTEGER NOT NULL,
    USERNO INTEGER,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (BRDNO, USERNO)
);

CREATE TABLE TBL_BOARDGROUP (
    BGNO INTEGER PRIMARY KEY AUTOINCREMENT,
    BGNAME TEXT,
    BGUSED TEXT DEFAULT 'Y',
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE COM_LOGINOUT (
    USERNO INTEGER,
    LOGINOUT TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_CRUD (
    CRUDNO INTEGER PRIMARY KEY AUTOINCREMENT,
    CRUDTITLE TEXT,
    CRUDMEMO TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE COM_DATE (
    DATENO INTEGER PRIMARY KEY AUTOINCREMENT,
    DATETYPE TEXT,
    DATETITLE TEXT,
    DATECONTENT TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE EML_ADDRESS (
    EMINO INTEGER PRIMARY KEY AUTOINCREMENT,
    USERNO INTEGER,
    EMAIL TEXT,
    EMAILNM TEXT
);

CREATE TABLE EML_MAIL (
    EMINO INTEGER PRIMARY KEY AUTOINCREMENT,
    EMTYPE TEXT,
    USERNO INTEGER,
    EMSUBJECT TEXT,
    EMCONTENT TEXT,
    EMTO TEXT,
    EMCC TEXT,
    EMBCC TEXT,
    EMSENDDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IDX_EML_MAIL_01 ON EML_MAIL (EMTYPE, USERNO, EMINO);

CREATE TABLE EML_MAILFILE (
    EMINO INTEGER,
    FILENO INTEGER PRIMARY KEY AUTOINCREMENT,
    FILENAME TEXT,
    REALNAME TEXT,
    FILESIZE INTEGER
);

CREATE TABLE EML_MAILINFO (
    EMINO INTEGER PRIMARY KEY AUTOINCREMENT,
    EMTYPE TEXT,
    USERNO INTEGER,
    EMSUBJECT TEXT,
    EMCONTENT TEXT,
    EMTO TEXT,
    EMCC TEXT,
    EMBCC TEXT,
    EMSENDDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    EMSTATUS TEXT DEFAULT 'N'
);

CREATE TABLE SCH_DETAIL (
    SCHNO INTEGER PRIMARY KEY AUTOINCREMENT,
    SCHTYPE TEXT,
    SCHTITLE TEXT,
    SCHCONTENT TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SCH_HOLIDAY (
    HOLIDAYNO INTEGER PRIMARY KEY AUTOINCREMENT,
    HOLIDAYDATE DATE,
    HOLIDAYNM TEXT,
    HOLIDAYTYPE TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SCH_SCHEDULE (
    SCHNO INTEGER PRIMARY KEY AUTOINCREMENT,
    SCHTYPE TEXT,
    SCHTITLE TEXT,
    SCHCONTENT TEXT,
    SCHSTARTDATE DATE,
    SCHENDDATE DATE,
    SCHSTARTTIME TIME,
    SCHENDTIME TIME,
    SCHREPEAT TEXT DEFAULT '1',
    SCHREPEATWEEK TEXT,
    SCHREPEATMONTH TEXT,
    SCHREPEATYEAR TEXT,
    SCHPUBLIC TEXT DEFAULT 'Y',
    USERNO INTEGER,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SGN_DOC (
    DOCNO INTEGER PRIMARY KEY AUTOINCREMENT,
    DOCTYPE INTEGER,
    DOCTITLE TEXT,
    DOCCONTENT TEXT,
    DOCSTATUS TEXT DEFAULT '0',
    USERNO INTEGER,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGUSERNO INTEGER
);

CREATE TABLE SGN_DOCFILE (
    DOCNO INTEGER,
    FILENO INTEGER PRIMARY KEY AUTOINCREMENT,
    FILENAME TEXT,
    REALNAME TEXT,
    FILESIZE INTEGER
);

CREATE TABLE SGN_DOCTYPE (
    DOCTYPENO INTEGER PRIMARY KEY AUTOINCREMENT,
    DOCTYPENAME TEXT,
    DOCTYPEUSED TEXT DEFAULT 'Y',
    DELETEFLAG TEXT DEFAULT 'N'
);

CREATE TABLE SGN_PATH (
    PATHNO INTEGER PRIMARY KEY AUTOINCREMENT,
    DOCNO INTEGER,
    PATHORDER INTEGER,
    PATHSTATUS TEXT DEFAULT '0',
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE SGN_PATHUSER (
    PATHNO INTEGER NOT NULL,
    USERNO INTEGER NOT NULL,
    PATHUSERORDER INTEGER,
    PATHUSERSTATUS TEXT DEFAULT '0',
    PATHUSERMEMO TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (PATHNO, USERNO)
);

CREATE TABLE SGN_SIGN (
    SIGNNO INTEGER PRIMARY KEY AUTOINCREMENT,
    DOCNO INTEGER,
    USERNO INTEGER,
    SIGNSTATUS TEXT DEFAULT '0',
    SIGNMEMO TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE COM_MENU (
    MENUNO INTEGER PRIMARY KEY AUTOINCREMENT,
    MENUPARENT INTEGER DEFAULT 0,
    MENUORDER INTEGER,
    MENUNAME TEXT,
    MENULINK TEXT,
    MENUTYPE TEXT,
    MENUUSED TEXT DEFAULT 'Y',
    DELETEFLAG TEXT DEFAULT 'N',
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_SRV_HW (
    HW_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    HW_NAME TEXT,
    HW_TYPE TEXT,
    HW_SPEC TEXT,
    HW_LOCATION TEXT,
    HW_STATUS TEXT DEFAULT 'A',
    HW_PURCHASE_DATE DATE,
    HW_WARRANTY_DATE DATE,
    HW_MANUFACTURER TEXT,
    HW_MODEL TEXT,
    HW_SERIAL TEXT,
    HW_IP_ADDRESS TEXT,
    HW_MAC_ADDRESS TEXT,
    HW_DESCRIPTION TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE TBL_SRV_SW (
    SW_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    HW_ID INTEGER,
    SW_NAME TEXT,
    SW_VERSION TEXT,
    SW_TYPE TEXT,
    SW_LICENSE TEXT,
    SW_INSTALL_DATE DATE,
    SW_EXPIRY_DATE DATE,
    SW_VENDOR TEXT,
    SW_DESCRIPTION TEXT,
    SW_STATUS TEXT DEFAULT 'A',
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (HW_ID) REFERENCES TBL_SRV_HW(HW_ID)
);

CREATE INDEX IDX_TBL_SRV_SW_HW_ID ON TBL_SRV_SW(HW_ID);

CREATE TABLE TBL_SRV_SVC (
    SVC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    HW_ID INTEGER,
    SW_ID INTEGER,
    SVC_NAME TEXT,
    SVC_TYPE TEXT,
    SVC_PORT INTEGER,
    SVC_PROTOCOL TEXT,
    SVC_STATUS TEXT DEFAULT 'A',
    SVC_START_DATE DATE,
    SVC_DESCRIPTION TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (HW_ID) REFERENCES TBL_SRV_HW(HW_ID),
    FOREIGN KEY (SW_ID) REFERENCES TBL_SRV_SW(SW_ID)
);

CREATE INDEX IDX_SRV_SVC_HW_ID ON TBL_SRV_SVC(HW_ID);
CREATE INDEX IDX_SRV_SVC_SW_ID ON TBL_SRV_SVC(SW_ID);

CREATE TABLE TBL_SRV_CONN (
    CONN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    SVC_ID INTEGER,
    CONN_NAME TEXT,
    CONN_TYPE TEXT,
    CONN_SOURCE TEXT,
    CONN_DESTINATION TEXT,
    CONN_PROTOCOL TEXT,
    CONN_PORT INTEGER,
    CONN_STATUS TEXT DEFAULT 'A',
    CONN_DESCRIPTION TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (SVC_ID) REFERENCES TBL_SRV_SVC(SVC_ID)
);

CREATE INDEX IDX_TBL_SRV_CONN_SVC_ID ON TBL_SRV_CONN(SVC_ID);

CREATE TABLE TBL_SRV_ETC (
    ETC_ID INTEGER PRIMARY KEY AUTOINCREMENT,
    CONN_ID INTEGER,
    ETC_NAME TEXT,
    ETC_TYPE TEXT,
    ETC_VALUE TEXT,
    ETC_DESCRIPTION TEXT,
    ETC_STATUS TEXT DEFAULT 'A',
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    CHGDATE DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (CONN_ID) REFERENCES TBL_SRV_CONN(CONN_ID)
);

CREATE INDEX IDX_TBL_SRV_ETC_CONN_ID ON TBL_SRV_ETC(CONN_ID);

CREATE TABLE TBL_BUCKET (
    BUCKETNO INTEGER PRIMARY KEY AUTOINCREMENT,
    BUCKETNAME TEXT,
    BUCKETTYPE TEXT,
    BUCKETCONTENT TEXT,
    REGDATE DATETIME DEFAULT CURRENT_TIMESTAMP
); 