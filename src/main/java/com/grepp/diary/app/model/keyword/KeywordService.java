package com.grepp.diary.app.model.keyword;

import com.grepp.diary.app.model.keyword.dto.KeywordDto;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
import com.querydsl.core.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final ModelMapper mapper;

    public List<List<Object>> getMostPopular() {
        List<Tuple> result = keywordRepository.getMostPopularKeywords();

        return result.stream()
            .map(tuple -> {
                Keyword keyword = tuple.get(0, Keyword.class);
                Long count = tuple.get(1, Long.class);
                KeywordDto keywordDto = mapper.map(keyword, KeywordDto.class);

                List<Object> list = new ArrayList<>();
                list.add(keywordDto);
                list.add(count.intValue());

                return list;
            })
            .collect(Collectors.toList());
    }

    public List<Keyword> findAllKeywordEntities() {
        return keywordRepository.findAll();
    }
}
