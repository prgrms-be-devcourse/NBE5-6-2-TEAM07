package com.grepp.diary.app.controller.web.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {


    @GetMapping("/login")
    public String showLoginForm(Model model) {
        if (!model.containsAttribute("signinForm")) {
            model.addAttribute("signinForm", new SigninForm());
        }
        return "index";
    }


    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("signinForm") SigninForm signinForm,
        BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            // 👉 BindingResult와 SigninForm을 flash로 전달 (함께 전달해야 오류메시지 1회성으로(=새로고침시엔삭제) 남음)
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signinForm", bindingResult);
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/member/login";
        }

        // 로그인 성공
        // 로그인 기능 미구현xxxxxxxxxx
        if ("admin".equals(signinForm.getUserId()) && "1234".equals(signinForm.getPassword())) {
            return "redirect:/app";
        } else {
            // 실패 시에도 form 다시 넘기기
            redirectAttributes.addFlashAttribute("error", "아이디나 비밀번호가 틀렸습니다.");
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/member/login";
        }
    }

}
