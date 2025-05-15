package com.grepp.diary.app.model.auth;

import com.grepp.diary.app.model.auth.domain.Principal;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.mail.MailTemplate;
import com.grepp.diary.infra.response.ResponseCode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthService implements UserDetailsService {

    private final MailTemplate mailTemplate;
    private final Map<String, String> authCodeStorage = new HashMap<>(); // 인증번호 저장용
    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public UserDetails loadUserByUsername(String username) {

        Member member = memberRepository.findById(username)
            .orElseThrow(() -> new UsernameNotFoundException("아이디가 존재하지 않습니다."));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(member.getRole().name()));

        // 스프링시큐리티는 기본적으로 권한 앞에 ROLE_ 이 있음을 가정
        // hasRole("ADMIN") =>  ROLE_ADMIN 권한이 있는 지 확인.
        return Principal.createPrincipal(member, authorities);
    }

    public String generateAndSendCode(String email) {
        // 1. 인증번호 생성
        String code = String.valueOf((int) ((Math.random() * 900000) + 100000));
        authCodeStorage.put(email, code);

        // 2. 메일 내용 구성
        String subject = "Diary 인증번호 안내"; // 추후에 앱이름으로 대체
        String text = "안녕하세요.\n\n" +
            "요청하신 인증번호는 아래와 같습니다.\n\n" +
            "인증번호는 [" + code + "] 입니다.\n\n" +
            "감사합니다.";

        // 3. 메일 전송
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setFrom(from);
        message.setSubject(subject);
        message.setText(text);

        javaMailSender.send(message);

        return code;
    }

    public String findUserIdByEmail(String sessionEmail) {
        return memberRepository.findByEmail(sessionEmail)
            .map(Member::getUserId)
            .orElseThrow(
                () -> new CommonException(ResponseCode.BAD_REQUEST, "해당 이메일로 가입된 계정이 없습니다."));
    }

    public boolean existsByUserIdAndEmail(String userId, String email) {
        return memberRepository.existsByUserIdAndEmail(userId, email);
    }

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
}
