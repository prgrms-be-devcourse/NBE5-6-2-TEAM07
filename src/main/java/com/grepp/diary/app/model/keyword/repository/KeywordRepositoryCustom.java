package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.querydsl.core.Tuple;
import java.util.List;

public interface KeywordRepositoryCustom {
    List<Tuple> getMostPopularKeywords();
}
