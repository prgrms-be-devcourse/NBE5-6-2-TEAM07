package com.grepp.diary.app.model.member;

import com.grepp.diary.app.model.auth.code.Role;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public Integer getAllMemberCount() {
        return memberRepository.countByEnabledTrue();
    }

    public void signup(Member dto, Role role) {
        if (memberRepository.existsById(dto.getUserId())){
            throw new IllegalArgumentException("이미 존재하는 사용자 ID입니다.");
        }

        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        dto.setPassword(encodedPassword);

        dto.setRole(role);
        memberRepository.save(dto);

    }
}
