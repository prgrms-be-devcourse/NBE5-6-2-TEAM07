package com.grepp.diary.app.controller.web.member;

import com.grepp.diary.app.controller.web.member.form.SettingEmailForm;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {


    private final MemberService memberService;
    private final CustomService customService;

    @PostMapping("/change-pw")
    public String changePw(@RequestParam String userId,
        @RequestParam String email,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        Model model) {

        // 1. 비밀번호 일치 여부 확인
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }

        try {
            memberService.changePassword(userId, email, newPassword);
            return "member/find-idpw-verification";

        } catch (CommonException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }
    }


    @GetMapping("/onboarding-qna")
    public String showOnboardingQnaPqge() {
        return "onboarding/onboarding-qna";
    }

    @GetMapping("/onboarding-result")
    public String showOnboardingResultPage(HttpSession session, Model model) {
        Integer aiId = (Integer) session.getAttribute("aiId");
        String userId = (String) session.getAttribute("userId");

        // 사용자이름 조회
        Member member = memberService.getMemberByUserId(userId);
        String name = member.getName();

        model.addAttribute("aiId", aiId);
        model.addAttribute("name", name);
        return "onboarding/onboarding-result";
    }

    @PostMapping("/custom-ai")
    public String registerCustomAiResult(@RequestParam("aiId") int aiId,
        Authentication authentication,
        HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String userId = authentication.getName();

        boolean isFormal = "1".equals(String.valueOf(session.getAttribute("isFormal")));
        boolean isLong = "1".equals(String.valueOf(session.getAttribute("isLong")));

        customService.registerCustomSettings(userId, aiId, isFormal, isLong);

        return "redirect:/app";
    }

    // 회원 이메일 변경 요청
    @PostMapping("/settings/update-email")
    public String updateEmail(
        Authentication authentication,
        @Valid @ModelAttribute("emailForm") SettingEmailForm settingEmailForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes
    ) {
        if(bindingResult.hasErrors()) {
            return "redirect:/app/settings/email";
        }

        String userId = authentication.getName();
        System.out.println("[DEBUG]" + userId);
        System.out.println("[DEBUG]" + settingEmailForm.getPassword());
        System.out.println("[DEBUG]" + settingEmailForm.getNewEmail());

        // 비밀번호 확인 실패시
        if(!memberService.validUser(userId, settingEmailForm.getPassword())) {
            bindingResult.rejectValue("password", "password.invalid", "비밀번호가 일치하지 않습니다");
            return "redirect:/app/settings/email";
        }

        boolean isSuccess = memberService.updateEmail(userId, settingEmailForm.getNewEmail());
        if(!isSuccess) {
            redirectAttributes.addFlashAttribute("message", "이메일 변경도중 문제가 발생하였습니다");
            return "redirect:/app/settings/email";
        }
        redirectAttributes.addFlashAttribute("message", "성공적으로 이메일을 변경하였습니다");
        return "redirect:/app/settings";
    }
}
