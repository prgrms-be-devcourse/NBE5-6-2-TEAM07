package com.grepp.diary.app.model.member;

import com.grepp.diary.app.model.member.entity.Member;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getMemberByUserId(String userId) {
        Optional<Member> result = memberRepository.findById(userId);
        if (result.isEmpty()) {
            throw new RuntimeException("존재하지 않는 회원입니다.");
        }
        return result.get();
    }
}
