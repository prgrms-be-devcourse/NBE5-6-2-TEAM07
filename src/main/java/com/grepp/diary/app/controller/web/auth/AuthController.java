package com.grepp.diary.app.controller.web.auth;

import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.controller.web.auth.form.SignupForm;
import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.dto.MemberDto;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {


    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AuthService authService;

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("signinForm") SigninForm signinForm,
        BindingResult bindingResult,
        HttpSession session,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signinForm", bindingResult);
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/";
        }

        try {
            UserDetails userDetails = authService.loadUserByUsername(signinForm.getUserId());

            if (!passwordEncoder.matches(signinForm.getPassword(), userDetails.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            request.getSession().setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


            return "redirect:/app";

        } catch (UsernameNotFoundException | IllegalArgumentException e) {
            // 로그인 실패 시
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/";
        }
    }

    // 회원가입 페이지 반환
    @GetMapping("/regist")
    public String regist(Model model) {
        if(!model.containsAttribute("signupForm")) {
            model.addAttribute("signupForm", new SignupForm());
        }
        return "/member/regist";
    }


    // 회원가입 요청 → 이메일 인증 메일 전송
    @PostMapping("/regist")
    public String regist(@Valid @ModelAttribute("signupForm") SignupForm signupForm,
        BindingResult bindingResult,
        HttpSession session,
        RedirectAttributes redirectAttributes) {

        if (!signupForm.getPassword().equals(signupForm.getRepassword())) {
            bindingResult.rejectValue("repassword", "password.mismatch", "비밀번호가 일치하지 않습니다.");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.signupForm", bindingResult);
            redirectAttributes.addFlashAttribute("signupForm", signupForm);
            return "redirect:/member/regist";
        }

        // 1. 이메일 인증 토큰 생성 및 세션 저장
        String token = UUID.randomUUID().toString();
        MemberDto dto = signupForm.toDto();
        memberService.sendVerificationMail(token, dto);
        session.setAttribute(token, dto);

        redirectAttributes.addFlashAttribute("message", "인증 메일을 전송했습니다. 이메일을 확인해 주세요.");
        return "redirect:/member/regist-mail";
    }

    @GetMapping("/regist-mail")
    public String showRegistMailPage() {
        return "member/regist-mail";
    }

    @GetMapping("regist/{token}")
    public String verifiedRegist(
        @PathVariable
        String token,
        HttpSession session
    ){
        MemberDto dto = (MemberDto) session.getAttribute(token);

        if (dto == null) {
            throw new CommonException(ResponseCode.INVALID_TOKEN);
        }

        memberService.signup(dto, Role.ROLE_USER);
        session.removeAttribute(token);

        // 자동 로그인 처리
        UserDetails userDetails = authService.loadUserByUsername(dto.getUserId());

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // SPRING_SECURITY_CONTEXT 세션에 설정
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "redirect:/app";
    }
}
