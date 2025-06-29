# SBJ 3.5 개발 표준 가이드

---

## 1. 개요

### 1.1 목적
- 본 문서는 SBJ 3.5 프로젝트의 개발을 위한 표준을 제시하는 데 목적이 있습니다.
- 모든 개발자가 일관된 규칙을 따름으로써 유지보수성과 협업 효율을 극대화합니다.

### 1.2 범위
- 본 문서는 SBJ 3.5 신규 개발 시스템을 범위로 하며, 솔루션 및 외부 라이브러리는 제외합니다.

---

## 2. 목차
1. 네이밍 규칙(Name Rule)
    - 기본 Naming Rule
    - 패키지
    - 클래스
    - 메서드
    - 변수
    - 상수
    - DB 테이블/컬럼
    - Mapper/SQL
2. Java 코딩 스타일
    - 라인 길이
    - 들여쓰기
    - 공백 및 개행
    - 주석
    - 파일/디렉토리 구조
3. Swagger 및 API 문서화
4. 기타 개발 표준
    - 예외 처리
    - Git/버전관리
    - 기타

---

## 3. 네이밍 규칙(Name Rule)

### 3.1 기본 Naming Rule
- **Full English Description**: 누구나 이해할 수 있는 영어 단어 사용
- **CamelCase**: 변수, 메서드, 함수
- **PascalCase**: 클래스, 인터페이스
- **대문자 스네이크(CONTENT_MANAGEMENT)**: 상수
- **소문자**: 패키지
- **두 단어 이상 조합 시 두 번째 단어는 대문자** (ex: userName, getUserList)
- **이니셜 단어(URL, HTML 등)는 대문자 그대로**
- **최대 30자 이하** (클래스, 인터페이스, 메서드, 변수, 상수, 파일명)
- **예약어 사용 금지**
- **복수형 지양, 단수형 사용**
- **약어 사용 최소화** (명확한 의미 전달이 우선)

#### 예시
| 구분   | 표준 예시           | 비표준 예시      |
|--------|---------------------|-----------------|
| 변수   | userName            | usr_nm, nmUser  |
| 메서드 | getUserList         | get_user_list   |
| 클래스 | UserController      | usercontroller  |
| 상수   | MAX_SIZE            | maxSize         |
| 패키지 | com.sbj35.api       | com.SBJ35.Api   |

---

### 3.2 패키지
- **소문자만 사용**
- **레벨화**: com.sbj35.api, com.sbj35.domain, com.sbj35.service 등
- **2~15자 내외의 영문 소문자**
- **밑줄, 대문자, 숫자 시작 금지**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| com.sbj35.api       | com.sbj35.API   |
| com.sbj35.domain    | com.sbj35.Domain|

---

### 3.3 클래스
- **PascalCase**
- **간단하고 명시적**
- **밑줄 금지**
- **Controller: 서비스명+Controller.java**
- **Exception: 명사+Exception.java**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| UserController      | usercontroller  |
| CustomException     | custom_exception|

---

### 3.4 메서드
- **동사로 시작, 필요시 동사+명사**
- **CamelCase**
- **명확한 의미**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| getUser()           | get_user        |
| insertProject()     | insert_project  |
| updateBoard()       | updateboard     |
| deleteTask()        | delete_task     |

---

### 3.5 변수
- **CamelCase**
- **약어 사용 자제, 의미 명확**
- **밑줄, 달러($) 시작 금지**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| userName            | usr_nm          |
| projectTitle        | prj_title       |

---

### 3.6 상수
- **static final**
- **대문자 스네이크 표기법**
- **밑줄, 달러($) 시작 금지**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| MAX_SIZE            | maxSize         |
| DEFAULT_FLAG        | defaultFlag     |

---

### 3.7 DB 테이블/컬럼
- **Full English, 의미 명확, 약어 최소화, 30자 이하**
- **테이블명: 단수형, 대문자, 언더스코어 사용 가능**
- **컬럼명: 단수형, 대문자, 언더스코어 사용 가능**
- **복수형 지양**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| USER                | USERS           |
| PROJECT_TASK        | PROJECTTASKS    |
| USER_ID             | USERID          |
| PROJECT_TITLE       | PRJ_TITLE       |

---

### 3.8 Mapper/SQL
- **Mapper 파일명: Mapper 접미사 사용**
- **SQL ID: Java 메서드명과 동일하게 CamelCase**
- **Namespace: 패키지명 사용**

#### 예시
| 표준 예시           | 비표준 예시      |
|---------------------|-----------------|
| UserMapper.java     | user_mapper.java|
| selectUser          | select_user     |
| com.sbj35.mapper.UserMapper | sbj35.UserMapper |

---

## 4. Java 코딩 스타일

### 4.1 라인 길이
- 모든 줄은 120자 이내에서 작성
- 연산자 앞에서 개행 후 다음 줄에서 이어서 작성

