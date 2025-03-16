# React.js 컴포넌트 공통화 설계 문서

## 1. 개요

이 문서는 기존 JSP 기반 프로젝트를 React.js로 전환할 때 컴포넌트 공통화 관점에서의 설계를 정의합니다. 재사용 가능한 컴포넌트 구조를 통해 개발 효율성을 높이고, 일관된 사용자 경험을 제공하는 것을 목표로 합니다. 

### 1.1 JSP에서 React.js로의 전환 원칙

JSP에서 React.js로 전환할 때 다음 원칙을 따릅니다:

1. **점진적 전환**: 모든 페이지를 한 번에 전환하지 않고, 우선순위에 따라 점진적으로 전환합니다.
2. **기능 동등성**: 기존 JSP 페이지의 모든 기능이 React.js로 구현되어야 합니다.
3. **사용자 경험 향상**: 단순한 기능 이전을 넘어, React.js의 장점을 활용하여 사용자 경험을 개선합니다.
4. **코드 품질 향상**: 테스트 가능하고 유지보수가 용이한 코드 구조를 지향합니다.
5. **재사용성 극대화**: 공통 컴포넌트를 통해 코드 중복을 최소화합니다.

### 1.2 JSP에서 React.js로의 완전 전환 전략

마이그레이션 기간 동안 JSP에서 React.js로 완전히 전환하기 위한 전략입니다:

#### 1.2.1 단계적 전환 접근 방식

1. **마이크로 프론트엔드 패턴**: 
   - 우선순위에 따라 페이지를 React.js로 단계적으로 전환합니다.
   - 공통 헤더/푸터를 통해 일관된 사용자 경험을 유지합니다.

2. **독립적인 React 애플리케이션 구축**:
   ```typescript
   // index.tsx
   import React from 'react';
   import ReactDOM from 'react-dom/client';
   import App from './App';

   // React 애플리케이션 초기화
   const root = ReactDOM.createRoot(document.getElementById('root'));
   root.render(
     <React.StrictMode>
       <App initialData={window.__INITIAL_DATA__} />
     </React.StrictMode>
   );
   ```

3. **상태 관리**:
   - 클라이언트 상태 관리를 위한 Zustand 활용
   - 서버 측에서 일관된 데이터 제공을 위한 REST API 구축

#### 1.2.2 라우팅 전략

1. **React Router 기반 라우팅**:
   - 모든 페이지에 React Router를 사용하여 클라이언트 사이드 라우팅 구현
   - 히스토리 API를 활용한 부드러운 페이지 전환 제공

2. **라우팅 구성**:
   ```typescript
   // App.tsx
   import { BrowserRouter, Routes, Route } from 'react-router-dom';
   import { lazy, Suspense } from 'react';
   import LoadingSpinner from './components/atoms/LoadingSpinner';

   // 코드 스플리팅을 위한 지연 로딩
   const BoardListPage = lazy(() => import('./pages/Board/BoardListPage'));
   const UserProfilePage = lazy(() => import('./pages/User/UserProfilePage'));
   const AdminSettingsPage = lazy(() => import('./pages/Admin/AdminSettingsPage'));

   function App() {
     return (
       <BrowserRouter>
         <Suspense fallback={<LoadingSpinner />}>
           <Routes>
             <Route path="/" element={<HomePage />} />
             <Route path="/board/list" element={<BoardListPage />} />
             <Route path="/user/profile" element={<UserProfilePage />} />
             <Route path="/admin/settings" element={<AdminSettingsPage />} />
             <Route path="*" element={<NotFoundPage />} />
           </Routes>
         </Suspense>
       </BrowserRouter>
     );
   }
   ```

#### 1.2.3 빌드 및 배포 전략

1. **독립적인 빌드 파이프라인**:
   - React 애플리케이션을 독립적으로 빌드하고 배포
   - CI/CD 파이프라인을 통한 자동화된 배포 프로세스 구축

2. **환경별 설정 분리**:
   ```typescript
   // config.ts
   const config = {
     development: {
       apiBaseUrl: 'http://localhost:8080/api/v1',
       enableMocking: true
     },
     production: {
       apiBaseUrl: '/api/v1',
       enableMocking: false
     }
   };

   export default config[process.env.NODE_ENV || 'development'];
   ```

