package com.grepp.diary.app.controller.api.admin;

import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordIdRequest;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordResponse;
import com.grepp.diary.app.controller.api.admin.payload.AdminKeywordWriteRequest;
import com.grepp.diary.app.model.keyword.KeywordService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminApiController {

    private final KeywordService keywordService;

    @GetMapping("keyword")
    public AdminKeywordResponse getAllKeywords(
        @RequestParam String type
    ) {
        return AdminKeywordResponse.fromDtoList(
            keywordService.findKeywordsByType(type)
        );
    }

    @PatchMapping("keyword/active")
    public List<Integer> modifyKeywordActive(
        @RequestBody AdminKeywordIdRequest request
    ) {
        List<Integer> keywordIds = request.getKeywordIds();

        return keywordService.modifyKeywordActivate(keywordIds);
    }

    @PatchMapping("keyword/nonactive")
    public List<Integer> modifyKeywordNonActive(
        @RequestBody AdminKeywordIdRequest request
    ) {
        List<Integer> keywordIds = request.getKeywordIds();

        return keywordService.modifyKeywordNonActivate(keywordIds);
    }

    @PatchMapping("keyword/modify")
    public Boolean modifyKeyword(
        @RequestBody AdminKeywordWriteRequest keywordWriteRequest
    ) {
        keywordService.modifyKeyword(keywordWriteRequest);

        return true;
    }

    @PostMapping("keyword")
    public Boolean createKeyword(
        @RequestBody AdminKeywordWriteRequest keywordWriteRequest
    ) {
        keywordService.createKeyword(keywordWriteRequest);

        return true;
    }

}
