# Spring Boot Profile 구조 가이드

## 개요
이 프로젝트는 Spring Boot의 Profile Group 기능을 활용하여 데이터베이스별 설정을 체계적으로 관리합니다. 
YAML 기반에서 Properties 기반으로 변경하여 가독성과 유지보수성을 향상시켰습니다.

## Profile 구조

### 1. Profile Groups (application.properties)
```properties
# 프로필 그룹 설정 (로컬 환경에서 다양한 데이터베이스 선택 가능)
spring.profiles.group.local-h2=local,local-h2
spring.profiles.group.local-oracle=local,local-oracle
spring.profiles.group.local-mysql=local,local-mysql
spring.profiles.group.local-tibero=local,local-tibero
spring.profiles.group.local-sqlite=local,local-sqlite

# 기본 활성 프로필 설정
spring.profiles.active=local-h2
```

### 2. Profile 계층 구조

#### **application.properties** (기본 설정)
- 애플리케이션 정보 및 메타데이터
- Spring Custom 설정
- Server 관련 설정
- SSL 및 Context 경로 설정
- MyBatis 기본 설정
- Spring MVC 및 Thymeleaf 설정
- Tomcat AJP 설정
- 로그, ANSI, P6Spy 출력 관련 설정
- CORS 설정
- 쿼리포맷/늦은쿼리 확인 설정
- SpringDoc API 설정
- 연계 설정 (Spring Boot Server Admin, Prometheus, Flyway, Elasticsearch, Micrometer Tracing + Zipkin)
- Git/GitHub Configuration
- 공공데이터포털 API 설정

#### **application-local.properties** (로컬 환경 공통 설정)
- 개발 도구 설정 (devtools, livereload 등)
- 로깅 레벨 설정 (DEBUG)
- JPA 설정 (개발 환경)
- 캐시 설정 (개발 환경)
- 세션 설정
- 파일 업로드 설정
- 서버 설정
- Tomcat 설정
- JSP 설정
- Management 설정 (모든 Actuator 엔드포인트 활성화)
- Metrics 설정
- Tracing 설정
- Elasticsearch 설정
- Spring Boot Admin 설정
- CORS 설정
- P6Spy, Flexy Pool, DataSource Proxy 설정
- SpringDoc 설정
- Actuator 설정
- Flyway 설정
- ANSI 설정
- Structured logging

#### **application-local-{database}.properties** (DB별 설정)
- **application-local-h2.properties**: H2 데이터베이스 설정
- **application-local-oracle.properties**: Oracle 데이터베이스 설정
- **application-local-mysql.properties**: MySQL/MariaDB 데이터베이스 설정
- **application-local-tibero.properties**: Tibero 데이터베이스 설정
- **application-local-sqlite.properties**: SQLite 데이터베이스 설정

#### **운영 환경**
- **application-dev.properties**: 개발 서버 환경 (Oracle 기반)
- **application-prod.properties**: 운영 환경 (Oracle 기반, 성능 최적화)

## 사용 방법

### 1. 기본 실행 (H2)
```bash
# 기본값은 local-h2
java -jar mtsSBJ35.jar
```

### 2. 특정 데이터베이스로 실행
```bash
# Oracle
java -jar mtsSBJ35.jar --spring.profiles.active=local-oracle

# MySQL/MariaDB
java -jar mtsSBJ35.jar --spring.profiles.active=local-mysql

# Tibero
java -jar mtsSBJ35.jar --spring.profiles.active=local-tibero

# SQLite
java -jar mtsSBJ35.jar --spring.profiles.active=local-sqlite
```

### 3. 운영 환경 실행
```bash
# 개발 서버
java -jar mtsSBJ35.jar --spring.profiles.active=dev

# 운영
java -jar mtsSBJ35.jar --spring.profiles.active=prod
```

## 데이터베이스별 설정

### H2 (local-h2)
- **포트**: 9090
- **드라이버**: `org.h2.Driver`
- **URL**: `jdbc:h2:file:./database/h2db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE;MODE=Oracle`
- **사용자**: sa
- **비밀번호**: (없음)
- **H2 콘솔**: http://localhost:9090/h2-console
- **MyBatis 매퍼**: `classpath:mapper/h2/*.xml`
- **Flyway**: `classpath:/db/migration,classpath:/db/migration/h2`

### Oracle (local-oracle)
- **포트**: 9090
- **드라이버**: `com.p6spy.engine.spy.P6SpyDriver`
- **URL**: `jdbc:p6spy:oracle:thin:@localhost:1521:XE`
- **사용자**: mts
- **비밀번호**: mts
- **MyBatis 매퍼**: `classpath:mapper/oracle/*.xml`
- **Flyway**: `classpath:/db/migration,classpath:/db/migration/oracle`

### MySQL/MariaDB (local-mysql)
- **포트**: 9090
- **드라이버**: `com.p6spy.engine.spy.P6SpyDriver`
- **URL**: `jdbc:p6spy:mariadb://localhost:3306/mts?useUnicode=true&characterEncoding=UTF-8`
- **사용자**: mts
- **비밀번호**: mts
- **MyBatis 매퍼**: `classpath:mapper/mysql/*.xml`
- **Flyway**: `classpath:/db/migration,classpath:/db/migration/mysql`

