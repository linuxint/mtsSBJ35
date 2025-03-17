# REST API 구현 문서

## 개요

이 문서는 기존 JSP 기반 웹 프로젝트를 React.js 기반의 SPA로 전환하기 위한 REST API 구현 내용을 설명합니다. 
현재 컨트롤러는 그대로 유지하면서 동일한 위치에 REST API를 추가하여 점진적인 마이그레이션을 가능하게 합니다.

## 구현된 기능

1. JWT 기반 인증 시스템
   - 로그인 API
   - 토큰 갱신 API
   - 액세스 토큰 및 리프레시 토큰 지원

2. 메인 페이지 데이터 API
   - 로그인 없는 메인 페이지 데이터
   - 로그인 후 메인 페이지 데이터
   - 캘린더 데이터

## 구현 상세

### 1. 공통 응답 형식

모든 API 응답은 일관된 형식을 따릅니다:

```json
{
  "success": true,
  "data": {
    "key": "value"
  },
  "error": null,
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

오류 발생 시:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "ERROR_CODE",
    "message": "오류 메시지",
    "details": {
      "field": "상세 오류 정보"
    }
  },
  "timestamp": "2023-03-15T12:34:56.789Z"
}
```

### 2. JWT 인증 시스템

#### 2.1 JWT 유틸리티 클래스 개선

기존 `JwtUtil` 클래스를 확장하여 다음 기능을 추가했습니다:

- 액세스 토큰 및 리프레시 토큰 생성
- 토큰 유효성 검증
- 리프레시 토큰을 통한 액세스 토큰 갱신

#### 2.2 인증 API 엔드포인트

##### 로그인 API

- **URL**: `/api/v1/auth/login`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "username": "사용자이름",
    "password": "비밀번호"
  }
  ```
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "tokenType": "Bearer",
      "expiresIn": 3600,
      "username": "사용자이름",
      "role": "[ROLE_USER]"
    },
    "error": null,
    "timestamp": "2023-03-15T12:34:56.789Z"
  }
  ```

##### 토큰 갱신 API

- **URL**: `/api/v1/auth/refresh`
- **Method**: POST
- **Request Body**:
  ```json
  {
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
  ```
- **Response**: 로그인 API와 동일한 형식

### 3. 메인 페이지 API

#### 3.1 메인 페이지 데이터 API

- **URL**: `/api/v1/main`
- **Method**: GET
- **Query Parameters**:
  - `searchKeyword` (선택): 검색 키워드
  - `page` (선택): 페이지 번호
  - `size` (선택): 페이지 크기
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "searchVO": { "searchKeyword": "프로젝트" },
      "projectlistview": [ { "id": 1, "title": "프로젝트 1" } ],
      "listview": [ { "id": 1, "title": "뉴스 1" } ],
      "noticeList": [ { "id": 1, "title": "공지사항 1" } ],
      "listtime": [ { "id": 1, "content": "타임라인 항목 1" } ],
      "calenList": [ { "date": "2023-03-10", "events": [] } ],
      "month": 3,
      "week": 2,
      "preWeek": "2023-03-06",
      "nextWeek": "2023-03-13"
    },
    "error": null,
    "timestamp": "2023-03-15T12:34:56.789Z"
  }
  ```

#### 3.2 캘린더 데이터 API

- **URL**: `/api/v1/main/calendar`
- **Method**: GET
- **Query Parameters**:
  - `date` (선택): 조회할 날짜 (YYYY-MM-DD 형식)
- **Response**:
  ```json
  {
    "success": true,
    "data": {
      "month": 3,
      "week": 2,
      "calenList": [ 
        { 
          "date": "2023-03-10", 
          "istoday": true,
          "list": [
            { "id": 1, "title": "회의", "time": "14:00" }
          ]
        } 
      ],
      "preWeek": "2023-03-06",
      "nextWeek": "2023-03-13"
    },
    "error": null,
    "timestamp": "2023-03-15T12:34:56.789Z"
  }
  ```

## 구현 클래스 목록

1. **API 응답 클래스**
   - `ApiResponse`: 모든 API 응답의 공통 형식을 정의

2. **인증 관련 클래스**
   - `AuthResponseDTO`: 인증 응답 DTO
   - `AuthRestController`: 인증 관련 REST API 컨트롤러

3. **메인 페이지 관련 클래스**
   - `MainRestController`: 메인 페이지 관련 REST API 컨트롤러

4. **JWT 관련 클래스**
   - `JwtUtil`: JWT 토큰 생성 및 검증 유틸리티 (기존 클래스 확장)

## 테스트 방법

### 로그인 API 테스트

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin"}'
```

### 메인 페이지 API 테스트

```bash
curl -X GET http://localhost:8080/api/v1/main \
  -H "Authorization: Bearer {액세스토큰}"
```

### 캘린더 API 테스트

```bash
curl -X GET http://localhost:8080/api/v1/main/calendar?date=2023-03-15 \
  -H "Authorization: Bearer {액세스토큰}"
```

## 향후 개선 사항

1. 보안 강화
   - CORS 설정 추가
   - CSRF 보호 구현
   - 토큰 블랙리스트 관리

2. 기능 확장
   - 사용자 등록 API
   - 사용자 프로필 관리 API
   - 게시판 관련 API

3. 성능 최적화
   - 응답 데이터 캐싱
   - 페이지네이션 최적화
   - 데이터 로딩 최적화
