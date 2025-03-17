import axios from 'axios';

export interface MenuItem {
  mnuNo: string;
  mnuParent: string | null;
  mnuType: string;
  mnuNm: string;
  mnuMsgCd: string;
  mnuDesc: string;
  mnuTarget: string;
  mnuFilenm: string;
  mnuImgpath: string;
  mnuCustom: string;
  mnuDesktop: string;
  mnuMobile: string;
  mnuOrder: string;
  mnuCertType: string;
  mnuExtnConnYn: string;
  mnuStartHour: string;
  mnuEndHour: string;
  deleteflag: string;
  children?: MenuItem[];
}

/**
 * 메뉴 데이터를 계층 구조로 변환하는 함수
 * 
 * @param menuItems 평면적인 메뉴 아이템 배열
 * @returns 계층 구조로 변환된 메뉴 아이템 배열
 */
export const buildMenuTree = (menuItems: MenuItem[]): MenuItem[] => {
  const menuMap: Record<string, MenuItem> = {};
  const rootItems: MenuItem[] = [];

  // 먼저 모든 메뉴 아이템을 맵에 저장
  menuItems.forEach(item => {
    menuMap[item.mnuNo] = { ...item, children: [] };
  });

  // 부모-자식 관계 설정
  menuItems.forEach(item => {
    if (item.mnuParent && menuMap[item.mnuParent]) {
      // 부모가 있는 경우 부모의 children 배열에 추가
      if (!menuMap[item.mnuParent].children) {
        menuMap[item.mnuParent].children = [];
      }
      menuMap[item.mnuParent].children!.push(menuMap[item.mnuNo]);
    } else {
      // 부모가 없거나 부모를 찾을 수 없는 경우 루트 아이템으로 추가
      rootItems.push(menuMap[item.mnuNo]);
    }
  });

  // 각 레벨에서 mnuOrder에 따라 정렬
  const sortMenuItems = (items: MenuItem[]): MenuItem[] => {
    return items
      .sort((a, b) => parseInt(a.mnuOrder) - parseInt(b.mnuOrder))
      .map(item => {
        if (item.children && item.children.length > 0) {
          item.children = sortMenuItems(item.children);
        }
        return item;
      });
  };

  return sortMenuItems(rootItems);
};

/**
 * TreeVO 형식의 데이터를 MenuItem 형식으로 변환하는 함수
 * 
 * @param treeData TreeVO 형식의 데이터
 * @returns MenuItem 형식으로 변환된 데이터
 */
export const convertTreeToMenuItem = (treeData: any[]): MenuItem[] => {
  return treeData.map(item => ({
    mnuNo: item.key,
    mnuParent: item.parent,
    mnuType: '',
    mnuNm: item.title,
    mnuMsgCd: '',
    mnuDesc: '',
    mnuTarget: '',
    mnuFilenm: '',
    mnuImgpath: '',
    mnuCustom: '',
    mnuDesktop: '',
    mnuMobile: '',
    mnuOrder: '0',
    mnuCertType: '',
    mnuExtnConnYn: '',
    mnuStartHour: '',
    mnuEndHour: '',
    deleteflag: 'N'
  }));
};

/**
 * 메뉴 데이터를 가져오는 함수
 * 
 * @returns 계층 구조로 정리된 메뉴 데이터
 */
export const fetchMenuData = async (): Promise<MenuItem[]> => {
  try {
    const response = await axios.get('/api/v1/menu/list');

    if (response.status === 200) {
      // API 응답 구조 확인
      if (response.data && Array.isArray(response.data)) {
        // TreeVO 형식의 데이터를 MenuItem 형식으로 변환
        const menuItems = convertTreeToMenuItem(response.data);
        return buildMenuTree(menuItems);
      } else if (response.data && response.data.success && Array.isArray(response.data.data)) {
        // ApiResponse 래퍼 객체가 반환된 경우
        const menuItems = convertTreeToMenuItem(response.data.data);
        return buildMenuTree(menuItems);
      } else {
        throw new Error('Unexpected response format');
      }
    }

    throw new Error('Failed to fetch menu data');
  } catch (error) {
    console.error('Error fetching menu data:', error);
    throw error;
  }
};
