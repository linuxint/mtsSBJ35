# 프론트엔드 폼 컴포넌트 변경 계획

## 현재 상태
- HTML form 태그 기반의 단순 폼 구조
- 유효성 검사 로직 부재
- 비동기 제출 처리 미흡
- 에러 메시지 표시 기능 부재
- 폼 상태 관리 미흡

## 변경 계획 개요
React Hook Form과 Material-UI를 활용하여 재사용 가능한 폼 컴포넌트를 구현하고, Zod를 사용한 유효성 검사, 비동기 제출 처리, 에러 메시지 표시 등의 기능을 제공합니다.

## 상세 변경 내용

### 폼 인터페이스 (types/form.ts)
```typescript
import { z } from 'zod';
import { UseFormReturn } from 'react-hook-form';

export interface FormProps<T extends z.ZodType> {
  defaultValues?: z.infer<T>;
  schema: T;
  onSubmit: (data: z.infer<T>) => Promise<void>;
  children: (methods: UseFormReturn<z.infer<T>>) => React.ReactNode;
}

export interface FormFieldProps {
  name: string;
  label: string;
  required?: boolean;
  disabled?: boolean;
  fullWidth?: boolean;
  error?: boolean;
  helperText?: string;
}

export interface SelectFieldProps extends FormFieldProps {
  options: Array<{
    value: string | number;
    label: string;
  }>;
  multiple?: boolean;
}

export interface DateFieldProps extends FormFieldProps {
  minDate?: Date;
  maxDate?: Date;
  disablePast?: boolean;
  disableFuture?: boolean;
}

export interface FileFieldProps extends FormFieldProps {
  accept?: string;
  multiple?: boolean;
  maxSize?: number;
  onFileSelect?: (files: File[]) => void;
}
```

### 기본 폼 컴포넌트 (components/Form/Form.tsx)
```typescript
import React from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { FormProps } from '@/types/form';
import { useFormSubmit } from '@/hooks/useFormSubmit';

export const Form = <T extends z.ZodType>({
  defaultValues,
  schema,
  onSubmit,
  children,
}: FormProps<T>) => {
  const methods = useForm<z.infer<T>>({
    defaultValues,
    resolver: zodResolver(schema),
    mode: 'onChange',
  });

  const { handleSubmit, isSubmitting, submitError } = useFormSubmit<z.infer<T>>(
    onSubmit
  );

  return (
    <form onSubmit={methods.handleSubmit(handleSubmit)} noValidate>
      {children(methods)}
      {submitError && (
        <div className="error-message">{submitError.message}</div>
      )}
    </form>
  );
};
```

### 폼 필드 컴포넌트들 (components/Form/fields/*)

#### 텍스트 필드 (TextField.tsx)
```typescript
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';
import { TextField as MuiTextField } from '@mui/material';
import { FormFieldProps } from '@/types/form';

export const TextField: React.FC<FormFieldProps> = ({
  name,
  label,
  required = false,
  disabled = false,
  fullWidth = true,
}) => {
  const {
    control,
    formState: { errors },
  } = useFormContext();

  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <MuiTextField
          {...field}
          label={label}
          required={required}
          disabled={disabled}
          fullWidth={fullWidth}
          error={!!errors[name]}
          helperText={errors[name]?.message as string}
        />
      )}
    />
  );
};
```

#### 선택 필드 (SelectField.tsx)
```typescript
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';
import {
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  FormHelperText,
} from '@mui/material';
import { SelectFieldProps } from '@/types/form';

export const SelectField: React.FC<SelectFieldProps> = ({
  name,
  label,
  options,
  required = false,
  disabled = false,
  fullWidth = true,
  multiple = false,
}) => {
  const {
    control,
    formState: { errors },
  } = useFormContext();

  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <FormControl
          fullWidth={fullWidth}
          error={!!errors[name]}
          required={required}
          disabled={disabled}
        >
          <InputLabel>{label}</InputLabel>
          <Select
            {...field}
            multiple={multiple}
            label={label}
          >
            {options.map((option) => (
              <MenuItem key={option.value} value={option.value}>
                {option.label}
              </MenuItem>
            ))}
          </Select>
          {errors[name] && (
            <FormHelperText>{errors[name]?.message as string}</FormHelperText>
          )}
        </FormControl>
      )}
    />
  );
};
```

#### 날짜 필드 (DateField.tsx)
```typescript
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';
import { DatePicker } from '@mui/x-date-pickers';
import { TextField } from '@mui/material';
import { DateFieldProps } from '@/types/form';

export const DateField: React.FC<DateFieldProps> = ({
  name,
  label,
  required = false,
  disabled = false,
  minDate,
  maxDate,
  disablePast = false,
  disableFuture = false,
}) => {
  const {
    control,
    formState: { errors },
  } = useFormContext();

  return (
    <Controller
      name={name}
      control={control}
      render={({ field }) => (
        <DatePicker
          {...field}
          label={label}
          disabled={disabled}
          minDate={minDate}
          maxDate={maxDate}
          disablePast={disablePast}
          disableFuture={disableFuture}
          renderInput={(params) => (
            <TextField
              {...params}
              required={required}
              error={!!errors[name]}
              helperText={errors[name]?.message as string}
              fullWidth
            />
          )}
        />
      )}
    />
  );
};
```

