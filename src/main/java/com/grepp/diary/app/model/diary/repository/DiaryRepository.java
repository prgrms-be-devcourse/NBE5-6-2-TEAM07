package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Integer>, DiaryRepositoryCustom {
    List<Diary> findByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(String userId, LocalDateTime start, LocalDateTime end);

    Integer countByCreatedAtBetweenAndIsUseTrue(LocalDateTime startDateTime, LocalDateTime endDateTime);
}
