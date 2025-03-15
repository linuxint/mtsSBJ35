# Cursor AI 프로젝트 마이그레이션을 위한 프롬프트

## 프로젝트 개요
Spring MVC + JSP 애플리케이션을 React + Spring Boot REST API 아키텍처로 마이그레이션해야 합니다. 마이그레이션에는 다음이 포함됩니다:

1. 백엔드를 Spring MVC에서 Spring Boot REST API로 전환
2. 프론트엔드를 JSP에서 TypeScript를 사용한 React로 전환
3. 데이터베이스를 Oracle에서 PostgreSQL로 마이그레이션
4. 모놀리식 아키텍처를 프론트엔드와 백엔드로 분리

## 참조 문서
마이그레이션 계획을 설명하는 세 가지 참조 문서가 있습니다:
1. `outline.md` - 고수준 마이그레이션 계획
2. `backend.md` - 상세 백엔드 아키텍처 설계
3. `backend_file.md` - 변경 대상 파일
4. `front.md` - 상세 프론트엔드 아키텍처 설계
5. `front_file.md` - 변경 대상 파일

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
2. JWT 기반 인증 구현
3. `backend.md`에 따라 패키지 구조 재구성
4. PostgreSQL을 위한 MyBatis 쿼리 최적화
5. REST API를 위한 적절한 오류 처리 및 응답 형식 구현

### 프론트엔드 변경 사항
1. TypeScript를 사용한 새로운 React 애플리케이션 생성
2. `front.md`에 설명된 대로 컴포넌트 구조 구현
3. 상태 관리에 Zustand 사용
4. API 통신에 React Query 및 Axios 사용
5. styled-components 또는 tailwindcss를 사용한 반응형 디자인 구현

### 데이터베이스 변경 사항
1. Oracle에서 PostgreSQL로 마이그레이션
2. 테이블 구조 및 인덱스 최적화
3. MyBatis 매퍼의 데이터베이스 쿼리 업데이트

## 구현 전략
1. 프론트엔드와 백엔드 모두에 대한 기본 프로젝트 구조 설정으로 시작
2. 먼저 공통 컴포넌트 및 유틸리티 구현
3. 핵심 기능을 모듈별로 마이그레이션
4. 마이그레이션된 각 모듈에 대한 종합적인 테스트 구현
5. 자동화된 테스트 및 배포를 위한 CI/CD 파이프라인 설정

## 특정 작업

### 백엔드 작업
1. `backend.md`에 정의된 구조로 새로운 Spring Boot 프로젝트 생성
2. JWT 인증 구현
3. 기존 컨트롤러를 REST 컨트롤러로 변환
4. REST 컨트롤러와 함께 작동하도록 서비스 계층 업데이트
5. PostgreSQL을 위한 MyBatis로 데이터베이스 접근 최적화
6. 적절한 오류 처리 및 응답 형식 구현
7. backend_file.md에 파일별로 변경한 사항을 기록해죠

### 프론트엔드 작업
1. TypeScript를 사용한 새로운 React 프로젝트 설정
2. `front.md`에 정의된 대로 컴포넌트 구조 구현
3. 재사용 가능한 UI 컴포넌트 생성
4. Zustand를 사용한 상태 관리 구현
5. React Query 및 Axios를 사용한 API 통신 설정
6. 반응형 디자인 구현
7. front_list.md에 파일별로 변경한 사항을 기록해죠

## 일정
`outline.md`에 설명된 일정을 따릅니다:
1. 1단계: 기반 (4주)
2. 2단계: 핵심 기능 (8주)
3. 3단계: 추가 기능 (4주)
4. 4단계: 안정화 (2주)

## 품질 보증
1. 프론트엔드와 백엔드 모두에 대한 단위 테스트 구현
2. 엔드-투-엔드 테스트 설정
3. 성능 테스트 수행
4. 보안 모범 사례 준수 확인
5. 접근성 준수 검증

프론트엔드와 백엔드 모두에 대한 기본 프로젝트 구조 설정부터 시작하여 이 마이그레이션 계획을 단계별로 구현하는 데 도움을 주세요.