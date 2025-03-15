# Database Migration Plan

## 1. Overview

기존 데이터베이스에서 새로운 데이터베이스로의 마이그레이션 계획을 정의합니다.

### 1.1 Migration Principles

1. **데이터 무결성 보장**
   - 모든 데이터는 손실 없이 마이그레이션
   - 관계 정보 유지
   - 데이터 정합성 검증

2. **단계적 마이그레이션**
   - 테이블 그룹별 순차적 마이그레이션
   - 각 단계별 검증 진행
   - 롤백 계획 수립

3. **다운타임 최소화**
   - 사전 데이터 복제
   - 증분 데이터 동기화
   - 전환 시점 최적화

## 2. Migration Steps

### 2.1 User & Department Migration

#### Step 1: User Data Migration
```sql
INSERT INTO AUTH_USER (
    user_id, username, email, password, full_name,
    department_id, position_code, role_type, profile_image,
    is_active, created_at, updated_at
)
SELECT 
    USERNO, USERID, USERID || '@example.com', USERPW, USERNM,
    DEPTNO, USERPOS, USERROLE, PHOTO,
    CASE WHEN DELETEFLAG = 'N' THEN true ELSE false END,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COM_USER;
```

#### Step 2: Department Data Migration
```sql
INSERT INTO AUTH_DEPARTMENT (
    department_id, parent_id, name, is_active,
    created_at, updated_at
)
SELECT 
    DEPTNO, PARENTNO, DEPTNM,
    CASE WHEN DELETEFLAG = 'N' THEN true ELSE false END,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COM_DEPT;
```

### 2.2 Code Data Migration

#### Step 1: Code Group Migration
```sql
INSERT INTO COMMON_CODE_GROUP (
    group_id, name, is_active,
    created_at, updated_at
)
SELECT DISTINCT
    CLASSNO, MAX(CODENM), true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COM_CODE
GROUP BY CLASSNO;
```

#### Step 2: Code Data Migration
```sql
INSERT INTO COMMON_CODE (
    code_id, group_id, name, is_active,
    created_at, updated_at
)
SELECT 
    CODECD, CLASSNO, CODENM, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM COM_CODE;
```

### 2.3 Board Data Migration

#### Step 1: Board Master Migration
```sql
INSERT INTO BOARD_MASTER (
    board_id, name, board_type,
    read_permission, write_permission,
    use_comment, use_attachment, is_active,
    created_at, updated_at
)
SELECT DISTINCT
    BGNO, 'Board ' || BGNO, 'GENERAL',
    'ALL', 'USER',
    true, true, true,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM TBL_BOARD;
```

#### Step 2: Board Post Migration
```sql
INSERT INTO BOARD_POST (
    post_id, board_id, title, content,
    view_count, is_notice, is_deleted,
    created_at, updated_at, created_by, updated_by
)
SELECT 
    BRDNO, BGNO, BRDTITLE, BRDMEMO,
    0, CASE WHEN BRDNOTICE = 'Y' THEN true ELSE false END,
    CASE WHEN DELETEFLAG = 'Y' THEN true ELSE false END,
    REGDATE, CHGDATE, USERNO, CHGUSERNO
FROM TBL_BOARD;
```

#### Step 3: Board File Migration
```sql
INSERT INTO FILE_INFO (
    original_name, saved_name, file_path,
    file_size, mime_type, target_type, target_id,
    is_deleted, created_at, created_by
)
SELECT 
    FILENAME, REALNAME, '/files/board/',
    FILESIZE, 'application/octet-stream', 'BOARD',
    BRDNO,
    CASE WHEN DELETEFLAG = 'Y' THEN true ELSE false END,
    CURRENT_TIMESTAMP, NULL
FROM TBL_BOARDFILE;
```

### 2.4 Project Data Migration

#### Step 1: Project Migration
```sql
INSERT INTO PROJECT (
    project_id, name, start_date, end_date,
    status, is_active, created_at, updated_at,
    created_by
)
SELECT 
    PRNO, PRTITLE, PRSTARTDATE, PRENDDATE,
    PRSTATUS, 
    CASE WHEN DELETEFLAG = 'N' THEN true ELSE false END,
    REGDATE, REGDATE, USERNO
FROM PRJ_PROJECT;
```

#### Step 2: Task Migration
```sql
INSERT INTO PROJECT_TASK (
    task_id, project_id, parent_id,
    title, start_date, end_date, actual_end_date,
    progress, sort_order, is_deleted,
    created_at, updated_at
)
SELECT 
    TSNO, PRNO, TSPARENT,
    TSTITLE, TSSTARTDATE, TSENDDATE, TSENDREAL,
    TSRATE, TSSORT,
    CASE WHEN DELETEFLAG = 'Y' THEN true ELSE false END,
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
FROM PRJ_TASK;
```

#### Step 3: Task Assignee Migration
```sql
INSERT INTO PROJECT_TASK_ASSIGNEE (
    task_id, user_id, assigned_at, assigned_by
)
SELECT 
    TSNO, USERNO, CURRENT_TIMESTAMP, USERNO
FROM PRJ_TASKUSER;
```

### 2.5 Mail Data Migration

#### Step 1: Mail Migration
```sql
INSERT INTO MAIL (
    mail_id, subject, content,
    sender_id, mail_type, status,
    sent_at, created_at, updated_at
)
SELECT 
    EMNO, EMSUBJECT, EMCONTENTS,
    USERNO, EMTYPE, 
    CASE WHEN DELETEFLAG = 'N' THEN 'SENT' ELSE 'DELETED' END,
    REGDATE, REGDATE, REGDATE
FROM EML_MAIL;
```

#### Step 2: Mail Recipient Migration
```sql
INSERT INTO MAIL_RECIPIENT (
    mail_id, email_address, recipient_type, sequence
)
SELECT 
    EMNO, EAADDRESS, EATYPE, EASEQ
FROM EML_ADDRESS;
```

## 3. Verification Steps

### 3.1 Data Count Verification
각 마이그레이션 단계 후 다음 검증 수행:

1. 원본 테이블과 대상 테이블의 레코드 수 비교
2. NULL이 허용되지 않는 필드의 NULL 값 검사
3. 외래 키 참조 무결성 검사

### 3.2 Data Sampling Verification
각 테이블에서 샘플 데이터를 추출하여 다음 검증 수행:

1. 데이터 값의 정확성 검증
2. 날짜/시간 데이터의 정확성 검증
3. 특수 문자 및 인코딩 처리 검증

## 4. Rollback Plan

### 4.1 Rollback Procedures

1. 각 마이그레이션 단계별 롤백 스크립트 준비
2. 원본 데이터베이스 백업 유지
3. 마이그레이션 로그 기록

### 4.2 Rollback Triggers

다음 상황 발생 시 롤백 수행:

1. 데이터 검증 실패
2. 시스템 성능 저하
3. 애플리케이션 오류 발생

## 5. Post-Migration Tasks

### 5.1 Clean-up Tasks

1. 임시 테이블 및 인덱스 제거
2. 불필요한 로그 정리
3. 데이터베이스 통계 갱신

### 5.2 Performance Optimization

1. 인덱스 재구성
2. 테이블 통계 갱신
3. 성능 모니터링 설정 