### 4.2 들여쓰기
- Tab 대신 공백(Space) 4칸 사용
- 중괄호({})는 새 줄에 작성

### 4.3 공백 및 개행
- 연산자, 쉼표 뒤에는 한 칸 공백
- 논리적 블록 사이에는 한 줄 개행

### 4.4 주석
- 클래스/메서드/복잡한 로직에는 Javadoc 주석 사용
- 한글 주석은 가급적 피하고, 반드시 필요한 경우만 사용
- TODO, FIXME 등 표준 태그 사용

#### 예시
```java
/**
 * 사용자 정보를 반환합니다.
 * @param userId 사용자 ID
 * @return 사용자 정보
 */
public User getUser(String userId) {
    // TODO: 캐시 적용
    return userRepository.findById(userId);
}
```

---

### 4.5 파일/디렉토리 구조
- 패키지 구조와 일치하도록 디렉토리 구성
- Controller, Service, Domain, DTO, Mapper 등 역할별로 분리

#### 예시
```
src/main/java/com/sbj35/
    ├── api
    ├── domain
    ├── dto
    ├── service
    ├── config
    └── exception
```

---

## 5. Swagger 및 API 문서화
- Swagger(OpenAPI) 어노테이션 적극 활용
- @Schema, @Operation, @Tag 등으로 API 명세 작성
- 예시, 설명, 필수여부 등 상세 기재
- API 문서 자동화 도구(Swagger UI 등) 활용

#### 예시
```java
@Tag(name = "사용자 API")
public interface UserControllerDoc {
    @Operation(summary = "사용자 목록 조회", description = "사용자 전체 목록을 조회합니다.")
    List<UserDTO> getUserList();
}

@Data
public class UserDTO {
    @Schema(description = "사용자 ID", example = "user01")
    private String userId;
    @Schema(description = "사용자명", example = "홍길동")
    private String userName;
}
```

---

## 6. 기타 개발 표준

### 6.1 예외 처리
- 사용자 정의 Exception은 Exception 접미사 사용
- 공통 예외 처리 클래스 활용
- 예외 메시지는 명확하게 작성

### 6.2 Git/버전관리
- 커밋 메시지는 명확하게, 한글/영문 혼용 가능
- 기능/버그/문서/리팩토링 등 Prefix 사용 (feat, fix, docs, refactor 등)
- Pull Request 시 리뷰어 지정, 설명 필수

### 6.3 기타
- 불필요한 코드/주석/테스트코드는 커밋 전 반드시 제거
- 외부 라이브러리 사용 시 버전 명시 및 보안 검토

---

## 7. 부록: 금지/권장 사례 요약

| 항목      | 권장 사례                        | 금지 사례                |
|-----------|----------------------------------|--------------------------|
| 변수명    | userName, projectTitle           | usr_nm, prj_title        |
| 클래스명  | UserController, ProjectService   | usercontroller, prjSvc   |
| 상수명    | MAX_SIZE, DEFAULT_FLAG           | maxSize, defaultFlag     |
| 패키지명  | com.sbj35.api, com.sbj35.domain  | com.SBJ35.Api, api       |
| 테이블명  | USER, PROJECT_TASK               | USERS, PROJECTTASKS      |
| 컬럼명    | USER_ID, PROJECT_TITLE           | USERID, PRJ_TITLE        |
| 메서드명  | getUser, insertProject           | get_user, insert_project |

---

