package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.ai.dto.AiAdminDto;
import java.util.ArrayList;
import java.util.List;

public record AdminAiResponse(
    List<AiInfo> aiInfos
) {
    public record AiInfo(
        Integer aiId,
        String name,
        String mbti,
        String info,
        String prompt,
        Boolean isUse,
        Integer count
    ) {
        public static AiInfo fromDto(AiAdminDto aiAdminDto) {
            return new AiInfo(
                aiAdminDto.getAiId(),
                aiAdminDto.getName(),
                aiAdminDto.getMbti(),
                aiAdminDto.getInfo(),
                aiAdminDto.getPrompt(),
                aiAdminDto.getIsUse(),
                aiAdminDto.getCount()
            );
        }
    }

    public static AdminAiResponse fromDtoList(List<AiAdminDto> ais) {
        List<AiInfo> aiInfos = ais.stream().map(AiInfo::fromDto).toList();
        return new AdminAiResponse(aiInfos);
    }
}
