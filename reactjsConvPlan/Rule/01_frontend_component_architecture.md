# React.js 컴포넌트 공통화 설계 문서

## 1. 개요

이 문서는 기존 프로젝트를 React.js로 전환할 때 컴포넌트 공통화 관점에서의 설계를 정의합니다. 재사용 가능한 컴포넌트 구조를 통해 개발 효율성을 높이고, 일관된 사용자 경험을 제공하는 것을 목표로 합니다.

## 2. 컴포넌트 계층 구조

### 2.1 아토믹 디자인 패턴 적용

컴포넌트를 다음과 같은 계층으로 구분하여 재사용성을 극대화합니다:

1. **아톰(Atoms)**: 버튼, 입력 필드, 아이콘 등 가장 기본적인 UI 요소
2. **분자(Molecules)**: 아톰을 조합한 작은 기능 단위 (예: 검색 필드, 폼 필드)
3. **유기체(Organisms)**: 분자를 조합한 복잡한 UI 섹션 (예: 헤더, 푸터, 폼)
4. **템플릿(Templates)**: 페이지 레이아웃을 정의하는 와이어프레임
5. **페이지(Pages)**: 실제 콘텐츠가 채워진 완성된 화면

### 2.2 디렉토리 구조

```
src/
├── components/
│   ├── atoms/
│   │   ├── Button/
│   │   ├── Input/
│   │   └── ...
│   ├── molecules/
│   │   ├── FormField/
│   │   ├── SearchBar/
│   │   └── ...
│   ├── organisms/
│   │   ├── Header/
│   │   ├── Footer/
│   │   ├── Form/
│   │   ├── Table/
│   │   ├── Modal/
│   │   └── ...
│   └── templates/
│       ├── MainLayout/
│       ├── AdminLayout/
│       └── ...
├── pages/
│   ├── Home/
│   ├── Board/
│   ├── Admin/
│   └── ...
├── hooks/
│   ├── useForm.ts
│   ├── useTable.ts
│   ├── usePagination.ts
│   ├── useModal.ts
│   └── ...
├── contexts/
│   ├── AuthContext.tsx
│   ├── ThemeContext.tsx
│   └── ...
├── api/
│   ├── client.ts
│   ├── boardApi.ts
│   └── ...
├── utils/
│   ├── validation.ts
│   ├── formatting.ts
│   └── ...
└── types/
    ├── common.ts
    ├── form.ts
    ├── table.ts
    └── ...
```

## 3. 컴포넌트 관계도

아래 다이어그램은 주요 컴포넌트 간의 관계와 계층 구조를 보여줍니다:

```
                                  +----------------+
                                  |   App (Root)   |
                                  +-------+--------+
                                          |
                +-------------------------+-------------------------+
                |                         |                         |
    +-----------v-----------+  +----------v-----------+  +---------v----------+
    |    AuthProvider       |  |    ThemeProvider     |  |   QueryProvider    |
    +-----------+-----------+  +----------+-----------+  +---------+----------+
                |                         |                         |
                +-------------------------+-------------------------+
                                          |
                              +-----------v-----------+
                              |     Router/Routes     |
                              +-----------+-----------+
                                          |
            +---------------------+-------+-------+---------------------+
            |                     |               |                     |
+-----------v-----------+ +-------v-------+ +-----v------+ +-----------v-----------+
|    MainLayout         | | AdminLayout   | | AuthLayout | | ErrorBoundary        |
| (Header, Nav, Footer) | |               | |            | |                      |
+-----------+-----------+ +-------+-------+ +-----+------+ +-----------+-----------+
            |                     |               |                     |
            +---------------------+---------------+---------------------+
                                          |
                                          |
                     +-------------------+v+-------------------+
                     |                 Pages                   |
                     |  (Home, Board, Admin, Settings, etc.)  |
                     +-------------------+++------------------+
                                          |
        +------------------+---------------+---------------+------------------+
        |                  |               |               |                  |
+-------v------+  +--------v-------+ +-----v------+ +------v------+  +-------v------+
|    Form      |  |    Table       | |   Modal    | | Pagination  |  | Other UI     |
| Components   |  | Components     | | Components | | Components  |  | Components   |
+-------+------+  +--------+-------+ +-----+------+ +------+------+  +-------+------+
        |                  |               |               |                  |
        +------------------+---------------+---------------+------------------+
                                          |
                                +---------v---------+
                                |  Atoms/Molecules  |
                                | (Button, Input,   |
                                |  Card, etc.)      |
                                +-------------------+
```