### 1.3 서버 사이드 렌더링(SSR)과 클라이언트 사이드 렌더링(CSR) 전략

요구사항에 따라 메인 페이지는 SSR을, 나머지 페이지는 CSR을 적용하는 전략입니다:

#### 1.3.1 Next.js를 활용한 SSR 구현

1. **메인 페이지 SSR 구현**:
   ```typescript
   // pages/index.tsx (Next.js)
   export async function getServerSideProps() {
     // 서버에서 데이터 페칭
     const mainData = await fetchMainPageData();

     return {
       props: {
         mainData,
         lastUpdated: new Date().toISOString()
       }
     };
   }

   function MainPage({ mainData, lastUpdated }) {
     return (
       <MainLayout>
         <HeroBanner data={mainData.banner} />
         <FeaturedContent items={mainData.featuredItems} />
         <RecentUpdates updates={mainData.recentUpdates} />
         <footer>Last updated: {new Date(lastUpdated).toLocaleString()}</footer>
       </MainLayout>
     );
   }
   ```

2. **하이브리드 렌더링 설정**:
   - 메인 페이지: `getServerSideProps`를 사용한 SSR
   - 정적 콘텐츠 페이지: `getStaticProps`를 사용한 SSG(Static Site Generation)
   - 동적 페이지: CSR 또는 필요에 따라 SSR

#### 1.3.2 React 전용 SSR 구현

Next.js를 사용하지 않는 경우, React 애플리케이션에서 직접 SSR을 구현하는 방법:

```typescript
// server.js (Express 서버)
import express from 'express';
import React from 'react';
import { renderToString } from 'react-dom/server';
import { StaticRouter } from 'react-router-dom/server';
import App from './src/App';
import path from 'path';
import fs from 'fs';

const app = express();
const PORT = process.env.PORT || 3000;

// 정적 파일 제공
app.use(express.static(path.resolve(__dirname, 'build')));

// API 엔드포인트
app.get('/api/data', (req, res) => {
  res.json({
    mainData: {
      banner: { title: '환영합니다', imageUrl: '/images/banner.jpg' },
      featuredItems: [/* ... */],
      recentUpdates: [/* ... */]
    },
    lastUpdated: new Date().toISOString()
  });
});

// 모든 요청에 대한 SSR 처리
app.get('*', (req, res) => {
  // 데이터 페칭
  const mainData = {
    banner: { title: '환영합니다', imageUrl: '/images/banner.jpg' },
    featuredItems: [/* ... */],
    recentUpdates: [/* ... */]
  };
  const lastUpdated = new Date().toISOString();

  // React 컴포넌트 렌더링
  const appHtml = renderToString(
    <StaticRouter location={req.url}>
      <App initialData={{ mainData, lastUpdated }} />
    </StaticRouter>
  );

  // HTML 템플릿 읽기
  const indexFile = path.resolve('./build/index.html');
  fs.readFile(indexFile, 'utf8', (err, data) => {
    if (err) {
      return res.status(500).send('서버 오류가 발생했습니다.');
    }

    // 초기 상태 및 렌더링된 HTML 삽입
    const html = data
      .replace('<div id="root"></div>', `<div id="root">${appHtml}</div>`)
      .replace(
        '</head>',
        `<script>window.__INITIAL_DATA__ = ${JSON.stringify({
          mainData,
          lastUpdated
        })};</script></head>`
      );

    return res.send(html);
  });
});

app.listen(PORT, () => {
  console.log(`서버가 http://localhost:${PORT}에서 실행 중입니다.`);
});
```

### 1.4 국제화(i18n) 지원 전략

React.js 애플리케이션에서 국제화(i18n)를 구현하는 전략입니다:

#### 1.4.1 React-Intl 라이브러리 활용

```typescript
// i18n/provider.tsx
import { IntlProvider } from 'react-intl';
import { useState, useEffect } from 'react';
import ko from './messages/ko.json';
import en from './messages/en.json';

const messages = { ko, en };

