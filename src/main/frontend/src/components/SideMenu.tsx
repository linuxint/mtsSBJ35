import React, { useState, useEffect } from 'react';
import { 
  Box, 
  Drawer, 
  List, 
  // ListItem,
  ListItemButton,
  ListItemIcon, 
  ListItemText, 
  Collapse, 
  IconButton, 
  Typography,
  Divider,
  useTheme,
  useMediaQuery
} from '@mui/material';
import { 
  ExpandLess, 
  ExpandMore, 
  ChevronLeft, 
  ChevronRight,
  Dashboard as DashboardIcon,
  Description as DescriptionIcon,
  Search as SearchIcon,
  Assignment as AssignmentIcon,
  NoteAlt as NoteAltIcon,
  CalendarMonth as CalendarIcon,
  Email as EmailIcon,
  MusicNote as MusicNoteIcon,
  Settings as SettingsIcon,
  Code as CodeIcon
} from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';
import { fetchMenuData, MenuItem } from '../services/menuService';

// 메뉴 아이콘 매핑
const getMenuIcon = (iconPath: string | null): React.ReactNode => {
  if (!iconPath) return <DescriptionIcon />;

  // 아이콘 경로에 따라 적절한 아이콘 반환
  switch (iconPath.toLowerCase()) {
    case 'fa-files-o':
      return <DescriptionIcon />;
    case 'fa-search':
      return <SearchIcon />;
    case 'fa-tasks':
      return <AssignmentIcon />;
    case 'fa-edit':
      return <NoteAltIcon />;
    case 'fa-calendar':
    case 'fa-calendar-o':
      return <CalendarIcon />;
    case 'fa-envelope-o':
      return <EmailIcon />;
    case 'fa-music':
      return <MusicNoteIcon />;
    case 'fa-gear':
      return <SettingsIcon />;
    case 'fa-sitemap':
      return <CodeIcon />;
    default:
      return <DashboardIcon />;
  }
};

interface SideMenuProps {
  open: boolean;
  onClose: () => void;
  width?: number;
}

const SideMenu: React.FC<SideMenuProps> = ({ 
  open, 
  onClose, 
  width = 240 
}) => {
  const [menuItems, setMenuItems] = useState<MenuItem[]>([]);
  const [expandedItems, setExpandedItems] = useState<Record<string, boolean>>({});
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const navigate = useNavigate();
  const location = useLocation();

  // 메뉴 데이터 로드
  useEffect(() => {
    const loadMenuData = async () => {
      try {
        setLoading(true);
        const data = await fetchMenuData();
        setMenuItems(data);
        setError(null);
      } catch (err) {
        console.error('Failed to load menu data:', err);
        setError('메뉴를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadMenuData();
  }, []);

  // 메뉴 아이템 클릭 핸들러
  const handleMenuItemClick = (item: MenuItem) => {
    if (item.children && item.children.length > 0) {
      // 하위 메뉴가 있는 경우 확장/축소 토글
      setExpandedItems(prev => ({
        ...prev,
        [item.mnuNo]: !prev[item.mnuNo]
      }));
    } else if (item.mnuTarget) {
      // 링크가 있는 경우 해당 페이지로 이동
      navigate(`/${item.mnuTarget}`);
      if (isMobile) {
        onClose(); // 모바일에서는 메뉴 클릭 후 사이드바 닫기
      }
    }
  };

  // 재귀적으로 메뉴 아이템 렌더링
  const renderMenuItem = (item: MenuItem, depth: number = 0) => {
    const hasChildren = item.children && item.children.length > 0;
    const isExpanded = expandedItems[item.mnuNo] || false;
    const isActive = location.pathname === `/${item.mnuTarget}`;

    return (
      <React.Fragment key={item.mnuNo}>
        <ListItemButton 
          onClick={() => handleMenuItemClick(item)}
          sx={{ 
            pl: 2 + depth * 2,
            backgroundColor: isActive ? 'rgba(0, 0, 0, 0.08)' : 'transparent',
            '&:hover': {
              backgroundColor: 'rgba(0, 0, 0, 0.04)',
            }
          }}
        >
          <ListItemIcon>
            {getMenuIcon(item.mnuImgpath)}
          </ListItemIcon>
          <ListItemText 
            primary={item.mnuNm} 
            primaryTypographyProps={{ 
              variant: 'body2',
              noWrap: true
            }}
          />
          {hasChildren && (isExpanded ? <ExpandLess /> : <ExpandMore />)}
        </ListItemButton>

        {hasChildren && (
          <Collapse in={isExpanded} timeout="auto" unmountOnExit>
            <List component="div" disablePadding>
              {item.children!.map(child => renderMenuItem(child, depth + 1))}
            </List>
          </Collapse>
        )}
      </React.Fragment>
    );
  };

  return (
    <Drawer
      variant={isMobile ? "temporary" : "persistent"}
      open={open}
      onClose={onClose}
      sx={{
        width: open ? width : 0,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width,
          boxSizing: 'border-box',
          overflowX: 'hidden'
        },
      }}
    >
      <Box
        sx={{
          display: 'flex',
          alignItems: 'center',
          padding: theme.spacing(0, 1),
          ...theme.mixins.toolbar,
          justifyContent: 'flex-end',
        }}
      >
        <Typography variant="h6" noWrap component="div" sx={{ flexGrow: 1, ml: 2 }}>
          MTS
        </Typography>
        <IconButton onClick={onClose}>
          {theme.direction === 'ltr' ? <ChevronLeft /> : <ChevronRight />}
        </IconButton>
      </Box>

      <Divider />

      {loading ? (
        <Box sx={{ p: 2 }}>
          <Typography>로딩 중...</Typography>
        </Box>
      ) : error ? (
        <Box sx={{ p: 2 }}>
          <Typography color="error">{error}</Typography>
        </Box>
      ) : (
        <List>
          {menuItems.flatMap(item => 
            // Only render children of level 1 menus (skip level 1 menus themselves)
            item.children ? item.children.map(child => renderMenuItem(child)) : []
          )}
        </List>
      )}
    </Drawer>
  );
};

export default SideMenu;
