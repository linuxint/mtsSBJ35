# 프론트엔드 상세 설계

## 1. 프로젝트 초기 설정
- Node.js 및 npm 최신 버전 설치
- Create React App 또는 Vite로 새 프로젝트 생성
- TypeScript 설정
- 필수 라이브러리 설치
  • zustand (상태관리)
  • react-router-dom (라우팅)
  • axios (API 통신)
  • styled-components 또는 tailwindcss (스타일링)
  • react-query (서버 상태관리)
  • date-fns (날짜 처리)
  • zod (데이터 검증)

## 2. 프로젝트 구조
```
/src
  /assets        # 이미지, 폰트 등 정적 파일
  /components    # 재사용 가능한 컴포넌트
    /common      # 공통 컴포넌트
    /layout      # 레이아웃 관련 컴포넌트
  /hooks         # 커스텀 훅
  /pages         # 페이지 컴포넌트
  /stores        # Zustand 스토어
  /services      # API 서비스
  /types         # TypeScript 타입 정의
  /utils         # 유틸리티 함수
  /constants     # 상수 정의
```

## 3. 공통 컴포넌트 상세 설계
### 3.1 버튼 컴포넌트
/components/common/buttons
- Button.tsx              # 기본 버튼
- IconButton.tsx         # 아이콘 버튼
- ToggleButton.tsx       # 토글 버튼
- ButtonGroup.tsx        # 버튼 그룹
- FloatingButton.tsx     # 플로팅 버튼

### 3.2 입력 컴포넌트
/components/common/inputs
- Input.tsx              # 기본 입력 필드
- SearchInput.tsx        # 검색 입력
- TextArea.tsx           # 여러 줄 텍스트
- Select.tsx             # 선택 컴포넌트
- Checkbox.tsx           # 체크박스
- Radio.tsx              # 라디오 버튼
- Switch.tsx             # 스위치 토글
- DatePicker.tsx         # 날짜 선택기
- TimePicker.tsx         # 시간 선택기
- FileUpload.tsx         # 파일 업로드
- Editor.tsx             # 리치 텍스트 에디터

### 3.3 피드백 컴포넌트
/components/common/feedback
- Alert.tsx              # 경고/알림
- Toast.tsx              # 토스트 메시지
- Progress.tsx           # 진행 상태바
- Spinner.tsx            # 로딩 스피너
- Skeleton.tsx           # 스켈레톤 로딩
- Badge.tsx              # 뱃지
- ErrorBoundary.tsx      # 에러 경계

### 3.4 레이아웃 컴포넌트
/components/common/layout
- Grid.tsx               # 그리드 시스템
- Flex.tsx              # Flexbox 컨테이너
- Box.tsx               # 기본 컨테이너
- Card.tsx              # 카드 컴포넌트
- Divider.tsx           # 구분선
- Spacer.tsx            # 여백 컴포넌트
- Container.tsx         # 컨테이너 레이아웃

### 3.5 네비게이션 컴포넌트
/components/common/navigation
- Breadcrumb.tsx        # 경로 표시
- Pagination.tsx        # 페이지네이션
- Tabs.tsx             # 탭 네비게이션
- Dropdown.tsx          # 드롭다운 메뉴
- Menu.tsx             # 메뉴 컴포넌트
- Sidebar.tsx          # 사이드바
- Header.tsx           # 헤더
- Footer.tsx           # 푸터

### 3.6 데이터 표시 컴포넌트
/components/common/data
- Table.tsx            # 테이블
- List.tsx             # 리스트
- Tree.tsx             # 트리 구조
- Timeline.tsx         # 타임라인
- Tag.tsx              # 태그
- Avatar.tsx           # 아바타/프로필
- Chip.tsx             # 칩

### 3.7 오버레이 컴포넌트
/components/common/overlay
- Modal.tsx            # 모달
- Drawer.tsx           # 드로어
- Popover.tsx          # 팝오버
- Tooltip.tsx          # 툴팁
- Dialog.tsx           # 다이얼로그
- ContextMenu.tsx      # 컨텍스트 메뉴

### 3.8 타이포그래피 컴포넌트
/components/common/typography
- Text.tsx             # 텍스트
- Heading.tsx          # 제목
- Paragraph.tsx        # 문단
- Link.tsx             # 링크

### 3.9 유틸리티 컴포넌트
/components/common/utils
- Portal.tsx           # 포털
- Collapse.tsx         # 접었다 펴기
- ScrollArea.tsx       # 스크롤 영역
- InfiniteScroll.tsx   # 무한 스크롤
- LazyLoad.tsx         # 지연 로딩

### 3.10 차트 컴포넌트
/components/common/chart
- LineChart.tsx        # 선 그래프
- BarChart.tsx         # 막대 그래프
- PieChart.tsx         # 파이 차트
- AreaChart.tsx        # 영역 차트

### 3.11 폼 컴포넌트
/components/common/form
- Form.tsx             # 폼 컨테이너
- FormField.tsx        # 폼 필드
- FormLabel.tsx        # 폼 레이블
- FormError.tsx        # 폼 에러
- FormHelperText.tsx   # 폼 도움말

## 4. 공통 Props 인터페이스
```typescript
interface CommonProps {
  className?: string;
  style?: React.CSSProperties;
  id?: string;
  testId?: string;
  disabled?: boolean;
  loading?: boolean;
  size?: 'sm' | 'md' | 'lg';
  variant?: 'solid' | 'outline' | 'ghost';
  colorScheme?: string;
}
```

