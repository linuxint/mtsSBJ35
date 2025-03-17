import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Container,
  TextField,
  Typography,
  Paper,
  Alert,
  // FormHelperText,
} from '@mui/material';
import { useAuthStore } from '../store/authStore';

const LoginPage = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [usernameError, setUsernameError] = useState('');
  const login = useAuthStore((state) => state.login);

  // Validate username when it changes
  useEffect(() => {
    if (username && !/^[a-zA-Z0-9]+$/.test(username)) {
      setUsernameError('사용자 이름은 영문자와 숫자만 포함할 수 있습니다.');
    } else {
      setUsernameError('');
    }
  }, [username]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!username || !password) {
      setError('사용자 이름과 비밀번호를 입력해주세요.');
      return;
    }

    // Check if username contains only alphanumeric characters
    if (!/^[a-zA-Z0-9]+$/.test(username)) {
      setError('사용자 이름은 영문자와 숫자만 포함할 수 있습니다.');
      return;
    }

    // Don't submit if there's a username validation error
    if (usernameError) {
      setError(usernameError);
      return;
    }

    setError('');
    setLoading(true);

    try {
      const success = await login(username, password);

      if (!success) {
        setError('로그인에 실패했습니다. 사용자 이름과 비밀번호를 확인해주세요.');
      }
    } catch (err) {
      setError('로그인 중 오류가 발생했습니다. 나중에 다시 시도해주세요.');
      console.error('Login error:', err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Container maxWidth="sm">
      <Box
        sx={{
          display: 'flex',
          flexDirection: 'column',
          alignItems: 'center',
          justifyContent: 'center',
          minHeight: '100vh',
        }}
      >
        <Paper
          elevation={3}
          sx={{
            p: 4,
            width: '100%',
            borderRadius: 2,
          }}
        >
          <Typography component="h1" variant="h4" align="center" gutterBottom>
            로그인
          </Typography>

          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}

          <Box component="form" onSubmit={handleSubmit} noValidate>
            <TextField
              margin="normal"
              required
              fullWidth
              id="username"
              label="사용자 이름"
              name="username"
              autoComplete="username"
              autoFocus
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              disabled={loading}
              error={!!usernameError}
              helperText={usernameError}
              inputProps={{
                pattern: '[a-zA-Z0-9]*',
                title: '영문자와 숫자만 입력 가능합니다.'
              }}
            />
            <TextField
              margin="normal"
              required
              fullWidth
              name="password"
              label="비밀번호"
              type="password"
              id="password"
              autoComplete="current-password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              disabled={loading}
            />
            <Button
              type="submit"
              fullWidth
              variant="contained"
              sx={{ mt: 3, mb: 2 }}
              disabled={loading}
            >
              {loading ? '로그인 중...' : '로그인'}
            </Button>
          </Box>
        </Paper>
      </Box>
    </Container>
  );
};

export default LoginPage;
