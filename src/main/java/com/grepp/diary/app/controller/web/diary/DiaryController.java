package com.grepp.diary.app.controller.web.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final KeywordService keywordService;

    @GetMapping
    public String showDiaryWritePage(Model model) {
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




}
