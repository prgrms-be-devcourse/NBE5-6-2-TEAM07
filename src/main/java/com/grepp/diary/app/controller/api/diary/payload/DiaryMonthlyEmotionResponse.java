package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public record DiaryMonthlyEmotionResponse (
    List<DiaryMonthlyEmotion> diaryMonthlyEmotionList
) {
    public record DiaryMonthlyEmotion (
        LocalDate date,
        Emotion emotion
    ) {
        public static DiaryMonthlyEmotion fromEntity(Diary diary) {
            return new DiaryMonthlyEmotion(
                diary.getCreatedAt().toLocalDate(),
                diary.getEmotion()
            );
        }
    }

    public static DiaryMonthlyEmotionResponse fromEntityList(List<Diary> diaries) {
        List<DiaryMonthlyEmotion> diaryMonthlyEmotionList = diaries.stream().map(DiaryMonthlyEmotion::fromEntity).toList();
        if (diaryMonthlyEmotionList.isEmpty()) return null;
        return new DiaryMonthlyEmotionResponse(diaryMonthlyEmotionList);
    }
}
