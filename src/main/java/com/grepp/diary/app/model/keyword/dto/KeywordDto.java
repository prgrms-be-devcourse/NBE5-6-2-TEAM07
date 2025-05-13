package com.grepp.diary.app.model.keyword.dto;

import com.grepp.diary.app.model.keyword.code.KeywordType;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KeywordDto {
    private Integer keywordId;
    private String name;
    private Boolean isUse;
    private KeywordType keywordType;

    public KeywordDto(String name, Boolean isUse, KeywordType keywordType) {
        this.name = name;
        this.isUse = true;
        this.keywordType = keywordType;
    }
}
