import axios from 'axios';

export interface MainPageData {
  searchVO?: {
    searchKeyword?: string;
  };
  projectlistview?: Array<{
    id: number;
    title: string;
    [key: string]: any;
  }>;
  listview?: Array<{
    id: number;
    title: string;
    [key: string]: any;
  }>;
  noticeList?: Array<{
    id: number;
    title: string;
    [key: string]: any;
  }>;
  listtime?: Array<{
    id: number;
    content: string;
    [key: string]: any;
  }>;
  calenList?: Array<{
    date: string;
    events: Array<any>;
    [key: string]: any;
  }>;
  month?: number;
  week?: number;
  preWeek?: string;
  nextWeek?: string;
  [key: string]: any;
}

export interface CalendarData {
  month: number;
  week: number;
  calenList: Array<{
    date: string;
    istoday: boolean;
    list: Array<{
      id: number;
      title: string;
      time: string;
      [key: string]: any;
    }>;
    [key: string]: any;
  }>;
  preWeek: string;
  nextWeek: string;
  [key: string]: any;
}

export const fetchMainPageData = async (searchKeyword?: string): Promise<MainPageData> => {
  try {
    const params: Record<string, string> = {};
    if (searchKeyword) {
      params.searchKeyword = searchKeyword;
    }

    const response = await axios.get('/api/v1/main', { params });

    // 응답 구조 확인
    if (response.data) {
      if (response.data.success && response.data.data) {
        // ApiResponse 래퍼 객체가 반환된 경우
        return response.data.data;
      } else if (typeof response.data === 'object' && !response.data.success) {
        // 오류 응답인 경우
        throw new Error(response.data.error?.message || 'Failed to fetch main page data');
      } else {
        // 직접 데이터가 반환된 경우
        return response.data as MainPageData;
      }
    }

    throw new Error('Failed to fetch main page data');
  } catch (error) {
    console.error('Error fetching main page data:', error);
    throw error;
  }
};

export const fetchCalendarData = async (date?: string): Promise<CalendarData> => {
  try {
    const params: Record<string, string> = {};
    if (date) {
      params.date = date;
    }

    const response = await axios.get('/api/v1/main/calendar', { params });

    // 응답 구조 확인
    if (response.data) {
      if (response.data.success && response.data.data) {
        // ApiResponse 래퍼 객체가 반환된 경우
        return response.data.data;
      } else if (typeof response.data === 'object' && !response.data.success) {
        // 오류 응답인 경우
        throw new Error(response.data.error?.message || 'Failed to fetch calendar data');
      } else {
        // 직접 데이터가 반환된 경우
        return response.data as CalendarData;
      }
    }

    throw new Error('Failed to fetch calendar data');
  } catch (error) {
    console.error('Error fetching calendar data:', error);
    throw error;
  }
};
