# 백엔드 파일 변경 체크리스트

## 컨트롤러 파일
- [Yes | REST API 전환, JWT 인증 적용] CodeController.java
- [Yes | REST API 전환, JWT 인증 적용] DepartmentController.java
- [Yes | REST API 전환, JWT 인증 적용] HWController.java
- [Yes | REST API 전환, JWT 인증 적용] SWController.java
- [Yes | REST API 전환, JWT 인증 적용, 파일 첨부 기능 개선] BoardController.java
- [Yes | REST API 전환, 파일 처리 보안 강화] FileDownload.java
- [Yes | REST API 전환, JWT 인증 적용, 사용자 관리 기능 개선] MemberController.java
- [Yes | REST API 전환, JWT 인증 적용] TaskController.java

## 서비스 파일
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가] CodeService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가, 부서 트리 구조 처리 개선] DepartmentService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가] HWService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가] SWService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가, 파일 처리 로직 개선] BoardService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가, 인증/인가 처리 개선] MemberService.java
- [Yes | 비즈니스 로직 분리, 트랜잭션 관리 추가] TaskService.java

## 엔티티 파일
- [Yes | MyBatis VO 변환, 직렬화 지원 추가] CodeVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가, 트리 구조 지원] DepartmentVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가] HWVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가] SWVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가, 파일 목록 처리 개선] BoardVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가, 보안 정보 처리 개선] MemberVO.java
- [Yes | MyBatis VO 변환, 직렬화 지원 추가] TaskVO.java

## DTO 파일
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가] CodeDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가, 트리 구조 지원] DepartmentDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가] HWDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가] SWDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가, 파일 처리 지원] BoardDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가, 인증 정보 처리] MemberDTO.java
- [Yes | 신규 생성, 요청/응답 분리, 유효성 검사 추가] TaskDTO.java

## 설정 및 보안 파일
- [Yes | JWT 기반 보안 설정, CORS 설정 추가] SecurityConfig.java
- [Yes | CORS, 인터셉터 설정, API 경로 설정] WebConfig.java
- [Yes | PostgreSQL 연결 설정, 커넥션 풀 최적화] DatabaseConfig.java
- [Yes | JWT 설정, 토큰 만료 시간 설정] JwtConfig.java
- [Yes | JWT 토큰 생성 및 검증 로직 구현] JwtTokenProvider.java
- [Yes | 사용자 인증 처리, 권한 처리 개선] CustomUserDetailsService.java
- [Yes | JWT 인증 필터 구현, 토큰 갱신 처리] JwtAuthenticationFilter.java

## 유틸리티 파일
- [Yes | 파일 처리 보안 강화, 안전한 파일 업로드/다운로드 구현] FileUtil.java
- [Yes | 문자열 처리 유틸리티, XSS 방지 기능 추가] StringUtil.java
- [Yes | 날짜 처리 유틸리티, 타임존 처리 개선] DateUtil.java
- [Yes | 보안 관련 유틸리티, 민감 정보 마스킹 기능 추가] SecurityUtil.java