package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.dto.DiaryEmotionAvgDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다. */
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

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들의 개수를 반환합니다. */
    public Integer getUserDiaryCount(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.countByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId, startDateTime, endDateTime);
    }

    /**
     * 작성된 일기들을 size 만큼 가져옵니다.
     * 일기들은 포함된 이미지와 함께 반환됩니다.
     * 일기들을 하나의 페이지에 표시하기 위해 사용됩니다.
     * */
    public List<Diary> getDiariesWithImages(String userId, int page, int size) {
        Pageable limit = PageRequest.of(page, size);
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

    /** 특정 년도에 작성된 일기들을 기준으로 월별 평균 기분점수를 반환합니다. */
    public List<DiaryEmotionAvgDto> getMonthlyEmotionAvgOfYear(String userId, int year){
        List<Object []> emotionsByDate = diaryRepository.findDateAndEmotionByUserIdAndYear(userId, year);

        Map<Emotion, Integer> emotionScore = Map.of(
            Emotion.VERY_GOOD, 5,
            Emotion.GOOD, 4,
            Emotion.COMMON, 3,
            Emotion.BAD, 2,
            Emotion.VERY_BAD, 1
        );

        // 월별 감정 점수 모으기
        Map<Integer, List<Integer>> monthToScores = new HashMap<>();
        for (Object [] row : emotionsByDate) {
            LocalDate date = (LocalDate) row[0];
            Emotion emotion = (Emotion) row[1];
            int month = date.getMonthValue();

            int score = emotionScore.getOrDefault(emotion, 0);
            monthToScores.computeIfAbsent(month, k -> new ArrayList<>()).add(score);
        }

        // 평균 계산
        List<DiaryEmotionAvgDto> result = new ArrayList<>();
        for(int month = 1; month <= 12; month++) {
            List<Integer> scores = monthToScores.get(month);
            if(scores == null || scores.isEmpty()) {
                continue;   // 일기가 없는 달은 생략 하도록 합니다.
            }

            double avg = scores.stream().mapToInt(i -> i).average().orElse(0.0);
            result.add(new DiaryEmotionAvgDto(month, avg));
        }

        return result;
    }
}
