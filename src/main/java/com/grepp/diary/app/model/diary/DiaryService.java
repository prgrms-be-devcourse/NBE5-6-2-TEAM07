package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public Diary saveDiary(DiaryRequest form, String userId) {
        Diary diary = new Diary();
        //diary.setUserId(userId);
        diary.setEmotion(form.getEmotion());
        diary.setContent(form.getContent());
        diary.setCreatedAt(LocalDateTime.now());
        diary.setModifiedAt(LocalDateTime.now());
        diary.setIsUse(true);

        return diaryRepository.save(diary); // 바로 save() 가능!
    }
    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다. */
    public List<Diary> getDiariesDateBetween(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.findByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId, startDateTime, endDateTime);
    }

    public Integer getMonthDiariesCount() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);

        return diaryRepository.countByCreatedAtBetweenAndIsUseTrue(startDateTime, endDateTime);
    }



}
