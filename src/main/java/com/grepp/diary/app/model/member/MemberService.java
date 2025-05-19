package com.grepp.diary.app.model.member;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.member.dto.MemberDto;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.mail.MailTemplate;
import com.grepp.diary.infra.response.ResponseCode;
import jakarta.transaction.Transactional;
import java.nio.channels.FileChannel;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final MailTemplate mailTemplate;

    @Value("${app.domain}")
    private String domain;

    public Integer getAllMemberCount() {
        return memberRepository.countByEnabledTrue();
    }

    public void signup(MemberDto dto, Role role) {
        if (memberRepository.existsById(dto.getUserId())){
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);

        dto.setRole(role);
        memberRepository.save(dto.toEntity());
    }

    public void sendVerificationMail(String token, MemberDto dto) {
        if(memberRepository.existsById(dto.getUserId()))
            throw new CommonException(ResponseCode.BAD_REQUEST);

        mailTemplate.setTo(dto.getEmail());
        mailTemplate.setTemplatePath("/member/regist-verification");
        mailTemplate.setSubject("회원가입을 환영합니다!");
        mailTemplate.setProperties("domain", domain);
        mailTemplate.setProperties("token", token);
        mailTemplate.send();
    }

    public Member getMemberByUserId(String userId) {

        Optional<Member> result = memberRepository.findById(userId);
        if (result.isEmpty()) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
        return result.get();
    }

    @Transactional
    public boolean existsByEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }

    @Transactional
    public Optional<Member> findById(String userId) {
        return memberRepository.findById(userId);
    }

    @Transactional
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    @Transactional
    public boolean existsByUserIdAndEmail(String userId, String email) {
        return memberRepository.existsByUserIdAndEmail(userId, email);
    }

    @Transactional
    public Optional<Member> findByUserIdAndEmail(String userId, String email) {
        return memberRepository.findByUserIdAndEmail(userId, email);
    }

    // 비밀번호 비교를 통한 사용자 검증
    @Transactional
    public boolean isPasswordValid(String userId, String rawPassword) {
        Member member = memberRepository.findByUserId(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.MEMBER_NOT_FOUND));

        return passwordEncoder.matches(rawPassword, member.getPassword());
    }

    @Transactional
    public boolean updateEmail(String userId, String email) {
        return memberRepository.updateEmail(userId, email) > 0;
    }

    @Transactional
    public String getEncodedPassword(String userId, String email) {
        return memberRepository.findByUserIdAndEmail(userId, email)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND))
            .getPassword();
    }

    @Transactional
    public void updatePassword(String userId, String email, String encodedPassword) {
        Optional<Member> optionalMember = memberRepository.findByUserIdAndEmail(userId, email);
        if (optionalMember.isPresent()) {
            Member member = optionalMember.get();
            member.setPassword(encodedPassword); // 암호화된 비밀번호 설정
        } else {
            throw new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.");
        }
    }

    @Transactional
    public String findUserIdByEmail(String sessionEmail) {
        return memberRepository.findByEmail(sessionEmail)
            .map(Member::getUserId)
            .orElseThrow(
                () -> new CommonException(ResponseCode.BAD_REQUEST, "해당 이메일로 가입된 계정이 없습니다."));
    }
}
