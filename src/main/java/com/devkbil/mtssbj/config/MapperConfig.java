package com.devkbil.mtssbj.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfig {
    /**
     * ModelMapper Config
     *
     * @return
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

    // mapToDto
    /*
    private CommentDto mapToDto(Comment comment) {
        return mapper.map(comment, CommentDto.class);
    }
    */
    // mapToEntity
    /*
    private Comment mapToEntity(CommentDto commentDto) {
        return mapper.map(commentDto, Comment.class);
    }
    */
}
