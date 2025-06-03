# MTS 스프링 부트 웹 프로젝트 템플릿

Oracle과 MariaDB 데이터베이스를 모두 지원하는 MyBatis 3가 포함된 Spring Boot 3.5.0-M2 기반의 종합적인 웹 프로젝트 템플릿입니다.

## 개요

이 프로젝트는 세 가지 주요 구성 요소를 통합된 웹 애플리케이션 프레임워크로 결합합니다:
1. 전자 결재 시스템
2. 프로젝트 관리
3. 다중 게시판 시스템

이 프로젝트는 일반적으로 사용되는 기능들을 미리 구현하여 신속한 애플리케이션 개발에 적합한 견고한 기반을 제공하는 것을 목표로 합니다.

## 핵심 기능

### 1. 전자 결재 시스템
- **일정 관리**
  - 월간 캘린더 보기와 이벤트 계획
  - 회의 및 일정 추적
  - 알림 기능
- **문서 워크플로우**
  - 문서 초안 작성 및 생성
  - 다단계 승인 프로세스
  - 전자 서명 통합
  - 문서 상태 추적 및 이력
  - 템플릿 관리
- **이메일 통합**
  - 받은 편지함 관리 (새 메일, 받은 메일, 보낸 메일)
  - 이메일 알림 및 경고
  - 첨부 파일 처리 및 저장
  - 이메일 템플릿 지원
- **공지사항 게시판**
  - 공지사항 및 알림
  - 우선순위 기반 표시
  - 카테고리 관리
  - 리치 미디어 지원

### 2. 프로젝트 관리
- **작업 관리**
  - 작업 생성 및 할당
  - 진행 상황 추적 및 상태 업데이트
  - 개인 및 팀 작업 보기
  - 마감일 관리
  - 작업 의존성
- **리소스 계획**
  - 사용자 할당 및 역할 관리
  - 일정 관리
  - 작업량 분배
  - 리소스 할당 추적
  - 팀 용량 계획

### 3. 다중 게시판 및 사용자 관리
- **고급 게시판 기능**
  - 다중 게시판 유형 및 카테고리
  - 무한 댓글 스레딩
  - 좋아요/반응 기능
  - 리치 텍스트 콘텐츠 지원
  - 파일 첨부 처리
- **사용자 관리**
  - 역할 기반 접근 제어 (사용자/관리자)
  - 부서 기반 조직
  - 팝업 인터페이스가 있는 사용자 선택
  - 프로필 관리
  - 권한 시스템

### 4. 시스템 통합 및 도구
- **검색 시스템**
  - 전문 검색 기능
  - 문서 인덱싱
  - 검색 결과 하이라이팅
  - 고급 검색 필터
- **지도 통합**
  - 주소 변환
  - 위치 매핑
  - 지오코딩 서비스
- **문서 처리**
  - PDF 생성 및 처리
  - QR 코드 생성
  - 문서 템플릿
- **시스템 기능**
  - 다국어 지원
  - Excel 내보내기/가져오기 (jXLS)
  - 반응형 디자인
  - 오류 처리 및 모니터링
  - 종합적인 로깅
  - 차트 통합 및 시각화

### 5. 관리 및 모니터링
- **시스템 관리**
  - 서버 리소스 모니터링
  - 하드웨어/소프트웨어 관리
  - 서비스 상태 추적
  - 구성 관리
- **보안 관리**
  - 접근 제어
  - 인증 관리
  - 보안 로깅
  - 세션 관리
- **개발 도구**
  - 데이터베이스 도구
  - 파일 검색 유틸리티
  - 코드 의존성 분석
  - 로그 뷰어

## 기술 스택

### 백엔드
- **Java 23**
- **Spring Boot 3.5.0-M2**
  - Spring Security 6.5.0-M2
  - Spring Integration
  - Spring Data JPA
- **MyBatis 3**
- **데이터베이스**
  - Oracle 11g
  - MariaDB
- **추가 서비스**
  - Elasticsearch 8.15.1
  - Redis
  - James Mail Server

### 프론트엔드
- **핵심 라이브러리**
  - jQuery 2.2.3
  - Bootstrap (SB-Admin 2)
- **UI 컴포넌트**
  - CKEditor 4.5.10
  - FullCalendar v5
  - Morris Charts v0.5.0
  - DatePicker
  - DynaTree 1.2.4
  - jQuery EasyUI 1.4.3

