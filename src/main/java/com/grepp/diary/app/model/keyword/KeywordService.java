package com.grepp.diary.app.model.keyword;

import com.grepp.diary.app.model.keyword.entity.Keyword;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    public List<String> findAllKeywords() {
        return keywordRepository.findAll().stream()
                                     .map(Keyword::getName)
                                     .distinct()
                                     .collect(Collectors.toList());
    }


}
