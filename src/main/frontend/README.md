# MTSSBJ Frontend

React.js 기반의 MTSSBJ 애플리케이션 프론트엔드입니다.

## 기능

- JWT 기반 인증 (로그인/로그아웃)
- 메인 페이지 데이터 표시
  - 프로젝트 목록
  - 뉴스 목록
  - 공지사항 목록
  - 타임라인
  - 캘린더
- 검색 기능

## 기술 스택

- React 19
- TypeScript
- Material-UI
- Zustand (상태 관리)
- Axios (API 통신)
- Vite (빌드 도구)

## 설치 및 실행

### 필수 조건

- Node.js 23.10.0
- npm 또는 yarn

### 설치

```bash
# 프로젝트 디렉토리로 이동
cd src/main/frontend

# 의존성 설치
npm install
# 또는
yarn install
```

### 개발 서버 실행

```bash
# 개발 서버 실행 (http://localhost:3000)
npm start
# 또는
yarn start
```

### 프로덕션 빌드

```bash
# 프로덕션용 빌드 생성
npm run build
# 또는
yarn build
```

빌드된 파일은 `build` 디렉토리에 생성됩니다.

## 백엔드 연동

프론트엔드는 기본적으로 `http://localhost:9090`에서 실행 중인 백엔드 API 서버와 통신하도록 설정되어 있습니다. 이 설정은 `vite.config.ts` 파일의 proxy 설정에서 변경할 수 있습니다.

## 프로젝트 구조

```
src/
  ├── components/       # 재사용 가능한 UI 컴포넌트
  ├── pages/            # 페이지 컴포넌트
  ├── services/         # API 서비스
  ├── store/            # 상태 관리 (Zustand)
  ├── types/            # TypeScript 타입 정의
  ├── utils/            # 유틸리티 함수
  ├── App.tsx           # 애플리케이션 루트 컴포넌트
  ├── main.tsx          # 애플리케이션 진입점
  └── theme.ts          # Material-UI 테마 설정
```

## 인증

인증은 JWT 토큰을 사용하여 구현되었습니다. 로그인 시 액세스 토큰과 리프레시 토큰이 발급되며, 이 토큰들은 localStorage에 저장됩니다. 액세스 토큰이 만료되면 리프레시 토큰을 사용하여 자동으로 새 액세스 토큰을 발급받습니다.
