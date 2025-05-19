package com.grepp.diary.app.model.custom.repository;

import com.querydsl.core.Tuple;
import java.util.List;

public interface CustomRepositoryCustom {
    List<Tuple> getAiByLimit(Integer limit);
}