export function I18nProvider({ children }) {
  // 사용자 언어 설정 로드 (localStorage 또는 서버 API에서)
  const [locale, setLocale] = useState(localStorage.getItem('locale') || navigator.language || 'ko');

  useEffect(() => {
    // 언어 변경 이벤트 리스너
    const handleLanguageChange = (newLocale) => {
      setLocale(newLocale);
      localStorage.setItem('locale', newLocale);
    };

    window.addEventListener('languageChange', handleLanguageChange);
    return () => window.removeEventListener('languageChange', handleLanguageChange);
  }, []);

  return (
    <IntlProvider locale={locale} messages={messages[locale.split('-')[0]]}>
      {children}
    </IntlProvider>
  );
}

// 언어 변경 함수
export function changeLanguage(locale) {
  window.dispatchEvent(new CustomEvent('languageChange', { detail: locale }));
}
```

#### 1.4.2 메시지 추출 및 변환

React.js에서 사용할 메시지 리소스 파일 형식:

```bash
# 기존 프로퍼티 파일 형식 (참고용)
greeting=안녕하세요
welcome=환영합니다, {0}님!

# React.js JSON 메시지 파일 (ko.json)
{
  "greeting": "안녕하세요",
  "welcome": "환영합니다, {name}님!"
}
```

#### 1.4.3 컴포넌트에서 사용 예시

```tsx
import { FormattedMessage, useIntl } from 'react-intl';

function WelcomeComponent({ username }) {
  const intl = useIntl();

  return (
    <div>
      <h1><FormattedMessage id="greeting" /></h1>
      <p>{intl.formatMessage({ id: 'welcome' }, { name: username })}</p>
    </div>
  );
}
```

### 1.5 JSP 컴포넌트를 React 컴포넌트로 변환 예시

다음은 일반적인 JSP 컴포넌트 패턴과 이를 React로 변환한 예시입니다:

#### 1.5.1 폼 처리

**JSP 폼:**

```jsp
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<form:form action="/member/save" method="post" modelAttribute="memberForm">
  <div class="form-group">
    <form:label path="username">사용자명</form:label>
    <form:input path="username" cssClass="form-control" />
    <form:errors path="username" cssClass="text-danger" />
  </div>

  <div class="form-group">
    <form:label path="email">이메일</form:label>
    <form:input path="email" cssClass="form-control" />
    <form:errors path="email" cssClass="text-danger" />
  </div>

  <div class="form-group">
    <form:label path="role">역할</form:label>
    <form:select path="role" cssClass="form-control">
      <form:option value="">-- 선택하세요 --</form:option>
      <form:options items="${roleList}" itemValue="code" itemLabel="name" />
    </form:select>
  </div>

  <button type="submit" class="btn btn-primary">저장</button>
  <a href="/member/list" class="btn btn-secondary">취소</a>
</form:form>
```

**React 폼 (React Hook Form + Zod):**

```tsx
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useMutation } from 'react-query';
import { memberApi } from '@/api/member';
import { useNavigate } from 'react-router-dom';

// 유효성 검증 스키마
const memberSchema = z.object({
  username: z.string().min(3, '사용자명은 3자 이상이어야 합니다'),
  email: z.string().email('유효한 이메일 주소를 입력하세요'),
  role: z.string().min(1, '역할을 선택하세요')
});

type MemberFormData = z.infer<typeof memberSchema>;

