# 프론트엔드 메시지 컴포넌트 변경 계획

## 현재 상태
- JSP/Thymeleaf 기반의 단순한 alert 사용
- 기본 JavaScript alert() 함수 사용
- 스타일링이 제한적인 메시지 표시
- 일관성 없는 에러 처리

## 변경 계획 개요
React 컴포넌트 기반의 현대적이고 일관된 메시지 시스템을 구축하고, 사용자 경험을 개선하는 알림 컴포넌트를 구현합니다.

## 상세 변경 내용

### 알림(Alert) 컴포넌트 (Alert.tsx)
```typescript
import React from 'react';
import {
  Alert as MuiAlert,
  AlertProps as MuiAlertProps,
  AlertTitle,
  IconButton,
} from '@mui/material';
import { Close as CloseIcon } from '@mui/icons-material';

interface AlertProps extends Omit<MuiAlertProps, 'onClose'> {
  title?: string;
  onClose?: () => void;
}

export const Alert: React.FC<AlertProps> = ({
  title,
  children,
  onClose,
  ...props
}) => {
  return (
    <MuiAlert
      {...props}
      action={
        onClose ? (
          <IconButton
            aria-label="close"
            color="inherit"
            size="small"
            onClick={onClose}
          >
            <CloseIcon fontSize="inherit" />
          </IconButton>
        ) : undefined
      }
    >
      {title && <AlertTitle>{title}</AlertTitle>}
      {children}
    </MuiAlert>
  );
};
```

### 토스트(Toast) 컴포넌트 (Toast.tsx)
```typescript
import React from 'react';
import { Snackbar, Alert, AlertColor } from '@mui/material';

interface ToastProps {
  open: boolean;
  message: string;
  severity?: AlertColor;
  autoHideDuration?: number;
  onClose: () => void;
}

export const Toast: React.FC<ToastProps> = ({
  open,
  message,
  severity = 'info',
  autoHideDuration = 6000,
  onClose,
}) => {
  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'center' }}
    >
      <Alert onClose={onClose} severity={severity} sx={{ width: '100%' }}>
        {message}
      </Alert>
    </Snackbar>
  );
};
```

### 토스트 컨테이너 (ToastContainer.tsx)
```typescript
import React from 'react';
import { Toast } from './Toast';
import { useToast } from '@/hooks/useToast';

export const ToastContainer: React.FC = () => {
  const { toasts, removeToast } = useToast();

  return (
    <>
      {toasts.map((toast) => (
        <Toast
          key={toast.id}
          open={true}
          message={toast.message}
          severity={toast.severity}
          onClose={() => removeToast(toast.id)}
        />
      ))}
    </>
  );
};
```

### 토스트 컨텍스트 및 훅 (ToastContext.tsx)
```typescript
import React, { createContext, useContext, useReducer, useCallback } from 'react';
import { AlertColor } from '@mui/material';

interface Toast {
  id: string;
  message: string;
  severity: AlertColor;
}

interface ToastContextState {
  toasts: Toast[];
}

interface ToastContextValue extends ToastContextState {
  addToast: (message: string, severity?: AlertColor) => void;
  removeToast: (id: string) => void;
}

const ToastContext = createContext<ToastContextValue | undefined>(undefined);

interface ToastProviderProps {
  children: React.ReactNode;
}

export const ToastProvider: React.FC<ToastProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer(
    (state: ToastContextState, action: any) => {
      switch (action.type) {
        case 'ADD_TOAST':
          return {
            ...state,
            toasts: [...state.toasts, action.toast],
          };
        case 'REMOVE_TOAST':
          return {
            ...state,
            toasts: state.toasts.filter((toast) => toast.id !== action.id),
          };
        default:
          return state;
      }
    },
    { toasts: [] }
  );

  const addToast = useCallback((message: string, severity: AlertColor = 'info') => {
    const id = Math.random().toString(36).substr(2, 9);
    dispatch({
      type: 'ADD_TOAST',
      toast: { id, message, severity },
    });

    setTimeout(() => {
      dispatch({ type: 'REMOVE_TOAST', id });
    }, 6000);
  }, []);

  const removeToast = useCallback((id: string) => {
    dispatch({ type: 'REMOVE_TOAST', id });
  }, []);

  return (
    <ToastContext.Provider value={{ ...state, addToast, removeToast }}>
      {children}
      <ToastContainer />
    </ToastContext.Provider>
  );
};

export const useToast = () => {
  const context = useContext(ToastContext);
  if (context === undefined) {
    throw new Error('useToast must be used within a ToastProvider');
  }
  return context;
};
```

### 확인 대화상자 컴포넌트 (ConfirmDialog.tsx)
```typescript
import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
} from '@mui/material';

interface ConfirmDialogProps {
  open: boolean;
  title: string;
  message: string;
  confirmLabel?: string;
  cancelLabel?: string;
  onConfirm: () => void;
  onCancel: () => void;
}

export const ConfirmDialog: React.FC<ConfirmDialogProps> = ({
  open,
  title,
  message,
  confirmLabel = '확인',
  cancelLabel = '취소',
  onConfirm,
  onCancel,
}) => {
  return (
    <Dialog
      open={open}
      onClose={onCancel}
      aria-labelledby="confirm-dialog-title"
      aria-describedby="confirm-dialog-description"
    >
      <DialogTitle id="confirm-dialog-title">{title}</DialogTitle>
      <DialogContent>
        <DialogContentText id="confirm-dialog-description">
          {message}
        </DialogContentText>
      </DialogContent>
      <DialogActions>
        <Button onClick={onCancel} color="primary">
          {cancelLabel}
        </Button>
        <Button onClick={onConfirm} color="primary" variant="contained" autoFocus>
          {confirmLabel}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
```

### 사용 예시
```typescript
import React, { useState } from 'react';
import { Alert } from '@/components/common/Alert';
import { useToast } from '@/hooks/useToast';
import { ConfirmDialog } from '@/components/common/ConfirmDialog';

export const ExampleComponent: React.FC = () => {
  const { addToast } = useToast();
  const [isConfirmOpen, setIsConfirmOpen] = useState(false);

  const handleSuccess = () => {
    addToast('작업이 성공적으로 완료되었습니다.', 'success');
  };

  const handleError = () => {
    addToast('오류가 발생했습니다. 다시 시도해주세요.', 'error');
  };

  const handleDelete = () => {
    setIsConfirmOpen(true);
  };

  const handleConfirmDelete = () => {
    // 삭제 로직 실행
    setIsConfirmOpen(false);
    addToast('항목이 삭제되었습니다.', 'success');
  };

  return (
    <div>
      <Alert severity="info" title="알림">
        중요한 정보를 확인해주세요.
      </Alert>

      <button onClick={handleSuccess}>성공</button>
      <button onClick={handleError}>에러</button>
      <button onClick={handleDelete}>삭제</button>

      <ConfirmDialog
        open={isConfirmOpen}
        title="삭제 확인"
        message="정말로 이 항목을 삭제하시겠습니까?"
        onConfirm={handleConfirmDelete}
        onCancel={() => setIsConfirmOpen(false)}
      />
    </div>
  );
};
```

## 변경 이점
- 일관된 디자인의 메시지 컴포넌트
- 다양한 알림 유형 지원 (success, error, warning, info)
- 자동으로 사라지는 토스트 메시지
- 중앙 집중식 메시지 관리
- 재사용 가능한 컴포넌트
- 접근성 고려
- 애니메이션 효과로 사용자 경험 향상 