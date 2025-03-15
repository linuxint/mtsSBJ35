# 프론트엔드 모달 컴포넌트 변경 계획

## 현재 상태
- 단순한 alert/confirm 다이얼로그 사용
- 모달 상태 관리 미흡
- 재사용성 부족
- 접근성 고려 부족
- 애니메이션 효과 미적용

## 변경 계획 개요
Material-UI의 Dialog 컴포넌트를 기반으로 재사용 가능한 모달 컴포넌트를 구현하고, 상태 관리, 접근성, 애니메이션 효과 등을 개선합니다.

## 상세 변경 내용

### 모달 인터페이스 (types/modal.ts)
```typescript
import { ReactNode } from 'react';

export interface ModalProps {
  open: boolean;
  onClose: () => void;
  title?: string;
  children: ReactNode;
  maxWidth?: 'xs' | 'sm' | 'md' | 'lg' | 'xl';
  fullWidth?: boolean;
  disableBackdropClick?: boolean;
  disableEscapeKeyDown?: boolean;
}

export interface ConfirmModalProps extends Omit<ModalProps, 'children'> {
  message: string;
  confirmText?: string;
  cancelText?: string;
  onConfirm: () => void;
  onCancel?: () => void;
  severity?: 'info' | 'success' | 'warning' | 'error';
}

export interface FormModalProps<T> extends Omit<ModalProps, 'children'> {
  onSubmit: (data: T) => Promise<void>;
  defaultValues?: T;
  children: ReactNode;
}
```

### 기본 모달 컴포넌트 (components/Modal/Modal.tsx)
```typescript
import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  useTheme,
  useMediaQuery,
} from '@mui/material';
import { Close } from '@mui/icons-material';
import { ModalProps } from '@/types/modal';

export const Modal: React.FC<ModalProps> = ({
  open,
  onClose,
  title,
  children,
  maxWidth = 'sm',
  fullWidth = true,
  disableBackdropClick = false,
  disableEscapeKeyDown = false,
}) => {
  const theme = useTheme();
  const fullScreen = useMediaQuery(theme.breakpoints.down('sm'));

  const handleClose = (event: {}, reason: 'backdropClick' | 'escapeKeyDown') => {
    if (
      (reason === 'backdropClick' && disableBackdropClick) ||
      (reason === 'escapeKeyDown' && disableEscapeKeyDown)
    ) {
      return;
    }
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      fullScreen={fullScreen}
      aria-labelledby="modal-title"
    >
      {title && (
        <DialogTitle
          id="modal-title"
          sx={{
            m: 0,
            p: 2,
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
          }}
        >
          {title}
          <IconButton
            aria-label="close"
            onClick={() => onClose()}
            sx={{
              position: 'absolute',
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            <Close />
          </IconButton>
        </DialogTitle>
      )}
      <DialogContent dividers>{children}</DialogContent>
    </Dialog>
  );
};
```

### 확인 모달 컴포넌트 (components/Modal/ConfirmModal.tsx)
```typescript
import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions,
  Button,
  Alert,
} from '@mui/material';
import { ConfirmModalProps } from '@/types/modal';

export const ConfirmModal: React.FC<ConfirmModalProps> = ({
  open,
  onClose,
  title,
  message,
  confirmText = '확인',
  cancelText = '취소',
  onConfirm,
  onCancel,
  severity = 'info',
  maxWidth = 'xs',
  fullWidth = true,
}) => {
  const handleCancel = () => {
    onCancel?.();
    onClose();
  };

  const handleConfirm = () => {
    onConfirm();
    onClose();
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      aria-labelledby="confirm-dialog-title"
      aria-describedby="confirm-dialog-description"
    >
      {title && <DialogTitle id="confirm-dialog-title">{title}</DialogTitle>}
      <DialogContent>
        <Alert severity={severity} sx={{ mb: 2 }}>
          <DialogContentText id="confirm-dialog-description">
            {message}
          </DialogContentText>
        </Alert>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleCancel} color="inherit">
          {cancelText}
        </Button>
        <Button
          onClick={handleConfirm}
          color={severity === 'error' ? 'error' : 'primary'}
          variant="contained"
          autoFocus
        >
          {confirmText}
        </Button>
      </DialogActions>
    </Dialog>
  );
};
```

