package com.devkbil.mtssbj.admin.organ;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * DeptVO 클래스
 * - 부서 정보를 표현하는 데이터 모델 클래스입니다.
 * - 이 클래스는 부서코드, 부서명, 상위부서코드, 삭제여부를 정의합니다.
 * - JPA 엔터티와 XML, Swagger API 문서 생성을 위한 설정이 포함되어 있습니다.
 */
@Schema(description = "부서 데이터 모델 : DeptVO") // Swagger에서 클래스 설명
@XmlRootElement(name = "DeptVO") // XML 루트 태그 이름 정의
@XmlType(propOrder = {"deptno", "deptnm", "parentno", "deleteflag"}) // XML 및 Swagger 설명에서 필드 순서 정의
@Getter
@Setter
@Entity(name = "com_dept") // JPA 엔터티 설정
public class DeptVO implements Serializable {

    private static final long serialVersionUID = 1L; // 직렬화 식별자 설정

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // ID 자동 생성 설정
    @Column(name = "deptno", nullable = false, length = 10)
    @Schema(description = "부서 코드 (고유 식별자)", example = "D001") // Swagger 필드 설명 및 예제 추가
    private String deptno;

    @Column(name = "deptnm", nullable = false, length = 20)
    @Schema(description = "부서명", example = "IT부서") // Swagger 필드 설명 및 예제
    private String deptnm;

    @Column(name = "parentno", nullable = false, length = 10)
    @Schema(description = "상위 부서 코드", example = "P001") // Swagger API에서 필드 설명 및 예제 명시
    private String parentno;

    @Column(name = "deleteflag", nullable = false, length = 1)
    @Schema(description = "삭제 여부", example = "0", allowableValues = {"0", "1"}) // 값의 범위를 Swagger에 명시
    private String deleteflag;

}