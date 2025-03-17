import { useState, useEffect } from 'react';
import { 
  Box, 
  Container, 
  Grid, 
  Typography, 
  AppBar, 
  Toolbar, 
  // Button,
  TextField,
  InputAdornment,
  IconButton,
  CircularProgress,
  Paper,
  Menu,
  MenuItem,
  Avatar,
  Divider,
  useTheme,
  // useMediaQuery
} from '@mui/material';
import SearchIcon from '@mui/icons-material/Search';
import LogoutIcon from '@mui/icons-material/Logout';
import PersonIcon from '@mui/icons-material/Person';
import PeopleIcon from '@mui/icons-material/People';
import MenuIcon from '@mui/icons-material/Menu';
import { useAuthStore } from '../store/authStore';
import { fetchMainPageData, fetchCalendarData } from '../services/mainService';
import ProjectList from '../components/ProjectList';
import NewsList from '../components/NewsList';
import Timeline from '../components/Timeline';
import Calendar from '../components/Calendar';
import SideMenu from '../components/SideMenu';

const MainPage = () => {
  const { username, logout } = useAuthStore();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [mainData, setMainData] = useState<any>(null);
  const [calendarData, setCalendarData] = useState<any>(null);
  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const [menuOpen, setMenuOpen] = useState(false);

  const theme = useTheme();
  // const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const drawerWidth = 240;

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  const toggleDrawer = () => {
    setMenuOpen(!menuOpen);
  };

  const handleProfileClick = () => {
    window.location.href = '/memberForm';
    handleMenuClose();
  };

  const handleUserSearchClick = () => {
    window.location.href = '/searchMember';
    handleMenuClose();
  };

  const handleLogoutClick = () => {
    logout();
    handleMenuClose();
  };

  // 데이터 매핑 함수: API 응답 데이터를 프론트엔드 컴포넌트가 기대하는 형식으로 변환
  const mapApiDataToComponentData = (apiData: any) => {
    if (!apiData) return null;

    const mappedData = { ...apiData };

    // 프로젝트 목록 매핑
    if (apiData.projectlistview) {
      mappedData.projectlistview = apiData.projectlistview.map((project: any) => ({
        id: project.prno || project.id || 0,
        title: project.prtitle || project.title || '',
        description: `${project.usernm || ''} (${project.prstatus || ''}) ${project.prstartdate || ''} ~ ${project.prenddate || ''}`,
        ...project
      }));
    }

    // 뉴스 목록 매핑
    if (apiData.listview) {
      mappedData.listview = apiData.listview.map((news: any) => ({
        id: news.brdno || news.id || 0,
        title: news.brdtitle || news.title || '',
        brdtitle: news.brdtitle || news.title || '',
        usernm: news.brdwriter || news.usernm || '',
        regdate: news.regdate || '',
        bgname: news.bgname || '',
        ...news
      }));
    }

    // 공지사항 목록 매핑
    if (apiData.noticeList) {
      mappedData.noticeList = apiData.noticeList.map((notice: any) => ({
        id: notice.brdno || notice.id || 0,
        title: notice.brdtitle || notice.title || '',
        brdtitle: notice.brdtitle || notice.title || '',
        usernm: notice.brdwriter || notice.usernm || '',
        regdate: notice.regdate || '',
        bgname: notice.bgname || '',
        ...notice
      }));
    }

    // 타임라인 목록 매핑
    if (apiData.listtime) {
      mappedData.listtime = apiData.listtime.map((item: any, index: number) => ({
        id: item.reno || item.id || index, // Use index as fallback to ensure unique IDs
        content: item.rememo || item.content || '',
        writer: item.rewriter || '',
        date: item.regdate || '',
        ...item
      }));
    }

    return mappedData;
  };

  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      setError(null);

      try {
        const [mainPageData, calendarPageData] = await Promise.all([
          fetchMainPageData(),
          fetchCalendarData()
        ]);

        // 디버그 로깅 추가
        console.log('Main page data received:', mainPageData);
        console.log('Project list data:', mainPageData?.projectlistview);
        console.log('News list data:', mainPageData?.listview);
        console.log('Notice list data:', mainPageData?.noticeList);
        console.log('Timeline data:', mainPageData?.listtime);
        console.log('Calendar data received:', calendarPageData);

        // API 데이터를 컴포넌트에 맞게 변환
        const mappedMainData = mapApiDataToComponentData(mainPageData);
        console.log('Mapped main page data:', mappedMainData);

        setMainData(mappedMainData);
        setCalendarData(calendarPageData);
      } catch (err) {
        console.error('Error loading main page data:', err);
        setError('데이터를 불러오는 중 오류가 발생했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, []);

  const handleSearch = async () => {
    setLoading(true);
    setError(null);

    try {
      const mainPageData = await fetchMainPageData(searchKeyword);

      // 디버그 로깅 추가
      console.log('Search results received:', mainPageData);

      // API 데이터를 컴포넌트에 맞게 변환
      const mappedMainData = mapApiDataToComponentData(mainPageData);
      console.log('Mapped search results:', mappedMainData);

      setMainData(mappedMainData);
    } catch (err) {
      console.error('Error searching:', err);
      setError('검색 중 오류가 발생했습니다.');
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  if (loading && !mainData) {
    return (
      <Box
        display="flex"
        justifyContent="center"
        alignItems="center"
        minHeight="100vh"
      >
        <CircularProgress />
      </Box>
    );
  }

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar 
        position="fixed" 
        sx={{ 
          zIndex: (theme) => theme.zIndex.drawer + 1,
          transition: theme.transitions.create(['width', 'margin'], {
            easing: theme.transitions.easing.sharp,
            duration: theme.transitions.duration.leavingScreen,
          }),
          ...(menuOpen && {
            marginLeft: drawerWidth,
            width: `calc(100% - ${drawerWidth}px)`,
            transition: theme.transitions.create(['width', 'margin'], {
              easing: theme.transitions.easing.sharp,
              duration: theme.transitions.duration.enteringScreen,
            }),
          }),
        }}
      >
        <Toolbar>
          <IconButton
            color="inherit"
            aria-label="open drawer"
            onClick={toggleDrawer}
            edge="start"
            sx={{ mr: 2 }}
          >
            <MenuIcon />
          </IconButton>
          <Typography variant="h6" component="div" sx={{ flexGrow: 1 }}>
            MTSSBJ 애플리케이션
          </Typography>
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="body1" sx={{ mr: 2 }}>
              {username} 님 환영합니다
            </Typography>
            <IconButton
              color="inherit"
              onClick={handleMenuOpen}
              size="large"
              edge="end"
              aria-label="account of current user"
              aria-haspopup="true"
            >
              <Avatar sx={{ width: 32, height: 32, bgcolor: 'secondary.main' }}>
                {username ? username.charAt(0).toUpperCase() : 'U'}
              </Avatar>
            </IconButton>
            <Menu
              anchorEl={anchorEl}
              open={Boolean(anchorEl)}
              onClose={handleMenuClose}
              anchorOrigin={{
                vertical: 'bottom',
                horizontal: 'right',
              }}
              transformOrigin={{
                vertical: 'top',
                horizontal: 'right',
              }}
            >
              <MenuItem onClick={handleProfileClick}>
                <PersonIcon fontSize="small" sx={{ mr: 1 }} />
                <Typography variant="body2">{username}</Typography>
              </MenuItem>
              <MenuItem onClick={handleUserSearchClick}>
                <PeopleIcon fontSize="small" sx={{ mr: 1 }} />
                <Typography variant="body2">사용자 검색</Typography>
              </MenuItem>
              <Divider />
              <MenuItem onClick={handleLogoutClick}>
                <LogoutIcon fontSize="small" sx={{ mr: 1 }} />
                <Typography variant="body2">로그아웃</Typography>
              </MenuItem>
            </Menu>
          </Box>
        </Toolbar>
      </AppBar>

      <SideMenu open={menuOpen} onClose={toggleDrawer} width={drawerWidth} />

      <Container maxWidth="lg" sx={{ 
        mt: 10, // Add more top margin to account for the fixed AppBar
        mb: 4, 
        marginLeft: menuOpen ? drawerWidth : 0, 
        width: `calc(100% - ${menuOpen ? drawerWidth : 0}px)`, 
        transition: theme.transitions.create(['margin', 'width'], { 
          easing: theme.transitions.easing.sharp, 
          duration: theme.transitions.duration.leavingScreen 
        }) 
      }}>
        {error && (
          <Paper sx={{ p: 2, mb: 3, bgcolor: 'error.light', color: 'error.contrastText' }}>
            <Typography>{error}</Typography>
          </Paper>
        )}

        <Box sx={{ mb: 3 }}>
          <TextField
            fullWidth
            placeholder="검색어를 입력하세요"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            onKeyPress={handleKeyPress}
            InputProps={{
              endAdornment: (
                <InputAdornment position="end">
                  <IconButton onClick={handleSearch} edge="end">
                    <SearchIcon />
                  </IconButton>
                </InputAdornment>
              ),
            }}
          />
        </Box>

        <Grid container spacing={3}>
          {/* Projects */}
          <Grid item xs={12} md={6}>
            <ProjectList projects={mainData?.projectlistview || []} />
          </Grid>

          {/* News */}
          <Grid item xs={12} md={6}>
            <NewsList news={mainData?.listview || []} title="뉴스" />
          </Grid>

          {/* Notices */}
          <Grid item xs={12} md={6}>
            <NewsList news={mainData?.noticeList || []} title="공지사항" />
          </Grid>

          {/* Timeline */}
          <Grid item xs={12} md={6}>
            <Timeline items={mainData?.listtime || []} />
          </Grid>

          {/* Calendar */}
          <Grid item xs={12}>
            {calendarData && <Calendar initialData={calendarData} />}
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default MainPage;
