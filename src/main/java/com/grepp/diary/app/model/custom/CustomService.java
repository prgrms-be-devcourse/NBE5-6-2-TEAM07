package com.grepp.diary.app.model.custom;

import com.grepp.diary.app.model.ai.dto.AiAdminDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.dto.CustomAIDto;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.custom.repository.CustomRepository;
import com.querydsl.core.Tuple;
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

    public Optional<Custom> findByUserId(String userId) {
        return customRepository.findByMemberUserId(userId);
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

    public void save(Custom custom) {
        customRepository.save(custom);
    }

    public CustomAIDto getCustomAiByUserId(String id) {
        return customRepository.getCustomAiByUserId(id);
    }
}
