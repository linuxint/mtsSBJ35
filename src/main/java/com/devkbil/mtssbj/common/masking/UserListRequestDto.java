package com.devkbil.mtssbj.common.masking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserListRequestDto implements MaskingDto {
    private String disableMaskingYn;
}