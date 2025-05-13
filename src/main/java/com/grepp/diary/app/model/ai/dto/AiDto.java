package com.grepp.diary.app.model.ai.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AiDto {
    private Integer aiId;
    private String name;
    private String prompt;
    private Boolean isUse;
}
