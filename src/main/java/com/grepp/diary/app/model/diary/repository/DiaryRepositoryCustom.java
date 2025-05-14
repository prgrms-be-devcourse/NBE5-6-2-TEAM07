package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface DiaryRepositoryCustom {
    List<Diary> findRecentDiariesWithImages(String userId, Pageable pageable);
}
