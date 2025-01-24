package com.devkbil.mtssbj.admin.sign;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import lombok.Setter;

/**
 * SignDocTypeVO 클래스
 * - 문서 유형 관리의 데이터 모델을 정의합니다.
 * - 문서 번호, 제목, 내용 등의 데이터를 포함하며, 유효성 검사와 Swagger API 문서를 제공합니다.
 */
@Schema(description = "문서 양식 : SignDocTypeVO") // Swagger API에서 표시될 데이터 모델 설명
@XmlRootElement(name = "SignDocTypeVO")
@XmlType(propOrder = {"dtno", "dttitle", "dtcontents"}) // XML 변환 시 필드 순서 지정
@Getter
@Setter
public class SignDocTypeVO {

    @Schema(description = "문서 고유 번호") // Swagger API 설명
    @NotBlank(message = "문서 번호는 필수 입력 값입니다.") // 유효성 검사: 공백 또는 null 불가
    private String dtno;

    @Schema(description = "문서 제목", maxLength = 100) // Swagger에 최대 길이 명시
    @NotBlank(message = "문서 제목은 필수 입력 값입니다.") // 유효성 검사: 공백 또는 null 불가
    @Size(max = 100, message = "문서 제목은 최대 100자까지 입력 가능합니다.") // 최대 문자열 길이 제한
    private String dttitle;

    @Schema(description = "문서 내용") // Swagger API 설명
    @NotBlank(message = "문서 내용은 필수 입력 값입니다.") // 유효성 검사: 공백 또는 null 불가
    private String dtcontents;

}