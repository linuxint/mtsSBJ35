# Tibero 데이터베이스 설정 가이드

## 개요
이 프로젝트는 Tibero 데이터베이스를 지원합니다. Tibero는 Oracle과 매우 유사한 문법을 사용하므로 Oracle 기반으로 구성되어 있습니다.

## 지원 데이터베이스
- Oracle
- MariaDB/MySQL
- SQLite
- H2
- **Tibero** (새로 추가됨)

## Tibero 설정

### 1. 의존성
`build.gradle`에 Tibero JDBC 드라이버가 추가되어 있습니다:
```gradle
implementation 'com.tmax.tibero6:tibero6-jdbc:6.0.0'
```

### 2. 프로필 설정
`application.yml`에서 `tibero` 프로필을 사용하여 Tibero 데이터베이스에 연결할 수 있습니다:

```bash
# Tibero 프로필로 실행
java -jar your-app.jar --spring.profiles.active=tibero
```

### 3. 데이터베이스 연결 설정
기본 Tibero 연결 설정:
- **드라이버**: `com.p6spy.engine.spy.P6SpyDriver` (P6Spy 모니터링 포함)
- **URL**: `jdbc:p6spy:tibero:thin:@localhost:8629:tibero`
- **포트**: 8085
- **사용자명**: tibero
- **비밀번호**: tibero

### 4. 파일 구조
```
src/main/resources/
├── mapper/tibero/           # Tibero 전용 MyBatis 매퍼 XML 파일들
├── db/migration/tibero/     # Tibero 전용 마이그레이션 스크립트
│   ├── tables_tibero.sql
│   ├── sequence_tibero.sql
│   ├── function_tibero.sql
│   ├── foreignkey_tibero.sql
│   └── tableData_tibero.sql
```

### 5. Tibero 특화 설정

#### P6Spy 모니터링
Tibero 연결도 P6Spy를 통해 모니터링됩니다:
- SQL 쿼리 로깅
- 느린 쿼리 감지
- 연결 풀 모니터링

#### Flyway 마이그레이션
Tibero 전용 마이그레이션 스크립트가 자동으로 실행됩니다.

## Tibero vs Oracle 차이점

### 유사한 부분
- 시퀀스 사용법: `SELECT BRDNO_SEQ.NEXTVAL FROM DUAL`
- 날짜 함수: `SYSDATE`
- 문자열 연결: `'%'||#{searchKeyword}||'%'`
- 페이징: `ROW_NUMBER() OVER`

### 주의사항
Tibero는 Oracle과 거의 동일한 문법을 사용하므로, 기존 Oracle XML 매퍼 파일들이 Tibero에서도 잘 작동합니다.

## 사용 예시

### 1. 개발 환경에서 Tibero 사용
```bash
# Tibero 프로필로 실행
./gradlew bootRun --args='--spring.profiles.active=tibero'
```

### 2. 프로덕션 환경에서 Tibero 사용
```bash
java -jar mtsSBJ35.jar --spring.profiles.active=tibero
```

### 3. Docker 환경에서 Tibero 사용
```bash
docker run -e SPRING_PROFILES_ACTIVE=tibero your-app-image
```

## 문제 해결

### 1. 연결 오류
- Tibero 서버가 실행 중인지 확인
- 포트 8629가 열려있는지 확인
- 사용자명/비밀번호가 올바른지 확인

### 2. 드라이버 오류
- Tibero JDBC 드라이버가 클래스패스에 있는지 확인
- 드라이버 버전이 Tibero 서버 버전과 호환되는지 확인

### 3. SQL 문법 오류
- Oracle 문법과 동일하게 사용
- Tibero 특화 함수가 필요한 경우 별도 수정 필요

## 추가 정보
- [Tibero 공식 문서](https://technet.tmaxsoft.com/ko/front/main/main.do)
- [Tibero JDBC 드라이버 다운로드](https://technet.tmaxsoft.com/ko/front/main/main.do) 