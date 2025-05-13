package com.grepp.diary.app.controller.api.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryCalendarResponse;
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

    @GetMapping("/calendar")
    public DiaryCalendarResponse getEmotionsForCalendar(
        @RequestParam String userId,    // -> Auth 구현되면 @AuthnticationPrincipal CustomUserDetails user 로 변경 할 것
        @RequestParam int year,
        @RequestParam int month
    ) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return DiaryCalendarResponse.fromEntityList(
            diaryService.getDiariesDateBetween(userId, start, end)
        );
    }
}