## 4. 공통 컴포넌트 설계

### 4.1 UI 컴포넌트

#### 4.1.1 레이아웃 컴포넌트

- **MainLayout**: 기본 레이아웃 (헤더, 푸터, 네비게이션 포함)
- **AdminLayout**: 관리자 페이지 레이아웃
- **AuthLayout**: 인증 페이지 레이아웃

#### 4.1.2 폼 컴포넌트

- **Form**: 폼 상태 관리 및 제출 처리를 담당하는 컨테이너
- **TextField**: 텍스트 입력 필드
- **SelectField**: 선택 필드
- **DateField**: 날짜 선택 필드
- **FileField**: 파일 업로드 필드
- **CheckboxField**: 체크박스 필드
- **RadioField**: 라디오 버튼 필드

#### 4.1.3 테이블 컴포넌트

- **Table**: 데이터 테이블 컴포넌트
- **TableSkeleton**: 테이블 로딩 상태 표시
- **TableFilter**: 테이블 필터링 UI
- **TableExport**: 테이블 데이터 내보내기 기능

#### 4.1.4 모달 컴포넌트

- **Modal**: 기본 모달 컴포넌트
- **ConfirmModal**: 확인 다이얼로그
- **FormModal**: 폼이 포함된 모달

#### 4.1.5 페이징 컴포넌트

- **Pagination**: 페이지네이션 UI
- **InfiniteScroll**: 무한 스크롤 컴포넌트

#### 4.1.6 피드백 컴포넌트

- **Toast**: 토스트 메시지
- **Alert**: 알림 메시지
- **ErrorBoundary**: 에러 처리 컴포넌트
- **LoadingIndicator**: 로딩 표시기

### 4.2 커스텀 훅

- **useForm**: 폼 상태 관리
- **useFormSubmit**: 폼 제출 처리
- **useTable**: 테이블 상태 관리
- **usePagination**: 페이지네이션 상태 관리
- **useModal**: 모달 상태 관리
- **useAuth**: 인증 상태 관리
- **useToast**: 토스트 메시지 표시
- **useQueryWithRetry**: 재시도 로직이 포함된 쿼리 훅

### 4.3 컨텍스트 제공자

- **AuthProvider**: 인증 상태 관리
- **ThemeProvider**: 테마 관리
- **ToastProvider**: 토스트 메시지 관리
- **QueryClientProvider**: React Query 클라이언트 제공

## 5. 컴포넌트 재사용 패턴

### 5.1 합성 패턴 (Composition Pattern)

컴포넌트를 작은 단위로 분리하고 `children` prop을 활용하여 유연하게 조합합니다.

```tsx
// 예시: Card 컴포넌트
<Card>
  <Card.Header>제목</Card.Header>
  <Card.Body>내용</Card.Body>
  <Card.Footer>푸터</Card.Footer>
</Card>
```

### 5.2 고차 컴포넌트 (HOC)

공통 기능을 여러 컴포넌트에 적용할 때 사용합니다.

```tsx
// 예시: withAuth HOC
const ProtectedPage = withAuth(UserProfilePage);
```

### 5.3 커스텀 훅 (Custom Hooks)

상태 로직을 재사용 가능한 훅으로 분리합니다.

```tsx
// 예시: usePagination 훅 사용
const { page, pageSize, handlePageChange } = usePagination({
  defaultPage: 1,
  defaultPageSize: 10,
  total: 100
});
```

### 5.4 렌더 프롭 (Render Props)

컴포넌트의 렌더링 로직을 외부에서 주입받습니다.

```tsx
// 예시: Form 컴포넌트의 렌더 프롭 패턴
<Form onSubmit={handleSubmit}>
  {({ isSubmitting, isValid }) => (
    <>
      <TextField name="username" label="사용자명" />
      <Button type="submit" disabled={!isValid || isSubmitting}>
        저장
      </Button>
    </>
  )}
</Form>
```

