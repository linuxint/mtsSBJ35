# JSP to React Component Migration Plan

## 1. 현재 JSP 파일 목록

아래 파일들을 기능별로 그룹화하여 React 컴포넌트로 변환해야 합니다:

### 1.1 레이아웃 및 공통 컴포넌트
```
./index.jsp
./WEB-INF/jsp/inc/header.jsp
./WEB-INF/jsp/inc/footer.jsp
./WEB-INF/jsp/common/pagingforSubmit.jsp
./WEB-INF/jsp/common/message.jsp
./WEB-INF/jsp/common/navigation.jsp
./WEB-INF/jsp/common/noAuth.jsp
./WEB-INF/jsp/common/pagingforAjax.jsp
./WEB-INF/jsp/common/blank.jsp
./WEB-INF/jsp/admin/inc/ad_header.jsp
./WEB-INF/jsp/admin/inc/ad_footer.jsp
```

### 1.2 스케줄 관리
```
./WEB-INF/jsp/schedule/SchForm.jsp
./WEB-INF/jsp/schedule/SchRead.jsp
./WEB-INF/jsp/schedule/SchList.jsp
./WEB-INF/jsp/schedule/SchRead4Ajax.jsp
```

### 1.3 게시판
```
./WEB-INF/jsp/board/BoardReadAjax4Reply.jsp
./WEB-INF/jsp/board/BoardListAllSub.jsp
./WEB-INF/jsp/board/BoardGroupFail.jsp
./WEB-INF/jsp/board/BoardList.jsp
./WEB-INF/jsp/board/BoardRead.jsp
./WEB-INF/jsp/board/BoardListSub.jsp
./WEB-INF/jsp/board/BoardListAll.jsp
./WEB-INF/jsp/board/BoardForm.jsp
./WEB-INF/jsp/admin/board/BoardGroupList.jsp
```

### 1.4 메일
```
./WEB-INF/jsp/mail/MailInfoForm.jsp
./WEB-INF/jsp/mail/ReceiveMailRead.jsp
./WEB-INF/jsp/mail/SendMails.jsp
./WEB-INF/jsp/mail/MailInfoGuide.jsp
./WEB-INF/jsp/mail/ReceiveMails.jsp
./WEB-INF/jsp/mail/MailForm.jsp
./WEB-INF/jsp/mail/MailInfoList.jsp
./WEB-INF/jsp/mail/SendMailRead.jsp
```

### 1.5 사용자 관리
```
./WEB-INF/jsp/member/memberLogin.jsp
./WEB-INF/jsp/member/searchMember.jsp
./WEB-INF/jsp/member/memberForm.jsp
./WEB-INF/jsp/admin/organ/UserList.jsp
./WEB-INF/jsp/admin/organ/Dept.jsp
./WEB-INF/jsp/admin/organ/User.jsp
```

### 1.6 팝업 및 기타
```
./WEB-INF/jsp/etc/popupUser.jsp
./WEB-INF/jsp/etc/popupUsers.jsp
./WEB-INF/jsp/etc/fileAllIndex.jsp
./WEB-INF/jsp/etc/list4User.jsp
./WEB-INF/jsp/etc/alertList4Ajax.jsp
./WEB-INF/jsp/etc/popupDept.jsp
./WEB-INF/jsp/etc/popupUsersByDept.jsp
./WEB-INF/jsp/etc/popupUsers4SignPath.jsp
./WEB-INF/jsp/etc/popupUsers4Users.jsp
./WEB-INF/jsp/etc/alertList.jsp
```

### 1.7 관리자 기능
```
./WEB-INF/jsp/admin/code/CodeForm.jsp
./WEB-INF/jsp/admin/code/CodeRead.jsp
./WEB-INF/jsp/admin/code/CodeList.jsp
./WEB-INF/jsp/admin/menu/MenuList.jsp
./WEB-INF/jsp/admin/sign/SignDocTypeForm.jsp
./WEB-INF/jsp/admin/sign/SignDocTypeList.jsp
```

### 1.8 검색
```
./WEB-INF/jsp/search/search.jsp
```

### 1.9 프로젝트 관리
```
./WEB-INF/jsp/project/ProjectForm.jsp
./WEB-INF/jsp/project/TaskCalendar.jsp
./WEB-INF/jsp/project/ProjectList4Ajax.jsp
./WEB-INF/jsp/project/TaskCalenPopup.jsp
./WEB-INF/jsp/project/TaskMineForm.jsp
./WEB-INF/jsp/project/TaskWorker.jsp
./WEB-INF/jsp/project/ProjectList.jsp
./WEB-INF/jsp/project/TaskMine.jsp
./WEB-INF/jsp/project/Task.jsp
```

