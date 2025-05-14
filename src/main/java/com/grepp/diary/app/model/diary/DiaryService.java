package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다.
     */
    public List<Diary> getDiariesDateBetween(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.findByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId,
            startDateTime, endDateTime);
    }

    public Integer getMonthDiariesCount() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);

        return diaryRepository.countByCreatedAtBetweenAndIsUseTrue(startDateTime, endDateTime);
    }

    /** 최근 14개의 작성된 일기를 가져옵니다 */
    public List<Diary> getRecentDiariesWithImages(String userId) {
        Pageable limit = PageRequest.of(0, 14);
        return diaryRepository.findRecentDiariesWithImages(userId, limit);
    }

    public Diary getDiaryById(int diaryId) {
        Optional<Diary> result = diaryRepository.findById(diaryId);
        if (result.isEmpty()) {
            throw new RuntimeException("일기가 존재하지 않습니다.");
        }
        return result.get();
    }

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
