package com.grepp.diary.app.controller.api.admin.payload;

import java.util.List;

public class AdminKeywordIdRequest {
    private List<Integer> keywordIds;

    public List<Integer> getKeywordIds() {
        return keywordIds;
    }

    public void setKeywordIds(List<Integer> keywordIds) {
        this.keywordIds = keywordIds;
    }
}
