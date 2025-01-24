package com.devkbil.mtssbj.common.masking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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