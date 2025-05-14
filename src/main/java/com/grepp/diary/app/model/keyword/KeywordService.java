package com.grepp.diary.app.model.keyword;

import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordWriteRequest;
import com.grepp.diary.app.model.keyword.dto.KeywordAdminDto;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Optional;
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

    public List<KeywordAdminDto> getMostPopular() {
        List<Tuple> result = keywordRepository.getMostPopularKeywords();

        return result.stream()
            .map(tuple -> {
                Keyword keyword = tuple.get(0, Keyword.class);
                Long count = tuple.get(1, Long.class);

                KeywordAdminDto keywordAdminDto = mapper.map(keyword, KeywordAdminDto.class);
                keywordAdminDto.setCount(count.intValue());

                return keywordAdminDto;
            })
            .collect(Collectors.toList());
    }

    public List<String> findAllKeywordName() {
        return keywordRepository.findAll().stream()
                                     .map(Keyword::getName)
                                     .distinct()
                                     .collect(Collectors.toList());
    }

    public List<KeywordAdminDto> findKeywordsByType(String keywordType) {
        return keywordRepository.findKeywordsByType(keywordType);
    }

    @Transactional
    public List<Integer> modifyKeywordActivate(List<Integer> keywordIds) {
        return keywordRepository.activeKeywords(keywordIds);
    }

    @Transactional
    public List<Integer> modifyKeywordNonActivate(List<Integer> keywordIds) {
        return keywordRepository.nonActiveKeywords(keywordIds);
    }

    @Transactional
    public Boolean modifyKeyword(AdminKeywordWriteRequest keywordWriteRequest) {
        Optional<Keyword> optionalKeyword = keywordRepository.findById(keywordWriteRequest.id());

        if (optionalKeyword.isEmpty()) {
            throw new RuntimeException("Keyword not found");
        }

        Keyword keyword = optionalKeyword.get();
        keyword.setName(keywordWriteRequest.name());
        keyword.setType(keywordWriteRequest.keywordType());

        keywordRepository.save(keyword);

        return true;
    }

    @Transactional
    public Boolean createKeyword(AdminKeywordWriteRequest keywordWriteRequest) {
        Keyword keyword = new Keyword();

        keyword.setName(keywordWriteRequest.name());
        keyword.setType(keywordWriteRequest.keywordType());
        keyword.setIsUse(true);

        keywordRepository.save(keyword);

        return true;
    }
}
