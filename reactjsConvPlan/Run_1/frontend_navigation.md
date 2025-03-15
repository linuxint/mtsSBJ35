# 프론트엔드 네비게이션 컴포넌트 변경 계획

## 현재 상태
- JSP/Thymeleaf 기반의 정적 메뉴 구조
- 단순한 링크 기반 네비게이션
- 권한에 따른 메뉴 필터링 부재
- 중첩 메뉴 지원 미흡

## 변경 계획 개요
React 컴포넌트 기반의 동적이고 유연한 네비게이션 시스템을 구축하고, 권한 기반 메뉴 필터링과 중첩 메뉴 구조를 지원합니다.

## 상세 변경 내용

### 메뉴 아이템 인터페이스 (types/menu.ts)
```typescript
import { SvgIconComponent } from '@mui/icons-material';

export interface MenuItem {
  id: string;
  label: string;
  path?: string;
  icon?: SvgIconComponent;
  children?: MenuItem[];
  requiredRole?: string[];
}

export interface MenuSection {
  id: string;
  title: string;
  items: MenuItem[];
}
```

### 메뉴 구성 (config/menu.ts)
```typescript
import {
  Dashboard,
  People,
  Assignment,
  Settings,
  Business,
  Computer,
} from '@mui/icons-material';
import { MenuSection } from '@/types/menu';

export const menuConfig: MenuSection[] = [
  {
    id: 'main',
    title: '메인 메뉴',
    items: [
      {
        id: 'dashboard',
        label: '대시보드',
        path: '/dashboard',
        icon: Dashboard,
      },
      {
        id: 'member',
        label: '회원 관리',
        icon: People,
        children: [
          {
            id: 'member-list',
            label: '회원 목록',
            path: '/members',
            requiredRole: ['ROLE_ADMIN'],
          },
          {
            id: 'member-approval',
            label: '가입 승인',
            path: '/members/approval',
            requiredRole: ['ROLE_ADMIN'],
          },
        ],
      },
      {
        id: 'board',
        label: '게시판',
        icon: Assignment,
        children: [
          {
            id: 'notice',
            label: '공지사항',
            path: '/boards/notice',
          },
          {
            id: 'free',
            label: '자유게시판',
            path: '/boards/free',
          },
        ],
      },
    ],
  },
  {
    id: 'admin',
    title: '관리자 메뉴',
    items: [
      {
        id: 'admin-settings',
        label: '시스템 설정',
        icon: Settings,
        requiredRole: ['ROLE_ADMIN'],
        children: [
          {
            id: 'code',
            label: '코드 관리',
            path: '/admin/codes',
          },
          {
            id: 'department',
            label: '부서 관리',
            path: '/admin/departments',
          },
        ],
      },
      {
        id: 'admin-server',
        label: '서버 관리',
        icon: Computer,
        requiredRole: ['ROLE_ADMIN'],
        children: [
          {
            id: 'hardware',
            label: '하드웨어 관리',
            path: '/admin/servers/hardware',
          },
          {
            id: 'software',
            label: '소프트웨어 관리',
            path: '/admin/servers/software',
          },
        ],
      },
    ],
  },
];
```

