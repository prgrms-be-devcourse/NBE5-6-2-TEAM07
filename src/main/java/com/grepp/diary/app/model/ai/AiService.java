package com.grepp.diary.app.model.ai;

import com.grepp.diary.app.controller.api.admin.payload.AdminAiWriteRequest;
import com.grepp.diary.app.model.ai.dto.AiDto;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.ai.repository.AiImgRepository;
import com.grepp.diary.app.model.ai.repository.AiRepository;
import com.grepp.diary.app.model.common.code.ImgType;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.response.ResponseCode;
import com.grepp.diary.infra.util.file.FileDto;
import com.grepp.diary.infra.util.file.FileUtil;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AiService {

    private final AiRepository aiRepository;
    private final AiImgRepository aiImgRepository;
    private final FileUtil fileUtil;

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
    public Boolean createAi(List<MultipartFile> images, AdminAiWriteRequest request) {
        try {
            Ai ai = new Ai();

            ai.setName(request.getName());
            ai.setMbti(request.getMbti());
            ai.setInfo(request.getInfo());
            ai.setPrompt(request.getPrompt());
            ai.setIsUse(true);

            aiRepository.save(ai);

            if (images.size() > 0) {
                List<FileDto> imageList = fileUtil.upload(images, "ai", ai.getAiId());
                AiImg aiImg = new AiImg(ImgType.THUMBNAIL, imageList.getFirst());

                aiImg.setAi(ai);
                aiImg.setIsUse(true);

                aiImgRepository.save(aiImg);
            }

            return true;
        } catch (IOException e) {
            throw new CommonException(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