### Tibero (local-tibero)
- **포트**: 9090
- **드라이버**: `com.p6spy.engine.spy.P6SpyDriver`
- **URL**: `jdbc:p6spy:tibero:thin:@localhost:8629:tibero`
- **사용자**: tibero
- **비밀번호**: tibero
- **MyBatis 매퍼**: `classpath:mapper/tibero/*.xml`
- **Flyway**: `classpath:/db/migration,classpath:/db/migration/tibero`

### SQLite (local-sqlite)
- **포트**: 9090
- **드라이버**: `org.sqlite.JDBC`
- **URL**: `jdbc:sqlite:./database/sqlite.db`
- **사용자**: (없음)
- **비밀번호**: (없음)
- **MyBatis 매퍼**: `classpath:mapper/sqlite/*.xml`
- **Flyway**: `classpath:/db/migration,classpath:/db/migration/sqlite`

## 설정 파일 구조

```
src/main/resources/
├── application.properties              # 기본 설정 + 프로필 그룹 정의
├── application-local.properties       # 로컬 환경 공통 설정
├── application-local-h2.properties    # H2 데이터베이스 설정
├── application-local-oracle.properties # Oracle 데이터베이스 설정
├── application-local-mysql.properties  # MySQL 데이터베이스 설정
├── application-local-tibero.properties # Tibero 데이터베이스 설정
├── application-local-sqlite.properties # SQLite 데이터베이스 설정
├── application-dev.properties         # 개발 환경 (Oracle)
└── application-prod.properties        # 운영 환경 (Oracle)
```

### 각 파일의 역할

#### **application.properties**
- 애플리케이션 기본 정보 및 메타데이터
- Profile Groups 정의
- Git/GitHub 설정
- API 키 및 외부 서비스 설정
- 연계 프로그램 설정 (Spring Boot Admin, Prometheus, Elasticsearch 등)

#### **application-local.properties**
- 로컬 개발 환경의 공통 설정
- 개발 도구, 로깅, 서버, 캐시, 세션 등
- 데이터베이스 설정은 제외 (각 DB별 프로필에서 관리)

#### **application-local-{database}.properties**
- 각 데이터베이스별 고유 설정
- DataSource 설정
- MyBatis 매퍼 위치
- Flyway 마이그레이션 설정
- JPA 설정

#### **application-dev.properties**
- 개발 서버 환경 설정
- Oracle 데이터베이스 기반
- 상세한 로깅 및 모니터링

#### **application-prod.properties**
- 운영 환경 설정
- Oracle 데이터베이스 기반
- 성능 최적화 및 보안 강화
- 최소한의 로깅

## 장점

1. **설정 중복 제거**: 공통 설정은 `application-local.properties`에 정의
2. **DB별 독립성**: 각 데이터베이스별로 독립적인 설정 파일
3. **확장성**: 새로운 데이터베이스 추가 시 해당 프로필 파일만 추가
4. **명확한 구조**: Properties 형식으로 가독성 향상
5. **유지보수성**: 설정 변경 시 해당 파일만 수정
6. **프로필 그룹 활용**: `local` + `local-{database}` 조합으로 유연한 설정

## 새로운 데이터베이스 추가 방법

1. **build.gradle**에 JDBC 드라이버 의존성 추가
2. **application.properties**에 새로운 프로필 그룹 추가:
   ```properties
   spring.profiles.group.local-newdb=local,local-newdb
   ```
3. **application-local-newdb.properties** 파일 생성
4. **mapper/newdb/** 디렉토리 생성 및 XML 파일 복사
5. **db/migration/newdb/** 디렉토리 생성 및 SQL 파일 복사

## 주의사항

- 각 DB별 프로필은 `local` 프로필을 상속받습니다 (Profile Group 사용)
- 데이터베이스 연결 정보는 실제 환경에 맞게 수정해야 합니다
- P6Spy를 사용하여 SQL 쿼리를 모니터링할 수 있습니다
- 운영 환경에서는 보안을 위해 민감한 정보를 환경 변수로 관리하는 것을 권장합니다

## 환경별 특징

### 로컬 환경 (local-*)
- **로깅 레벨**: DEBUG
- **H2 콘솔**: 활성화 (H2만)
- **Actuator**: 모든 엔드포인트 활성화
- **개발 도구**: 활성화
- **캐시**: 개발용 설정

### 개발 환경 (dev)
- **로깅 레벨**: DEBUG
- **데이터베이스**: Oracle
- **Actuator**: 모든 엔드포인트 활성화
- **성능 모니터링**: 상세 설정

### 운영 환경 (prod)
- **로깅 레벨**: INFO/WARN
- **데이터베이스**: Oracle
- **Actuator**: 제한된 엔드포인트만 활성화
- **성능 최적화**: 캐시 크기 증가, 로깅 최소화
- **보안 강화**: 상세 정보 노출 제한

### 📊 포트 구성
- **모든 로컬 환경**: 9090
- **개발 환경**: 9090
- **운영 환경**: 9090 