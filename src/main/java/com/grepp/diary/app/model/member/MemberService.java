package com.grepp.diary.app.model.member;

import com.grepp.diary.app.model.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Integer getAllMemberCount() {
        return memberRepository.countByEnabledTrue();
    }
}
