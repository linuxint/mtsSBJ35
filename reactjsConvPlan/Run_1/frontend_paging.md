# 프론트엔드 페이징 컴포넌트 변경 계획

## 현재 상태
- JSP/Thymeleaf 기반의 단순한 페이징 처리
- 기본적인 이전/다음 페이지 네비게이션
- 서버 사이드 페이징에 의존
- 페이지 크기 고정

## 변경 계획 개요
React 컴포넌트 기반의 유연하고 재사용 가능한 페이징 시스템을 구축하고, 클라이언트 사이드 상태 관리와 서버 사이드 페이징을 효율적으로 통합합니다.

## 상세 변경 내용

### 페이지네이션 UI 컴포넌트 (Pagination.tsx)
```typescript
import React from 'react';
import {
  Pagination as MuiPagination,
  PaginationItem,
  Select,
  MenuItem,
  Box,
  Typography,
} from '@mui/material';
import { useSearchParams } from 'react-router-dom';

interface PaginationProps {
  total: number;
  page: number;
  pageSize: number;
  onPageChange: (page: number) => void;
  onPageSizeChange: (pageSize: number) => void;
}

export const Pagination: React.FC<PaginationProps> = ({
  total,
  page,
  pageSize,
  onPageChange,
  onPageSizeChange,
}) => {
  const totalPages = Math.ceil(total / pageSize);
  const [searchParams, setSearchParams] = useSearchParams();

  const handlePageChange = (event: React.ChangeEvent<unknown>, value: number) => {
    onPageChange(value);
    searchParams.set('page', value.toString());
    setSearchParams(searchParams);
  };

  const handlePageSizeChange = (event: React.ChangeEvent<{ value: unknown }>) => {
    const newPageSize = event.target.value as number;
    onPageSizeChange(newPageSize);
    searchParams.set('pageSize', newPageSize.toString());
    searchParams.set('page', '1');
    setSearchParams(searchParams);
  };

  return (
    <Box
      sx={{
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        gap: 2,
        my: 2,
      }}
    >
      <Box sx={{ display: 'flex', alignItems: 'center' }}>
        <Typography variant="body2" sx={{ mr: 1 }}>
          페이지당 항목:
        </Typography>
        <Select
          value={pageSize}
          onChange={handlePageSizeChange}
          size="small"
          sx={{ minWidth: 80 }}
        >
          <MenuItem value={10}>10</MenuItem>
          <MenuItem value={20}>20</MenuItem>
          <MenuItem value={50}>50</MenuItem>
          <MenuItem value={100}>100</MenuItem>
        </Select>
      </Box>

      <MuiPagination
        page={page}
        count={totalPages}
        onChange={handlePageChange}
        color="primary"
        size="large"
        showFirstButton
        showLastButton
        renderItem={(item) => (
          <PaginationItem
            {...item}
            sx={{
              '&.Mui-selected': {
                backgroundColor: 'primary.main',
                color: 'white',
                '&:hover': {
                  backgroundColor: 'primary.dark',
                },
              },
            }}
          />
        )}
      />

      <Typography variant="body2" sx={{ ml: 2 }}>
        총 {total}개 항목 중 {(page - 1) * pageSize + 1}-
        {Math.min(page * pageSize, total)}
      </Typography>
    </Box>
  );
};
```

### 페이지네이션 커스텀 훅 (usePagination.ts)
```typescript
import { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';

interface UsePaginationProps {
  defaultPage?: number;
  defaultPageSize?: number;
  total: number;
}

interface UsePaginationReturn {
  page: number;
  pageSize: number;
  totalPages: number;
  handlePageChange: (page: number) => void;
  handlePageSizeChange: (pageSize: number) => void;
  offset: number;
  limit: number;
}

export const usePagination = ({
  defaultPage = 1,
  defaultPageSize = 10,
  total,
}: UsePaginationProps): UsePaginationReturn => {
  const [searchParams, setSearchParams] = useSearchParams();
  const [page, setPage] = useState(
    parseInt(searchParams.get('page') || defaultPage.toString())
  );
  const [pageSize, setPageSize] = useState(
    parseInt(searchParams.get('pageSize') || defaultPageSize.toString())
  );

  useEffect(() => {
    const urlPage = parseInt(searchParams.get('page') || defaultPage.toString());
    const urlPageSize = parseInt(
      searchParams.get('pageSize') || defaultPageSize.toString()
    );

    if (urlPage !== page) {
      setPage(urlPage);
    }
    if (urlPageSize !== pageSize) {
      setPageSize(urlPageSize);
    }
  }, [searchParams]);

  const totalPages = Math.ceil(total / pageSize);

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
    searchParams.set('page', newPage.toString());
    setSearchParams(searchParams);
  };

  const handlePageSizeChange = (newPageSize: number) => {
    setPageSize(newPageSize);
    setPage(1);
    searchParams.set('pageSize', newPageSize.toString());
    searchParams.set('page', '1');
    setSearchParams(searchParams);
  };

  return {
    page,
    pageSize,
    totalPages,
    handlePageChange,
    handlePageSizeChange,
    offset: (page - 1) * pageSize,
    limit: pageSize,
  };
};
```

