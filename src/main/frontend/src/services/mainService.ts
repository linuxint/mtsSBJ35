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
      ssno?: string | null;
      sddate?: string | null;
      sdhour?: string | null;
      sdminute?: string | null;
      userno?: string | null;
      sstitle: string;
      fontcolor?: string;
      sdseq?: number | null;
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

    // 디버그 로깅 추가
    console.log('Main page API response:', response.data);

    // 응답 구조 확인
    if (response.data) {
      if (response.data.success === true && response.data.data) {
        // ApiResponse 래퍼 객체가 반환된 경우
        console.log('Unwrapping API response data:', response.data.data);
        return response.data.data;
      } else if (typeof response.data === 'object' && response.data.success === false) {
        // 오류 응답인 경우
        console.error('API returned error:', response.data.error);
        throw new Error(response.data.error?.message || 'Failed to fetch main page data');
      } else if (typeof response.data === 'object') {
        // 직접 데이터가 반환된 경우 (API가 래퍼 없이 직접 데이터를 반환하는 경우)
        console.warn('API returned unwrapped data:', response.data);

        // 필수 필드가 있는지 확인
        if (response.data.projectlistview || response.data.listview || response.data.noticeList || response.data.listtime) {
          return response.data as MainPageData;
        }
      }
    }

    // 응답이 없거나 예상치 못한 형식인 경우
    console.error('Invalid API response format:', response.data);
    throw new Error('Failed to fetch main page data: Invalid response format');
  } catch (error: any) {
    // 네트워크 오류 또는 서버 오류 처리
    console.error('Error fetching main page data:', error);

    // 더 구체적인 오류 메시지 제공
    if (error.response) {
      // 서버가 응답을 반환했지만 2xx 범위를 벗어난 상태 코드
      console.error('Server responded with error:', error.response.status, error.response.data);
      throw new Error(`Failed to fetch main page data: Server error (${error.response.status})`);
    } else if (error.request) {
      // 요청이 이루어졌지만 응답을 받지 못함
      console.error('No response received from server');
      throw new Error('Failed to fetch main page data: No response from server');
    } else {
      // 요청 설정 중에 오류 발생
      throw error;
    }
  }
};

export const fetchCalendarData = async (date?: string): Promise<CalendarData> => {
  try {
    const params: Record<string, string> = {};
    if (date) {
      params.date = date;
    }

    const response = await axios.get('/api/v1/main/calendar', { params });

    // 디버그 로깅 추가
    console.log('Calendar API response:', response.data);

    // 응답 구조 확인
    if (response.data) {
      if (response.data.success === true && response.data.data) {
        // ApiResponse 래퍼 객체가 반환된 경우
        console.log('Unwrapping API response data:', response.data.data);
        return response.data.data;
      } else if (typeof response.data === 'object' && response.data.success === false) {
        // 오류 응답인 경우
        console.error('API returned error:', response.data.error);
        throw new Error(response.data.error?.message || 'Failed to fetch calendar data');
      } else if (typeof response.data === 'object') {
        // 직접 데이터가 반환된 경우 (API가 래퍼 없이 직접 데이터를 반환하는 경우)
        console.warn('API returned unwrapped data:', response.data);

        // 필수 필드가 있는지 확인
        if (response.data.month !== undefined && response.data.week !== undefined && response.data.calenList) {
          return response.data as CalendarData;
        }
      }
    }

    // 응답이 없거나 예상치 못한 형식인 경우
    console.error('Invalid API response format:', response.data);
    throw new Error('Failed to fetch calendar data: Invalid response format');
  } catch (error: any) {
    // 네트워크 오류 또는 서버 오류 처리
    console.error('Error fetching calendar data:', error);

    // 더 구체적인 오류 메시지 제공
    if (error.response) {
      // 서버가 응답을 반환했지만 2xx 범위를 벗어난 상태 코드
      console.error('Server responded with error:', error.response.status, error.response.data);
      throw new Error(`Failed to fetch calendar data: Server error (${error.response.status})`);
    } else if (error.request) {
      // 요청이 이루어졌지만 응답을 받지 못함
      console.error('No response received from server');
      throw new Error('Failed to fetch calendar data: No response from server');
    } else {
      // 요청 설정 중에 오류 발생
      throw error;
    }
  }
};
