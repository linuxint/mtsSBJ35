package com.devkbil.mtssbj.common.masking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 선택적 마스킹 기능이 있는 사용자 목록 요청을 처리하기 위한 데이터 전송 객체(DTO) 클래스입니다.
 *
 * 이 클래스는 {@link MaskingDto} 인터페이스를 구현하여 마스킹 프레임워크 내에서 사용할 수 있습니다.
 * `disableMaskingYn` 속성은 응답의 중요 데이터에 마스킹을 적용할지 여부를 결정합니다.
 *
 * 주요 기능:
 * - `disableMaskingYn` 속성을 통해 마스킹 활성화/비활성화 지원
 * - 마스킹 aspect 로직과 통합하여 조건부 데이터 마스킹 적용
 * - {@code Mask}, {@code MaskingUtil}, {@code MaskingAspect}와 같은 
 *   관련 클래스들과 함께 마스킹 어노테이션 활용
 *
 * 사용 목적:
 * - 이 클래스는 특정 비즈니스 로직에 따라 민감한 정보를 보호하기 위해 
 *   조건부 마스킹이 필요한 데이터 응답 시나리오에서 주로 사용됩니다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequestDto implements MaskingDto {
    private String disableMaskingYn;
}
