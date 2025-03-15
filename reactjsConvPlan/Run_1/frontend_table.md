# 프론트엔드 테이블 컴포넌트 변경 계획

## 현재 상태
- HTML table 태그 기반의 단순 테이블 구조
- 정렬, 필터링, 페이징 기능 부재
- 반응형 디자인 미지원
- 데이터 로딩 상태 처리 미흡
- 행 선택 기능 미구현

## 변경 계획 개요
Material-UI의 DataGrid를 기반으로 하는 재사용 가능한 테이블 컴포넌트를 구현하여, 정렬, 필터링, 페이징, 행 선택 등의 기능을 제공하고 반응형 디자인을 지원합니다.

## 상세 변경 내용

### 테이블 인터페이스 (types/table.ts)
```typescript
import { GridColDef, GridRowParams, GridSortModel } from '@mui/x-data-grid';

export interface TableColumn extends GridColDef {
  field: string;
  headerName: string;
  width?: number;
  flex?: number;
  sortable?: boolean;
  filterable?: boolean;
  renderCell?: (params: any) => React.ReactNode;
}

export interface TableProps<T> {
  columns: TableColumn[];
  rows: T[];
  loading?: boolean;
  pageSize?: number;
  rowCount?: number;
  page?: number;
  sortModel?: GridSortModel;
  selectionModel?: string[];
  checkboxSelection?: boolean;
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (pageSize: number) => void;
  onSortModelChange?: (model: GridSortModel) => void;
  onSelectionModelChange?: (selectionModel: string[]) => void;
  onRowClick?: (params: GridRowParams) => void;
}

export interface TableFilterState {
  field: string;
  operator: string;
  value: any;
}
```

### 기본 테이블 컴포넌트 (components/Table/Table.tsx)
```typescript
import React from 'react';
import { DataGrid, GridToolbar } from '@mui/x-data-grid';
import { Box, Paper } from '@mui/material';
import { TableProps } from '@/types/table';
import { TableSkeleton } from './TableSkeleton';
import { useTableState } from '@/hooks/useTableState';

export const Table = <T extends { id: string }>({
  columns,
  rows,
  loading = false,
  pageSize = 10,
  rowCount = 0,
  page = 0,
  sortModel = [],
  selectionModel = [],
  checkboxSelection = false,
  onPageChange,
  onPageSizeChange,
  onSortModelChange,
  onSelectionModelChange,
  onRowClick,
}: TableProps<T>) => {
  const {
    currentPage,
    currentPageSize,
    currentSortModel,
    currentSelectionModel,
    handlePageChange,
    handlePageSizeChange,
    handleSortModelChange,
    handleSelectionModelChange,
  } = useTableState({
    initialPage: page,
    initialPageSize: pageSize,
    initialSortModel: sortModel,
    initialSelectionModel: selectionModel,
    onPageChange,
    onPageSizeChange,
    onSortModelChange,
    onSelectionModelChange,
  });

  if (loading) {
    return <TableSkeleton columns={columns.length} rows={currentPageSize} />;
  }

  return (
    <Paper elevation={2}>
      <Box sx={{ width: '100%', height: 'auto' }}>
        <DataGrid
          rows={rows}
          columns={columns}
          pagination
          page={currentPage}
          pageSize={currentPageSize}
          rowCount={rowCount}
          sortModel={currentSortModel}
          selectionModel={currentSelectionModel}
          checkboxSelection={checkboxSelection}
          onPageChange={handlePageChange}
          onPageSizeChange={handlePageSizeChange}
          onSortModelChange={handleSortModelChange}
          onSelectionModelChange={handleSelectionModelChange}
          onRowClick={onRowClick}
          components={{
            Toolbar: GridToolbar,
          }}
          componentsProps={{
            toolbar: {
              showQuickFilter: true,
              quickFilterProps: { debounceMs: 500 },
            },
          }}
          disableColumnMenu={false}
          disableSelectionOnClick={!checkboxSelection}
          autoHeight
          sx={{
            '& .MuiDataGrid-row:hover': {
              backgroundColor: 'action.hover',
            },
          }}
        />
      </Box>
    </Paper>
  );
};
```

