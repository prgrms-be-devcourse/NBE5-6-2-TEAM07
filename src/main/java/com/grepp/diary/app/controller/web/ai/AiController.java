package com.grepp.diary.app.controller.web.ai;

import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final XssProtectionUtils xssUtils;


    @GetMapping("chat")
    public String chatView(@RequestParam int diaryId, Model model) {
        Diary diary = diaryService.getDiaryById(diaryId);
        String replyContent = diary.getReply().getContent();
        String aiName = diary.getMember().getCustom().getAi().getName();
        Integer aiId = diary.getMember().getCustom().getAi().getAiId();
        model.addAttribute("diaryId", diaryId);
        // 렌더링 시 이스케이프 처리 X -> 여기서 이스케이프
        model.addAttribute("diaryReply", xssUtils.escapeHtmlWithLineBreaks(replyContent));
        model.addAttribute("aiName", aiName);
        model.addAttribute("aiId", aiId);
        return "api/ai/chat";
    }

}
