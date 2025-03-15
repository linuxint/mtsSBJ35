# Cursor AI 프로젝트 마이그레이션을 위한 프롬프트

## 프로젝트 개요
Spring MVC + JSP 애플리케이션을 React + Spring Boot REST API 아키텍처로 마이그레이션해야 합니다. 마이그레이션에는 다음이 포함됩니다:

1. 백엔드를 Spring MVC에서 Spring Boot REST API로 전환
2. 프론트엔드를 JSP에서 TypeScript를 사용한 React로 전환
3. 데이터베이스를 Oracle에서 PostgreSQL로 마이그레이션
4. 모놀리식 아키텍처를 프론트엔드와 백엔드로 분리

## 참조 문서
마이그레이션 계획을 설명하는 여러 참조 문서가 있습니다:
1. `outline.md` - 고수준 마이그레이션 계획
2. `backend.md` - 상세 백엔드 아키텍처 설계
3. `backend_file.md` - 변경 대상 파일
4. `backend_config.md` - Spring Boot를 위한 설정 변경 사항
5. `backend_security.md` - 보안 설정 및 JWT 구현
6. `backend_service.md` - 서비스 계층 변경 및 비즈니스 로직 업데이트
7. `backend_controller.md`, `backend_dto.md` 등 - 각 백엔드 컴포넌트에 대한 상세 명세
8. `front.md` - 상세 프론트엔드 아키텍처 설계
9. `front_list.md` - React 컴포넌트로 변환할 JSP 파일 목록

## 문서화된 파일
이미 마이그레이션이 필요한 많은 파일을 문서화했습니다. 이들은 `backend_file.md`에서 다음과 같이 분류됩니다:
1. 변경이 필요한 파일 (상세 마이그레이션 계획 포함)
2. 변경 없이 사용할 수 있는 파일
3. 아직 문서화가 필요한 파일

지금까지 완료된 문서화 작업은 다음과 같습니다:
- 구성 설정 (Redis, Cache, Elasticsearch)
- 환경별 구성
- 보안 확장 및 권한 어노테이션
- 애플리케이션 이벤트 리스너
- 메일 모듈 확장
- QR 코드 기능

## 현재 프로젝트 구조
현재 프로젝트는 JSP 뷰를 사용하는 Spring MVC 애플리케이션입니다. 주요 패키지는 다음과 같습니다:
- `/src/main/java/com/devkbil/mtssbj` - 메인 애플리케이션 코드
  - `/admin` - 관리자 기능
  - `/board` - 게시판 기능
  - `/common` - 공통 유틸리티
  - `/config` - 구성
  - `/crud` - CRUD 작업
  - `/develop` - 테스트 코드
  - `/error` - 오류 처리
  - `/etc` - 기타 기능
  - `/mail` - 메일 기능
  - `/main` - 메인 애플리케이션
  - `/member` - 회원 관리
  - `/monitor` - 모니터링
  - `/project` - 프로젝트 관리
  - `/schedule` - 일정 관리
  - `/search` - 검색 기능
  - `/sign` - 인증
- `/src/main/webapp` - JSP 뷰
- `/src/main/resources` - 구성 및 정적 리소스

## 필요한 변경 사항

### 백엔드 변경 사항
1. Spring MVC 컨트롤러를 JSP 뷰 대신 JSON을 반환하는 REST 컨트롤러로 변환
2. `backend_security.md`에 명시된 대로 세션 기반 인증을 대체하는 JWT 기반 인증 구현
3. `backend.md`에 따라 패키지 구조 재구성
4. PostgreSQL을 위한 MyBatis 쿼리 최적화
5. REST API를 위한 적절한 오류 처리 및 응답 형식 구현
6. `backend_config.md`에 문서화된 모든 구성 변경 사항 적용
7. `backend_security.md`에 따라 보안 구현 업데이트
8. `backend_service.md`에 따라 서비스 컴포넌트 수정
9. `backend_file.md`에 각 파일에 대한 모든 변경 사항 기록