### 네비게이션 컴포넌트 (Navigation.tsx)
```typescript
import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Collapse,
  Box,
  Typography,
  Divider,
} from '@mui/material';
import { ExpandLess, ExpandMore } from '@mui/icons-material';
import { menuConfig } from '@/config/menu';
import { MenuItem, MenuSection } from '@/types/menu';
import { useAuth } from '@/hooks/useAuth';

interface NavigationProps {
  open: boolean;
  onClose: () => void;
}

export const Navigation: React.FC<NavigationProps> = ({ open, onClose }) => {
  const location = useLocation();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [expandedItems, setExpandedItems] = React.useState<string[]>([]);

  const handleItemClick = (item: MenuItem) => {
    if (item.children) {
      setExpandedItems((prev) =>
        prev.includes(item.id)
          ? prev.filter((id) => id !== item.id)
          : [...prev, item.id]
      );
    } else if (item.path) {
      navigate(item.path);
      onClose();
    }
  };

  const hasRequiredRole = (item: MenuItem): boolean => {
    if (!item.requiredRole) return true;
    return item.requiredRole.some((role) => user?.roles.includes(role));
  };

  const renderMenuItem = (item: MenuItem) => {
    if (!hasRequiredRole(item)) return null;

    const isExpanded = expandedItems.includes(item.id);
    const isSelected = location.pathname === item.path;

    return (
      <React.Fragment key={item.id}>
        <ListItem
          button
          onClick={() => handleItemClick(item)}
          selected={isSelected}
          sx={{
            pl: item.children ? 2 : 3,
            '&.Mui-selected': {
              backgroundColor: 'primary.light',
              '&:hover': {
                backgroundColor: 'primary.light',
              },
            },
          }}
        >
          {item.icon && (
            <ListItemIcon>
              <item.icon />
            </ListItemIcon>
          )}
          <ListItemText primary={item.label} />
          {item.children && (isExpanded ? <ExpandLess /> : <ExpandMore />)}
        </ListItem>
        {item.children && (
          <Collapse in={isExpanded} timeout="auto" unmountOnExit>
            <List component="div" disablePadding>
              {item.children.map((child) => (
                <Box key={child.id} sx={{ pl: 2 }}>
                  {renderMenuItem(child)}
                </Box>
              ))}
            </List>
          </Collapse>
        )}
      </React.Fragment>
    );
  };

  const renderSection = (section: MenuSection) => {
    const hasVisibleItems = section.items.some((item) => hasRequiredRole(item));
    if (!hasVisibleItems) return null;

    return (
      <React.Fragment key={section.id}>
        <Box sx={{ px: 3, py: 2 }}>
          <Typography variant="subtitle2" color="text.secondary">
            {section.title}
          </Typography>
        </Box>
        <List>
          {section.items.map((item) => renderMenuItem(item))}
        </List>
        <Divider />
      </React.Fragment>
    );
  };

  return (
    <Drawer
      anchor="left"
      open={open}
      onClose={onClose}
      sx={{
        '& .MuiDrawer-paper': {
          width: 280,
          boxSizing: 'border-box',
        },
      }}
    >
      <Box sx={{ overflow: 'auto' }}>
        {menuConfig.map((section) => renderSection(section))}
      </Box>
    </Drawer>
  );
};
```

### 권한 기반 메뉴 필터링 훅 (useMenuFilter.ts)
```typescript
import { useMemo } from 'react';
import { MenuItem, MenuSection } from '@/types/menu';
import { useAuth } from '@/hooks/useAuth';

export const useMenuFilter = (menuSections: MenuSection[]) => {
  const { user } = useAuth();

  const filterMenuItem = (item: MenuItem): MenuItem | null => {
    if (item.requiredRole && !item.requiredRole.some((role) => user?.roles.includes(role))) {
      return null;
    }

    if (item.children) {
      const filteredChildren = item.children
        .map(filterMenuItem)
        .filter((child): child is MenuItem => child !== null);

      if (filteredChildren.length === 0) {
        return null;
      }

      return {
        ...item,
        children: filteredChildren,
      };
    }

    return item;
  };

  const filteredMenu = useMemo(() => {
    return menuSections
      .map((section) => ({
        ...section,
        items: section.items
          .map(filterMenuItem)
          .filter((item): item is MenuItem => item !== null),
      }))
      .filter((section) => section.items.length > 0);
  }, [menuSections, user?.roles]);

  return filteredMenu;
};
```

### 중첩 메뉴 상태 관리 훅 (useMenuState.ts)
```typescript
import { useState, useCallback } from 'react';
import { useLocation } from 'react-router-dom';
import { MenuItem } from '@/types/menu';

export const useMenuState = () => {
  const [expandedItems, setExpandedItems] = useState<string[]>([]);
  const location = useLocation();

  const isMenuItemActive = useCallback(
    (item: MenuItem): boolean => {
      if (item.path === location.pathname) return true;
      if (item.children) {
        return item.children.some(isMenuItemActive);
      }
      return false;
    },
    [location]
  );

  const toggleMenuItem = useCallback((itemId: string) => {
    setExpandedItems((prev) =>
      prev.includes(itemId)
        ? prev.filter((id) => id !== itemId)
        : [...prev, itemId]
    );
  }, []);

  const expandActiveMenuItems = useCallback(
    (items: MenuItem[]) => {
      const activeItemIds: string[] = [];
      const checkItem = (item: MenuItem) => {
        if (isMenuItemActive(item)) {
          activeItemIds.push(item.id);
          if (item.children) {
            item.children.forEach(checkItem);
          }
        }
      };

      items.forEach(checkItem);
      setExpandedItems(activeItemIds);
    },
    [isMenuItemActive]
  );

  return {
    expandedItems,
    toggleMenuItem,
    expandActiveMenuItems,
    isMenuItemActive,
  };
};
```

## 변경 이점
- 권한 기반의 동적 메뉴 필터링
- 중첩 메뉴 구조 지원
- 재사용 가능한 컴포넌트 구조
- 타입 안정성 확보
- 메뉴 상태 관리 용이
- 유연한 메뉴 구성 가능
- 사용자 경험 향상 