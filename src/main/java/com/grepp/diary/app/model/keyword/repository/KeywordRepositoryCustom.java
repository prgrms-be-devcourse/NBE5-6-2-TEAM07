package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.List;

public interface KeywordRepositoryCustom {
    List<Keyword> findMostPopularKeywords();
}
