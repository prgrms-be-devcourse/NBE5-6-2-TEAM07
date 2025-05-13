package com.grepp.diary.app.controller.web.admin;

import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.member.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("admin")
public class AdminController {

    private final KeywordService keywordService;
    private final MemberService memberService;
    private final DiaryService diaryService;

    @GetMapping
    public String index() {
        keywordService.findMostPopular();
        log.info("popular : {}", keywordService.findMostPopular());
        memberService.getAllMemberCount();
        log.info("memberCount : {}", memberService.getAllMemberCount());
        diaryService.getMonthDiariesCount();
        log.info("month diaries : {}", diaryService.getMonthDiariesCount());

        return "admin/admin-index";
    }

    @GetMapping("keyword")
    public String keyword() {
        return "admin/admin-keyword";
    }
}
