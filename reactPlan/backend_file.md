# 백엔드 파일 목록 및 변경 사항

## 1. 공통 모듈
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 1.1 | common/HtmlCharacterEscapes.java | HTML 문자 이스케이프 처리 | [변경사항](backend_common.md#html-character-escapes) |
| 1.2 | common/util/DateUtil.java | 날짜 관련 유틸리티 | [변경사항](backend_common.md#date-util) |
| 1.3 | common/util/FileUtil.java | 파일 처리 유틸리티 | [변경사항](backend_common.md#file-util) |
| 1.4 | common/util/NewStringUtil.java | 문자열 처리 유틸리티 | [변경사항](backend_common.md#string-util) |
| 1.5 | common/tree/TreeMaker.java | 트리 구조 처리 | [변경사항](backend_common.md#tree-maker) |
| 1.6 | common/util/JsonUtil.java | JSON 처리 유틸리티 | [변경사항](backend_common.md#json-util) |
| 1.7 | common/util/JwtUtil.java | JWT 토큰 처리 | [변경사항](backend_common.md#jwt-util) |
| 1.8 | common/masking/MaskingUtil.java | 데이터 마스킹 처리 | [변경사항](backend_common.md#masking-util) |

## 2. 설정
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 2.1 | MtssbjApplication.java | 애플리케이션 메인 클래스 | [변경사항](backend_config.md#application) |
| 2.2 | config/WebMvcConfig.java | 웹 MVC 설정 | [변경사항](backend_config.md#web-mvc) |
| 2.3 | config/security/SpringSecurityConfig.java | 스프링 시큐리티 설정 | [변경사항](backend_config.md#security) |
| 2.4 | config/MyBatisConfig.java | MyBatis 설정 | [변경사항](backend_config.md#mybatis) |
| 2.5 | config/JPAConfig.java | JPA 설정 | [변경사항](backend_config.md#jpa) |
| 2.6 | config/CorsConfig.java | CORS 설정 | [변경사항](backend_config.md#cors) |
| 2.7 | config/SpringDocConfig.java | API 문서 설정 | [변경사항](backend_config.md#springdoc) |

## 3. 관리자 기능
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 3.1 | admin/code/CodeController.java | 코드 관리 컨트롤러 | [변경사항](backend_controller.md#code-controller) |
| 3.2 | admin/menu/MenuController.java | 메뉴 관리 컨트롤러 | [변경사항](backend_controller.md#menu-controller) |
| 3.3 | admin/organ/DepartmentController.java | 조직 관리 컨트롤러 | [변경사항](backend_controller.md#department-controller) |
| 3.4 | admin/board/BoardGroupController.java | 게시판 그룹 관리 컨트롤러 | [변경사항](backend_controller.md#board-group-controller) |

## 4. 게시판
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 4.1 | board/BoardController.java | 게시판 컨트롤러 | [변경사항](backend_controller.md#board-controller) |
| 4.2 | board/BoardService.java | 게시판 서비스 | [변경사항](backend_service.md#board-service) |
| 4.3 | board/BoardVO.java | 게시판 VO | [변경사항](backend_entity.md#board-entity) |
| 4.4 | board/BoardReplyVO.java | 게시판 댓글 VO | [변경사항](backend_entity.md#board-reply-entity) |

## 5. 회원
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 5.1 | member/MemberController.java | 회원 컨트롤러 | [변경사항](backend_controller.md#member-controller) |
| 5.2 | member/MemberService.java | 회원 서비스 | [변경사항](backend_service.md#member-service) |
| 5.3 | member/auth/AuthController.java | 인증 컨트롤러 | [변경사항](backend_controller.md#auth-controller) |
| 5.4 | member/auth/AuthService.java | 인증 서비스 | [변경사항](backend_service.md#auth-service) |

## 6. 일정
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 6.1 | schedule/SchController.java | 일정 컨트롤러 | [변경사항](backend_controller.md#schedule-controller) |
| 6.2 | schedule/SchService.java | 일정 서비스 | [변경사항](backend_service.md#schedule-service) |
| 6.3 | schedule/SchVO.java | 일정 VO | [변경사항](backend_entity.md#schedule-entity) |
| 6.4 | schedule/CalendarVO.java | 캘린더 VO | [변경사항](backend_entity.md#calendar-entity) |

## 7. 메일
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 7.1 | mail/MailController.java | 메일 컨트롤러 | [변경사항](backend_controller.md#mail-controller) |
| 7.2 | mail/MailService.java | 메일 서비스 | [변경사항](backend_service.md#mail-service) |
| 7.3 | mail/MailVO.java | 메일 VO | [변경사항](backend_entity.md#mail-entity) |
| 7.4 | mail/ImportMail.java | 메일 가져오기 | [변경사항](backend_service.md#import-mail) |

## 8. 검색
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 8.1 | search/SearchController.java | 검색 컨트롤러 | [변경사항](backend_controller.md#search-controller) |
| 8.2 | search/IndexingController.java | 인덱싱 컨트롤러 | [변경사항](backend_controller.md#indexing-controller) |
| 8.3 | search/SearchVO.java | 검색 VO | [변경사항](backend_entity.md#search-entity) |

## 9. 프로젝트
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 9.1 | project/ProjectController.java | 프로젝트 컨트롤러 | [변경사항](backend_controller.md#project-controller) |
| 9.2 | project/ProjectService.java | 프로젝트 서비스 | [변경사항](backend_service.md#project-service) |
| 9.3 | project/TaskController.java | 작업 컨트롤러 | [변경사항](backend_controller.md#task-controller) |
| 9.4 | project/TaskService.java | 작업 서비스 | [변경사항](backend_service.md#task-service) |

## 10. 에러 처리
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 10.1 | error/GlobalExceptionHandler.java | 전역 예외 처리 | [변경사항](backend_error.md#global-exception) |
| 10.2 | error/BusinessExceptionHandler.java | 비즈니스 예외 처리 | [변경사항](backend_error.md#business-exception) |
| 10.3 | error/ErrorResponse.java | 에러 응답 | [변경사항](backend_error.md#error-response) |
| 10.4 | error/ApiResponse.java | API 응답 | [변경사항](backend_error.md#api-response) |

# 추가 변경 필요 파일 목록

## 11. 개발 도구
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 11.1 | develop/dbtool/DbtoolController.java | DB 도구 컨트롤러 | [변경사항](backend_controller.md#dbtool-controller) |
| 11.2 | develop/dbtool/DbtoolService.java | DB 도구 서비스 | [변경사항](backend_service.md#dbtool-service) |
| 11.3 | develop/naver/map/NaverApiController.java | 네이버 지도 API | [변경사항](backend_controller.md#naver-api) |
| 11.4 | develop/qrcode/QrCodeController.java | QR코드 컨트롤러 | [변경사항](backend_controller.md#qrcode-controller) |
| 11.5 | develop/ratelimit/RateLimitingController.java | 속도 제한 컨트롤러 | [변경사항](backend_controller.md#ratelimit-controller) |

## 12. 모니터링
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 12.1 | monitor/CacheMonitorController.java | 캐시 모니터링 | [변경사항](backend_controller.md#cache-monitor) |
| 12.2 | monitor/MonitorSecurityConfig.java | 모니터링 보안 설정 | [변경사항](backend_config.md#monitor-security) |

## 13. 서버 관리
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 13.1 | admin/server/SvcController.java | 서비스 관리 | [변경사항](backend_controller.md#service-management) |
| 13.2 | admin/server/HWController.java | 하드웨어 관리 | [변경사항](backend_controller.md#hardware-management) |
| 13.3 | admin/server/SWController.java | 소프트웨어 관리 | [변경사항](backend_controller.md#software-management) |

## 14. 공통 인터셉터
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 14.1 | common/interceptor/LoginInterceptor.java | 로그인 인터셉터 | [변경사항](backend_security.md#login-interceptor) |
| 14.2 | common/interceptor/AdminInterceptor.java | 관리자 인터셉터 | [변경사항](backend_security.md#admin-interceptor) |
| 14.3 | common/interceptor/CommonInterceptor.java | 공통 인터셉터 | [변경사항](backend_security.md#common-interceptor) |

## 15. 이벤트 리스너
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 15.1 | common/listener/ApplicationStartingEventListener.java | 앱 시작 리스너 | [변경사항](backend_config.md#app-lifecycle) |
| 15.2 | common/listener/ApplicationReadyEventListener.java | 앱 준비 리스너 | [변경사항](backend_config.md#app-lifecycle) |
| 15.3 | common/listener/TransactionEventListener.java | 트랜잭션 리스너 | [변경사항](backend_config.md#transaction-events) |

## 16. 기타 유틸리티
| 번호 | 파일명 | 파일설명 | 변경사항 |
|------|--------|----------|-----------|
| 16.1 | common/Upload4ckeditor.java | CKEditor 업로드 | [변경사항](backend_common.md#file-upload) |
| 16.2 | common/MakeExcel.java | 엑셀 생성 | [변경사항](backend_common.md#excel-util) |
| 16.3 | common/code/CodeCacheService.java | 코드 캐시 서비스 | [변경사항](backend_service.md#code-cache) |

# 변경 불필요 파일 목록
다음 파일들은 현재 구조에서 그대로 사용 가능합니다:

1. 개발 도구 관련
   - develop/logview/* (로그 뷰어 관련 파일들)
   - develop/filesearch/* (파일 검색 관련 파일들)
   - develop/qrcode/QrCodeMapper.java
   - develop/qrcode/QrConstant.java

2. 공통 유틸리티
   - common/util/* (기본 유틸리티 클래스들)
   - common/masking/* (마스킹 처리 관련 클래스들)
   - common/log/* (로깅 관련 클래스들)

3. 기타 VO 클래스들
   - */vo/* (값 객체 클래스들)
   - */dto/* (데이터 전송 객체 클래스들)

이러한 파일들은 Spring Boot로의 마이그레이션에서 큰 변경 없이 재사용할 수 있습니다.


# 전체파일목록
./com/devkbil/mtssbj/monitor/MonitorSecurityConfig.java
./com/devkbil/mtssbj/monitor/CacheMonitorController.java
./com/devkbil/mtssbj/develop/moutain/SherpaController.java
./com/devkbil/mtssbj/develop/logview/DevelopLogbackDevCheckController.java
./com/devkbil/mtssbj/develop/logview/DevelopLogbackAppenderService.java
./com/devkbil/mtssbj/develop/logview/DevelopLogbackConfig.java
./com/devkbil/mtssbj/develop/logview/DevelopLogbackAppender.java
./com/devkbil/mtssbj/develop/dbtool/DbtoolVO.java
./com/devkbil/mtssbj/develop/dbtool/DbtoolRestController.java
./com/devkbil/mtssbj/develop/dbtool/DbtoolController.java
./com/devkbil/mtssbj/develop/dbtool/DbtoolService.java
./com/devkbil/mtssbj/develop/naver/map/NaverMapService.java
./com/devkbil/mtssbj/develop/naver/map/MapAddrConvController.java
./com/devkbil/mtssbj/develop/naver/map/NaverApiController.java
./com/devkbil/mtssbj/develop/filesearch/FileSearchController.java
./com/devkbil/mtssbj/develop/filesearch/DependencyController.java
./com/devkbil/mtssbj/develop/qrcode/QRCodeGenerator.java
./com/devkbil/mtssbj/develop/qrcode/QrCodeController.java
./com/devkbil/mtssbj/develop/qrcode/QrCodeMapper.java
./com/devkbil/mtssbj/develop/qrcode/QrCodeRepository.java
./com/devkbil/mtssbj/develop/qrcode/QrCodeServiceImpl.java
./com/devkbil/mtssbj/develop/qrcode/QrConstant.java
./com/devkbil/mtssbj/develop/qrcode/QrCodeService.java
./com/devkbil/mtssbj/develop/ratelimit/RateLimitingAspect.java
./com/devkbil/mtssbj/develop/ratelimit/RateLimit.java
./com/devkbil/mtssbj/develop/ratelimit/RateLimitingService.java
./com/devkbil/mtssbj/develop/ratelimit/APIRateLimiter.java
./com/devkbil/mtssbj/develop/ratelimit/RateLimitingController.java
./com/devkbil/mtssbj/develop/doc/pdf/PdfController.java
./com/devkbil/mtssbj/etc/ClassCodeVO.java
./com/devkbil/mtssbj/etc/PopUserController.java
./com/devkbil/mtssbj/etc/List4User.java
./com/devkbil/mtssbj/etc/AlertMsgController.java
./com/devkbil/mtssbj/etc/EtcService.java
./com/devkbil/mtssbj/admin/organ/DepartmentRepository.java
./com/devkbil/mtssbj/admin/organ/UserService.java
./com/devkbil/mtssbj/admin/organ/DeptVO.java
./com/devkbil/mtssbj/admin/organ/DeptServiceImpl.java
./com/devkbil/mtssbj/admin/organ/DepartmentServiceImpl.java
./com/devkbil/mtssbj/admin/organ/DeptMapper.java
./com/devkbil/mtssbj/admin/organ/DeptService.java
./com/devkbil/mtssbj/admin/organ/DeptController.java
./com/devkbil/mtssbj/admin/organ/DepartmentService.java
./com/devkbil/mtssbj/admin/organ/UserController.java
./com/devkbil/mtssbj/admin/server/SvcService.java
./com/devkbil/mtssbj/admin/server/SrvEtcController.java
./com/devkbil/mtssbj/admin/server/HWController.java
./com/devkbil/mtssbj/admin/server/SvcController.java
./com/devkbil/mtssbj/admin/server/SWController.java
./com/devkbil/mtssbj/admin/server/SrvEtcService.java
./com/devkbil/mtssbj/admin/server/SWService.java
./com/devkbil/mtssbj/admin/server/ConnVO.java
./com/devkbil/mtssbj/admin/server/HWVO.java
./com/devkbil/mtssbj/admin/server/ConnController.java
./com/devkbil/mtssbj/admin/server/HWService.java
./com/devkbil/mtssbj/admin/server/SvcVO.java
./com/devkbil/mtssbj/admin/server/ConnService.java
./com/devkbil/mtssbj/admin/server/SWVO.java
./com/devkbil/mtssbj/admin/server/SrvEtcVO.java
./com/devkbil/mtssbj/admin/sign/SignDocController.java
./com/devkbil/mtssbj/admin/sign/SignDocService.java
./com/devkbil/mtssbj/admin/sign/SignDocTypeVO.java
./com/devkbil/mtssbj/common/listener/ApplicationStartingEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationReadyEventListener.java
./com/devkbil/mtssbj/common/listener/TransactionEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationFailedEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationPreparedEventListener.java
./com/devkbil/mtssbj/common/listener/README.md
./com/devkbil/mtssbj/common/listener/ApplicationContextInitializedEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationContextClosedEventListener.java
./com/devkbil/mtssbj/common/listener/CustomApplicationEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationEnvironmentPreparedEventListener.java
./com/devkbil/mtssbj/common/listener/ApplicationContextRefreshedEventListener.java
./com/devkbil/mtssbj/common/TransactionProfiler.java
./com/devkbil/mtssbj/common/Upload4ckeditor.java
./com/devkbil/mtssbj/common/MakeExcel.java
./com/devkbil/mtssbj/common/LocalDateFormatter.java
./com/devkbil/mtssbj/common/FileVO.java
./com/devkbil/mtssbj/common/LocaleMessage.java
./com/devkbil/mtssbj/common/JwtRequestFilter.java
./com/devkbil/mtssbj/common/code/CodeCacheDAO.java
./com/devkbil/mtssbj/common/code/CodeConstant.java
./com/devkbil/mtssbj/common/code/CodeCacheService.java
./com/devkbil/mtssbj/common/PagingVO.java
./com/devkbil/mtssbj/common/ExtFieldVO.java
./com/devkbil/mtssbj/common/ExtendReloadableResourceBundleMessageSource.java
./com/devkbil/mtssbj/common/ExcelConstant.java
./com/devkbil/mtssbj/common/log/MDCFilter.java
./com/devkbil/mtssbj/common/log/MyStructuredLoggingFormatter.java
./com/devkbil/mtssbj/common/log/MDCKey.java
./com/devkbil/mtssbj/common/log/AccessLogConfig.java
./com/devkbil/mtssbj/common/CountVO.java
./com/devkbil/mtssbj/common/events/CustomApplicationEvent.java
./com/devkbil/mtssbj/common/TreeVO.java
./com/devkbil/mtssbj/common/interceptor/ThemeInterceptor.java
./com/devkbil/mtssbj/common/interceptor/LoginInterceptor.java
./com/devkbil/mtssbj/common/interceptor/ModifiableHttpServletRequest.java
./com/devkbil/mtssbj/common/interceptor/CommonInterceptor.java
./com/devkbil/mtssbj/common/interceptor/TradingTimeInterceptor.java
./com/devkbil/mtssbj/common/interceptor/UrlMappingInterceptor.java
./com/devkbil/mtssbj/common/interceptor/RoleMappingsJson.java
./com/devkbil/mtssbj/common/interceptor/DeviceDetectorInterceptor.java
./com/devkbil/mtssbj/common/interceptor/AdminInterceptor.java
./com/devkbil/mtssbj/common/interceptor/RoleBasedMapping.java
./com/devkbil/mtssbj/common/interceptor/RoleMappingLoader.java
./com/devkbil/mtssbj/common/RepositoryProfiler.java
./com/devkbil/mtssbj/common/masking/ExampleController.java
./com/devkbil/mtssbj/common/masking/MaskingType.java
./com/devkbil/mtssbj/common/masking/MaskingUtil.java
./com/devkbil/mtssbj/common/masking/MaskingPatternLayout.java
./com/devkbil/mtssbj/common/masking/Mask.java
./com/devkbil/mtssbj/common/masking/UserInfoResponseDto.java
./com/devkbil/mtssbj/common/masking/ExampleService.java
./com/devkbil/mtssbj/common/masking/UserInfoRequestDto.java
./com/devkbil/mtssbj/common/masking/UserListRequestDto.java
./com/devkbil/mtssbj/common/masking/MaskingDto.java
./com/devkbil/mtssbj/common/masking/MaskingAspect.java
./com/devkbil/mtssbj/common/masking/ApplyMasking.java
./com/devkbil/mtssbj/common/FileDownload.java
./com/devkbil/mtssbj/crud/CrudController.java
./com/devkbil/mtssbj/crud/CrudVO.java
./com/devkbil/mtssbj/crud/CrudService.java
./com/devkbil/mtssbj/crud/ChkController.java
./com/devkbil/mtssbj/main/SampleController.java
./com/devkbil/mtssbj/main/IndexService.java
./com/devkbil/mtssbj/main/IndexController.java
./com/devkbil/mtssbj/main/SampleService.java
./com/devkbil/mtssbj/sign/SignDocVO.java
./com/devkbil/mtssbj/sign/SignService.java
./com/devkbil/mtssbj/sign/SignController.java
./com/devkbil/mtssbj/sign/SignVO.java
./com/devkbil/common/tree/TreeMaker.java
./com/devkbil/common/util/FileList.java
./com/devkbil/common/util/FileSearchUtil.java
./com/devkbil/common/util/FileClasspath.java
./com/devkbil/common/util/JsonUtil.java
./com/devkbil/common/util/CalUtil.java
./com/devkbil/common/util/JwtUtil.java
./com/devkbil/common/util/FileCore.java
./com/devkbil/common/util/SecurityUtil.java
./com/devkbil/common/util/UtilEtc.java
./com/devkbil/common/util/OtpUtil.java
./com/devkbil/common/util/FileSize.java
./com/devkbil/common/util/FileOperation.java
./com/devkbil/common/util/HttpUtil.java
./com/devkbil/common/util/SocketUtil.java
./com/devkbil/common/util/FileDirectory.java
./com/devkbil/common/util/FileUtil.java
./com/devkbil/common/util/SftpUtil.java
./com/devkbil/common/util/FileIO.java
./com/devkbil/common/util/ValidateUtil.java
./com/devkbil/common/util/SftpService.java
./com/devkbil/common/util/SSLUtils.java
./com/devkbil/common/util/DateUtil.java
./com/devkbil/common/util/JgitUtil.java
./com/devkbil/common/util/DecompressZip.java
./com/devkbil/common/util/NewStringUtil.java
./com/devkbil/common/util/TimeUtil.java
./com/devkbil/common/util/IfUtil.java
./com/devkbil/common/util/FormatUtil.java
./com/devkbil/common/util/HostUtil.java
./com/devkbil/common/util/PdfUtil.java
./com/devkbil/common/util/RequestUtil.java
./com/devkbil/common/util/UUIDUtil.java
./com/devkbil/common/util/FileUpload.java
./com/devkbil/common/util/DateLunar.java
./com/devkbil/common/HtmlCharacterEscapes.java