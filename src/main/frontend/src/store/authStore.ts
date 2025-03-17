import { create } from 'zustand';
import axios from 'axios';

interface AuthState {
  accessToken: string | null;
  refreshToken: string | null;
  username: string | null;
  role: string | null;
  isAuthenticated: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  checkAuth: () => Promise<boolean>;
  refreshAccessToken: () => Promise<boolean>;
}

export const useAuthStore = create<AuthState>((set, get) => ({
  accessToken: localStorage.getItem('accessToken'),
  refreshToken: localStorage.getItem('refreshToken'),
  username: localStorage.getItem('username'),
  role: localStorage.getItem('role'),
  isAuthenticated: !!localStorage.getItem('accessToken'),

  login: async (username: string, password: string) => {
    try {
      const response = await axios.post('/api/v1/auth/login', {
        username,
        password,
      });

      if (response.data.success) {
        const { accessToken, refreshToken, username, role } = response.data.data;
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('refreshToken', refreshToken);
        localStorage.setItem('username', username);
        localStorage.setItem('role', role);
        
        set({
          accessToken,
          refreshToken,
          username,
          role,
          isAuthenticated: true,
        });
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Login error:', error);
      return false;
    }
  },

  logout: () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    
    set({
      accessToken: null,
      refreshToken: null,
      username: null,
      role: null,
      isAuthenticated: false,
    });
  },

  checkAuth: async () => {
    const { accessToken, refreshToken, refreshAccessToken } = get();
    
    if (!accessToken) {
      if (refreshToken) {
        return await refreshAccessToken();
      }
      return false;
    }
    
    try {
      // Set up axios to use the token for all requests
      axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
      return true;
    } catch (error) {
      if (refreshToken) {
        return await refreshAccessToken();
      }
      return false;
    }
  },

  refreshAccessToken: async () => {
    const { refreshToken } = get();
    
    if (!refreshToken) {
      return false;
    }
    
    try {
      const response = await axios.post('/api/v1/auth/refresh', {
        refreshToken,
      });
      
      if (response.data.success) {
        const { accessToken, username, role } = response.data.data;
        
        localStorage.setItem('accessToken', accessToken);
        localStorage.setItem('username', username);
        localStorage.setItem('role', role);
        
        set({
          accessToken,
          username,
          role,
          isAuthenticated: true,
        });
        
        // Set up axios to use the new token for all requests
        axios.defaults.headers.common['Authorization'] = `Bearer ${accessToken}`;
        
        return true;
      }
      return false;
    } catch (error) {
      console.error('Token refresh error:', error);
      get().logout();
      return false;
    }
  },
}));

// Set up axios interceptor to handle token expiration
axios.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    
    // If the error is 401 (Unauthorized) and we haven't already tried to refresh the token
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      
      const refreshed = await useAuthStore.getState().refreshAccessToken();
      
      if (refreshed) {
        // Retry the original request with the new token
        originalRequest.headers['Authorization'] = `Bearer ${useAuthStore.getState().accessToken}`;
        return axios(originalRequest);
      }
    }
    
    return Promise.reject(error);
  }
);