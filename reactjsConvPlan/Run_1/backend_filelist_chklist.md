# 백엔드 파일 변경 체크리스트

## 컨트롤러 파일
- [ ] CodeController.java - REST API 전환, JWT 인증 적용
- [ ] DepartmentController.java - REST API 전환, JWT 인증 적용
- [ ] HWController.java - REST API 전환, JWT 인증 적용
- [ ] SWController.java - REST API 전환, JWT 인증 적용
- [ ] BoardController.java - REST API 전환, JWT 인증 적용
- [ ] FileDownload.java - REST API 전환, 파일 처리 보안 강화
- [ ] MemberController.java - REST API 전환, JWT 인증 적용
- [ ] TaskController.java - REST API 전환, JWT 인증 적용

## 서비스 파일
- [ ] CodeService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] DepartmentService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] HWService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] SWService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] BoardService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] MemberService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가
- [ ] TaskService.java - 비즈니스 로직 분리, 트랜잭션 관리 추가

## 엔티티 파일
- [ ] CodeVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] DepartmentVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] HWVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] SWVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] BoardVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] MemberVO.java - JPA 엔티티 변환, 유효성 검사 추가
- [ ] TaskVO.java - JPA 엔티티 변환, 유효성 검사 추가

## DTO 파일
- [ ] CodeDTO.java - 신규 생성, 요청/응답 분리
- [ ] DepartmentDTO.java - 신규 생성, 요청/응답 분리
- [ ] HWDTO.java - 신규 생성, 요청/응답 분리
- [ ] SWDTO.java - 신규 생성, 요청/응답 분리
- [ ] BoardDTO.java - 신규 생성, 요청/응답 분리
- [ ] MemberDTO.java - 신규 생성, 요청/응답 분리
- [ ] TaskDTO.java - 신규 생성, 요청/응답 분리

## 설정 및 보안 파일
- [ ] SecurityConfig.java - JWT 기반 보안 설정
- [ ] WebConfig.java - CORS, 인터셉터 설정
- [ ] DatabaseConfig.java - PostgreSQL 연결 설정
- [ ] JwtConfig.java - JWT 설정
- [ ] JwtTokenProvider.java - JWT 토큰 처리
- [ ] CustomUserDetailsService.java - 사용자 인증 처리
- [ ] JwtAuthenticationFilter.java - JWT 인증 필터

## 유틸리티 파일
- [ ] FileUtil.java - 파일 처리 보안 강화
- [ ] StringUtil.java - 문자열 처리 유틸리티
- [ ] DateUtil.java - 날짜 처리 유틸리티
- [ ] SecurityUtil.java - 보안 관련 유틸리티 