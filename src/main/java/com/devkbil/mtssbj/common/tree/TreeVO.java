package com.devkbil.mtssbj.common.tree;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * 계층적 관계를 가진 트리 구조를 나타냅니다.
 *
 * 이 클래스는 트리 내의 노드를 정의하는 데 사용되며, 각 노드는 키, 제목,
 * 부모 노드에 대한 참조를 가질 수 있으며 하위 노드를 포함할 수 있습니다.
 * 또한 `isfolder` 속성을 통해 노드의 폴더와 같은 동작을 포함합니다.
 *
 * `TreeVO` 클래스는 `Cloneable` 인터페이스를 구현하여, 하위 노드를 포함한
 * 트리 구조의 깊은 복제를 가능하게 합니다.
 *
 * 주석은 스키마 정의 및 XML 바인딩을 위해 사용되며, 직렬화 및 역직렬화 목적의
 * TreeVO 구조와 속성을 정의합니다.
 */
@Schema(description = "트리구조 : TreeVO")
@XmlRootElement(name = "TreeVO")
@XmlType(propOrder = {"key", "title", "parent", "isfolder", "children"})
@Getter
@Setter
public class TreeVO implements Cloneable {

    @Schema(description = "트리노드키")
    private String key;

    @Schema(description = "트리노드명")
    private String title;

    @Schema(description = "상위노트키")
    private String parent;

    @Schema(description = "노트속성:폴더여부")
    private boolean isfolder;

    @Schema(description = "하위 노트 목록")
    private List children;

    @Override
    public Object clone() {
        try {
            TreeVO clone = (TreeVO) super.clone();
            if (this.children != null) {
                clone.children = new ArrayList<>(this.children);
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

}
