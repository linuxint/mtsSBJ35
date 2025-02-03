package com.devkbil.mtssbj.admin.code;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * 공통코드 : CodeVO
 * 이 클래스는 공통 코드 데이터를 표현하는 모델입니다.
 */
@Schema(description = "공통코드 : CodeVO") // Swagger에서 이 클래스의 설명을 정의
@XmlRootElement(name = "CodeVO") // XML 루트 태그 이름 정의
@XmlType(propOrder = {"classno", "codecd", "codenm"}) // 필드 순서를 XML 출력 시 지정
@Getter
@Setter
public class CodeVO {

    @Schema(description = "대분류", example = "CLASS01") // Swagger에 필드 설명과 예제 값 제공
    private String classno;

    @Schema(description = "코드", example = "CODE001") // Swagger에 필드 설명과 예제 값 제공
    private String codecd;

    @Schema(description = "코드명", example = "코드 이름 예시") // Swagger에 필드 설명과 예제 값 제공
    private String codenm;

}