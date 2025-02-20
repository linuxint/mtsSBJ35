package com.devkbil.mtssbj.common.masking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * <pre>
 * 사용자 정보 응답을 위한 데이터 전송 객체.
 * 이 클래스는 데이터 프라이버시와 보호를 위해 일부 필드가 마스킹 처리되는
 * 사용자 정보 필드들을 포함합니다.
 *
 * 필드:
 * - userId: 사용자의 고유 식별자
 * - userName: 사용자의 이름. {@code Mask} 어노테이션과 {@code MaskingType.NAME}을
 * 사용하여 마스킹 처리됨
 * - phoneNumber: 사용자의 전화번호. {@code Mask} 어노테이션과 {@code MaskingType.PHONE_NUMBER}를
 * 사용하여 마스킹 처리됨
 * - email: 사용자의 이메일 주소. {@code Mask} 어노테이션과 {@code MaskingType.EMAIL}을
 * 사용하여 마스킹 처리됨
 *
 * 어노테이션:
 * - {@code @Getter, @Setter}: 필드들의 getter와 setter 메서드를 자동 생성
 * - {@code @NoArgsConstructor}: 파라미터 없는 생성자 생성
 * - {@code @AllArgsConstructor}: 모든 필드를 파라미터로 받는 생성자 생성
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponseDto {

    private String userId;

    @Mask(type = MaskingType.NAME)
    private String userName;

    @Mask(type = MaskingType.PHONE_NUMBER)
    private String phoneNumber;

    @Mask(type = MaskingType.EMAIL)
    private String email;
}