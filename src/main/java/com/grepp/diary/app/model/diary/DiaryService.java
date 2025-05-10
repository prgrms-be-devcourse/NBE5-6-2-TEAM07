package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    public DiaryDto getDto(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        return DiaryDto.fromEntity(result.get());
    }

    public List<DiaryDto> getNoReplyDtos() {
        List<Diary> diaries = diaryRepository.findAll();
        List<Diary> NoReplyDiaries = diaries.stream().filter(e -> e.getReply() == null).toList();
        return NoReplyDiaries.stream().map(DiaryDto::fromEntity).toList();
    }

    @Transactional
    public void registReply(int diaryId, String replyContent) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }

        Diary diary = result.get();
        Reply reply = diary.getReply();
        Member member = diary.getMember();
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        if (reply == null) { // 등록된 reply X
            reply = new Reply();
            reply.setContent(replyContent);
            reply.setAi(ai);
            reply.setDiary(diary);
            diary.setReply(reply);
        } else { // 등록된 reply O
            reply.setContent(replyContent);
            reply.setAi(ai);
        }
        diaryRepository.save(diary);
    }
}
