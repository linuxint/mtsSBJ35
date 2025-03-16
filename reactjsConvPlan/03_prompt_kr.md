# Cursor AI 프로젝트 마이그레이션을 위한 프롬프트

## 프로젝트 개요
Spring MVC + JSP 애플리케이션을 React + Spring Boot REST API 아키텍처로 마이그레이션해야 합니다. 마이그레이션에는 다음이 포함됩니다:

1. 백엔드를 Spring MVC에서 Spring Boot REST API로 전환
2. 프론트엔드를 JSP에서 TypeScript를 사용한 React로 전환
3. 데이터베이스를 Oracle에서 PostgreSQL로 마이그레이션
4. 모놀리식 아키텍처를 프론트엔드와 백엔드로 분리

## backend 기술 구현 전략
마이그레이션에는 다음과 같은 상세 전략이 포함됩니다:
- JDK 17 기준
- Spring Boot 버전 유지
- Spring MVC → Spring Boot REST API
- JSP 뷰 → JSON 응답
- MyBatis 최적화
- JWT 기반 인증
- JPA는 인증 이외에는 삭제

### REST API 표준화
- **리소스 중심 설계**: URL은 명사 형태의 리소스를 표현
- **계층 구조 반영**: 리소스 간의 관계를 URL 구조에 반영
- **버전 관리**: API 버전을 URL에 포함 (`/api/v1/*`)
- **일관된 복수형 사용**: 리소스 컬렉션은 복수형으로 표현
- **소문자와 하이픈 사용**: URL은 소문자와 하이픈으로 구성