### 개발 도구
- **IDE**: IntelliJ IDEA (권장)
- **빌드 도구**: Gradle
- **코드 품질**
  - Checkstyle
  - Spotless
  - Spring Java Format
- **문서화**: SpringDoc OpenAPI

### 설정 파일
- **핵심 설정**
  - application.properties/yml
  - DevkbilApplication.java
- **보안**
  - WebMvcConfig.java (CORS 설정)
- **사용자 정의**
  - mts-banner.txt (사용자 정의 배너)

## 설치 및 설정

### 전제 조건
1. **개발 도구**
   ```bash
   # Docker와 Colima 설치 (macOS용)
   brew install colima docker qemu

   # Colima 시작
   colima start --memory 4 --arch x86_64

   # Docker 컨텍스트 설정
   docker context use colima
   ```

2. **필수 소프트웨어**
   ```bash
   # 이미지 처리 도구 설치
   brew install imagemagick tesseract tesseract-lang exiftool ffmpeg leptonica
   ```

### 데이터베이스 설정

#### Oracle 데이터베이스
```bash
# Oracle 컨테이너 가져오기 및 실행
docker pull linuxint/oraclexe11g
docker run --name oracle11g -d -p 1521:1521 linuxint/oracle11g

# 설정 스크립트 실행
# 1. user_database_oracle.sql
# 2. tables_oracle.sql
# 3. tableData_oracle.sql
```

#### MariaDB
```bash
# 설정 스크립트 실행
# 1. user_database_mariadb.sql
# 2. tables_mariadb.sql
# 3. tableData_mariadb.sql
```

### 서비스 설정

#### Elasticsearch
```bash
# Elasticsearch 가져오기 및 실행
docker pull elasticsearch:8.15.1
docker run -p 9200:9200 -p 9300:9300 --name elasticsearch \
    -e "discovery.type=single-node" \
    -e "xpack.ml.enabled=false" \
    elasticsearch:8.15.1

# Elasticsearch 설정
docker cp elasticsearch:/usr/share/elasticsearch/config/elasticsearch.yml ./
# elasticsearch.yml 편집: xpack.security.enabled: false 설정
docker cp elasticsearch.yml elasticsearch:/usr/share/elasticsearch/config/

# Nori 분석기 설치
docker exec -it elasticsearch elasticsearch-plugin install analysis-nori

# 사전 복사
docker cp stopwords.txt elasticsearch:/usr/share/elasticsearch/config/
docker cp synonyms.txt elasticsearch:/usr/share/elasticsearch/config/
docker cp userdict.txt elasticsearch:/usr/share/elasticsearch/config/

# 인덱스 생성
curl -XPUT localhost:9200/mts -d @index_board.json \
    -H "Content-Type: application/json"

# 사용자 비밀번호 설정
docker exec -it elasticsearch /bin/bash
./bin/elasticsearch-setup-passwords interactive
# 다음 사용자의 비밀번호 설정:
# - elastic
# - apm_system
# - kibana_system
# - logstash_system
# - beats_system
# - remote_monitoring_user
# 기본 비밀번호 권장: manager

# 비밀번호 설정 프로세스 예시:
# Initiating the setup of passwords for reserved users elastic,apm_system,kibana,
# kibana_system,logstash_system,beats_system,remote_monitoring_user.
# You will be prompted to enter passwords as the process progresses.
# Please confirm that you would like to continue [y/N]y
#
# Enter password for [elastic]: manager
# Reenter password for [elastic]: manager
# Enter password for [apm_system]: manager
# ... (모든 사용자에 대해 반복)
```
#### logstash
```bash
docker pull docker.elastic.co/logstash/logstash:8.15.1
docker run --name logstash \
-p 5044:5044 \
-p 5000:5000/tcp \
-p 5000:5000/udp \
-p 9600:9600 \
-d docker.elastic.co/logstash/logstash:8.15.1
```

#### kibana
```bash
docker pull docker.elastic.co/kibana/kibana:8.15.1
docker run --name kibana \
-p 5601:5601 \
-d docker.elastic.co/kibana/kibana:8.15.1
```

