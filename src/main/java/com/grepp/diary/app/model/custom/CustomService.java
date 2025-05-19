package com.grepp.diary.app.model.custom;

import com.grepp.diary.app.model.ai.dto.AiAdminDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.custom.repository.CustomRepository;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.querydsl.core.Tuple;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomService {

    private final CustomRepository customRepository;
    private final ModelMapper mapper;
    private final MemberService memberService;
    private final AiRepository aiRepository;

    public Optional<Custom> findByUserId(String userId) {
        return customRepository.findByMember_UserId(userId);
    }

    public List<AiAdminDto> getAiByLimit(Integer limit) {
        List<Tuple> result = customRepository.getAiByLimit(limit);

        return result.stream()
            .map(tuple -> {
                Ai ai = tuple.get(0, Ai.class);
                Long count = tuple.get(1, Long.class);

                AiAdminDto aiDto = mapper.map(ai, AiAdminDto.class);
                aiDto.setCount(count.intValue());

                return aiDto;
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public void registerCustomSettings(String userId, int aiId, boolean isFormal, boolean isLong) {
        Custom custom = new Custom();
        custom.setFormal(isFormal);
        custom.setLong(isLong);

        Member member = memberService.findById(userId)
            .orElseThrow(() -> new CommonException(ResponseCode.MEMBER_NOT_FOUND));
        custom.setMember(member);

        Ai ai = aiRepository.findById(aiId)
            .orElseThrow(() -> new CommonException(ResponseCode.NOT_FOUND, "해당 ID의 AI가 존재하지 않습니다."));
        custom.setAi(ai);

        customRepository.save(custom);
    }
}
