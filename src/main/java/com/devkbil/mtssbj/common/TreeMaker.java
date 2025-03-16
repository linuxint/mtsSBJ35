package com.devkbil.mtssbj.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 부모-자식 관계를 기반으로 평면적인 객체 리스트에서 계층적 트리 구조를 생성하는 서비스입니다.
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
        Assert.notEmpty(listview, "List must not be empty");
        log.debug("Creating hierarchical tree from {} items", listview.size());

        // 복사본 생성
        List<TreeVO> copiedList = listview.stream()
            .map(obj -> (TreeVO) ((TreeVO) obj).clone())
            .collect(Collectors.toList());

        List<TreeVO> rootlist = new ArrayList<>();

        // 루트 노드 찾기 (부모가 없는 노드)
        copiedList.stream()
            .filter(node -> !StringUtils.hasText(node.getParent()))
            .forEach(rootlist::add);

        // 부모-자식 관계 설정
        copiedList.stream()
            .filter(node -> StringUtils.hasText(node.getParent()))
            .forEach(childNode -> {
                copiedList.stream()
                    .filter(parentNode -> childNode.getParent().equals(parentNode.getKey()))
                    .findFirst()
                    .ifPresent(parentNode -> {
                        if (parentNode.getChildren() == null) {
                            parentNode.setChildren(new ArrayList<>());
                        }
                        parentNode.getChildren().add(childNode);
                        parentNode.setIsfolder(true);
                    });
            });

        try {
            return new ObjectMapper().writeValueAsString(rootlist);
        } catch (JsonProcessingException ex) {
            log.error("Error serializing tree structure to JSON", ex);
            return "[]";
        }
    }
}
