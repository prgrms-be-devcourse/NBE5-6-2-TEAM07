package com.grepp.diary.app.controller.web.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("app")
public class AppController {

    @GetMapping
    public String showHome() {
        return "app/home";
    }

    @GetMapping("/timeline")
    public String showTimeline(){
        return "app/timeline";
    }

    @GetMapping("/dashboard")
    public String showDashboard(){
        return "app/member-dashboard";
    }

    @GetMapping("/settings")
    public String showSetting(){
        return "app/settings";
    }
}
