package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record DiaryCalendarResponse(
    List<DiaryEmotion> diaryEmotions
) {
    public record DiaryEmotion(
        Integer diaryId,
        Emotion emotion,
        LocalDate createdAt
    ){
        public static DiaryEmotion fromEntity(Diary diary) {
            return new DiaryEmotion(
                diary.getDiaryId(),
                diary.getEmotion(),
                diary.getCreatedAt().toLocalDate()
            );
        }
    }

    public static DiaryCalendarResponse fromEntityList(List<Diary> diaries){
        List<DiaryEmotion> diaryEmotions = diaries.stream().map(DiaryEmotion::fromEntity).toList();
        return new DiaryCalendarResponse(diaryEmotions);
    }
}


