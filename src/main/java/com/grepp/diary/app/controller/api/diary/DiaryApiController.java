package com.grepp.diary.app.controller.api.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryCalendarResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryCardResponse;
import com.grepp.diary.app.model.diary.DiaryService;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/diary")
public class DiaryApiController {

    private final DiaryService diaryService;

    //TODO : Auth 구현되면 @AuthnticationPrincipal CustomUserDetails user 로 변경 할 것
    @GetMapping("/calendar")
    public DiaryCalendarResponse getEmotionsForCalendar(
        @RequestParam String userId,
        @RequestParam int year,
        @RequestParam int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return DiaryCalendarResponse.fromEntityList(
            diaryService.getDiariesDateBetween(userId, start, end)
        );
    }

    //TODO : Auth 구현되면 @AuthnticationPrincipal CustomUserDetails user 로 변경 할 것
    @GetMapping("/recent")
    public DiaryCardResponse getRecentDiaryCards(
        @RequestParam String userId
    ) {
        return DiaryCardResponse.fromEntityList(
            // 최근 작성된 14개의 일기를 가져옵니다.
            diaryService.getDiariesWithImages(userId, 0, 14)
        );
    }
}
