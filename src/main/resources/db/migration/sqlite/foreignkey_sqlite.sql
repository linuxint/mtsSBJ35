-- SQLite 외래키 제약조건 (H2, MySQL, Oracle 기준 동기화)

-- COM_USER 테이블 외래키
-- DEPTNO -> COM_DEPT(DEPTNO)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_BOARD 테이블 외래키
-- USERNO -> COM_USER(USERNO)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_BOARDREPLY 테이블 외래키
-- BRDNO -> TBL_BOARD(BRDNO)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_SRV_SW 테이블 외래키
-- HW_ID -> TBL_SRV_HW(HW_ID)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_SRV_SVC 테이블 외래키
-- HW_ID -> TBL_SRV_HW(HW_ID)
-- SW_ID -> TBL_SRV_SW(SW_ID)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_SRV_CONN 테이블 외래키
-- SVC_ID -> TBL_SRV_SVC(SVC_ID)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- TBL_SRV_ETC 테이블 외래키
-- CONN_ID -> TBL_SRV_CONN(CONN_ID)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- COM_CODE_V2 테이블 외래키
-- PCODECD -> COM_CODE_V2(CODECD)
-- SQLite에서는 이미 테이블 생성 시 외래키가 정의되어 있음

-- 참고: SQLite는 테이블 생성 시 외래키를 정의하므로 별도의 ALTER TABLE 문이 필요하지 않음
-- 외래키 제약조건은 이미 tables_sqlite.sql에서 정의되어 있음 