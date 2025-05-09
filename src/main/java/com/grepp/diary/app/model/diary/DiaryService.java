package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.mapper.Mapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryDto getDto(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        System.out.println(result);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        Diary diary = result.get();
        return DiaryDto.fromEntity(diary);
    }

    public List<DiaryDto> getNotRepliedDtos() {
        List<Diary> diaries = diaryRepository.findByReplyIsNull();
        System.out.println(diaries);
        return diaries.stream().map(DiaryDto::fromEntity).toList();
    }

    public void saveReply(Integer diaryId, String replyContent) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        Diary diary = result.get();
        Reply reply = new Reply();
        reply.setContent(replyContent);
        diary.setReply(reply);

        diaryRepository.save(diary);
    }
}
