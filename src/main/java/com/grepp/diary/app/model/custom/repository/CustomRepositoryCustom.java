package com.grepp.diary.app.model.custom.repository;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.querydsl.core.Tuple;
import java.util.List;

public interface CustomRepositoryCustom {
    List<Tuple> getMostPopularAis();
}