function MemberForm({ initialData, roleList }) {
  const navigate = useNavigate();

  const { register, handleSubmit, formState: { errors } } = useForm<MemberFormData>({
    resolver: zodResolver(memberSchema),
    defaultValues: initialData || {}
  });

  const mutation = useMutation(memberApi.saveMember, {
    onSuccess: () => {
      navigate('/member/list');
    }
  });

  const onSubmit = (data: MemberFormData) => {
    mutation.mutate(data);
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <div className="form-group">
        <label htmlFor="username">사용자명</label>
        <input
          id="username"
          className="form-control"
          {...register('username')}
        />
        {errors.username && (
          <p className="text-danger">{errors.username.message}</p>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="email">이메일</label>
        <input
          id="email"
          className="form-control"
          {...register('email')}
        />
        {errors.email && (
          <p className="text-danger">{errors.email.message}</p>
        )}
      </div>

      <div className="form-group">
        <label htmlFor="role">역할</label>
        <select
          id="role"
          className="form-control"
          {...register('role')}
        >
          <option value="">-- 선택하세요 --</option>
          {roleList.map(role => (
            <option key={role.code} value={role.code}>
              {role.name}
            </option>
          ))}
        </select>
        {errors.role && (
          <p className="text-danger">{errors.role.message}</p>
        )}
      </div>

      <button type="submit" className="btn btn-primary" disabled={mutation.isLoading}>
        {mutation.isLoading ? '저장 중...' : '저장'}
      </button>
      <button
        type="button"
        className="btn btn-secondary ml-2"
        onClick={() => navigate('/member/list')}
      >
        취소
      </button>
    </form>
  );
}
```

#### 1.5.2 테이블/그리드 컴포넌트

**JSP 테이블:**

```jsp
<table class="table table-striped">
  <thead>
    <tr>
      <th>ID</th>
      <th>제목</th>
      <th>작성자</th>
      <th>작성일</th>
      <th>조회수</th>
      <th>관리</th>
    </tr>
  </thead>
  <tbody>
    <c:forEach items="${boardList}" var="board">
      <tr>
        <td>${board.id}</td>
        <td><a href="/board/view?id=${board.id}">${board.title}</a></td>
        <td>${board.writer}</td>
        <td><fmt:formatDate value="${board.createdDate}" pattern="yyyy-MM-dd" /></td>
        <td>${board.viewCount}</td>
        <td>
          <a href="/board/edit?id=${board.id}" class="btn btn-sm btn-primary">수정</a>
          <a href="javascript:void(0);" onclick="deleteBoard(${board.id})" class="btn btn-sm btn-danger">삭제</a>
        </td>
      </tr>
    </c:forEach>
    <c:if test="${empty boardList}">
      <tr>
        <td colspan="6" class="text-center">게시글이 없습니다.</td>
      </tr>
    </c:if>
  </tbody>
</table>

<div class="pagination-container">
  <ul class="pagination">
    <c:if test="${pageMaker.prev}">
      <li class="page-item">
        <a class="page-link" href="/board/list?page=${pageMaker.startPage - 1}">이전</a>
      </li>
    </c:if>

    <c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="pageNum">
      <li class="page-item ${pageNum == pageMaker.criteria.page ? 'active' : ''}">
        <a class="page-link" href="/board/list?page=${pageNum}">${pageNum}</a>
      </li>
    </c:forEach>

    <c:if test="${pageMaker.next}">
      <li class="page-item">
        <a class="page-link" href="/board/list?page=${pageMaker.endPage + 1}">다음</a>
      </li>
    </c:if>
  </ul>
</div>
```

**React 테이블:**

```tsx
import { useState } from 'react';
import { useQuery } from 'react-query';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { format } from 'date-fns';
import { boardApi } from '@/api/board';
import { Pagination } from '@/components/molecules/Pagination';
import { LoadingSpinner } from '@/components/atoms/LoadingSpinner';
import { ConfirmModal } from '@/components/molecules/ConfirmModal';

function BoardList() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const page = parseInt(searchParams.get('page') || '1', 10);

  const [deleteId, setDeleteId] = useState<number | null>(null);
  const [showDeleteModal, setShowDeleteModal] = useState(false);

  const { data, isLoading, refetch } = useQuery(
    ['boards', page],
    () => boardApi.getBoardList({ page, size: 10 })
  );

  const handleDeleteClick = (id: number) => {
    setDeleteId(id);
    setShowDeleteModal(true);
  };

  const confirmDelete = async () => {
    if (deleteId) {
      await boardApi.deleteBoard(deleteId);
      setShowDeleteModal(false);
      refetch();
    }
  };

  if (isLoading) return <LoadingSpinner />;

  const { content: boardList, totalPages } = data || { content: [], totalPages: 0 };

  return (
    <>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>ID</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
            <th>관리</th>
          </tr>
        </thead>
        <tbody>
          {boardList.length > 0 ? (
            boardList.map(board => (
              <tr key={board.id}>
                <td>{board.id}</td>
                <td>
                  <Link to={`/board/view/${board.id}`}>{board.title}</Link>
                </td>
                <td>{board.writer}</td>
                <td>{format(new Date(board.createdDate), 'yyyy-MM-dd')}</td>
                <td>{board.viewCount}</td>
                <td>
                  <button
                    className="btn btn-sm btn-primary mr-1"
                    onClick={() => navigate(`/board/edit/${board.id}`)}
                  >
                    수정
                  </button>
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteClick(board.id)}
                  >
                    삭제
                  </button>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={6} className="text-center">
                게시글이 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={(newPage) => navigate(`/board/list?page=${newPage}`)}
      />

      <ConfirmModal
        isOpen={showDeleteModal}
        title="게시글 삭제"
        message="정말로 이 게시글을 삭제하시겠습니까?"
        confirmText="삭제"
        cancelText="취소"
        onConfirm={confirmDelete}
        onCancel={() => setShowDeleteModal(false)}
      />
    </>
  );
}
```

#### 1.5.3 네비게이션 메뉴

**JSP 네비게이션 메뉴:**

```jsp
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <a class="navbar-brand" href="/">시스템</a>
  <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarNav">
    <span class="navbar-toggler-icon"></span>
  </button>
  <div class="collapse navbar-collapse" id="navbarNav">
    <ul class="navbar-nav mr-auto">
      <li class="nav-item ${currentMenu == 'home' ? 'active' : ''}">
        <a class="nav-link" href="/">홈</a>
      </li>
      <li class="nav-item ${currentMenu == 'board' ? 'active' : ''}">
        <a class="nav-link" href="/board/list">게시판</a>
      </li>
      <li class="nav-item ${currentMenu == 'schedule' ? 'active' : ''}">
        <a class="nav-link" href="/schedule/calendar">일정</a>
      </li>
      <c:if test="${isAdmin}">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" id="adminDropdown" role="button" 
             data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            관리자
          </a>
          <div class="dropdown-menu" aria-labelledby="adminDropdown">
            <a class="dropdown-item" href="/admin/users">사용자 관리</a>
            <a class="dropdown-item" href="/admin/settings">시스템 설정</a>
          </div>
        </li>
      </c:if>
    </ul>
    <ul class="navbar-nav">
      <c:choose>
        <c:when test="${not empty sessionScope.user}">
          <li class="nav-item">
            <a class="nav-link" href="/member/profile">${sessionScope.user.name}님</a>
          </li>
          <li class="nav-item">
            <a class="nav-link" href="/logout">로그아웃</a>
          </li>
        </c:when>
        <c:otherwise>
          <li class="nav-item">
            <a class="nav-link" href="/login">로그인</a>
          </li>
        </c:otherwise>
      </c:choose>
    </ul>
  </div>
</nav>
```

**React 네비게이션 메뉴:**

```tsx
import { useState } from 'react';
import { Link, NavLink, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';

function Navigation() {
  const [isOpen, setIsOpen] = useState(false);
  const { user, isAuthenticated, logout } = useAuthStore();
  const navigate = useNavigate();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg navbar-dark bg-dark">
      <Link className="navbar-brand" to="/">시스템</Link>
      <button 
        className="navbar-toggler" 
        type="button" 
        onClick={() => setIsOpen(!isOpen)}
      >
        <span className="navbar-toggler-icon"></span>
      </button>
      <div className={`collapse navbar-collapse ${isOpen ? 'show' : ''}`}>
        <ul className="navbar-nav mr-auto">
          <li className="nav-item">
            <NavLink 
              className={({ isActive }) => 
                `nav-link ${isActive ? 'active' : ''}`
              } 
              to="/"
            >
              홈
            </NavLink>
          </li>
          <li className="nav-item">
            <NavLink 
              className={({ isActive }) => 
                `nav-link ${isActive ? 'active' : ''}`
              } 
              to="/board/list"
            >
              게시판
            </NavLink>
          </li>
          <li className="nav-item">
            <NavLink 
              className={({ isActive }) => 
                `nav-link ${isActive ? 'active' : ''}`
              } 
              to="/schedule/calendar"
            >
              일정
            </NavLink>
          </li>
          {user?.isAdmin && (
            <li className="nav-item dropdown">
              <a 
                className="nav-link dropdown-toggle" 
                href="#" 
                id="adminDropdown" 
                role="button" 
                onClick={(e) => {
                  e.preventDefault();
                  document.getElementById('adminDropdownMenu')?.classList.toggle('show');
                }}
              >
                관리자
              </a>
              <div className="dropdown-menu" id="adminDropdownMenu">
                <Link className="dropdown-item" to="/admin/users">사용자 관리</Link>
                <Link className="dropdown-item" to="/admin/settings">시스템 설정</Link>
              </div>
            </li>
          )}
        </ul>
        <ul className="navbar-nav">
          {isAuthenticated ? (
            <>
              <li className="nav-item">
                <Link className="nav-link" to="/member/profile">
                  {user?.name}님
                </Link>
              </li>
              <li className="nav-item">
                <a className="nav-link" href="#" onClick={(e) => {
                  e.preventDefault();
                  handleLogout();
                }}>
                  로그아웃
                </a>
              </li>
            </>
          ) : (
            <li className="nav-item">
              <Link className="nav-link" to="/login">로그인</Link>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
}
```

#### 1.5.4 모달 다이얼로그

**JSP 모달 다이얼로그:**

```jsp
<!-- 모달 트리거 버튼 -->
<button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModal">
  모달 열기
</button>

<!-- 모달 다이얼로그 -->
<div class="modal fade" id="exampleModal" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title" id="exampleModalLabel">모달 제목</h5>
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        모달 내용이 여기에 표시됩니다.
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-dismiss="modal">닫기</button>
        <button type="button" class="btn btn-primary" onclick="saveChanges()">저장</button>
      </div>
    </div>
  </div>
</div>

<script>
  function saveChanges() {
    // 저장 로직
    alert('저장되었습니다.');
    $('#exampleModal').modal('hide');
  }
</script>
```

**React 모달 다이얼로그 (컴포넌트):**

```tsx
// components/molecules/Modal.tsx
import { ReactNode, useEffect } from 'react';
import { createPortal } from 'react-dom';

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: ReactNode;
  footer?: ReactNode;
}

export function Modal({ isOpen, onClose, title, children, footer }: ModalProps) {
  useEffect(() => {
    const handleEscape = (e: KeyboardEvent) => {
      if (e.key === 'Escape' && isOpen) {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscape);

    // 모달이 열릴 때 body 스크롤 방지
    if (isOpen) {
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.removeEventListener('keydown', handleEscape);
      document.body.style.overflow = '';
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return createPortal(
    <div className="modal-backdrop" onClick={onClose}>
      <div className="modal-container" onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h5 className="modal-title">{title}</h5>
          <button 
            type="button" 
            className="close" 
            onClick={onClose}
            aria-label="Close"
          >
            <span aria-hidden="true">&times;</span>
          </button>
        </div>
        <div className="modal-body">
          {children}
        </div>
        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>,
    document.getElementById('modal-root') || document.body
  );
}
```

**React 모달 사용 예시:**

```tsx
import { useState } from 'react';
import { Modal } from '@/components/molecules/Modal';

function ModalExample() {
  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSave = () => {
    // 저장 로직
    alert('저장되었습니다.');
    setIsModalOpen(false);
  };

  return (
    <div>
      <button 
        type="button" 
        className="btn btn-primary" 
        onClick={() => setIsModalOpen(true)}
      >
        모달 열기
      </button>

      <Modal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        title="모달 제목"
        footer={
          <>
            <button 
              type="button" 
              className="btn btn-secondary" 
              onClick={() => setIsModalOpen(false)}
            >
              닫기
            </button>
            <button 
              type="button" 
              className="btn btn-primary" 
              onClick={handleSave}
            >
              저장
            </button>
          </>
        }
      >
        모달 내용이 여기에 표시됩니다.
      </Modal>
    </div>
  );
}
```

#### 1.5.5 페이지네이션 컴포넌트

**JSP 페이지네이션:**

```jsp
<div class="pagination-container">
  <ul class="pagination">
    <c:if test="${pageMaker.prev}">
      <li class="page-item">
        <a class="page-link" href="${path}/board/list?page=${pageMaker.startPage - 1}">이전</a>
      </li>
    </c:if>

    <c:forEach begin="${pageMaker.startPage}" end="${pageMaker.endPage}" var="pageNum">
      <li class="page-item ${pageNum == pageMaker.criteria.page ? 'active' : ''}">
        <a class="page-link" href="${path}/board/list?page=${pageNum}">${pageNum}</a>
      </li>
    </c:forEach>

    <c:if test="${pageMaker.next}">
      <li class="page-item">
        <a class="page-link" href="${path}/board/list?page=${pageMaker.endPage + 1}">다음</a>
      </li>
    </c:if>
  </ul>
</div>
```

**React 페이지네이션 컴포넌트:**

```tsx
// components/molecules/Pagination.tsx
interface PaginationProps {
  currentPage: number;
  totalPages: number;
  onPageChange: (page: number) => void;
  maxPageItems?: number;
}

export function Pagination({ 
  currentPage, 
  totalPages, 
  onPageChange,
  maxPageItems = 5
}: PaginationProps) {
  // 페이지 범위 계산
  const startPage = Math.max(1, currentPage - Math.floor(maxPageItems / 2));
  const endPage = Math.min(totalPages, startPage + maxPageItems - 1);

  // 페이지 배열 생성
  const pages = Array.from(
    { length: endPage - startPage + 1 },
    (_, i) => startPage + i
  );

  // 이전/다음 페이지 버튼 표시 여부
  const showPrev = startPage > 1;
  const showNext = endPage < totalPages;

  return (
    <div className="pagination-container">
      <ul className="pagination">
        {showPrev && (
          <li className="page-item">
            <button
              className="page-link"
              onClick={() => onPageChange(startPage - 1)}
            >
              이전
            </button>
          </li>
        )}

        {pages.map(page => (
          <li 
            key={page} 
            className={`page-item ${page === currentPage ? 'active' : ''}`}
          >
            <button
              className="page-link"
              onClick={() => onPageChange(page)}
            >
              {page}
            </button>
          </li>
        ))}

        {showNext && (
          <li className="page-item">
            <button
              className="page-link"
              onClick={() => onPageChange(endPage + 1)}
            >
              다음
            </button>
          </li>
        )}
      </ul>
    </div>
  );
}
```

## 2. 컴포넌트 계층 구조

### 2.1 컴포넌트 계층
- Pages: 라우팅 진입점 역할의 페이지 컴포넌트
- Templates: 레이아웃을 담당하는 템플릿 컴포넌트
- Organisms: 독립적인 섹션을 구성하는 복합 컴포넌트
- Molecules: 재사용 가능한 UI 패턴 컴포넌트
- Atoms: 기본 UI 요소 컴포넌트

### 2.2 아토믹 디자인 패턴 적용

컴포넌트를 다음과 같은 계층으로 구분하여 재사용성을 극대화합니다:

1. **아톰(Atoms)**: 버튼, 입력 필드, 아이콘 등 가장 기본적인 UI 요소
2. **분자(Molecules)**: 아톰을 조합한 작은 기능 단위 (예: 검색 필드, 폼 필드)
3. **유기체(Organisms)**: 분자를 조합한 복잡한 UI 섹션 (예: 헤더, 푸터, 폼)
4. **템플릿(Templates)**: 페이지 레이아웃을 정의하는 와이어프레임
5. **페이지(Pages)**: 실제 콘텐츠가 채워진 완성된 화면

### 2.3 상태 관리
- 서버 상태 관리 (React Query)
   - 데이터 페칭 및 캐싱
   - 서버 데이터 동기화
   - 낙관적 업데이트
   - 에러 처리 및 재시도
   - 페이지네이션 및 무한 스크롤
- 클라이언트 상태 관리 (Zustand)
   - UI 상태 (모달, 드로어, 선택된 항목 등)
   - 필터 및 정렬 설정
   - 폼 임시 데이터
   - 테마 설정
   - 인증 상태

### 2.9 디렉토리 구조

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
