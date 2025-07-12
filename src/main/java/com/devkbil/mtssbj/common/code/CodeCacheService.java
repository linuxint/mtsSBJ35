package com.devkbil.mtssbj.common.code;

import jakarta.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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

    /**
     * 데이터 접근 객체(DAO)를 나타내며, {@code CodeCacheService}에서
     * 데이터베이스와 상호작용하여 코드 그룹 및 상세 코드 데이터를 검색하는 데 사용됩니다.
     * 이 DAO는 변경할 수 없도록 final 필드로 초기화되며,
     * MyBatis를 통해 코드 관련 데이터에 접근하기 위한 종속성을 유지합니다.
     */
    public final CodeCacheDAO codeCacheDAO;

    private static final List<Map<String, Object>> codeGroup = new ArrayList<>();
    private static final List<Map<String, Object>> code = new ArrayList<>();

    /**
     * codeGroup과 code 객체의 내용을 모두 삭제합니다.
     *
     * 이 메서드는 codeGroup과 code에 저장된 모든 요소나 데이터를 제거하며,
     * 이를 빈 상태로 초기화합니다.
     *
     * @throws Exception 내용 삭제 중 오류가 발생하면 예외를 던집니다.
     */
    public static void clear() throws Exception {
        codeGroup.clear();
        code.clear();
    }

    /**
     * 지정한 코드 식별자와 관련된 코드 그룹 이름을 검색합니다.
     *
     * @param codecd 코드 그룹에서 검색할 코드 식별자
     * @return 일치하는 코드 식별자에 해당하는 이름을 반환하며, 일치하는 코드 식별자가 없으면 빈 문자열을 반환
     * @throws Exception 처리 중 오류가 발생하면 예외를 던집니다.
     */
    public static String getCodeGroupNm(String codecd) throws Exception {
        String returnVal = "";
        Iterator<Map<String, Object>> iterator = codeGroup.iterator();
        Map<String, Object> map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (codecd.equals(map.get("codecd"))) {
                returnVal = (String)map.get("codenm");
                break;
            }
        }
        return returnVal;
    }

    /**
     * 부모 코드(pcodecd)와 상세 코드(codecd)에 해당하는 코드 이름(codenm)을 검색합니다.
     *
     * @param pcodecd 검색할 부모 코드
     * @param detailCode 지정된 부모 코드 아래에서 매칭될 상세 코드
     * @return 매칭되는 경우 코드 이름(codenm)을 반환하며, 그렇지 않으면 빈 문자열을 반환
     * @throws Exception 처리 과정 중 오류가 발생하면 예외를 던집니다.
     */
    public static String getCodeNm(String pcodecd, String detailCode) throws Exception {
        String returnVal = "";
        Iterator<Map<String, Object>> iterator = code.iterator();
        Map<String, Object> map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (pcodecd.equals(map.get("pcodecd")) && detailCode.equals(map.get("codecd"))) {
                returnVal = (String)map.get("codenm");
                break;
            }
        }
        return returnVal;
    }

    /**
     * 지정된 코드 그룹에 속하는 상세 코드 목록을 검색합니다.
     *
     * @param pcodecd 상세 코드를 검색하려는 코드 그룹의 ID
     * @return 지정된 코드 그룹에 대한 상세 코드 정보를 포함하는 맵 목록
     * @throws Exception 검색 과정 중 오류가 발생하면 예외를 던집니다.
     */
    public static List<Map<String, Object>> getCode(String pcodecd) throws Exception {
        List<Map<String, Object>> returnVal = new ArrayList<>();
        Iterator<Map<String, Object>> iterator = code.iterator();
        Map<String, Object> map;
        while (iterator.hasNext()) {
            map = iterator.next();
            if (pcodecd.equals(map.get("pcodecd"))) {
                returnVal.add(map);
            }
        }
        return returnVal;
    }

    /**
     * 모든 상세 코드의 목록을 검색합니다.
     *
     * @return 각 맵이 상세 코드를 나타내는 Map 객체의 리스트
     * @throws Exception 검색 과정 중 오류가 발생하면 예외를 던집니다.
     */
    public static List<Map<String, Object>> getCode() throws Exception {
        return code;
    }

    /**
     * 데이터베이스에서 코드 그룹 및 상세 코드 목록을 초기화하고 재설정합니다.
     *
     * 이 메서드는 `codeCacheDAO`를 사용하여 데이터 소스에서 코드를 검색하고
     * 이를 `codeGroup` 및 `code` 리스트에 로드합니다. 다음 단계를 수행합니다:
     *
     * 1. `codeGroup` 리스트가 비어있는지 확인합니다.
     * 2. 리스트가 비어있다면, 동기화를 통해 스레드 안전성을 보장하며 업데이트합니다.
     * 3. 데이터베이스에서 코드 그룹 데이터를 검색하고 `codeGroup` 리스트를 업데이트합니다.
     * 4. 데이터베이스에서 상세 코드 데이터를 검색하고 `code` 리스트를 업데이트합니다.
     *
     * @throws Exception 데이터베이스 접근 또는 리스트 조작 과정 중 문제가 발생하면 예외를 던집니다.
     */
    @PostConstruct
    public void resetCodeList() throws Exception {
        if (codeGroup.isEmpty()) {
            synchronized (codeGroup) {
                if (codeGroup.isEmpty()) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> mapList = (ArrayList<Map<String, Object>>)codeCacheDAO.selectListCodeGroup();    // codecd,  codenm
                    codeGroup.clear();
                    codeGroup.addAll(mapList);
                    // 상세코드
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> mapList2 = (ArrayList<Map<String, Object>>)codeCacheDAO.selectListCode();    // pcodecd, codecd, codenm
                    code.clear();
                    code.addAll(mapList2);
                }
            }
        }
    }
}
