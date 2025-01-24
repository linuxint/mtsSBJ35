package com.devkbil.mtssbj.common.tree;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 부모-자식 관계를 기반으로 평면적인 객체 리스트에서 계층적 트리 구조를 생성하는 유틸리티 클래스입니다.
 */
@Slf4j
public class TreeMaker {

    /**
     * 부모코드를 이용하여 계층형 트리 구성.
     *
     * @param listview
     * @return
     */
    public String makeTreeByHierarchy(List<?> listview) {
        // 복사본 생성
        List<TreeVO> copiedList = new ArrayList<>();
        for (Object obj : listview) {
            copiedList.add((TreeVO) ((TreeVO) obj).clone());
        }

        List<TreeVO> rootlist = new ArrayList<>();
        for (Object obj : copiedList) {
            TreeVO mtDO = (TreeVO) obj;

            if (!StringUtils.hasText(mtDO.getParent())) {
                rootlist.add(mtDO);
                continue;
            }
            for (Object objInner : copiedList) {
                TreeVO ptDO = (TreeVO) objInner;
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