package com.devkbil.mtssbj.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;

/**
 * ModelMapper 설정을 관리하는 Configuration 클래스.
 * DTO와 Entity 간의 자동 매핑을 위한 ModelMapper 설정을 제공합니다.
 */
@Configuration
public class MapperConfig {
    /**
     * ModelMapper 인스턴스를 생성하고 기본 설정을 적용합니다.
     * - private 필드에 대한 직접 접근 허용
     * - LOOSE 매칭 전략 사용
     * - 필드 이름 기반 자동 매핑 활성화
     *
     * @return 설정이 적용된 ModelMapper 인스턴스
     */
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                // Access level 이 private 레벨로 바꿔주면 setter 없이도 필드명이 같을 때 자동 매핑처리될 수 있다.
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper;
    }

}