### 폼 모달 컴포넌트 (components/Modal/FormModal.tsx)
```typescript
import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  CircularProgress,
} from '@mui/material';
import { Form } from '@/components/Form/Form';
import { FormModalProps } from '@/types/modal';

export const FormModal = <T extends object>({
  open,
  onClose,
  title,
  onSubmit,
  defaultValues,
  children,
  maxWidth = 'sm',
  fullWidth = true,
}: FormModalProps<T>) => {
  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth={maxWidth}
      fullWidth={fullWidth}
      aria-labelledby="form-dialog-title"
    >
      <Form
        defaultValues={defaultValues}
        onSubmit={async (data) => {
          await onSubmit(data);
          onClose();
        }}
      >
        {({ formState: { isSubmitting, isDirty, isValid } }) => (
          <>
            {title && (
              <DialogTitle id="form-dialog-title">{title}</DialogTitle>
            )}
            <DialogContent dividers>{children}</DialogContent>
            <DialogActions>
              <Button onClick={onClose} color="inherit">
                취소
              </Button>
              <Button
                type="submit"
                variant="contained"
                disabled={!isDirty || !isValid || isSubmitting}
                startIcon={
                  isSubmitting && (
                    <CircularProgress size={20} color="inherit" />
                  )
                }
              >
                저장
              </Button>
            </DialogActions>
          </>
        )}
      </Form>
    </Dialog>
  );
};
```

### 모달 상태 관리 훅 (hooks/useModal.ts)
```typescript
import { useState, useCallback } from 'react';

export const useModal = (initialState = false) => {
  const [isOpen, setIsOpen] = useState(initialState);

  const open = useCallback(() => {
    setIsOpen(true);
  }, []);

  const close = useCallback(() => {
    setIsOpen(false);
  }, []);

  const toggle = useCallback(() => {
    setIsOpen((prev) => !prev);
  }, []);

  return {
    isOpen,
    open,
    close,
    toggle,
  };
};
```

### 모달 사용 예시 (pages/BoardList.tsx)
```typescript
import React from 'react';
import { Button } from '@mui/material';
import { Modal } from '@/components/Modal/Modal';
import { ConfirmModal } from '@/components/Modal/ConfirmModal';
import { FormModal } from '@/components/Modal/FormModal';
import { useModal } from '@/hooks/useModal';
import { TextField } from '@/components/Form/fields/TextField';

export const BoardList: React.FC = () => {
  const detailModal = useModal();
  const deleteModal = useModal();
  const createModal = useModal();

  const handleDelete = async () => {
    // 삭제 처리
  };

  const handleCreate = async (data: any) => {
    // 생성 처리
  };

  return (
    <>
      <Button onClick={createModal.open}>글쓰기</Button>

      <Modal
        open={detailModal.isOpen}
        onClose={detailModal.close}
        title="게시글 상세"
      >
        {/* 상세 내용 */}
      </Modal>

      <ConfirmModal
        open={deleteModal.isOpen}
        onClose={deleteModal.close}
        title="게시글 삭제"
        message="정말 삭제하시겠습니까?"
        severity="error"
        onConfirm={handleDelete}
      />

      <FormModal
        open={createModal.isOpen}
        onClose={createModal.close}
        title="게시글 작성"
        onSubmit={handleCreate}
      >
        <TextField name="title" label="제목" required />
        <TextField
          name="content"
          label="내용"
          required
          multiline
          rows={4}
        />
      </FormModal>
    </>
  );
};
```

## 변경 이점
- 재사용 가능한 모달 컴포넌트 구조
- 일관된 사용자 경험 제공
- 접근성 향상
- 반응형 디자인 지원
- 애니메이션 효과 적용
- 상태 관리 용이
- 타입 안정성 확보
- 커스터마이징 용이 