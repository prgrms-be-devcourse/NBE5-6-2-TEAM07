package com.grepp.diary.app.controller.web.auth.form;

import com.grepp.diary.app.model.member.entity.Member;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignupForm {

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    private String userId;

    @Size(max = 20)
    @Pattern(regexp = "^(?=.*\\d)(?=.*[a-zA-Z])(?=.*[^a-zA-Zㄱ-힣])(?!.*[ㄱ-힣]).{8,}$"
        ,message = "비밀번호는 8자리 이상의 영문자, 숫자, 특수문자 조합으로 이루어져야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수 입력 항목입니다.")
    private String repassword;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(max = 20)
    private String name;

    public Member toDto() {
        Member member = new Member();
        member.setEmail(email);
        member.setName(name);
        member.setPassword(password);
        member.setUserId(userId);

        return member;
    }
}
