package com.grepp.diary.app.controller.api.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryCalendarResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryCardResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryEditRequest;
import com.grepp.diary.app.model.diary.DiaryService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping("/cards")
    public DiaryCardResponse getDiaryCards( // 기본적으로는 최근 작성된 14개의 일기를 가져옵니다.
        @RequestParam String userId,
        @RequestParam(defaultValue="0") int page,
        @RequestParam(defaultValue = "14") int size
    ) {
        return DiaryCardResponse.fromEntityList(
            diaryService.getDiariesWithImages(userId, page, size)
        );
    }

    @GetMapping("/dashboard/count")
    public int getDiaryCount(
        @RequestParam String userId,
        @RequestParam String period,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ){
        LocalDate now = (date != null)?date:LocalDate.now();
        LocalDate start, end;

        if("monthly".equals(period)){
            start = now.withDayOfMonth(1);
            end = now.withDayOfMonth(now.lengthOfMonth());
        } else if( "yearly".equals(period)){
            start = now.withDayOfYear(1);
            end = now.withDayOfYear(now.lengthOfYear());
        } else {
            throw new IllegalArgumentException("Invalid period value: " + period);
        }

        return diaryService.getUserDiaryCount(userId, start, end);
    }

    @PatchMapping
    public ResponseEntity<?> editDiary(
        @RequestPart("request") DiaryEditRequest request,
        @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) {
        diaryService.updateDiary(
            request.getDiaryId(),
            request.getEmotion(),
            request.getContent(),
            request.getKeywords(),
            request.getDeletedImageIds(),
            newImages
        );

        return ResponseEntity.ok().build();
    }
}