### 1.10 CRUD 샘플
```
./WEB-INF/jsp/crud/ChkList.jsp
./WEB-INF/jsp/crud/CrudForm.jsp
./WEB-INF/jsp/crud/CrudRead.jsp
./WEB-INF/jsp/crud/CrudList.jsp
```

### 1.11 메인 화면
```
./WEB-INF/jsp/main/sample4.jsp
./WEB-INF/jsp/main/indexCalen.jsp
./WEB-INF/jsp/main/sample1.jsp
./WEB-INF/jsp/main/sample.jsp
./WEB-INF/jsp/main/sample3.jsp
./WEB-INF/jsp/main/sample2.jsp
./WEB-INF/jsp/main/index.jsp
```

### 1.12 에러 페이지
```
./WEB-INF/jsp/error/error.jsp
./WEB-INF/jsp/error/404.jsp
./WEB-INF/jsp/error/500.jsp
```

### 1.13 결재 시스템
```
./WEB-INF/jsp/sign/SignDocList.jsp
./WEB-INF/jsp/sign/SignDocListTobe.jsp
./WEB-INF/jsp/sign/SignDocForm.jsp
./WEB-INF/jsp/sign/SignDocRead.jsp
./WEB-INF/jsp/sign/SignDocTypeList.jsp
```

### 1.14 기타 정적 파일 및 템플릿
```
./WEB-INF/jsp/dbtool/TableLayoutList.jsp
src/main/resources/static/js/mts.js
src/main/resources/templates/thymeleaf/dependencySearch.html
src/main/resources/templates/thymeleaf/dvLogView.html
src/main/resources/templates/thymeleaf/index.html
src/main/resources/templates/thymeleaf/jqtemplate.html
src/main/resources/templates/thymeleaf/mapaddr.html
src/main/resources/templates/thymeleaf/qrcode.html
src/main/resources/templates/thymeleaf/sherpa.html
src/main/resources/templates/thymeleaf/tabTemplate.html
src/main/resources/templates/thymeleaf/tableData.html
src/main/resources/templates/thymeleaf/tableLayout.html
src/main/resources/templates/thymeleaf/thymeleaftest.html
```

## 2. React 컴포넌트 변환 계획

### 2.1 컴포넌트 계층 구조

```
/src
  /components
    /layout
      Header.tsx
      Footer.tsx
      Navigation.tsx
      AdminHeader.tsx
      AdminFooter.tsx
      MainLayout.tsx
      AdminLayout.tsx
    /common
      Pagination.tsx
      Message.tsx
      NotFound.tsx
      NoAuthorization.tsx
      Loading.tsx
    /schedule
      ScheduleList.tsx
      ScheduleForm.tsx
      ScheduleDetail.tsx
    /board
      BoardList.tsx
      BoardForm.tsx
      BoardDetail.tsx
      BoardReply.tsx
      BoardGroupList.tsx
    /mail
      MailList.tsx
      MailForm.tsx
      MailDetail.tsx
      MailInfo.tsx
      InboxPage.tsx
      SentPage.tsx
    /member
      Login.tsx
      MemberForm.tsx
      MemberSearch.tsx
      UserList.tsx
      DepartmentTree.tsx
      UserDetail.tsx
    /popup
      UserPopup.tsx
      UsersPopup.tsx
      DepartmentPopup.tsx
      FileSelectPopup.tsx
      AlertList.tsx
    /admin
      CodeList.tsx
      CodeForm.tsx
      CodeDetail.tsx
      MenuList.tsx
      SignDocTypeForm.tsx
      SignDocTypeList.tsx
    /search
      GlobalSearch.tsx
    /project
      ProjectList.tsx
      ProjectForm.tsx
      TaskCalendar.tsx
      TaskDetail.tsx
      TaskForm.tsx
      TaskList.tsx
    /crud
      CrudList.tsx
      CrudForm.tsx
      CrudDetail.tsx
    /main
      Dashboard.tsx
      Calendar.tsx
      Statistics.tsx
    /error
      ErrorPage.tsx
      NotFoundPage.tsx
      ServerErrorPage.tsx
    /sign
      SignDocList.tsx
      SignDocForm.tsx
      SignDocDetail.tsx
      SignDocTypeList.tsx
  /pages
    /schedule
      ScheduleListPage.tsx
      ScheduleFormPage.tsx
      ScheduleDetailPage.tsx
    /board
      BoardListPage.tsx
      BoardFormPage.tsx
      BoardDetailPage.tsx
    /mail
      MailListPage.tsx
      MailFormPage.tsx
      MailDetailPage.tsx
      MailInfoPage.tsx
    /member
      LoginPage.tsx
      MemberFormPage.tsx
      MemberSearchPage.tsx
    /admin
      UserListPage.tsx
      DepartmentPage.tsx
      CodeListPage.tsx
      CodeFormPage.tsx
      CodeDetailPage.tsx
      MenuListPage.tsx
      SignDocTypeFormPage.tsx
      SignDocTypeListPage.tsx
    /search
      SearchPage.tsx
    /project
      ProjectListPage.tsx
      ProjectFormPage.tsx
      TaskCalendarPage.tsx
      TaskDetailPage.tsx
      TaskFormPage.tsx
      TaskListPage.tsx
    /crud
      CrudListPage.tsx
      CrudFormPage.tsx
      CrudDetailPage.tsx
    /main
      DashboardPage.tsx
    /sign
      SignDocListPage.tsx
      SignDocFormPage.tsx
      SignDocDetailPage.tsx
      SignDocTypeListPage.tsx
```

