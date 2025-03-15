# 백엔드 상세 설계

## 1. 프로젝트 구조
```
/src/main/java/com/devkbil/mtssbj
  /config        # 설정 클래스
  /common        # 공통 모듈
  /admin         # 관리자 기능
  /auth          # 인증 기능
  /board         # 게시판 기능
  /schedule      # 일정 관리
  /mail          # 메일 기능
  /project       # 프로젝트 관리
  /monitor       # 모니터링
  /search        # 검색 기능
```

## 2. 설정 변경
### 2.1 설정 클래스
/config
```
WebConfig.java        # CORS, 인터셉터 설정
SecurityConfig.java   # JWT 인증 방식
SwaggerConfig.java    # API 문서화
JwtConfig.java        # JWT 설정
DatabaseConfig.java   # MyBatis 설정
```

## 3. 공통 모듈
### 3.1 DTO
/common/dto
```
ApiResponse.java      # REST API 응답 형식
PageResponse.java     # 페이징 응답 형식
```

### 3.2 예외 처리
/common/exception
```
GlobalExceptionHandler.java  # 전역 예외 처리
CustomException.java         # 커스텀 예외
```

### 3.3 보안
/common/security
```
JwtTokenProvider.java        # JWT 토큰 관리
JwtAuthenticationFilter.java # JWT 인증 필터
```

### 3.4 유틸리티
/common/util
```
DateCore.java         # 날짜 핵심 기능
DateConverter.java    # 날짜 변환
DateCalculator.java   # 날짜 계산
DateVOMapper.java     # VO 변환
DateLegacy.java      # 레거시 지원

FileCore.java         # 파일 핵심 기능
FileIO.java          # 파일 입출력
FileOperation.java    # 파일 조작
FileDirectory.java    # 디렉토리 관리
FileList.java        # 파일 목록
FileUpload.java      # 파일 업로드
FileUtil.java        # 파일 유틸
FileSize.java        # 파일 크기
FileClasspath.java   # 클래스패스
```

## 4. 기능별 구조
### 4.1 관리자 기능
/admin
```
/controller
  CodeController.java       # 코드 관리 API
  MenuController.java       # 메뉴 관리 API
  BoardConfigController.java # 게시판 설정 API

/service
  CodeService.java
  MenuService.java
  BoardConfigService.java

/vo
  CodeVO.java
  MenuVO.java
  BoardConfigVO.java

/mapper
  CodeMapper.java
  MenuMapper.java
  BoardConfigMapper.java
```

### 4.2 인증 기능
/auth
```
/controller
  AuthController.java      # 인증 API
  ProfileController.java   # 프로필 API

/service
  AuthService.java
  ProfileService.java

/vo
  UserVO.java
  ProfileVO.java

/mapper
  UserMapper.java
  ProfileMapper.java
```

### 4.3 게시판 기능
/board
```
/controller
  BoardController.java     # 게시판 API
  CommentController.java   # 댓글 API

/service
  BoardService.java
  CommentService.java

/vo
  BoardVO.java
  CommentVO.java

/mapper
  BoardMapper.java
  CommentMapper.java
```

### 4.4 일정 관리
/schedule
```
/controller
  ScheduleController.java  # 일정 API
  CalendarController.java  # 캘린더 API

/service
  ScheduleService.java
  CalendarService.java

/vo
  ScheduleVO.java

/mapper
  ScheduleMapper.java
```

### 4.5 메일 기능
/mail
```
/controller
  MailController.java     # 메일 API
  ImapController.java     # IMAP API

/service
  MailService.java
  ImapService.java

/vo
  MailVO.java
  ImapConfigVO.java

/mapper
  MailMapper.java
```

### 4.6 프로젝트 관리
/project
```
/controller
  ProjectController.java  # 프로젝트 API
  TaskController.java     # 작업 API

/service
  ProjectService.java
  TaskService.java

/vo
  ProjectVO.java
  TaskVO.java

/mapper
  ProjectMapper.java
  TaskMapper.java
```

### 4.7 모니터링
/monitor
```
/controller
  SystemController.java   # 시스템 모니터링 API
  ActivityController.java # 활동 로그 API

/service
  SystemService.java
  ActivityService.java

/vo
  SystemVO.java
  ActivityVO.java

/mapper
  SystemMapper.java
  ActivityMapper.java
```

### 4.8 검색 기능
/search
```
/controller
  SearchController.java   # 통합 검색 API

/service
  SearchService.java

/vo
  SearchVO.java

/mapper
  SearchMapper.java
```

## 5. MyBatis 매퍼 XML 구조
/resources/mapper
```
/admin
  CodeMapper.xml
  MenuMapper.xml
  BoardConfigMapper.xml

/auth
  UserMapper.xml
  ProfileMapper.xml

/board
  BoardMapper.xml
  CommentMapper.xml

/schedule
  ScheduleMapper.xml

/mail
  MailMapper.xml

/project
  ProjectMapper.xml
  TaskMapper.xml

/monitor
  SystemMapper.xml
  ActivityMapper.xml

/search
  SearchMapper.xml
```

## 6. 성능 최적화
1. MyBatis 동적 쿼리 최적화
   - 조건부 쿼리 최적화
   - 배치 처리
   - 결과 매핑 개선

2. 캐시 전략
   - 메모리 캐시
   - Redis 캐시
   - 캐시 정책

3. 페이징 처리
   - 커서 기반 페이징
   - 오프셋 페이징
   - 성능 최적화

## 7. 보안
1. JWT 인증
   - 토큰 생성/검증
   - 리프레시 토큰
   - 토큰 저장소

2. Spring Security
   - 인증/인가 설정
   - 권한 관리
   - 보안 필터

3. API 보안
   - CORS 설정
   - XSS 방지
   - CSRF 방지

## 8. 로깅 및 모니터링
1. 로깅 전략
   - 로그 레벨
   - 로그 포맷
   - 로그 저장

2. 모니터링
   - 성능 모니터링
   - 에러 추적
   - 사용자 활동

## 9. 테스트
1. 단위 테스트
   - 서비스 레이어
   - 컨트롤러
   - 유틸리티

2. 통합 테스트
   - API 테스트
   - 데이터베이스
   - 보안

3. 성능 테스트
   - 부하 테스트
   - 스트레스 테스트
   - 병목 분석 