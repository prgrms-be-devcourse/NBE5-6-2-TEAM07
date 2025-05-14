package com.grepp.diary.app.controller.api.admin.payload;

import com.grepp.diary.app.model.keyword.code.KeywordType;

public record AdminKeywordWriteRequest(Integer id, String name, KeywordType keywordType) {}

