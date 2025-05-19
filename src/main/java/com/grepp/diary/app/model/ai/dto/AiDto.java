package com.grepp.diary.app.model.ai.dto;

import com.grepp.diary.app.model.ai.entity.Ai;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiDto {
    private Integer aiId;
    private String name;
    private String mbti;
    private String info;
    private String prompt;
    private Boolean isUse;

    public static AiDto fromEntity(Ai ai) {
        AiDto dto = new AiDto();
        dto.setAiId(ai.getAiId());
        dto.setName(ai.getName());
        dto.setMbti(ai.getMbti());
        dto.setInfo(ai.getInfo());
        dto.setPrompt(ai.getPrompt());
        dto.setIsUse(ai.getIsUse());
        return dto;
    }
}
