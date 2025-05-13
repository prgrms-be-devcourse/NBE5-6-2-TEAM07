package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다. */
    public List<Diary> getDiariesDateBetween(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.findByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId, startDateTime, endDateTime);
    }
}
