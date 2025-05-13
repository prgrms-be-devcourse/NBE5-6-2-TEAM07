package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.entity.QDiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.QKeyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final QKeyword keyword = QKeyword.keyword;
    private final QDiaryKeyword diaryKeyword = QDiaryKeyword.diaryKeyword;

    @Override
    public List<Keyword> findMostPopularKeywords() {
        return queryFactory
            .select(keyword)
            .from(diaryKeyword)
            .join(keyword).on(diaryKeyword.keywordId.eq(keyword))
            .groupBy(keyword.keywordId)
            .orderBy(diaryKeyword.count().desc())
            .limit(5)
            .fetch();
    }
}
