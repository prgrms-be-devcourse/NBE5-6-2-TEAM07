package com.grepp.diary.app.model.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class AiInfoDto {

    private Integer aiId;
    private String name;
    private String mbti;
    private String info;
    private String imgUrl;
}
