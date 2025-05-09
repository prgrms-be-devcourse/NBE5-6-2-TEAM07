package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryDto getDiaryDto(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        System.out.println(result);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        Diary diary = result.get();
        return DiaryDto.fromEntity(diary);
    }
}
