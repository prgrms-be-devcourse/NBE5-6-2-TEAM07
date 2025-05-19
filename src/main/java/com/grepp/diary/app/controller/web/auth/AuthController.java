package com.grepp.diary.app.controller.web.auth;

import com.grepp.diary.app.controller.web.auth.form.SettingEmailForm;
import com.grepp.diary.app.controller.web.auth.form.SigninForm;
import com.grepp.diary.app.controller.web.auth.form.SignupForm;
import com.grepp.diary.app.model.ai.AiChatService;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.auth.AuthService;
import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.custom.CustomService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.dto.MemberDto;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import dev.langchain4j.service.spring.AiService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@Slf4j
@RequestMapping("/member")
@RequiredArgsConstructor
public class AuthController {


    private final PasswordEncoder passwordEncoder;
    private final MemberService memberService;
    private final AuthService authService;
    private final CustomService customService;
    private final AiRepository aiRepository; // aiService는 사용 불가

    @PostMapping("/login")
    public String login(@Valid @ModelAttribute("signinForm") SigninForm signinForm,
        BindingResult bindingResult,
        HttpSession session,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.signinForm", bindingResult);
            redirectAttributes.addFlashAttribute("signinForm", signinForm);
            return "redirect:/";
        }

        try {
            UserDetails userDetails = authService.loadUserByUsername(signinForm.getUserId());

            if (!passwordEncoder.matches(signinForm.getPassword(), userDetails.getPassword())) {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }

            UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authToken);

            request.getSession()
                .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

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
        if (!model.containsAttribute("signupForm")) {
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

        if (memberService.existsByEmail(signupForm.getEmail())) {
            bindingResult.rejectValue("email", "email.exists", "등록된 이메일입니다.");
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                "org.springframework.validation.BindingResult.signupForm", bindingResult);
            redirectAttributes.addFlashAttribute("signupForm", signupForm);
            return "redirect:/member/regist";
        }