## 6. 상태 관리 전략

### 6.1 로컬 상태

- **useState**: 단일 컴포넌트 내 간단한 상태
- **useReducer**: 복잡한 로컬 상태 로직

### 6.2 전역 상태 관리

#### 6.2.1 Zustand

클라이언트 상태 관리를 위한 주요 도구로 Zustand를 사용합니다:

```typescript
// stores/auth.ts
interface AuthStore {
  user: User | null;
  isAuthenticated: boolean;
  login: (credentials: Credentials) => Promise<void>;
  logout: () => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
  user: null,
  isAuthenticated: false,
  login: async (credentials) => {
    const user = await loginApi(credentials);
    set({ user, isAuthenticated: true });
  },
  logout: () => set({ user: null, isAuthenticated: false }),
}));

// stores/theme.ts
interface ThemeStore {
  mode: 'light' | 'dark';
  toggleTheme: () => void;
}

export const useThemeStore = create<ThemeStore>((set) => ({
  mode: 'light',
  toggleTheme: () => set((state) => ({ 
    mode: state.mode === 'light' ? 'dark' : 'light' 
  })),
}));
```

#### 6.2.2 React Query와 Zustand 통합

서버 상태와 클라이언트 상태를 효율적으로 관리하기 위해 React Query와 Zustand를 조합하여 사용합니다:

```typescript
// stores/board.ts
interface BoardStore {
  selectedIds: string[];
  filters: BoardFilter;
  setSelectedIds: (ids: string[]) => void;
  setFilters: (filters: BoardFilter) => void;
}

export const useBoardStore = create<BoardStore>((set) => ({
  selectedIds: [],
  filters: { status: 'all', category: 'all' },
  setSelectedIds: (ids) => set({ selectedIds: ids }),
  setFilters: (filters) => set({ filters }),
}));

// hooks/useBoards.ts
export const useBoards = () => {
  const filters = useBoardStore((state) => state.filters);
  
  return useQuery({
    queryKey: ['boards', filters],
    queryFn: () => fetchBoards(filters),
    select: (data) => ({
      items: data.items,
      total: data.total,
    }),
  });
};

// hooks/useBoardMutation.ts
export const useBoardMutation = () => {
  const queryClient = useQueryClient();
  const selectedIds = useBoardStore((state) => state.selectedIds);
  
  return useMutation({
    mutationFn: (data: BoardData) => createBoard(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['boards'] });
      useBoardStore.getState().setSelectedIds([]);
    },
  });
};

// components/BoardList.tsx
export const BoardList = () => {
  const { data, isLoading } = useBoards();
  const { selectedIds, setSelectedIds } = useBoardStore();
  const { mutate: createBoard } = useBoardMutation();

  // 컴포넌트 구현
};
```

#### 6.2.3 상태 관리 패턴

1. **서버 상태 관리 (React Query)**
   - 데이터 페칭 및 캐싱
   - 서버 데이터 동기화
   - 낙관적 업데이트
   - 에러 처리 및 재시도
   - 페이지네이션 및 무한 스크롤

2. **클라이언트 상태 관리 (Zustand)**
   - UI 상태 (모달, 드로어, 선택된 항목 등)
   - 필터 및 정렬 설정
   - 폼 임시 데이터
   - 테마 설정
   - 인증 상태

3. **상태 통합 패턴**
```typescript
// hooks/useIntegratedBoardManagement.ts
export const useIntegratedBoardManagement = () => {
  // Zustand 로컬 상태
  const { filters, selectedIds, setFilters } = useBoardStore();
  
  // React Query 서버 상태
  const { 
    data,
    isLoading,
    error,
    refetch 
  } = useQuery({
    queryKey: ['boards', filters],
    queryFn: () => fetchBoards(filters),
  });
  
  // 뮤테이션
  const { mutate, isLoading: isMutating } = useMutation({
    mutationFn: updateBoards,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['boards'] });
    },
  });
  
  return {
    // 데이터 상태
    boards: data?.items ?? [],
    totalCount: data?.total ?? 0,
    
    // UI 상태
    filters,
    selectedIds,
    
    // 로딩 상태
    isLoading,
    isMutating,
    
    // 에러 상태
    error,
    
    // 액션
    setFilters,
    refetch,
    updateBoards: mutate,
  };
};
```

