package com.grepp.diary.app.model.keyword.repository;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRepository extends JpaRepository<Keyword, Integer>, KeywordRepositoryCustom {

}