### 페이지네이션 사용 예시 (BoardList.tsx)
```typescript
import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Pagination } from '@/components/common/Pagination';
import { usePagination } from '@/hooks/usePagination';
import { boardApi } from '@/api/boardApi';

export const BoardList: React.FC = () => {
  const {
    page,
    pageSize,
    handlePageChange,
    handlePageSizeChange,
    offset,
    limit,
  } = usePagination({
    defaultPage: 1,
    defaultPageSize: 10,
    total: 0,
  });

  const { data, isLoading } = useQuery(
    ['boards', page, pageSize],
    () => boardApi.getBoards({ offset, limit }),
    {
      keepPreviousData: true,
    }
  );

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  return (
    <div>
      {/* 게시글 목록 렌더링 */}
      <Pagination
        total={data.total}
        page={page}
        pageSize={pageSize}
        onPageChange={handlePageChange}
        onPageSizeChange={handlePageSizeChange}
      />
    </div>
  );
};
```

### 무한 스크롤 컴포넌트 (InfiniteScroll.tsx)
```typescript
import React, { useEffect, useRef, useCallback } from 'react';
import { Box, CircularProgress } from '@mui/material';

interface InfiniteScrollProps {
  hasMore: boolean;
  isLoading: boolean;
  onLoadMore: () => void;
  children: React.ReactNode;
}

export const InfiniteScroll: React.FC<InfiniteScrollProps> = ({
  hasMore,
  isLoading,
  onLoadMore,
  children,
}) => {
  const observer = useRef<IntersectionObserver | null>(null);
  const loadMoreRef = useRef<HTMLDivElement>(null);

  const handleObserver = useCallback(
    (entries: IntersectionObserverEntry[]) => {
      const [target] = entries;
      if (target.isIntersecting && hasMore && !isLoading) {
        onLoadMore();
      }
    },
    [hasMore, isLoading, onLoadMore]
  );

  useEffect(() => {
    const options = {
      root: null,
      rootMargin: '20px',
      threshold: 1.0,
    };

    observer.current = new IntersectionObserver(handleObserver, options);

    if (loadMoreRef.current) {
      observer.current.observe(loadMoreRef.current);
    }

    return () => {
      if (observer.current) {
        observer.current.disconnect();
      }
    };
  }, [handleObserver]);

  return (
    <>
      {children}
      <Box
        ref={loadMoreRef}
        sx={{
          display: 'flex',
          justifyContent: 'center',
          py: 2,
        }}
      >
        {isLoading && <CircularProgress />}
      </Box>
    </>
  );
};
```

### 무한 스크롤 사용 예시 (BoardInfiniteList.tsx)
```typescript
import React from 'react';
import { useInfiniteQuery } from '@tanstack/react-query';
import { InfiniteScroll } from '@/components/common/InfiniteScroll';
import { boardApi } from '@/api/boardApi';

export const BoardInfiniteList: React.FC = () => {
  const {
    data,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    isLoading,
  } = useInfiniteQuery(
    ['boards-infinite'],
    ({ pageParam = 0 }) =>
      boardApi.getBoards({ offset: pageParam, limit: 20 }),
    {
      getNextPageParam: (lastPage) =>
        lastPage.hasMore ? lastPage.nextOffset : undefined,
    }
  );

  if (isLoading) {
    return <div>로딩 중...</div>;
  }

  return (
    <InfiniteScroll
      hasMore={!!hasNextPage}
      isLoading={isFetchingNextPage}
      onLoadMore={() => fetchNextPage()}
    >
      {data.pages.map((page, i) => (
        <React.Fragment key={i}>
          {/* 게시글 목록 렌더링 */}
        </React.Fragment>
      ))}
    </InfiniteScroll>
  );
};
```

## 변경 이점
- 재사용 가능한 페이징 컴포넌트
- URL 기반의 페이지 상태 관리
- 유연한 페이지 크기 조정
- 무한 스크롤 지원
- React Query를 활용한 데이터 캐싱
- SEO 친화적인 URL 구조
- 사용자 경험 향상 