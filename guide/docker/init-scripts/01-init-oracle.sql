-- 사용자 생성 및 권한 부여
CREATE USER mts IDENTIFIED BY mts;
GRANT CONNECT, RESOURCE TO mts;

-- 테이블스페이스 생성
CREATE TABLESPACE mts_data
DATAFILE '/opt/oracle/oradata/XE/mts_data.dbf'
SIZE 100M
AUTOEXTEND ON NEXT 50M;

-- 사용자의 기본 테이블스페이스 설정
ALTER USER mts DEFAULT TABLESPACE mts_data;

-- 임시 테이블스페이스 설정
ALTER USER mts TEMPORARY TABLESPACE TEMP;

-- 시퀀스 생성
CREATE SEQUENCE mts_seq
    START WITH 1
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 테이블 생성
CREATE TABLE mts_users (
    user_id NUMBER PRIMARY KEY,
    username VARCHAR2(50) NOT NULL UNIQUE,
    password VARCHAR2(100) NOT NULL,
    email VARCHAR2(100),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP,
    updated_at TIMESTAMP DEFAULT SYSTIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_mts_users_username ON mts_users(username);
CREATE INDEX idx_mts_users_email ON mts_users(email);

-- 트리거 생성
CREATE OR REPLACE TRIGGER mts_users_bi_trg
BEFORE INSERT ON mts_users
FOR EACH ROW
BEGIN
    SELECT mts_seq.NEXTVAL INTO :NEW.user_id FROM DUAL;
END;
/

CREATE OR REPLACE TRIGGER mts_users_bu_trg
BEFORE UPDATE ON mts_users
FOR EACH ROW
BEGIN
    :NEW.updated_at := SYSTIMESTAMP;
END;
/ 