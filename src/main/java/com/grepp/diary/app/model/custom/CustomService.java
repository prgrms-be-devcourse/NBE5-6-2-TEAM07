package com.grepp.diary.app.model.custom;

import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.repository.CustomRepository;
import com.querydsl.core.Tuple;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    public List<List<Object>> getMostPopular() {
        List<Tuple> result = customRepository.getMostPopularAis();

        return result.stream()
            .map(tuple -> {
                Ai ai = tuple.get(0, Ai.class);
                Long count = tuple.get(1, Long.class);
                AiDto dto = mapper.map(ai, AiDto.class);

                List<Object> pairList = new ArrayList<>();
                pairList.add(dto);
                pairList.add(count.intValue());

                return pairList;
            })
            .collect(Collectors.toList());
    }
}