### 프론트엔드 변경 사항
1. TypeScript를 사용한 새로운 React 애플리케이션 생성
2. `front.md`에 설명된 대로 컴포넌트 구조 구현
3. 상태 관리에 Zustand 사용
4. API 통신에 React Query 및 Axios 사용
5. styled-components 또는 tailwindcss를 사용한 반응형 디자인 구현
6. `front_list.md`에 각 파일에 대한 모든 변경 사항 기록

### 데이터베이스 변경 사항
1. Oracle에서 PostgreSQL로 마이그레이션
2. 테이블 구조 및 인덱스 최적화
3. MyBatis 매퍼의 데이터베이스 쿼리 업데이트

## 구현 전략
1. 프론트엔드와 백엔드 모두에 대한 기본 프로젝트 구조 설정으로 시작
2. 먼저 공통 컴포넌트 및 유틸리티 구현
3. 이미 문서화된 컴포넌트를 우선적으로 핵심 기능을 모듈별로 마이그레이션
4. 다음으로 backend_file.md에 "아직 문서화가 필요한 파일"로 나열된 파일에 집중
5. 마이그레이션된 각 모듈에 대한 종합적인 테스트 구현
6. 자동화된 테스트 및 배포를 위한 CI/CD 파이프라인 설정

## 특정 작업

### 백엔드 작업
1. `backend.md`에 정의된 구조로 새로운 Spring Boot 프로젝트 생성
2. `backend_security.md`에 따른 JWT 인증 구현
3. 기존 컨트롤러를 REST 컨트롤러로 변환
4. `backend_service.md` 지침에 따라 REST 컨트롤러와 함께 작동하도록 서비스 계층 업데이트
5. `backend_config.md`에 문서화된 캐싱, Redis 및 Elasticsearch 구성 구현
6. 문서화된 모든 애플리케이션 이벤트 리스너 적용
7. 환경별 설정 구성
8. PostgreSQL을 위한 MyBatis로 데이터베이스 접근 최적화
9. 적절한 오류 처리 및 응답 형식 구현
10. `backend_file.md`에 각 파일에 대한 모든 변경 사항 기록

### 프론트엔드 작업
1. TypeScript를 사용한 새로운 React 프로젝트 설정
2. `front.md`에 정의된 대로 컴포넌트 구조 구현
3. 프론트엔드 아키텍처에 명시된 대로 재사용 가능한 UI 컴포넌트 생성
4. Zustand를 사용한 상태 관리 구현
5. React Query 및 Axios를 사용한 API 통신 설정
6. 반응형 디자인 구현
7. `front_list.md`에 각 파일에 대한 모든 변경 사항 기록

## 일정
`outline.md`에 설명된 일정을 따릅니다:
1. 1단계: 기반 (4주)
   - 개발 환경 설정
   - 공통 컴포넌트 개발
   - 데이터베이스 마이그레이션 준비
2. 2단계: 핵심 기능 (8주)
   - 인증/권한 부여
   - 게시판 기능
   - 일정 관리
   - 메일 시스템
3. 3단계: 추가 기능 (4주)
   - 관리자 기능
   - 통계/보고서
   - 시스템 모니터링
4. 4단계: 안정화 (2주)
   - 통합 테스트
   - 성능 최적화
   - 사용자 매뉴얼 작성

## 품질 보증
1. 프론트엔드와 백엔드 모두에 대한 단위 테스트 구현
2. 엔드-투-엔드 테스트 설정
3. 성능 테스트 수행
4. 보안 모범 사례 준수 확인
5. 접근성 준수 검증

## 위험 관리
1. 기술적 위험
   - 레거시 코드 의존성
   - 데이터 마이그레이션 과제
   - 성능 문제
2. 운영상 위험
   - 서비스 중단 최소화
   - 롤백 계획
   - 사용자 교육

## 진행 상황 추적
1. 백엔드 파일 마이그레이션 진행 상황을 추적하기 위해 backend_file.md 사용
2. 프론트엔드 마이그레이션 추적을 위한 유사한 구조 생성
3. 작업이 진행됨에 따라 문서 업데이트
4. 발생한 문제와 해결책 문서화

이미 문서화된 컴포넌트부터 시작하여 아직 문서화가 필요한 컴포넌트로 이동하면서 이 마이그레이션 계획을 단계별로 구현하는 데 도움을 주세요.