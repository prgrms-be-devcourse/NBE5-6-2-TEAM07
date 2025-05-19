package com.grepp.diary.app.controller.api.ai.payload;

import com.grepp.diary.app.model.ai.dto.AiInfoDto;
import java.util.List;
import java.util.stream.Collectors;

public record AiListResponse(
    List<AiInfo> aiInfoList
) {
    public record AiInfo(
        String name,
        String mbti,
        String info,
        String savePath
    ) {
        public static AiInfo fromDto(AiInfoDto aiInfoDto) {
            return new AiInfo(
                aiInfoDto.getName(),
                aiInfoDto.getMbti(),
                aiInfoDto.getInfo(),
                aiInfoDto.getImgUrl()
            );
        }
    }
    public static AiListResponse fromDtoList(List<AiInfoDto> aiInfoDtoList) {
        List<AiInfo> aiInfoList = aiInfoDtoList.stream().map(AiInfo::fromDto).toList();
        return new AiListResponse(aiInfoList);
    }
}
