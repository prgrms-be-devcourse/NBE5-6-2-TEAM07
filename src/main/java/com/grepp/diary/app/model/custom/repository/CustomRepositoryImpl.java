package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.ai.entity.QAi;
import com.grepp.diary.app.model.custom.entity.QCustom;
import com.querydsl.core.Tuple;
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
    public List<Tuple> getMostPopularAis() {
        return queryFactory
            .select(ai, custom.count())
            .from(custom)
            .join(custom.ai, ai)
            .groupBy(custom.ai)
            .orderBy(custom.count().desc())
            .fetch();
    }
}
