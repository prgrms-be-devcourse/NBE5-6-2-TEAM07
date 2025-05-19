package com.grepp.diary.app.controller.api.diary;

import com.grepp.diary.app.controller.api.diary.payload.DiaryCalendarResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryCardResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryDailyEmotionResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryEditRequest;
import com.grepp.diary.app.controller.api.diary.payload.DiaryEmotionCountResponse;
import com.grepp.diary.app.controller.api.diary.payload.DiaryMonthlyEmotionResponse;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.grepp.diary.infra.util.date.DateUtil;
import com.grepp.diary.infra.util.date.dto.DateRangeDto;
import jakarta.persistence.EntityNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final DateUtil dateUtil;


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

    // 월간/연간 작성된 일기수 데이터 API
    @GetMapping("/dashboard/count")
    public int getDiaryCount(
        @RequestParam String userId,
        @RequestParam String period,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE) LocalDate date
    ){
        DateRangeDto range = dateUtil.toDateRangeDto(period, date);
        LocalDate start = range.start();
        LocalDate end = range.end();

        return diaryService.getUserDiaryCount(userId, start, end);
    }

    // 기분 흐름 데이터(월간) API
    @GetMapping("/emotion/flow/monthly")
    public DiaryDailyEmotionResponse getEmotionFlow(
        @RequestParam String userId,
        @RequestParam(required = false) LocalDate date
    ) {
        DateRangeDto range = dateUtil.toDateRangeDto("monthly", date);
        LocalDate start = range.start();
        LocalDate end = range.end();

        return DiaryDailyEmotionResponse.fromEntityList(
            diaryService.getDiariesDateBetween(userId, start, end)
        );

    }

    // 기분 흐름 데이터(연간) API
    @GetMapping("/emotion/flow/yearly")
    public DiaryMonthlyEmotionResponse getMonthlyEmotionAvg(
        @RequestParam String userId,
        @RequestParam(required = false) int year
    ) {
        return DiaryMonthlyEmotionResponse.fromDtoList(
            diaryService.getMonthlyEmotionAvgOfYear(userId, year)
        );
    }

    // 특정 날에 대한 일기 유무 API
    @GetMapping("/check")
    public boolean checkDiaryOfDay(
        @RequestParam String userId,
        @RequestParam LocalDate date
    ) {
        LocalDate nextDate = date.plusDays(1);

        return !diaryService.getDiariesDateBetween(userId, date, nextDate).isEmpty();
    }

    @PatchMapping("/modify")
    public ResponseEntity<?> editDiary(
        @RequestPart("request") DiaryEditRequest request,
        @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages
    ) {
        String username = "user01";

        try {
            diaryService.updateDiary(username, request, newImages);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDiary(@PathVariable Integer id
        //@AuthenticationPrincipal UserDetails userDetails
    )
    {
        //String username = userDetails.getUsername();
        String username = "user01";

        try {
            diaryService.deleteDiary(id, username);
            return ResponseEntity.noContent().build();
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }


    // 특정 기간내의 작성된 일기 기준 감정별 개수 API
    @GetMapping("/emotion/count")
    public DiaryEmotionCountResponse getEmotionCount(
        @RequestParam String userId,
        @RequestParam String period,
        @RequestParam int value
    ) {
        return DiaryEmotionCountResponse.fromDtoList(
            diaryService.getEmotionsCount(userId, period, value)
        );
    }
}
