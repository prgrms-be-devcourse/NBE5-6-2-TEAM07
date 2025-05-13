package com.grepp.diary.app.controller.api.diary.payload;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.entity.Diary;
import java.time.LocalDate;
import java.util.List;

public record DiaryCardResponse (
    List<DiaryCard> diaryCards
){
    public record DiaryCard(
        Integer diaryId,
        LocalDate createdAt,
        Emotion emotion,
        String imagePath
    ){
        public static DiaryCard fromEntity(Diary diary){
            String imagePath = null;

            if (diary.getImages() != null && !diary.getImages().isEmpty()) {
                imagePath = diary.getImages().getFirst().getRenamedPath();
            }

            return new DiaryCard(
                diary.getDiaryId(),
                diary.getCreatedAt().toLocalDate(),
                diary.getEmotion(),
                imagePath
            );
        }
    }

    public static DiaryCardResponse fromEntityList(List<Diary> diaries){
        List<DiaryCard> diaryCards = diaries.stream().map(DiaryCard::fromEntity).toList();
        return new DiaryCardResponse(diaryCards);
    }
}
