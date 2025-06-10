package com.grepp.diary.app.model.custom.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.grepp.diary.app.model.custom.dto.CustomAiInfoDto;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
class CustomRepositoryTest {

    @Autowired
    private CustomRepository customRepository;

    @Test
    @DisplayName("사용자 ID로 AI 정보 조회 성공")
    void getCustomAiInfo_success() {
        // given
        String userId = "user01";

        // when
        Optional<CustomAiInfoDto> result =
            customRepository.getCustomAiInfoByUserId(userId);

        // then
        assertTrue(result.isPresent(), "결과가 존재해야 합니다.");
    }

    @Test
    @DisplayName("사용자 ID로 AI 정보 조회 실패")
    void getCustomAiInfo_fail() {
        // given
        String userId = "user999";

        // when
        Optional<CustomAiInfoDto> result =
            customRepository.getCustomAiInfoByUserId(userId);

        // then
        assertFalse(result.isPresent(), "존재하지 않는 사용자 ID로 조회시 결과가 비어있어야 합니다.");
    }
}