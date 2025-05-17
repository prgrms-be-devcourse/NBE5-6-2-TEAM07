package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface DiaryRepositoryCustom {
    // 일기들을 포함된 이미지와 함께 반환합니다.
    List<Diary> findRecentDiariesWithImages(String userId, Pageable pageable);
    List<Object []> findDateAndEmotionByUserIdAndYear(String userId, int year);
}
