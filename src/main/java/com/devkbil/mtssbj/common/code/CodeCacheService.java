package com.devkbil.mtssbj.common.code;

import jakarta.annotation.PostConstruct;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * CodeCacheService 클래스는 코드 그룹 및 상세 코드 정보를 관리하고 캐싱하는 역할을 담당합니다.
 * 이 서비스는 CodeCacheDAO와 상호작용하여 데이터베이스에서 코드 데이터를 가져오며,
 * 메모리 내 리스트를 유지하여 효율적인 조회를 제공합니다.
 *
 * 코드 그룹 ID와 상세 코드 ID를 기반으로 코드 이름과 목록을 조회할 수 있는 다양한 기능을 제공합니다.
 * 또한 초기화 시 메모리에 코드를 등록하고, 캐시된 데이터를 초기화하는 기능도 포함되어 있습니다.
 *
 * 캐시 리스트를 갱신하고 재초기화할 때 스레드 안정성이 보장됩니다.
 */
@Slf4j
@Service("CodeCacheService")
@RequiredArgsConstructor
public class CodeCacheService { //extends EgovAbstractServiceImpl

    public final CodeCacheDAO codeCacheDAO;

    private static final List<Map> codeGroup = new ArrayList<Map>();
    private static final List<Map> code = new ArrayList<Map>();

    public static void clear() throws Exception {
        codeGroup.clear();
        code.clear();
    }

    /**
     * @param codecd 코드그룹 ID
     * @return String 그룹코드명
     */
    public static String getCodeGroupNm(String codecd) throws Exception {
        String returnVal = "";
        Iterator<Map> iterator = codeGroup.iterator();
        Map map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (codecd.equals(map.get("codecd"))) {
                returnVal = (String) map.get("codenm");
                break;
            }
        }
        return returnVal;
    }

    /**
     * @param pcodecd    코드그룹 ID
     * @param detailCode code 코드
     * @return String 상세코드명
     */
    public static String getCodeNm(String pcodecd, String detailCode) throws Exception {
        String returnVal = "";
        Iterator<Map> iterator = code.iterator();
        Map map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (pcodecd.equals(map.get("pcodecd")) && detailCode.equals(map.get("codecd"))) {
                returnVal = (String) map.get("codenm");
                break;
            }
        }
        return returnVal;
    }

    /**
     * @param pcodecd 코드그룹 ID
     * @return List<Map> 코드그룹에 속한 상세 코드 List
     */
    public static List<Map> getCode(String pcodecd) throws Exception {

        List<Map> returnVal = new ArrayList<Map>();

        Iterator<Map> iterator = code.iterator();
        Map map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (pcodecd.equals(map.get("pcodecd"))) {
                returnVal.add(map);
            }
        }
        return returnVal;
    }

    /**
     * @param
     * @return List<Map> 상세 코드 List
     */
    public static List<Map> getCode() throws Exception {
        return code;
    }

    /**
     * 공통코드 메모리 등록
     */
    @SuppressWarnings("unchecked")
    @PostConstruct
    public void resetCodeList() throws Exception {

        if (codeGroup.isEmpty()) {
            synchronized (codeGroup) {
                if (codeGroup.isEmpty()) {

                    List<Map> mapList;
                    // 코드 그룹
                    mapList = (ArrayList<Map>) codeCacheDAO.selectListCodeGroup();    // codecd,  codenm

                    codeGroup.clear();
                    codeGroup.addAll(mapList);

                    // 상세코드
                    mapList = (ArrayList<Map>) codeCacheDAO.selectListCode();    // pcodecd, codecd, codenm
                    code.clear();
                    code.addAll(mapList);

                }
            }
        }
    }
}
