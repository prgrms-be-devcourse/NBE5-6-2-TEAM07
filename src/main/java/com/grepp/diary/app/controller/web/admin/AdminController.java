package com.grepp.diary.app.controller.web.admin;

import com.grepp.diary.app.model.keyword.KeywordService;
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

    @GetMapping
    public String index() {
        keywordService.findMostPopular();
        log.info("popular : {}", keywordService.findMostPopular());
        return "admin/admin-index";
    }
}
