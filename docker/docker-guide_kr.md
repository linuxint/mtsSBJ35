# Docker 환경 설정 가이드

## 1. 개요

이 프로젝트는 다음 서비스들을 Docker 환경에서 실행합니다:
- Spring Boot 애플리케이션 (9090)
- Oracle Database (1521)
- Elasticsearch (9200)
- Prometheus (9091)
- Grafana (3001)

## 2. 사전 요구사항

- Docker Desktop 설치
- Docker Compose 설치
- Java 17 이상
- Gradle 8.x 이상

## 3. Docker 서비스 구성

### 3.1 서비스 포트 구성
| 서비스 | 포트 | 설명 |
|--------|------|------|
| Spring Boot | 9090 | 애플리케이션 서버 |
| Oracle DB | 1521 | 데이터베이스 |
| Elasticsearch | 9200 | 검색 엔진 |
| Prometheus | 9091 | 모니터링 메트릭 수집기 |
| Grafana | 3001 | 모니터링 대시보드 |

### 3.2 Docker 네트워크
- 모든 서비스는 `app-network`라는 Docker 네트워크에서 통신합니다.
- 서비스 간 통신은 컨테이너 이름을 호스트명으로 사용합니다.

### 3.3 데이터 영속성
- 각 서비스의 데이터는 Docker 볼륨에 저장됩니다:
  - `oracle_data`: Oracle 데이터베이스 데이터
  - `elasticsearch_data`: Elasticsearch 데이터
  - `prometheus_data`: Prometheus 메트릭 데이터
  - `grafana_data`: Grafana 대시보드 및 설정

## 4. 사용 방법

### 4.1 Gradle을 통한 실행

```bash
# 프로젝트 빌드 및 Docker 서비스 시작
./gradlew build

# Docker 서비스만 시작
./gradlew dockerUp

# Docker 서비스 중지
./gradlew dockerDown

# Docker 서비스 및 볼륨 제거
./gradlew dockerRemove

# 프로젝트 클린 및 Docker 서비스 제거
./gradlew clean
```

### 4.2 Docker 상태 확인

```bash
# Docker 서비스 상태 확인
./check-docker.sh
```

## 5. 서비스 접근 방법

| 서비스 | URL | 접근 정보 |
|--------|-----|-----------|
| Spring Boot | http://localhost:9090 | 애플리케이션 메인 |
| Oracle DB | localhost:1521 | SID: XE, PWD: oracle |
| Elasticsearch | http://localhost:9200 | 검색 엔진 API |
| Prometheus | http://localhost:9091 | 메트릭 수집기 UI |
| Grafana | http://localhost:3001 | ID: admin, PWD: admin |

## 6. 모니터링 설정

### 6.1 Prometheus 설정
- Spring Boot 애플리케이션의 메트릭을 수집
- 15초마다 메트릭 수집
- `/actuator/prometheus` 엔드포인트에서 메트릭 노출

### 6.2 Grafana 대시보드
- HTTP 요청 처리량 모니터링
- HTTP 응답 시간 모니터링
- 5초마다 자동 새로고침
- 6시간 데이터 보관

## 7. 문제 해결

### 7.1 Docker 서비스 시작 실패
1. Docker Desktop이 실행 중인지 확인
2. 포트 충돌 여부 확인
3. 로그 확인: `docker-compose logs`

### 7.2 데이터 영속성 문제
1. Docker 볼륨 상태 확인: `docker volume ls`
2. 볼륨 백업: `docker volume inspect [volume_name]`
3. 볼륨 복구: `docker volume create [volume_name]`

### 7.3 네트워크 연결 문제
1. Docker 네트워크 확인: `docker network ls`
2. 컨테이너 네트워크 연결 확인: `docker network inspect app-network`
3. 컨테이너 간 통신 테스트: `docker exec [container_name] ping [target_container]`

## 8. 보안 고려사항

- Grafana 기본 계정 비밀번호 변경 권장
- Oracle Database 접근 제한 설정
- Elasticsearch 보안 설정 활성화
- Prometheus 접근 제어 설정

## 9. 백업 및 복구

### 9.1 데이터 백업
```bash
# Oracle 데이터 백업
docker exec oracle exp userid=system/oracle file=/backup/oracle_backup.dmp

# Elasticsearch 데이터 백업
docker exec elasticsearch elasticsearch-dump --input=http://localhost:9200/ --output=/backup/es_backup.json

# Grafana 설정 백업
docker exec grafana grafana-cli admin reset-admin-password admin
```

### 9.2 데이터 복구
```bash
# Oracle 데이터 복구
docker exec oracle imp userid=system/oracle file=/backup/oracle_backup.dmp

# Elasticsearch 데이터 복구
docker exec elasticsearch elasticsearch-dump --input=/backup/es_backup.json --output=http://localhost:9200/
``` 