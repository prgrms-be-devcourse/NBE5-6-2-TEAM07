package com.grepp.diary.app.model.ai;

import com.grepp.diary.app.controller.api.admin.payload.AdminAiResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminAiWriteRequest;
import com.grepp.diary.app.model.ai.dto.AiAdminDto;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.querydsl.core.Tuple;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiService {

    private final AiRepository aiRepository;

    public AiDto getSingleAi(Integer id) {
        Ai ai = aiRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("AI not found"));

        return AiDto.fromEntity(ai);
    }

    @Transactional
    public List<Integer> modifyAiActivate(List<Integer> aiIds) {
        for (Integer id : aiIds) {
            aiRepository.findById(id).ifPresent(ai -> ai.setIsUse(true));
        }
        return aiIds;
    }

    @Transactional
    public List<Integer> modifyAiNonActivate(List<Integer> aiIds) {
        for (Integer id : aiIds) {
            aiRepository.findById(id).ifPresent(ai -> ai.setIsUse(false));
        }
        return aiIds;
    }

    @Transactional
    public Boolean modifyAi(AdminAiWriteRequest adminAiWriteRequest) {
        Optional<Ai> optionalAi = aiRepository.findById(adminAiWriteRequest.getId());

        if(optionalAi.isEmpty()) {
            throw new RuntimeException("Ai not found");
        }

        Ai ai = optionalAi.get();
        ai.setName(adminAiWriteRequest.getName());
        ai.setMbti(adminAiWriteRequest.getMbti());
        ai.setInfo(adminAiWriteRequest.getInfo());
        ai.setPrompt(adminAiWriteRequest.getPrompt());

        aiRepository.save(ai);

        return true;
    }

    @Transactional
    public Boolean createAi(AdminAiWriteRequest adminAiWriteRequest) {
        Ai ai = new Ai();

        ai.setName(adminAiWriteRequest.getName());
        ai.setMbti(adminAiWriteRequest.getMbti());
        ai.setInfo(adminAiWriteRequest.getInfo());
        ai.setPrompt(adminAiWriteRequest.getPrompt());
        ai.setIsUse(true);

        aiRepository.save(ai);

        return true;
    }
}