> 본 가이드는 [Chamomile 3.0.4 개발 표준 가이드](https://chamomile.lotteinnovate.com/new-guides/3.0.4/dev-standard-guide.html)를 참고하여 SBJ 3.5 프로젝트에 맞게 작성되었습니다.

## 데이터베이스 마이그레이션 (Flyway)

### 개요
이 프로젝트는 Flyway를 사용하여 H2, MySQL, Oracle, SQLite 데이터베이스의 형상관리를 지원합니다.

### 지원 데이터베이스
- **H2**: 개발 및 테스트 환경용 인메모리 데이터베이스
- **MySQL/MariaDB**: 개발 및 테스트 환경용 데이터베이스
- **Oracle**: 운영 환경용 데이터베이스
- **SQLite**: 경량 개발 및 테스트 환경용 데이터베이스

### Flyway 설정

#### 1. 의존성
```gradle
implementation 'org.flywaydb:flyway-core'
implementation 'org.flywaydb:flyway-database-oracle'
implementation 'org.xerial:sqlite-jdbc:3.50.1.0'
```

#### 2. 설정 파일
- `application.properties`: 전역 Flyway 설정
- `application.yml`: 환경별 Flyway 설정

#### 3. 마이그레이션 파일 구조
```
src/main/resources/db/migration/
├── V1__Add_Board_Indexes.sql          # 공통 마이그레이션
├── V3__Create_Initial_Schema_H2.sql   # H2 전용 스키마
├── V4__Create_Initial_Schema_Oracle.sql # Oracle 전용 스키마
├── V5__Create_Initial_Schema_SQLite.sql # SQLite 전용 스키마
├── h2/                                # H2 데이터베이스 스크립트
│   ├── tables_h2.sql
│   ├── sequence_h2.sql
│   ├── function_h2.sql
│   ├── foreignkey_h2.sql
│   └── tableData_h2.sql
├── mysql/                             # MySQL 데이터베이스 스크립트
│   ├── tables_mariadb.sql
│   └── tableData_mariadb.sql
├── oracle/                            # Oracle 데이터베이스 스크립트
│   ├── tables_oracle.sql
│   ├── tableData_oracle.sql
│   ├── sequence_oracle.sql
│   ├── function_oracle.sql
│   └── foreignkey_oracle.sql
└── sqlite/                            # SQLite 데이터베이스 스크립트
    ├── tables_sqlite.sql
    ├── tableData_sqlite.sql
    ├── function_sqlite.sql
    └── foreignkey_sqlite.sql
```

### 환경별 실행 방법

#### H2 환경
```bash
# H2 환경에서 실행
./gradlew bootRun --args='--spring.profiles.active=h2'
```

#### MySQL/MariaDB 환경
```bash
# MySQL 환경에서 실행
./gradlew bootRun --args='--spring.profiles.active=mysql'
```

#### Oracle 환경
```bash
# Oracle 환경에서 실행
./gradlew bootRun --args='--spring.profiles.active=dev'
# 또는
./gradlew bootRun --args='--spring.profiles.active=stag'
# 또는
./gradlew bootRun --args='--spring.profiles.active=prod'
```

#### SQLite 환경
```bash
# SQLite 환경에서 실행
./gradlew bootRun --args='--spring.profiles.active=sqlite'
```

### 데이터베이스별 특징

#### SQLite
- **장점**: 
  - 파일 기반 데이터베이스로 별도 서버 불필요
  - 경량화되어 개발/테스트에 적합
  - 단일 파일로 백업/복원 용이
- **사용 사례**: 
  - 개발 환경
  - 단위 테스트
  - 데모 환경
  - 모바일 애플리케이션

#### H2
- **장점**: 
  - 인메모리 데이터베이스로 빠른 실행
  - Java 기반으로 플랫폼 독립적
  - 웹 콘솔 제공
- **사용 사례**: 
  - 개발 환경
  - 통합 테스트
  - 프로토타이핑

#### MySQL/MariaDB
- **장점**: 
  - 오픈소스 RDBMS
  - 성능과 안정성의 균형
  - 풍부한 커뮤니티 지원
- **사용 사례**: 
  - 개발 환경
  - 중소규모 운영 환경
  - 웹 애플리케이션

#### Oracle
- **장점**: 
  - 엔터프라이즈급 안정성과 성능
  - 고급 기능 제공
  - 강력한 백업/복구 기능
- **사용 사례**: 
  - 대규모 운영 환경
  - 엔터프라이즈 애플리케이션
  - 고가용성이 요구되는 환경

### 마이그레이션 관리

#### 새로운 마이그레이션 추가
1. `V{버전}__{설명}.sql` 형식으로 파일 생성
2. 데이터베이스별 디렉토리에 해당 스크립트 추가
3. 애플리케이션 실행 시 자동 적용

#### 마이그레이션 상태 확인
```bash
# Flyway 상태 확인
./gradlew flywayInfo
```

#### 마이그레이션 실행
```bash
# Flyway 마이그레이션 실행
./gradlew flywayMigrate
```

#### 마이그레이션 롤백
```bash
# Flyway 롤백 (주의: 운영 환경에서는 신중하게 사용)
./gradlew flywayRepair
```

### 주의사항

1. **버전 관리**: 마이그레이션 파일의 버전 번호는 순차적으로 증가해야 함
2. **변경 금지**: 이미 실행된 마이그레이션 파일은 수정하지 말 것
3. **백업**: 운영 환경에서 마이그레이션 실행 전 반드시 백업
4. **테스트**: 새로운 마이그레이션은 테스트 환경에서 먼저 검증
5. **문법 차이**: 데이터베이스별 SQL 문법 차이를 고려하여 작성

### 문제 해결

#### 일반적인 문제들
1. **마이그레이션 실패**: 로그 확인 후 수동으로 문제 해결
2. **버전 충돌**: 팀원과 마이그레이션 버전 조율
3. **데이터 손실**: 백업에서 복원 후 마이그레이션 재실행

#### 로그 확인
```bash
# 애플리케이션 로그에서 Flyway 관련 메시지 확인
tail -f logs/application.log | grep -i flyway
``` 