        try {
            // 1. 이메일 인증 토큰 생성 및 세션 저장
            String token = UUID.randomUUID().toString();
            MemberDto dto = signupForm.toDto();
            memberService.sendVerificationMail(token, dto);
            session.setAttribute(token, dto);

            redirectAttributes.addFlashAttribute("message", "인증 메일을 전송했습니다. 이메일을 확인해 주세요.");
            return "redirect:/member/regist-mail";
        } catch (CommonException e) {
            if (e.code() == ResponseCode.BAD_REQUEST) {
                bindingResult.rejectValue("userId", "user.exists", "이미 사용 중인 아이디입니다.");
                redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.signupForm", bindingResult);
                redirectAttributes.addFlashAttribute("signupForm", signupForm);
                return "redirect:/member/regist";
            }

            // 기타 예외 처리
            redirectAttributes.addFlashAttribute("error", "메일 전송 중 오류가 발생했습니다.");
            return "redirect:/member/regist";
        }
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
    ) {
        MemberDto dto = (MemberDto) session.getAttribute(token);

        if (dto == null) {
            throw new CommonException(ResponseCode.INVALID_TOKEN);
        }

        memberService.signup(dto, Role.ROLE_USER);
        session.removeAttribute(token);

        // 자동 로그인 처리
        UserDetails userDetails = authService.loadUserByUsername(dto.getUserId());

        UsernamePasswordAuthenticationToken authToken =
            new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // SPRING_SECURITY_CONTEXT 세션에 설정
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return "redirect:/app";
    }


    // 아이디 찾기 페이지 반환
    @GetMapping("/find-idpw")
    public String findIdPage(Model model) {

        return "/member/find-idpw";
    }


    @PostMapping("/auth-id")
    public String sendAuthCodeForId(@RequestParam String email, Model model,RedirectAttributes redirectAttributes,
        @RequestParam(required = false) String code, HttpSession session) {
        // 이메일 형식 검증
        if (code == null || code.isBlank()) {
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                model.addAttribute("error", "유효한 이메일 형식이 아닙니다.");
                return "/member/find-idpw";
            }
            try {
                String generatedCode = authService.generateAndSendCode(email);
                session.setAttribute("authCode", generatedCode);
                session.setAttribute("authEmail", email);
                model.addAttribute("email", email);
                model.addAttribute("step", "verify");
                model.addAttribute("message", "인증번호가 전송되었습니다.");
                return "member/find-idpw";
            } catch (CommonException e) {
                model.addAttribute("error", e.getMessage()); // 사용자에게 오류 메시지 전달
                return "member/find-idpw";
            }
        }

        // 인증번호가 입력된 상태 → 인증번호 검증 단계
        String sessionCode = (String) session.getAttribute("authCode");
        String sessionEmail = (String) session.getAttribute("authEmail");

        if (sessionCode != null && sessionCode.equals(code)) {
            try {
                String userId = authService.findUserIdByEmail(sessionEmail);
                session.removeAttribute("authCode");
                session.removeAttribute("authEmail");
                model.addAttribute("message", userId);
                return "member/find-idpw-verification";
            } catch (CommonException e) {
                model.addAttribute("error", e.getMessage());
                model.addAttribute("step", null);
                return "member/find-idpw";
            }
        } else {
            model.addAttribute("error", "인증번호가 일치하지 않습니다.");
            model.addAttribute("email", email);
            model.addAttribute("step", "verify");

            return "member/find-idpw";

        }

    }

    @PostMapping("/auth-pw")
    public String sendAuthCodeForPw(@RequestParam String email,
        @RequestParam String userId,
        @RequestParam(required = false) String code,
        Model model,
        HttpSession session) {

        if (code == null || code.isBlank()) {
            // 1. 이메일 형식 확인
            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                model.addAttribute("error", "유효한 이메일 형식이 아닙니다.");
                model.addAttribute("type", "pw");
                return "member/find-idpw";
            }

            // 2. 이메일 + 아이디로 사용자 존재 확인
            if (!authService.existsByUserIdAndEmail(userId, email)) {
                model.addAttribute("error", "일치하는 계정이 없습니다.");
                model.addAttribute("type", "pw");
                return "member/find-idpw";
            }

            // 3. 인증 코드 생성 및 저장
            String generatedCode = authService.generateAndSendCode(email);
            session.setAttribute("authCode", generatedCode);
            session.setAttribute("authEmail", email);
            session.setAttribute("authUserId", userId);

            model.addAttribute("email", email);
            model.addAttribute("userId", userId);
            model.addAttribute("step", "verify");
            model.addAttribute("message", "인증번호가 전송되었습니다.");
            model.addAttribute("type", "pw");
            return "member/find-idpw";
        }

        // 4. 인증번호 확인
        String sessionCode = (String) session.getAttribute("authCode");
        String sessionEmail = (String) session.getAttribute("authEmail");
        String sessionUserId = (String) session.getAttribute("authUserId");

        if (sessionCode != null && sessionCode.equals(code)) {
            // 세션 제거
            session.removeAttribute("authCode");

            // 비밀번호 재설정 페이지로 이동
            model.addAttribute("email", sessionEmail);
            model.addAttribute("userId", sessionUserId);
            model.addAttribute("step", "verify");

            return "member/reset-password";
        }

        model.addAttribute("error", "인증번호가 일치하지 않습니다.");
        model.addAttribute("step", "verify");
        model.addAttribute("email", email);
        model.addAttribute("userId", userId);
        model.addAttribute("type", "pw");
        return "member/find-idpw";
    }


    @PostMapping("/change-pw")
    public String changePw(@RequestParam String userId,
        @RequestParam String email,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword,
        Model model) {

        // 비밀번호 일치 여부 확인
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }

        String passwordRegex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%?^&*()_+=-]).{8,}$";
        if (!newPassword.matches(passwordRegex)) {
            model.addAttribute("error", "비밀번호는 8자리 이상의 영문자, 숫자, 특수문자 조합이어야 합니다.");
            model.addAttribute("userId", userId);
            model.addAttribute("email", email);
            return "member/reset-password";
        }

        try {
            // 이전 비밀번호와 비교
            String currentEncodedPassword = authService.getEncodedPassword(userId, email);

            // 현재 비밀번호와 새 비밀번호 비교
            if (passwordEncoder.matches(newPassword, currentEncodedPassword)) {
                model.addAttribute("error", "이미 사용 중인 비밀번호입니다.");
                model.addAttribute("userId", userId);
                model.addAttribute("email", email);
                return "member/reset-password";
            }

            // 비밀번호 암호화 후 변경
            String encodedPassword = passwordEncoder.encode(newPassword);
            authService.updatePassword(userId, email, encodedPassword);

//            model.addAttribute("message", "비밀번호가 성공적으로 변경되었습니다.");
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
        @RequestParam("name") String name,
        Authentication authentication, HttpSession session) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/";
        }

        String userId = authentication.getName(); // 로그인된 사용자 ID
        Object formalObj = session.getAttribute("isFormal");
        Object longObj = session.getAttribute("isLong");


        boolean isFormal = formalObj != null && formalObj.toString().equals("1");
        boolean isLong = longObj != null && longObj.toString().equals("1");

        Custom custom = new Custom();
        custom.setFormal(isFormal);
        custom.setLong(isLong);


        Member member = memberService.findById(userId).orElseThrow();
        custom.setMember(member);

        Ai ai = aiRepository.findById(aiId).orElseThrow(() ->
            new IllegalArgumentException("해당 ID의 AI가 존재하지 않습니다."));
        custom.setAi(ai);

        customService.save(custom);

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
        if(!memberService.isPasswordValid(userId, settingEmailForm.getPassword())) {
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
