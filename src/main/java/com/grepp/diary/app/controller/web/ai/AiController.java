package com.grepp.diary.app.controller.web.ai;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("ai")
public class AiController {

    private final DiaryService diaryService;
    private final CustomService customService;
    private final XssProtectionUtils xssUtils;


    @GetMapping("chat")
    public String chatView(
        @RequestParam int diaryId,
        Authentication authentication,
        Model model
    ) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }
        String userId = authentication.getName();

        Optional<Custom> result = customService.findByUserId(userId);
        if (result.isEmpty()) {
            return "onboarding/onboarding";
        }
        Ai ai = result.get().getAi();

        Diary diary = diaryService.getDiaryById(diaryId);
        // 렌더링 시 이스케이프 처리 X -> 여기서 이스케이프
        model.addAttribute("diaryId", diaryId);
        model.addAttribute("diaryReply", xssUtils.escapeHtmlWithLineBreaks(diary.getReply().getContent()));
        model.addAttribute("aiName", ai.getName());
        model.addAttribute("aiId", ai.getAiId());
        return "api/ai/chat";
    }

}
