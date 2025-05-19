package com.grepp.diary.app.controller.web.app;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("app")
public class AppController {

    private final CustomService customService;
    private final MemberService memberService;

    @GetMapping
    public String showHome(Authentication authentication, Model model) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/"; // 인증 안 된 경우 로그인 페이지로
        }

        String userId = authentication.getName();
        Member member = memberService.getMemberByUserId(userId);
        Role role = member.getRole();

        // 관리자면 신규회원 전용 페이지가 아니라 /admin 페이지로 redirect
        if( role == Role.ROLE_ADMIN ) {
            return "redirect:/admin";
        }

        // custom 테이블에서 userId로 조회
        Optional<Custom> custom = customService.findByUserId(userId);

        String name = member.getName();
        model.addAttribute("name", name);

        if (custom.isEmpty()) {
            return "onboarding/onboarding"; // 신규회원 전용 페이지
        }
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
        return "app/settings/settings";
    }

    @GetMapping("/settings/email")
    public String showChangeEmail(){
        return "app/settings/settings-email";
    }

    @GetMapping("/settings/password")
    public String showChangePassword(){
        return "app/settings/settings-password";
    }

    @GetMapping("/settings/ai")
    public String showChangeAi(){
        return "app/settings/settings-ai";
    }
}
