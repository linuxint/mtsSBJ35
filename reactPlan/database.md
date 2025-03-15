# 데이터베이스 마이그레이션 계획

## 1. 개요
Oracle Database에서 PostgreSQL로의 마이그레이션

### 1.1 현재 환경
- Oracle Database 11g
- 문자셋: UTF-8
- 테이블스페이스 구성
- 사용자/스키마 구성

### 1.2 목표 환경
- PostgreSQL 15
- 문자셋: UTF-8
- 스키마 구성
- 확장 기능 활용

## 2. 마이그레이션 범위
### 2.1 데이터베이스 객체
1. 테이블
2. 인덱스
3. 시퀀스
4. 뷰
5. 함수/프로시저
6. 트리거
7. 제약조건

### 2.2 데이터
1. 마스터 데이터
2. 트랜잭션 데이터
3. 코드 데이터
4. 이력 데이터

## 3. 주요 변경 사항
### 3.1 데이터 타입 변경
```
Oracle -> PostgreSQL
NUMBER -> NUMERIC
VARCHAR2 -> VARCHAR
CLOB -> TEXT
DATE -> TIMESTAMP
BLOB -> BYTEA
```

### 3.2 시퀀스 변경
```sql
-- Oracle
CREATE SEQUENCE seq_name
START WITH 1
INCREMENT BY 1
NOCACHE
NOCYCLE;

-- PostgreSQL
CREATE SEQUENCE seq_name
START WITH 1
INCREMENT BY 1
NO CYCLE;
```

### 3.3 함수 변경
```sql
-- Oracle
NVL -> COALESCE
DECODE -> CASE
TO_CHAR -> TO_CHAR (다른 포맷)
SYSDATE -> CURRENT_TIMESTAMP
```

### 3.4 페이징 쿼리 변경
```sql
-- Oracle
SELECT * FROM (
    SELECT a.*, ROWNUM rnum FROM (
        SELECT * FROM table_name ORDER BY id
    ) a WHERE ROWNUM <= 20
) WHERE rnum > 10;

-- PostgreSQL
SELECT * FROM table_name
ORDER BY id
LIMIT 10 OFFSET 10;
```

## 4. 테이블 구조
### 4.1 공통 코드
```sql
CREATE TABLE tb_com_code (
    code_id VARCHAR(20) PRIMARY KEY,
    code_name VARCHAR(100) NOT NULL,
    code_desc TEXT,
    use_yn BOOLEAN DEFAULT true,
    sort_order INTEGER,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP
);

CREATE TABLE tb_com_code_detail (
    code_id VARCHAR(20),
    detail_code VARCHAR(20),
    detail_name VARCHAR(100) NOT NULL,
    detail_desc TEXT,
    use_yn BOOLEAN DEFAULT true,
    sort_order INTEGER,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP,
    PRIMARY KEY (code_id, detail_code)
);
```

### 4.2 메뉴
```sql
CREATE TABLE tb_com_menu (
    menu_id VARCHAR(20) PRIMARY KEY,
    parent_menu_id VARCHAR(20),
    menu_name VARCHAR(100) NOT NULL,
    menu_url VARCHAR(200),
    menu_desc TEXT,
    sort_order INTEGER,
    use_yn BOOLEAN DEFAULT true,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP
);
```

### 4.3 사용자
```sql
CREATE TABLE tb_com_user (
    user_id VARCHAR(50) PRIMARY KEY,
    user_name VARCHAR(100) NOT NULL,
    password VARCHAR(200) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    dept_code VARCHAR(20),
    position_code VARCHAR(20),
    use_yn BOOLEAN DEFAULT true,
    last_login_date TIMESTAMP,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP
);
```

### 4.4 게시판
```sql
CREATE TABLE tb_board (
    board_id BIGINT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    writer_id VARCHAR(50) NOT NULL,
    view_count INTEGER DEFAULT 0,
    notice_yn BOOLEAN DEFAULT false,
    delete_yn BOOLEAN DEFAULT false,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP,
    FOREIGN KEY (writer_id) REFERENCES tb_com_user(user_id)
);

CREATE TABLE tb_board_comment (
    comment_id BIGINT PRIMARY KEY,
    board_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    writer_id VARCHAR(50) NOT NULL,
    parent_id BIGINT,
    delete_yn BOOLEAN DEFAULT false,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP,
    FOREIGN KEY (board_id) REFERENCES tb_board(board_id),
    FOREIGN KEY (writer_id) REFERENCES tb_com_user(user_id),
    FOREIGN KEY (parent_id) REFERENCES tb_board_comment(comment_id)
);
```

### 4.5 일정
```sql
CREATE TABLE tb_schedule (
    schedule_id BIGINT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    all_day BOOLEAN DEFAULT false,
    repeat_yn BOOLEAN DEFAULT false,
    repeat_type VARCHAR(20),
    repeat_end_date TIMESTAMP,
    user_id VARCHAR(50) NOT NULL,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    mod_date TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES tb_com_user(user_id)
);
```

### 4.6 메일
```sql
CREATE TABLE tb_mail (
    mail_id BIGINT PRIMARY KEY,
    sender_id VARCHAR(50) NOT NULL,
    receiver_ids TEXT NOT NULL,
    cc_ids TEXT,
    bcc_ids TEXT,
    subject VARCHAR(500) NOT NULL,
    content TEXT,
    send_date TIMESTAMP,
    read_yn BOOLEAN DEFAULT false,
    delete_yn BOOLEAN DEFAULT false,
    reg_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id) REFERENCES tb_com_user(user_id)
);
```

## 5. 인덱스 설계
### 5.1 검색 최적화
```sql
-- 게시판 검색
CREATE INDEX idx_board_title ON tb_board (title);
CREATE INDEX idx_board_reg_date ON tb_board (reg_date);
CREATE INDEX idx_board_writer ON tb_board (writer_id);

-- 일정 검색
CREATE INDEX idx_schedule_date ON tb_schedule (start_date, end_date);
CREATE INDEX idx_schedule_user ON tb_schedule (user_id);

-- 메일 검색
CREATE INDEX idx_mail_subject ON tb_mail (subject);
CREATE INDEX idx_mail_sender ON tb_mail (sender_id);
CREATE INDEX idx_mail_date ON tb_mail (send_date);
```

### 5.2 외래키 인덱스
```sql
-- 자동 생성되는 외래키 인덱스 활용
-- 필요한 경우 추가 인덱스 생성
```

## 6. 마이그레이션 절차
1. 스키마 마이그레이션
   - 테이블 생성
   - 인덱스 생성
   - 제약조건 설정

2. 데이터 마이그레이션
   - 정적 데이터 이관
   - 트랜잭션 데이터 이관
   - 데이터 검증

3. 기능 마이그레이션
   - 함수/프로시저 변환
   - 트리거 변환
   - 뷰 생성

## 7. 성능 최적화
1. 테이블 파티셔닝
   - 대용량 테이블 파티셔닝
   - 파티션 전략 수립

2. 인덱스 최적화
   - 실행 계획 분석
   - 인덱스 재구성

3. 통계 정보
   - 통계 정보 수집
   - 실행 계획 최적화

## 8. 백업 및 복구
1. 백업 전략
   - 전체 백업
   - 증분 백업
   - 아카이브 로그

2. 복구 절차
   - 시점 복구
   - 장애 복구
   - 테스트 복구

## 9. 모니터링
1. 성능 모니터링
   - 쿼리 성능
   - 리소스 사용량
   - 락 상태

2. 공간 모니터링
   - 테이블스페이스
   - 인덱스 공간
   - 임시 공간 