### 테이블 스켈레톤 컴포넌트 (components/Table/TableSkeleton.tsx)
```typescript
import React from 'react';
import { Skeleton, TableContainer, Table, TableHead, TableBody, TableRow, TableCell } from '@mui/material';

interface TableSkeletonProps {
  columns: number;
  rows: number;
}

export const TableSkeleton: React.FC<TableSkeletonProps> = ({ columns, rows }) => {
  return (
    <TableContainer>
      <Table>
        <TableHead>
          <TableRow>
            {Array.from({ length: columns }).map((_, index) => (
              <TableCell key={index}>
                <Skeleton variant="text" width={100} />
              </TableCell>
            ))}
          </TableRow>
        </TableHead>
        <TableBody>
          {Array.from({ length: rows }).map((_, rowIndex) => (
            <TableRow key={rowIndex}>
              {Array.from({ length: columns }).map((_, colIndex) => (
                <TableCell key={colIndex}>
                  <Skeleton variant="text" width={100} />
                </TableCell>
              ))}
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
```

### 테이블 상태 관리 훅 (hooks/useTableState.ts)
```typescript
import { useState, useCallback } from 'react';
import { GridSortModel } from '@mui/x-data-grid';

interface UseTableStateProps {
  initialPage: number;
  initialPageSize: number;
  initialSortModel: GridSortModel;
  initialSelectionModel: string[];
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (pageSize: number) => void;
  onSortModelChange?: (model: GridSortModel) => void;
  onSelectionModelChange?: (selectionModel: string[]) => void;
}

export const useTableState = ({
  initialPage,
  initialPageSize,
  initialSortModel,
  initialSelectionModel,
  onPageChange,
  onPageSizeChange,
  onSortModelChange,
  onSelectionModelChange,
}: UseTableStateProps) => {
  const [currentPage, setCurrentPage] = useState(initialPage);
  const [currentPageSize, setCurrentPageSize] = useState(initialPageSize);
  const [currentSortModel, setCurrentSortModel] = useState(initialSortModel);
  const [currentSelectionModel, setCurrentSelectionModel] = useState(initialSelectionModel);

  const handlePageChange = useCallback(
    (page: number) => {
      setCurrentPage(page);
      onPageChange?.(page);
    },
    [onPageChange]
  );

  const handlePageSizeChange = useCallback(
    (pageSize: number) => {
      setCurrentPageSize(pageSize);
      onPageSizeChange?.(pageSize);
    },
    [onPageSizeChange]
  );

  const handleSortModelChange = useCallback(
    (model: GridSortModel) => {
      setCurrentSortModel(model);
      onSortModelChange?.(model);
    },
    [onSortModelChange]
  );

  const handleSelectionModelChange = useCallback(
    (selectionModel: string[]) => {
      setCurrentSelectionModel(selectionModel);
      onSelectionModelChange?.(selectionModel);
    },
    [onSelectionModelChange]
  );

  return {
    currentPage,
    currentPageSize,
    currentSortModel,
    currentSelectionModel,
    handlePageChange,
    handlePageSizeChange,
    handleSortModelChange,
    handleSelectionModelChange,
  };
};
```

### 테이블 사용 예시 (pages/BoardList.tsx)
```typescript
import React from 'react';
import { Table } from '@/components/Table/Table';
import { TableColumn } from '@/types/table';
import { useBoardList } from '@/hooks/useBoardList';

interface Board {
  id: string;
  title: string;
  author: string;
  createdAt: string;
  views: number;
}

const columns: TableColumn[] = [
  {
    field: 'title',
    headerName: '제목',
    flex: 1,
    sortable: true,
  },
  {
    field: 'author',
    headerName: '작성자',
    width: 150,
    sortable: true,
  },
  {
    field: 'createdAt',
    headerName: '작성일',
    width: 180,
    sortable: true,
    valueFormatter: (params) => new Date(params.value).toLocaleString(),
  },
  {
    field: 'views',
    headerName: '조회수',
    width: 100,
    sortable: true,
    align: 'right',
  },
];

export const BoardList: React.FC = () => {
  const {
    data,
    loading,
    page,
    pageSize,
    totalCount,
    sortModel,
    handlePageChange,
    handlePageSizeChange,
    handleSortModelChange,
    handleRowClick,
  } = useBoardList();

  return (
    <Table<Board>
      columns={columns}
      rows={data}
      loading={loading}
      page={page}
      pageSize={pageSize}
      rowCount={totalCount}
      sortModel={sortModel}
      onPageChange={handlePageChange}
      onPageSizeChange={handlePageSizeChange}
      onSortModelChange={handleSortModelChange}
      onRowClick={handleRowClick}
    />
  );
};
```

## 변경 이점
- 정렬, 필터링, 페이징 기능 제공
- 반응형 디자인 지원
- 데이터 로딩 상태 표시
- 행 선택 기능 구현
- 재사용 가능한 컴포넌트 구조
- 타입 안정성 확보
- 커스터마이징 용이
- 사용자 경험 향상 