### 공통 응답 형식
모든 API 응답은 일관된 형식을 따릅니다:
```json
{
  "success": true,
  "data": {
    "field1": "value1",
    "field2": "value2"
  },
  "error": null,
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

## backend 마이그레이션 상세 계획

### 공통 모듈 변경
- XSS 방지를 위한 HTML 이스케이프 유틸리티 구현
- 보안 및 오류 처리가 향상된 파일 처리 유틸리티 업데이트
- 적절한 유효성 검사가 있는 문자열 유틸리티 재구성
- 트리 구조 기능을 TypeScript로 변환
- 더 나은 오류 관리로 JSON 처리 개선
- 최신 보안 모범 사례가 적용된 JWT 유틸리티 구현
- PII 데이터를 위한 포괄적인 마스킹 유틸리티 추가

### 설정 변경
- 새로운 어노테이션(@EnableAsync, @EnableCaching)으로 애플리케이션 진입점 업데이트
- JWT 기반 보안 구성 구현
- 향상된 타입 처리 및 캐싱으로 MyBatis 구성 개선
- REST API 액세스를 위한 CORS 구성 업데이트
- Swagger를 SpringDoc OpenAPI로 대체
- 속도 제한 구성 구현
- 애플리케이션 이벤트 리스너 통합
- Redis 및 Cache 구성 추가
- Elasticsearch 통합 구현
- 환경별 구성 프로필 추가
- 이메일 템플릿용 Thymeleaf 구성 구현
- XSS 방지 구성 추가

### 보안 구현
- 세션 기반에서 JWT 기반 인증으로 전환
- 무상태 아키텍처를 위한 Spring Security 구성 업데이트
- JWT 토큰 생성, 검증 및 갱신 메커니즘 추가
- JWT 검증을 위한 요청 필터 구현
- CORS 구성 업데이트
- 역할 기반 액세스 제어로 권한 부여 강화
- BCrypt를 사용한 비밀번호 암호화 개선
- 세션 만료 처리 추가
- 사용자 정의 인증 성공 핸들러 구현
- 역할 및 권한 엔터티 정의
- 사용자 정의 보안 어노테이션(@AdminAuthorize, @UserAuthorize 등) 추가
- 역할 기반 URL 매핑 로더 구현

### 컨트롤러 변환
- 모든 MVC 컨트롤러를 REST 컨트롤러로 변환
- JSP 뷰 반환을 JSON 응답으로 대체
- OpenAPI 문서화 어노테이션 추가
- 표준화된 응답 형식 구현
- 파일 업로드/다운로드 엔드포인트 업데이트
- 적절한 유효성 검사 처리 추가
- RESTful URL 구조 구현
- 목록 작업에 페이징 지원 추가
- 보안 어노테이션 업데이트
- 응답에 적절한 HTTP 상태 코드 구현
- 필요한 경우 버전 관리 지원 추가

### DTO 구현
- Bean Validation 어노테이션 추가
- OpenAPI 문서화 어노테이션 추가
- JSON 직렬화를 위한 순환 참조 제거
- 적절한 페이지네이션 지원이 있는 검색 요청 객체 생성
- 필요한 경우 사용자 정의 직렬화/역직렬화 추가

### 엔티티 변경
- MyBatis 지원하도록 VO 업데이트
- 적절한 어노테이션 추가
- equals/hashCode/toString 메서드 구현
- Serializable 인터페이스 추가
- Builder 패턴 구현
- 감사 필드(생성/업데이트 타임스탬프) 구현

### 예외 처리
- 표준화된 ErrorResponse 구조 생성
- 전역 예외 처리 구현
- 비즈니스별 예외 추가
- 유효성 검사 예외 처리 추가
- 보안 예외 처리 구현
- 사용자 정의 JWT 인증 예외 생성
- 모든 예외에 대한 적절한 로깅 추가
- 적절한 HTTP 상태 코드 반환

### 리포지토리 변환
- 필요한 경우 JPA에서 MyBatis로 변환
- 매퍼 어노테이션 업데이트
- 동적 쿼리 구현
- PostgreSQL 호환성을 위한 결과 매핑 업데이트
- 적절한 트랜잭션 관리 구현
- 일괄 작업 지원 추가
- 쿼리 성능 최적화

### 서비스 계층 변경
- 비즈니스 유효성 검사 구현
- 비즈니스 로직을 컨트롤러에서 서비스로 이동
- 트랜잭션 관리 추가
- 적절한 경우 캐싱 구현
- 크로스커팅 관심사를 위한 이벤트 발행 추가
- 필요한 경우 비동기 처리 구현
- 적절한 오류 처리 추가

## front 기술 구현 전략
마이그레이션에는 다음과 같은 상세 전략이 포함됩니다:
- 메인페이지는 Server Side Rendering 사용
- Client side Rendering 사용
- 화면로딩 속도를 빠르게 처리
- Zustand를 사용한 상태 관리
- React Hook Form 및 Zod를 사용한 폼 처리
- React Router를 사용한 라우팅
- Styled-components 또는 TailwindCSS를 사용한 스타일링
- Axios와 React Query를 사용한 API 통신
- Jest와 Storybook을 사용한 컴포넌트 테스트

### 컴포넌트 계층 구조
아토믹 디자인 패턴을 적용하여 컴포넌트를 다음과 같은 계층으로 구분합니다:

1. **아톰(Atoms)**: 버튼, 입력 필드, 아이콘 등 가장 기본적인 UI 요소
2. **분자(Molecules)**: 아톰을 조합한 작은 기능 단위 (예: 검색 필드, 폼 필드)
3. **유기체(Organisms)**: 분자를 조합한 복잡한 UI 섹션 (예: 헤더, 푸터, 폼)
4. **템플릿(Templates)**: 페이지 레이아웃을 정의하는 와이어프레임
5. **페이지(Pages)**: 실제 콘텐츠가 채워진 완성된 화면

### 디렉토리 구조
```
src/
├── components/
│   ├── atoms/
│   ├── molecules/
│   ├── organisms/
│   └── templates/
├── pages/
├── hooks/
├── contexts/
├── api/
├── utils/
└── types/
```

### 공통 컴포넌트 설계
- **레이아웃 컴포넌트**: MainLayout, AdminLayout, AuthLayout
- **폼 컴포넌트**: Form, TextField, SelectField, DateField, FileField 등
- **테이블 컴포넌트**: Table, TableSkeleton, TableFilter, TableExport
- **모달 컴포넌트**: Modal, ConfirmModal, FormModal
- **페이징 컴포넌트**: Pagination, InfiniteScroll
- **피드백 컴포넌트**: Toast, Alert, ErrorBoundary, LoadingIndicator

### 상태 관리 전략
- **로컬 상태**: useState, useReducer
- **전역 상태 관리**: Zustand
- **서버 상태 관리**: React Query
- **폼 상태**: React Hook Form + Zod

## React 컴포넌트 구조
마이그레이션은 다음과 같은 컴포넌트 기반 아키텍처를 따릅니다:
- 기능별로 `/components` 폴더에 재사용 가능한 컴포넌트 배치
- 완전한 뷰를 구성하기 위해 `/pages`에 페이지 컴포넌트 배치
- UI 컴포넌트와 로직 간의 관심사 분리
- 기존 기능을 반영하는 명확한 폴더 구조

## DB 기술 구현 전략
- 테이블 구조 최적화
- 인덱스 재설계

### 데이터베이스 설계
주요 테이블 구조는 다음과 같습니다:

1. **사용자 관리**: AUTH_USER, AUTH_DEPARTMENT
2. **공통 코드 관리**: COMMON_CODE_GROUP, COMMON_CODE
3. **메뉴 관리**: MENU
4. **게시판 관리**: BOARD_MASTER, BOARD_POST, BOARD_COMMENT
5. **파일 관리**: FILE_INFO
6. **프로젝트 관리**: PROJECT, PROJECT_TASK, PROJECT_TASK_ASSIGNEE
7. **메일 시스템**: MAIL_CONFIG, MAIL, MAIL_RECIPIENT
8. **감사 및 보안**: AUDIT_LOG, PERSISTENT_LOGIN

### 인덱스 최적화
각 테이블에 대해 다음과 같은 인덱스 전략을 적용합니다:

- **기본 인덱스**: 자주 조회되는 단일 컬럼에 대한 인덱스
- **복합 인덱스**: 함께 조회되는 컬럼들에 대한 인덱스
- **성능 최적화**: 쿼리 패턴에 맞는 인덱스 설계

### 데이터 마이그레이션
데이터 마이그레이션은 다음 원칙에 따라 진행합니다:

1. **데이터 무결성 보장**: 모든 데이터는 손실 없이 마이그레이션
2. **단계적 마이그레이션**: 테이블 그룹별 순차적 마이그레이션
3. **다운타임 최소화**: 사전 데이터 복제, 증분 데이터 동기화

마이그레이션 후에는 데이터 검증, 롤백 계획 수립, 성능 최적화 작업을 수행합니다.

## 마이그레이션 전략
### 단계별 마이그레이션 계획
1. **준비 단계**
   - 개발 환경 설정
   - 데이터베이스 스키마 설계 검증
   - React.js 프로젝트 초기화
   - 기본 컴포넌트 및 라우팅 설정
   - API 클라이언트 구현

2. **데이터베이스 마이그레이션**
   - 스키마 변경 스크립트 작성
   - 데이터 마이그레이션 스크립트 작성
   - 테스트 환경에서 검증
   - 성능 테스트 및 최적화
   - 프로덕션 마이그레이션 실행

3. **핵심 기능 마이그레이션**
   - 인증 시스템 구현 (로그인/로그아웃)
   - 사용자 프로필 관리
   - 게시판 기능
   - 메일 기능
   - 일정 관리 기능

4. **추가 기능 마이그레이션**
   - 관리자 기능
   - 검색 기능
   - 파일 업로드/다운로드
   - 알림 시스템

5. **테스트 및 최적화**
   - 데이터베이스 성능 테스트
   - 단위 테스트 및 통합 테스트
   - 성능 최적화
   - 사용자 경험 개선
   - 크로스 브라우저 호환성 테스트

### 주요 고려사항
- **기술적 도전 과제**: 데이터베이스 스키마 변경 관리

<!-- AI 작업 지시: 
1. backend_filelist.md, frontend_filelist.md, db_filelist.md를 분석하여 파일별 변경 내용을 파악합니다. 
2. backend_filelist.md의 파일을 다음과 같은 카테고리로 구분해서 변경할 내용을 정리합니다:
   - `backend_common.md` : 공통 모듈 관련 변경 사항
   - `backend_config.md` : 설정 관련 변경 사항
   - `backend_controller.md` : Controller 관련 변경 사항
   - `backend_dto.md` : DTO 관련 변경 사항
   - `backend_entity.md` : Entity 관련 변경 사항
   - `backend_exception.md` : 예외 처리 관련 변경 사항
   - `backend_repository.md` : Repository(Data Access) 관련 변경 사항
   - `backend_security.md` : 보안(Security) 관련 변경 사항
   - `backend_service.md` : Service 관련 변경 사항

3. 분석 결과로 각 파일 변경 여부를 `backend_filelist_chklist.md`에 기록합니다. 
   - 변경사항이 있을 경우 `Yes | 변경 이유 요약`으로 표현합니다.
   - 변경사항이 없을 경우 `No`로 표현합니다.
4. 파일 분석 기준:
   - 기능 추가/제거, 메서드 변경, 주석 변경 등 명시적 변경.
   - 비즈니스 요구사항에 따른 코드 수정 여부.
   - 리팩토링(가독성 또는 성능 개선)의 필요성 여부.
-->

<!-- AI 작업 지시: 
1. frontend_filelist.md를 분석하여 파일별 변경 사항을 파악합니다.
2. 변경 사항을 다음 파일로 나누어 기록합니다:
   - `frontend_layout.md`: 레이아웃 관련 (헤더/푸터 컴포넌트, 관리자 헤더 컴포넌트, 메인 레이아웃 컴포넌트)
   - `frontend_paging.md`: 페이징 관련 (페이지네이션 UI 컴포넌트, 페이지네이션 로직을 위한 커스텀 훅)
   - `frontend_message.md`: 메시지 관련 (알림(Alert) 컴포넌트, 토스트(Toast) 컴포넌트 및 컨테이너)
   - `frontend_navigation.md`: 네비게이션 관련 (메뉴 아이템 인터페이스 정의, 권한 기반 메뉴 필터링, 중첩 메뉴 구조 지원)
   - `frontend_auth.md`: 인증 및 권한 관련 (권한 없음 페이지, 권한 검사 HOC)
   - `frontend_role.md`: 권한 및 역할 설정 관련.
   - `frontend_etc.md`: 기타 공통 컴포넌트 (로딩 컴포넌트, 모달 컴포넌트 등).

3. 각각 파일에 다음 항목을 기록합니다:
   - **변경 파일**: 파일명이 변경된 경우.
   - **작업 내용**: 코드 수정, 기능 추가/제거, 주석 수정, 리팩토링 등.
   - **변경 이유**: 왜 이 부분이 변경되었는지 작성 (버그 수정, 신규 기능 추가, 성능 개선 등).
   - **예상 영향도**: 수정 사항이 코드나 시스템에 미칠 잠재적인 영향 (추가 확인, 테스트 필요 여부 등).
-->

<!-- AI에게 지시: 
1. db_filelist.md 파일에 나열된 데이터베이스 관련 파일을 분석하여 각 파일의 변경 사항을 파악합니다.
2. 변경된 내용을 db_속성.md 파일에 다음 형식으로 기록해 주세요:
   - **파일 이름**: 변경한 데이터베이스 파일 이름
   - **변경 내용**: 추가, 수정, 삭제된 데이터베이스 테이블, 뷰, 속성 등 요약
   - **변경 이유**: 변경의 배경 및 목적, 예를 들어 "테이블 최적화" 또는 "새로운 비즈니스 요구 사항 반영" 등을 기재
-->

<!-- AI에게 지시: 
1. frontend_filelist.md, db_filelist.md, backend_filelist.md를 분석하여 파일별로 소스 수정 내용을 각각 다음 파일들에 기록해 주세요:
   - 프런트엔드 변경 기록: frontend_codechg.md
   - 데이터베이스 변경 기록: db_codechg.md
   - 백엔드 변경 기록: backend_codechg.md

2. 각 변경 내용 파일에는 다음 형식으로 내용을 기록해 주세요:
   - **파일 이름**: 소스 코드가 수정된 파일의 이름.
   - **수정 내용 요약**: 어떤 부분이 수정되었는지 기능/로직/구조 관점에서 설명 (추가, 수정, 삭제 등).
   - **수정 이유**: 왜 이 부분이 변경되었는지 작성 (버그 수정, 신규 기능 추가, 성능 개선 등).
   - **예상 영향도**: 수정 사항이 코드나 시스템에 미칠 잠재적인 영향 (추가 확인, 테스트 필요 여부 등).
-->