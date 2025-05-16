package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.custom.entity.QCustom;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CustomRepositoryImpl implements CustomRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QAi ai = QAi.ai;
    private final QCustom custom = QCustom.custom;

    @Override
    public List<Tuple> getAiByLimit(Integer limit) {
        NumberPath<Long> usageCount = Expressions.numberPath(Long.class, "count");

        JPAQuery<Tuple> query = queryFactory
            .select(ai, custom.count().as(usageCount))
            .from(ai)
            .leftJoin(custom).on(custom.ai.eq(ai))
            .groupBy(ai)
            .orderBy(usageCount.desc());

        if (limit != null && limit > 0) {
            query.limit(limit);
        }

        return query.fetch();
    }
}
