package com.grepp.diary.app.controller.api.custom.payload;

import com.grepp.diary.app.model.custom.dto.CustomAIDto;

public record CustomResponse(
    String name,
    boolean isFormal,
    boolean isLong
) {
    public static CustomResponse fromDto(CustomAIDto customAIDto) {
        return new CustomResponse(customAIDto.getName(), customAIDto.isFormal(), customAIDto.isLong());
    }
}
