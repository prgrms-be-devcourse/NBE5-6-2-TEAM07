package com.grepp.diary.app.model.keyword;

import com.grepp.diary.app.model.keyword.dto.KeywordDto;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
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

    public List<KeywordDto> findMostPopular() {
        List<Keyword> popularKeywords = keywordRepository.findMostPopularKeywords();
        return popularKeywords.stream().map(k -> mapper.map(k, KeywordDto.class)).collect(Collectors.toList());
    }

    public List<String> findAllKeywords() {
        return keywordRepository.findAll().stream()
                                     .map(Keyword::getName)
                                     .distinct()
                                     .collect(Collectors.toList());
    }
}