## 5. 스타일 시스템
```typescript
const styleSystem = {
  colors: {
    primary: {...},
    secondary: {...},
    error: {...}
  },
  spacing: {...},
  breakpoints: {...},
  typography: {...}
}
```

## 6. 기능별 컴포넌트 구조
### 6.1 관리자 기능
/components/admin
```
/code
  CodeList.tsx         # 코드 관리
  CodeForm.tsx         # 코드 등록/수정
/menu
  MenuList.tsx         # 메뉴 관리
  MenuForm.tsx         # 메뉴 등록/수정
/board
  BoardConfigList.tsx  # 게시판 설정
  BoardConfigForm.tsx  # 게시판 설정 등록/수정
```

### 6.2 인증 기능
/components/auth
```
Login.tsx             # 로그인
Register.tsx          # 회원가입
Profile.tsx           # 프로필
PasswordChange.tsx    # 비밀번호 변경
```

### 6.3 게시판 기능
/components/board
```
BoardList.tsx         # 게시글 목록
BoardWrite.tsx        # 게시글 작성
BoardDetail.tsx       # 게시글 상세
BoardComment.tsx      # 댓글
BoardSearch.tsx       # 게시글 검색
```

### 6.4 일정 관리 기능
/components/schedule
```
Calendar.tsx          # 캘린더 뷰
ScheduleForm.tsx      # 일정 등록/수정
ScheduleList.tsx      # 일정 목록
ScheduleDetail.tsx    # 일정 상세
RepeatSchedule.tsx    # 반복 일정 설정
```

### 6.5 메일 기능
/components/mail
```
MailList.tsx          # 메일 목록
MailWrite.tsx         # 메일 작성
MailDetail.tsx        # 메일 상세
MailConfig.tsx        # 메일 설정
ImapConfig.tsx        # IMAP 설정
```

### 6.6 프로젝트 관리 기능
/components/project
```
ProjectList.tsx       # 프로젝트 목록
ProjectForm.tsx       # 프로젝트 등록/수정
ProjectDetail.tsx     # 프로젝트 상세
TaskList.tsx          # 작업 목록
TaskForm.tsx          # 작업 등록/수정
```

### 6.7 모니터링 기능
/components/monitor
```
SystemStatus.tsx      # 시스템 상태
UserActivity.tsx      # 사용자 활동
ErrorLogs.tsx         # 에러 로그
```

### 6.8 검색 기능
/components/search
```
GlobalSearch.tsx      # 통합 검색
SearchResult.tsx      # 검색 결과
```

### 6.9 상태 관리
/components/health
```
HealthCheck.tsx       # 건강 체크
HealthReport.tsx      # 건강 리포트
```

## 7. 상태 관리 설계
### 7.1 인증 상태
/stores/auth
```
useAuthStore.ts       # 인증/권한 관리
useProfileStore.ts    # 프로필 정보 관리
```

### 7.2 관리자 상태
/stores/admin
```
useCodeStore.ts       # 코드 관리
useMenuStore.ts       # 메뉴 관리
useBoardConfigStore.ts # 게시판 설정
```

### 7.3 게시판 상태
/stores/board
```
useBoardStore.ts      # 게시판 상태
useCommentStore.ts    # 댓글 상태
```

### 7.4 일정 상태
/stores/schedule
```
useScheduleStore.ts   # 일정 관리
useCalendarStore.ts   # 캘린더 상태
```

### 7.5 메일 상태
/stores/mail
```
useMailStore.ts       # 메일 상태
useImapStore.ts       # IMAP 설정
```

### 7.6 프로젝트 상태
/stores/project
```
useProjectStore.ts    # 프로젝트 관리
useTaskStore.ts       # 작업 관리
```

### 7.7 모니터링 상태
/stores/monitor
```
useSystemStore.ts     # 시스템 모니터링
useActivityStore.ts   # 활동 로그
```

### 7.8 UI 상태
/stores/ui
```
useModalStore.ts      # 모달 상태
useToastStore.ts      # 알림 상태
useLoadingStore.ts    # 로딩 상태
```

## 8. API 서비스 구조
/services/api
### 8.1 관리자 API
```
/admin
  codeApi.ts         # 코드 관리 API
  menuApi.ts         # 메뉴 관리 API
  boardConfigApi.ts  # 게시판 설정 API
```

### 8.2 인증 API
```
/auth
  authApi.ts         # 인증 API
  profileApi.ts      # 프로필 API
```

### 8.3 게시판 API
```
/board
  boardApi.ts        # 게시판 API
  commentApi.ts      # 댓글 API
```

### 8.4 일정 API
```
/schedule
  scheduleApi.ts     # 일정 API
  calendarApi.ts     # 캘린더 API
```

### 8.5 메일 API
```
/mail
  mailApi.ts         # 메일 API
  imapApi.ts        # IMAP API
```

### 8.6 프로젝트 API
```
/project
  projectApi.ts      # 프로젝트 API
  taskApi.ts        # 작업 API
```

### 8.7 모니터링 API
```
/monitor
  systemApi.ts      # 시스템 모니터링 API
  activityApi.ts    # 활동 로그 API
```

### 8.8 검색 API
```
/search
  searchApi.ts      # 통합 검색 API
``` 