#### 파일 업로드 필드 (FileField.tsx)
```typescript
import React, { useCallback } from 'react';
import { Controller, useFormContext } from 'react-hook-form';
import { Button, FormHelperText, Box } from '@mui/material';
import { CloudUpload } from '@mui/icons-material';
import { FileFieldProps } from '@/types/form';

export const FileField: React.FC<FileFieldProps> = ({
  name,
  label,
  accept,
  multiple = false,
  maxSize,
  onFileSelect,
  required = false,
  disabled = false,
}) => {
  const {
    control,
    formState: { errors },
  } = useFormContext();

  const validateFiles = useCallback(
    (files: FileList) => {
      if (!files.length) return true;
      if (maxSize) {
        for (let i = 0; i < files.length; i++) {
          if (files[i].size > maxSize) {
            return '파일 크기가 제한을 초과했습니다.';
          }
        }
      }
      return true;
    },
    [maxSize]
  );

  return (
    <Controller
      name={name}
      control={control}
      render={({ field: { onChange, value } }) => (
        <Box>
          <input
            type="file"
            accept={accept}
            multiple={multiple}
            onChange={(e) => {
              const files = Array.from(e.target.files || []);
              const validation = validateFiles(e.target.files as FileList);
              if (validation === true) {
                onChange(files);
                onFileSelect?.(files);
              }
            }}
            style={{ display: 'none' }}
            id={`file-input-${name}`}
            disabled={disabled}
          />
          <label htmlFor={`file-input-${name}`}>
            <Button
              variant="outlined"
              component="span"
              startIcon={<CloudUpload />}
              disabled={disabled}
            >
              {label}
            </Button>
          </label>
          {value && Array.isArray(value) && (
            <Box mt={1}>
              {value.map((file: File, index: number) => (
                <div key={index}>{file.name}</div>
              ))}
            </Box>
          )}
          {errors[name] && (
            <FormHelperText error>{errors[name]?.message as string}</FormHelperText>
          )}
        </Box>
      )}
    />
  );
};
```

### 폼 제출 훅 (hooks/useFormSubmit.ts)
```typescript
import { useState, useCallback } from 'react';

interface SubmitError {
  message: string;
}

export const useFormSubmit = <T>(
  onSubmit: (data: T) => Promise<void>
) => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState<SubmitError | null>(null);

  const handleSubmit = useCallback(
    async (data: T) => {
      setIsSubmitting(true);
      setSubmitError(null);
      try {
        await onSubmit(data);
      } catch (error) {
        setSubmitError({
          message: error instanceof Error ? error.message : '제출 중 오류가 발생했습니다.',
        });
      } finally {
        setIsSubmitting(false);
      }
    },
    [onSubmit]
  );

  return {
    handleSubmit,
    isSubmitting,
    submitError,
  };
};
```

### 폼 사용 예시 (pages/BoardWrite.tsx)
```typescript
import React from 'react';
import { z } from 'zod';
import { Stack } from '@mui/material';
import { Form } from '@/components/Form/Form';
import { TextField } from '@/components/Form/fields/TextField';
import { FileField } from '@/components/Form/fields/FileField';
import { useCreateBoard } from '@/hooks/useCreateBoard';

const schema = z.object({
  title: z.string().min(1, '제목을 입력해주세요.'),
  content: z.string().min(1, '내용을 입력해주세요.'),
  attachments: z.array(z.custom<File>()).optional(),
});

type BoardFormData = z.infer<typeof schema>;

export const BoardWrite: React.FC = () => {
  const { createBoard, isLoading } = useCreateBoard();

  const handleSubmit = async (data: BoardFormData) => {
    await createBoard(data);
  };

  return (
    <Form<typeof schema>
      schema={schema}
      onSubmit={handleSubmit}
      defaultValues={{
        title: '',
        content: '',
        attachments: [],
      }}
    >
      {({ formState: { isValid } }) => (
        <Stack spacing={2}>
          <TextField
            name="title"
            label="제목"
            required
          />
          <TextField
            name="content"
            label="내용"
            required
            multiline
            rows={4}
          />
          <FileField
            name="attachments"
            label="첨부파일"
            multiple
            accept=".pdf,.doc,.docx,.xls,.xlsx"
            maxSize={5 * 1024 * 1024}
          />
          <button type="submit" disabled={!isValid || isLoading}>
            저장
          </button>
        </Stack>
      )}
    </Form>
  );
};
```

## 변경 이점
- 재사용 가능한 폼 컴포넌트 구조
- 타입 안정성 확보
- 유효성 검사 기능 제공
- 비동기 제출 처리
- 에러 메시지 표시
- 폼 상태 관리 용이
- 다양한 입력 필드 지원
- 사용자 경험 향상 