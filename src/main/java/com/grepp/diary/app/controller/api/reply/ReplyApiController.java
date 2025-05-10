package com.grepp.diary.app.controller.api.reply;

import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.ReplyAiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/ai")
public class ReplyApiController {

    private final ReplyAiService replyAiService;
    private final DiaryService diaryService;
    private final MemberService memberService;

    @GetMapping("test")
    public String chat(@RequestParam(required = false) String message){
        return replyAiService.chat(message);
    }

    @GetMapping("reply")
    public String singleReply(@RequestParam int diaryId){
        String finalPrompt = createFinalPrompt(diaryId);
        String replyContent = replyAiService.reply(finalPrompt);

        diaryService.registReply(diaryId, replyContent);
        return replyContent;
    }

    @GetMapping("replies")
    public String batchReply() {
        List<DiaryDto> dtos = diaryService.getNoReplyDtos();
        for (DiaryDto dto : dtos) {
            try {
                Integer diaryId = dto.getDiaryId();
                String finalPrompt = createFinalPrompt(diaryId);
                String replyContent = replyAiService.reply(finalPrompt);
                log.info("diary id: {}", diaryId);
                log.info("reply content: {}", replyContent);

                diaryService.registReply(diaryId, replyContent);
            } catch (Exception e) {
                log.error("Reply failed for diary id {}: {}", dto.getDiaryId(), e.getMessage());
            }
        }
        return "batch complete";
    }

    private String createFinalPrompt(int diaryId) {
        DiaryDto dto = diaryService.getDto(diaryId);
        String content = dto.getContent();
        String userId = dto.getUserId();

        Member member = memberService.getMemberByUserId(userId);
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        StringBuilder builder = new StringBuilder(ai.getPrompt());
        if (custom.isFormal()) {
            builder.append(" 정중한 경어체로");
        } else {
            builder.append(" 친구처럼 편한 말투로");
        }

        if (custom.isLong()) {
            builder.append(" 300자 정도로 작성해주세요.");
        } else {
            builder.append(" 간결하게 핵심만 넣어서 작성해주세요.");
        }

        return builder.append("일기 내용: ").append(content).toString();
    }
}