## 3. 우선순위 및 마이그레이션 순서

### 3.1 1단계: 핵심 인프라 및 공통 컴포넌트 (2주)
- 레이아웃 컴포넌트 (Header, Footer, Navigation)
- 공통 UI 컴포넌트 (Pagination, Message, Loading)
- 인증 관련 컴포넌트 (Login)
- 에러 페이지

### 3.2 2단계: 핵심 비즈니스 모듈 (4주)
- 게시판 관련 컴포넌트
- 일정 관리 컴포넌트
- 사용자 관리 컴포넌트

### 3.3 3단계: 보조 비즈니스 모듈 (4주)
- 메일 시스템 컴포넌트
- 결재 시스템 컴포넌트
- 프로젝트 관리 컴포넌트

### 3.4 4단계: 관리자 및 기타 기능 (2주)
- 관리자 기능 컴포넌트
- 검색 기능
- 팝업 및 기타 유틸리티 컴포넌트

## 4. 컴포넌트별 상세 변환 계획

### 4.1 상태 관리 전략
- Zustand 스토어 구조 설계
- 서버 상태와 클라이언트 상태 분리
- React Query를 사용한 API 통신

### 4.2 폼 처리 전략
- React Hook Form을 활용한 폼 상태 관리
- Zod를 활용한 폼 검증
- 비동기 제출 처리

### 4.3 라우팅 전략
- React Router를 활용한 라우팅 구성
- 권한 기반 라우트 접근 제어
- 중첩 라우팅 활용

### 4.4 스타일링 전략
- Styled-components 또는 TailwindCSS 활용
- 일관된 디자인 시스템 구축
- 테마 및 다크 모드 지원

## 5. API 통신 전략

### 5.1 API 클라이언트 구성
- Axios 인스턴스 설정
- 인터셉터를 통한 JWT 토큰 관리
- 에러 핸들링

### 5.2 데이터 페칭 및 캐싱
- React Query를 활용한 효율적인 데이터 페칭
- 캐시 무효화 전략
- 페이지네이션 및 무한 스크롤 처리

## 6. 테스트 전략

### 6.1 컴포넌트 테스트
- Jest와 React Testing Library를 활용한 단위 테스트
- Storybook을 활용한 컴포넌트 문서화 및 테스트

### 6.2 통합 테스트
- MSW(Mock Service Worker)를 활용한 API 모킹
- E2E 테스트를 위한 Cypress 또는 Playwright 활용

## 7. 진행 상황 추적

### 7.1 컴포넌트 완료 상태
- [ ] 레이아웃 컴포넌트
- [ ] 공통 UI 컴포넌트 
- [ ] 인증 관련 컴포넌트
- [ ] 게시판 컴포넌트
- [ ] 일정 관리 컴포넌트
- [ ] 메일 시스템 컴포넌트
- [ ] 사용자 관리 컴포넌트
- [ ] 결재 시스템 컴포넌트
- [ ] 프로젝트 관리 컴포넌트
- [ ] 관리자 기능 컴포넌트
- [ ] 검색 기능 컴포넌트
- [ ] 팝업 및 기타 유틸리티 컴포넌트
- [ ] 에러 페이지 컴포넌트