### 6.3 폼 상태

- **React Hook Form**: 폼 상태 및 유효성 검사
- **Zod**: 타입 안전한 스키마 유효성 검사

## 7. 스타일링 전략

### 7.1 Material-UI 활용

- **Theme Provider**: 일관된 디자인 시스템 적용
- **스타일 오버라이딩**: 컴포넌트별 스타일 커스터마이징
- **반응형 디자인**: 다양한 화면 크기 지원

### 7.2 스타일 구성

- **공통 테마**: 색상, 타이포그래피, 간격 등 공통 스타일 정의
- **컴포넌트별 스타일**: 컴포넌트 단위로 스타일 캡슐화

## 8. 접근성 및 성능 최적화

### 8.1 접근성 (A11y)

- ARIA 속성 적용
- 키보드 네비게이션 지원
- 색상 대비 고려
- 스크린 리더 호환성 확보

### 8.2 성능 최적화

- **React.memo**: 불필요한 리렌더링 방지
- **useMemo/useCallback**: 계산 비용이 큰 연산 최적화
- **코드 스플리팅**: React.lazy와 Suspense를 활용한 지연 로딩
- **이미지 최적화**: 적절한 크기와 포맷 사용

## 9. 테스트 전략

### 9.1 단위 테스트

- **Jest**: 테스트 러너
- **React Testing Library**: 컴포넌트 테스트
- **MSW**: API 모킹

### 9.2 테스트 범위

- 공통 컴포넌트 100% 테스트 커버리지 목표
- 주요 비즈니스 로직 테스트
- 사용자 인터랙션 테스트

## 10. 구현 가이드라인

### 10.1 컴포넌트 개발 원칙

1. **단일 책임 원칙**: 각 컴포넌트는 하나의 책임만 가짐
2. **인터페이스 우선**: 명확한 props 인터페이스 정의
3. **상태 최소화**: 필요한 상태만 유지
4. **접근성 고려**: 모든 컴포넌트는 접근성 표준 준수
5. **문서화**: 컴포넌트 사용법 문서화 (Storybook 활용)

### 10.2 코드 컨벤션

- TypeScript 사용으로 타입 안전성 확보
- ESLint, Prettier를 통한 코드 스타일 통일
- 명명 규칙 준수 (PascalCase for components, camelCase for functions)
- 주석 및 문서화 표준 준수

## 11. 마이그레이션 전략

### 11.1 점진적 마이그레이션

1. 공통 컴포넌트 라이브러리 먼저 개발
2. 새로운 기능은 React로 개발
3. 기존 페이지를 우선순위에 따라 점진적으로 마이그레이션
4. 레거시 코드와의 통합 지점 관리

### 11.2 우선순위

1. 사용자 경험에 중요한 페이지
2. 자주 사용되는 페이지
3. 유지보수가 어려운 레거시 페이지
4. 나머지 페이지

## 12. 결론

이 문서에서 정의한 React.js 컴포넌트 공통화 설계는 다음과 같은 이점을 제공합니다:

1. **개발 효율성 향상**: 재사용 가능한 컴포넌트를 통해 중복 코드를 줄이고 개발 속도를 높입니다.
2. **일관된 사용자 경험**: 공통 컴포넌트를 사용함으로써 전체 애플리케이션에서 일관된 UI/UX를 제공합니다.
3. **유지보수성 개선**: 컴포넌트 단위의 개발과 테스트로 버그 수정과 기능 추가가 용이해집니다.
4. **확장성 확보**: 아토믹 디자인 패턴과 명확한 계층 구조를 통해 애플리케이션의 확장이 용이합니다.
5. **품질 향상**: 재사용 컴포넌트에 대한 철저한 테스트로 전반적인 코드 품질이 향상됩니다.

이 설계를 바탕으로 React.js로의 전환을 진행하면, 현대적이고 유지보수가 용이한 프론트엔드 아키텍처를 구축할 수 있을 것입니다. 점진적인 마이그레이션 전략을 통해 리스크를 최소화하면서 기존 시스템을 안정적으로 개선해 나갈 수 있습니다.