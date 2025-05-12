package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public Diary saveDiary(DiaryRequest form, String userId) {
        Diary diary = new Diary();
        //diary.setUserId(userId);
        diary.setEmotion(form.getEmotion());
        diary.setContent(form.getContent());
        diary.setCreatedAt(LocalDateTime.now());
        diary.setUpdatedAt(LocalDateTime.now());
        diary.setIsUse(true);

        return diaryRepository.save(diary); // 바로 save() 가능!
    }

}
