package com.devkbil.common.tree;

import com.devkbil.mtssbj.common.TreeVO;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

/**
 * 부모-자식 관계를 기반으로 평면적인 객체 리스트에서 계층적 트리 구조를 생성하는 유틸리티 클래스입니다.
 */
@Slf4j
public class TreeMaker {

    /**
     * 평면적인 객체 리스트에서 부모-자식 관계를 기반으로 계층적인 트리 구조를 생성합니다.
     * <p>
     * 입력 리스트는 부모 키, 노드 키, 하위 노드와 같은 구조적 속성을 정의하는 `TreeVO` 타입의 객체를 포함해야 합니다.
     * 생성된 계층 구조는 JSON 문자열로 직렬화됩니다.
     * 부모가 없는 노드는 루트 노드로 간주되며, 자식 노드는 해당 부모 노드 아래로 중첩됩니다.
     *
     * @param listview 평면적인 객체 리스트로, 각 객체는 부모-자식 관계의 트리 노드를 나타냅니다 (보통 `TreeVO` 타입).
     * @return 입력 리스트로부터 생성된 계층적 트리 구조를 나타내는 JSON 문자열.
     */
    public String makeTreeByHierarchy(List<?> listview) {
        // 복사본 생성
        List<TreeVO> copiedList = new ArrayList<>();
        for (Object obj : listview) {
            copiedList.add((TreeVO)((TreeVO)obj).clone());
        }

        List<TreeVO> rootlist = new ArrayList<>();
        for (Object obj : copiedList) {
            TreeVO mtDO = (TreeVO)obj;

            if (!StringUtils.hasText(mtDO.getParent())) {
                rootlist.add(mtDO);
                continue;
            }
            for (Object objInner : copiedList) {
                TreeVO ptDO = (TreeVO)objInner;
                if (mtDO.getParent().equals(ptDO.getKey())) {
                    if (ptDO.getChildren() == null) {
                        ptDO.setChildren(new ArrayList<>());
                    }
                    ptDO.getChildren().add(mtDO);
                    ptDO.setIsfolder(true);
                    break;
                }
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String str = "";
        try {
            str = mapper.writeValueAsString(rootlist);
        } catch (IOException ex) {
            log.error("TreeMaker");
        }
        return str;
    }

}