#### 메일 서버 (James)
```bash
docker pull apache/james:demo-latest
docker run -p "465:465" -p "993:993" --name james apache/james:demo-latest
# IMAP: 993, SMTP: 465
# 기본 사용자: user01@james.local/1234
```

#### Redis
```bash
docker pull redis
docker run --name redis -d -p 6379:6379 redis
```

### 네트워크 설정
```bash
# Docker 네트워크 생성
docker network create mts-network

# 서비스 연결
docker network connect mts-network elasticsearch
docker network connect mts-network oracle11g
docker network connect mts-network james
docker network connect mts-network redis

# 연결 확인
docker network inspect mts-network
```

### 애플리케이션 설정
1. `application.properties`에 적절한 연결 설정 구성
2. 애플리케이션 빌드 및 실행:
   ```bash
   ./gradlew bootRun
   ```
3. http://localhost:9090 에서 애플리케이션 접속

### 기본 사용자
- 관리자: admin/1234
- 테스트 사용자: user1/1234, user2/1234

## 프로젝트 구조

```
.
├── src/
│   ├── main/
│   │   ├── java/          # Java 소스 파일
│   │   ├── resources/     # 애플리케이션 리소스
│   │   └── webapp/        # 웹 애플리케이션 파일
│   └── test/
│       └── java/          # 테스트 파일
├── database/              # 데이터베이스 스크립트
├── docker/               # Docker 설정
├── elasticsearch/        # Elasticsearch 설정
├── gradle/              # Gradle 래퍼 파일
└── docs/                # 문서
```

## 개발

### 코드 스타일
이 프로젝트는 Spring Java Format 규칙을 따릅니다. 일관된 코드 포맷팅을 위해:

1. IDE에 Spring Java Format 플러그인 설치
2. 커밋 전 코드 포맷팅:
   ```bash
   ./gradlew format
   ```
3. 포맷팅 확인:
   ```bash
   ./gradlew checkFormat
   ```

자세한 정보는 [Spring Java Format](https://github.com/spring-io/spring-javaformat)을 참조하세요.

### 빌드
```bash
# 정리 및 빌드
./gradlew clean build

# 테스트 실행
./gradlew test

# 테스트 없이 빌드
./gradlew build -x test
```

### 테스트
```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests "com.devkbil.mtssbj.YourTestClass"
```

## 기여하기

1. 저장소 포크
2. 기능 브랜치 생성:
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. 변경사항 커밋:
   ```bash
   git commit -m '멋진 기능 추가'
   ```
4. 브랜치에 푸시:
   ```bash
   git push origin feature/amazing-feature
   ```
5. Pull Request 열기

### 기여 가이드라인
- 기존 코드 스타일 준수
- 새로운 기능에 대한 테스트 추가
- 필요에 따라 문서 업데이트
- 커밋은 원자적이고 잘 설명되어야 함
- 커밋 메시지에 이슈 참조

## 문제 해결

### 일반적인 문제

1. **데이터베이스 연결 문제**
  - application.properties의 데이터베이스 자격 증명 확인
  - 데이터베이스 컨테이너 실행 확인:
    ```bash
    docker ps | grep oracle11g
    ```
  - 데이터베이스 로그 확인:
    ```bash
    docker logs oracle11g
    ```

2. **Elasticsearch 문제**
  - Elasticsearch 실행 확인:
    ```bash
    curl localhost:9200/_cluster/health
    ```
  - Elasticsearch 로그 확인:
    ```bash
    docker logs elasticsearch
    ```
  - elasticsearch.yml의 적절한 설정 확인

3. **메일 서버 문제**
  - James 서버 실행 확인:
    ```bash
    docker ps | grep james
    ```
  - SMTP 연결 테스트:
    ```bash
    telnet localhost 465
    ```
  - 메일 서버 로그 확인:
    ```bash
    docker logs james
    ```

4. **빌드 문제**
  - Gradle 캐시 정리:
    ```bash
    ./gradlew clean --refresh-dependencies
    ```
  - Java 버전 확인:
    ```bash
    java -version
    ```

더 많은 도움이 필요하시면 [이슈 트래커](https://github.com/your-repo/issues)를 확인하세요.

## 라이선스

이 프로젝트는 MIT 라이선스를 따릅니다 - 자세한 내용은 [LICENSE.md](LICENSE.md) 파일을 참조하세요.
