package com.devkbil.mtssbj.common;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

@Schema(description = "확장필드 : ExtFieldVO")
@XmlRootElement(name = "ExtFieldVO")
@XmlType(propOrder = {"field1", "field2", "field3"})
@Getter
@Setter
public class ExtFieldVO {
    @Schema(description = "확장필드1")
    private String field1;
    @Schema(description = "확장필드2")
    private String field2;
    @Schema(description = "확장필드3")
    private String field3;

    /**
     * 한번에 값 설정.
     */
    public ExtFieldVO() {
    }

    /**
     * Constructs an ExtFieldVO object with the provided field values.
     *
     * @param field1 the first field value
     * @param field2 the second field value
     * @param field3 the third field value
     */
    public ExtFieldVO(String field1, String field2, String field3) {
        this.field1 = field1;
        this.field2 = field2;
        this.field3 = field3;
    }

}
