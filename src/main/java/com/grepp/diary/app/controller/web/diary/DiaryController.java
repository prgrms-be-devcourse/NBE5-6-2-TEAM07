package com.grepp.diary.app.controller.web.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.infra.util.file.FileUtil;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final KeywordService keywordService;

    @GetMapping
    public String showDiaryWritePage(
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        Model model
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("diaryDate", targetDate);

        model.addAttribute("diaryRequest", new DiaryRequest());
        List<Keyword> allKeywords = keywordService.findAllKeywordEntities();
        Map<String, List<Keyword>> grouped = allKeywords.stream()
                                                        .collect(Collectors.groupingBy(k -> k.getType().name()));
        model.addAttribute("keywordGroups", grouped);

        return "diary/diary";
    }

    @PostMapping
    public String writeAndSaveDiary(@ModelAttribute("diaryRequest") DiaryRequest form
        //@AuthenticationPrincipal CustomUserDetails user
    ) {
        String userId = "user01";

//        diaryService.saveDiary(form, user.getMember());
        diaryService.saveDiary(form, userId);
        return "app/home"; // 작성 완료 후 목록 페이지로 이동
    }

    @GetMapping("/record")
    public String showDiaryRecordPage(
        Model model
//        @RequestParam("targetDate")
//        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // 문자열을 날짜 타입으로 변환 "yyyy-MM-dd" 형식(예: 2025-05-15)만 허용
//        LocalDate targetDate
        //@AuthenticationPrincipal CustomUserDetails user

    ) {
        LocalDate targetDate = LocalDate.of(2025, 5,16);

        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(LocalTime.MAX);

        String userId = "user01";
        Optional<Diary> diaryExist = diaryService.findDiaryByUserIdAndDate(userId, start, end);
        if (diaryExist.isPresent()) {
            //사진 파일 encoding
            List<DiaryImg> encodedImages = diaryExist.get().getImages().stream()
                                                     .map(img -> {
                                                    String encodedPath = FileUtil.encodeFilenameInPath(img.getSavePath());
                                                    DiaryImg copy = new DiaryImg();
                                                    copy.setSavePath(encodedPath);
                                                    copy.setOriginName(img.getOriginName());
                                                    // 필요한 다른 필드도 복사
                                                    return copy;
                                                }).collect(Collectors.toList());


            model.addAttribute("encodedImages", encodedImages);
            model.addAttribute("diary", diaryExist.get());
        } else {
            log.info("Diary not found");
            model.addAttribute("diary", new Diary()); // 빈 객체를 넘겨서 프론트에서 처리
        }
        return "diary/record";
    }

    @GetMapping("/edit/{id}")
    public String showDiaryEditPage(@PathVariable Integer id, Model model) {
        Diary diary = diaryService.findById(id);
        model.addAttribute("diary", diary);

        // 선택했던 키워드들
        List<String> keywordNames = diary.getKeywords().stream()
                                         .filter(k -> k.getKeywordId() != null)
                                         .map(k -> k.getKeywordId().getName())
                                         .collect(Collectors.toList());
        model.addAttribute("keywordNames", keywordNames);

        List<Keyword> allKeywords = keywordService.findAllKeywordEntities();
        Map<String, List<Keyword>> grouped = allKeywords.stream()
                                                        .collect(Collectors.groupingBy(k -> k.getType().name()));
        model.addAttribute("keywordGroups", grouped);
        return "diary/edit";
    }
}
