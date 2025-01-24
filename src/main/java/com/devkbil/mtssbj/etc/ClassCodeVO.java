package com.devkbil.mtssbj.etc;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * ClassCodeVO
 * - 코드데이터 정보를 담는 VO 클래스.
 */
@Schema(description = "코드 정보 객체 ClassCodeVO")
@XmlRootElement(name = "ClassCodeVO")
@XmlType(propOrder = {"codecd", "codenm", "tmp"})
@Getter
@Setter
public class ClassCodeVO {

    @Schema(description = "코드 값", example = "CODE001") // Swagger에 필드 설명과 예제 값 제공
    private String codecd;

    @Schema(description = "코드 이름", example = "코드 이름 예시") // Swagger에 필드 설명과 예제 값 제공
    private String codenm;

    @Schema(description = "임시 또는 다용도 필드")
    private String tmp; // 다용도 필드
}
