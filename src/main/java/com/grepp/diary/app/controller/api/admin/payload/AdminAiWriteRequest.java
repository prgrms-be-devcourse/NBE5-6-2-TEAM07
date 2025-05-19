package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminAiWriteRequest {
    private Integer id;
    private String name;
    private String mbti;
    private String info;
    private String prompt;
}
