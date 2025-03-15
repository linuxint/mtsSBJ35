# 프론트엔드 레이아웃 컴포넌트 변경 계획

## 현재 상태
- JSP/Thymeleaf 기반의 레이아웃 구현
- 단순한 HTML 테이블 구조
- 정적인 헤더/푸터 구현
- 기본적인 CSS 스타일링

## 변경 계획 개요
React 컴포넌트 기반의 현대적이고 재사용 가능한 레이아웃 시스템을 구축하고, 반응형 디자인과 사용자 경험을 개선합니다.

## 상세 변경 내용

### 헤더 컴포넌트 (Header.tsx)
```typescript
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '@/hooks/useAuth';
import { AppBar, Toolbar, Typography, Button, IconButton } from '@mui/material';
import { Menu as MenuIcon } from '@mui/icons-material';

interface HeaderProps {
  onMenuClick: () => void;
}

export const Header: React.FC<HeaderProps> = ({ onMenuClick }) => {
  const navigate = useNavigate();
  const { user, logout } = useAuth();

  const handleLogout = async () => {
    await logout();
    navigate('/login');
  };

  return (
    <AppBar position="fixed">
      <Toolbar>
        <IconButton
          edge="start"
          color="inherit"
          aria-label="menu"
          onClick={onMenuClick}
        >
          <MenuIcon />
        </IconButton>
        
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          MTS-SBJ
        </Typography>

        {user ? (
          <>
            <Typography variant="body1" sx={{ mr: 2 }}>
              {user.name}님 환영합니다
            </Typography>
            <Button color="inherit" onClick={handleLogout}>
              로그아웃
            </Button>
          </>
        ) : (
          <Button color="inherit" onClick={() => navigate('/login')}>
            로그인
          </Button>
        )}
      </Toolbar>
    </AppBar>
  );
};
```

### 푸터 컴포넌트 (Footer.tsx)
```typescript
import React from 'react';
import { Box, Container, Typography, Link } from '@mui/material';

export const Footer: React.FC = () => {
  return (
    <Box
      component="footer"
      sx={{
        py: 3,
        px: 2,
        mt: 'auto',
        backgroundColor: (theme) =>
          theme.palette.mode === 'light'
            ? theme.palette.grey[200]
            : theme.palette.grey[800],
      }}
    >
      <Container maxWidth="lg">
        <Typography variant="body1" align="center">
          © {new Date().getFullYear()} MTS-SBJ. All rights reserved.
        </Typography>
        <Typography variant="body2" color="text.secondary" align="center">
          <Link color="inherit" href="/privacy">
            개인정보처리방침
          </Link>
          {' | '}
          <Link color="inherit" href="/terms">
            이용약관
          </Link>
        </Typography>
      </Container>
    </Box>
  );
};
```

### 관리자 헤더 컴포넌트 (AdminHeader.tsx)
```typescript
import React from 'react';
import { useNavigate } from 'react-router-dom';
import { AppBar, Toolbar, Typography, Button, Chip } from '@mui/material';
import { useAuth } from '@/hooks/useAuth';

export const AdminHeader: React.FC = () => {
  const navigate = useNavigate();
  const { user } = useAuth();

  return (
    <AppBar position="fixed" color="secondary">
      <Toolbar>
        <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
          MTS-SBJ 관리자
        </Typography>
        
        <Chip
          label={`관리자: ${user?.name}`}
          color="primary"
          sx={{ mr: 2 }}
        />
        
        <Button
          color="inherit"
          onClick={() => navigate('/admin/dashboard')}
        >
          대시보드
        </Button>
        
        <Button
          color="inherit"
          onClick={() => navigate('/admin/users')}
        >
          사용자 관리
        </Button>
        
        <Button
          color="inherit"
          onClick={() => navigate('/admin/settings')}
        >
          시스템 설정
        </Button>
      </Toolbar>
    </AppBar>
  );
};
```

### 메인 레이아웃 컴포넌트 (MainLayout.tsx)
```typescript
import React, { useState } from 'react';
import { Box, CssBaseline, Container } from '@mui/material';
import { Header } from './Header';
import { Footer } from './Footer';
import { Navigation } from './Navigation';

interface MainLayoutProps {
  children: React.ReactNode;
}

export const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
  const [isNavOpen, setIsNavOpen] = useState(false);

  return (
    <Box sx={{ display: 'flex', minHeight: '100vh', flexDirection: 'column' }}>
      <CssBaseline />
      
      <Header onMenuClick={() => setIsNavOpen(!isNavOpen)} />
      
      <Navigation
        open={isNavOpen}
        onClose={() => setIsNavOpen(false)}
      />
      
      <Container
        component="main"
        maxWidth="lg"
        sx={{
          flexGrow: 1,
          py: 4,
          mt: 8, // Header height
        }}
      >
        {children}
      </Container>
      
      <Footer />
    </Box>
  );
};
```

### 레이아웃 테마 설정 (theme.ts)
```typescript
import { createTheme } from '@mui/material/styles';

export const theme = createTheme({
  palette: {
    primary: {
      main: '#1976d2',
      light: '#42a5f5',
      dark: '#1565c0',
    },
    secondary: {
      main: '#9c27b0',
      light: '#ba68c8',
      dark: '#7b1fa2',
    },
    background: {
      default: '#f5f5f5',
    },
  },
  typography: {
    fontFamily: [
      '-apple-system',
      'BlinkMacSystemFont',
      '"Segoe UI"',
      'Roboto',
      '"Helvetica Neue"',
      'Arial',
      'sans-serif',
    ].join(','),
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
        },
      },
    },
    MuiAppBar: {
      styleOverrides: {
        root: {
          boxShadow: '0px 2px 4px -1px rgba(0,0,0,0.1)',
        },
      },
    },
  },
});
```

## 변경 이점
- 재사용 가능한 컴포넌트 구조
- Material-UI를 활용한 현대적인 디자인
- 반응형 레이아웃 지원
- 일관된 디자인 시스템 적용
- 다크 모드 지원 가능
- 접근성 향상
- 성능 최적화 