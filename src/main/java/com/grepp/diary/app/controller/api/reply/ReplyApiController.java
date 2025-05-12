package com.grepp.diary.app.controller.api.reply;

import com.grepp.diary.app.controller.api.reply.payload.ChatRequest;
import com.grepp.diary.app.controller.api.reply.payload.Message;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.ReplyAiService;
import com.grepp.diary.app.model.reply.ReplyService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("api/ai")
public class ReplyApiController {

    private final ReplyAiService replyAiService;
    private final DiaryService diaryService;
    private final ReplyService replyService;
    private final MemberService memberService;

    @GetMapping("test")
    @ResponseBody
    public String test(@RequestParam(required = false) String message){
        return replyAiService.test(message);
    }

    @GetMapping("reply")
    @ResponseBody
    public String singleReply(@RequestParam int diaryId){
        String prompt = buildReplyPrompt(diaryId);
        String replyContent = replyAiService.reply(prompt);

        log.info("prompt : {}", prompt);
        diaryService.registReply(diaryId, replyContent);
        return replyContent;
    }

    @GetMapping("replies")
    @ResponseBody
    public String batchReply() {
        List<DiaryDto> dtos = diaryService.getNoReplyDtos();
        List<Integer> failedIds = new ArrayList<>();
        for (DiaryDto dto : dtos) {
            try {
                Integer diaryId = dto.getDiaryId();
                String finalPrompt = buildReplyPrompt(diaryId);
                String replyContent = replyAiService.reply(finalPrompt);

                log.info("diary id: {}", diaryId);
                log.info("reply content: {}", replyContent);

                diaryService.registReply(diaryId, replyContent);
            } catch (Exception e) {
                log.error("Reply failed for diary id {}: {}", dto.getDiaryId(), e.getMessage());
                failedIds.add(dto.getDiaryId());
            }
        }
        return "Batch complete. Failed diary IDs: " + failedIds;
    }

    // 매일 오전 3시 실행
    @Scheduled(cron = "0 0 3 * * *")
    public void scheduledBatchReply() {
        batchReply();
    }

    @GetMapping("chat")
    public String chatView(@RequestParam int diaryId, Model model) {
        Diary diary = diaryService.getDiaryById(diaryId);
        String aiName = diary.getMember().getCustom().getAi().getName();
        Integer aiId = diary.getMember().getCustom().getAi().getAiId();
        model.addAttribute("diaryId", diaryId);
        model.addAttribute("diaryReply", diary.getReply().getContent().replace("\n","<br/>"));
        model.addAttribute("aiName", aiName);
        model.addAttribute("aiId", aiId);
        model.addAttribute("initialPrompt", "어떤 얘기를 나누고 싶으신가요?");
        return "api/ai/chat";
    }

    @PostMapping("chat")
    @ResponseBody
    public String chatWithAi(@RequestBody ChatRequest chatRequest) {
        Diary diary = diaryService.getDiaryById(chatRequest.getDiaryId());
        String prompt = buildChatPrompt(diary, chatRequest.getChatHistory(), chatRequest.getUserMessage());
        log.info("prompt : {}", prompt);
        return replyAiService.chat(prompt);
    }

    @PostMapping("chat/memo")
    @ResponseBody
    public String chatMemo(@RequestBody ChatRequest chatRequest) {
        int diaryId = chatRequest.getDiaryId();
        Diary diary = diaryService.getDiaryById(diaryId);
        String prompt = buildMemoPrompt(diary, chatRequest.getChatHistory());
        log.info("prompt : {}", prompt);
        String memo = replyAiService.memo(prompt);

        log.info("diary id: {}", diaryId);
        log.info("memo content: {}", memo);

        // diary, reply 저장
        diaryService.registReply(diaryId, memo);
        return memo;
    }

    private String buildMemoPrompt(Diary diary, List<Message> chatHistory) {

        StringBuilder prompt = new StringBuilder();
        prompt.append("\n사용자가 작성했던 일기 내용: ").append(diary.getContent());
        prompt.append("\n당신이 작성했던 일기 답변: ").append(diary.getReply().getContent());

        // 이전 대화 내역
        prompt.append("\n--대화 내용--");
        for (Message msg : chatHistory) {
            prompt.append("\n사용자: ").append(msg.getUser());
            prompt.append("\n당신: ").append(msg.getAi());
        }

        return prompt.toString();
    }

    private String buildChatPrompt(Diary diary, List<Message> chatHistory, String userMessage){
        Member member = diary.getMember();
        Custom custom = member.getCustom();
        Ai ai = custom.getAi();

        StringBuilder prompt = new StringBuilder(ai.getPrompt());

        if (custom.isFormal()) {
            prompt.append(" 정중한 경어체로");
        } else {
            prompt.append(" 친구처럼 편한 말투로");
        }

        if (custom.isLong()) {
            prompt.append(" 상세하게 작성하되 200자 이내로 작성해주세요.");
        } else {
            prompt.append(" 간결하게 핵심만 작성하되 150자 이내로 작성해주세요.");
        }

        prompt.append("\n사용자가 작성했던 일기 내용: ").append(diary.getContent());
        prompt.append("\n당신이 작성했던 일기 답변: ").append(diary.getReply().getContent());

        // 이전 대화 내역
        prompt.append("\n--대화 내용--");
        if (chatHistory != null && !chatHistory.isEmpty()) {
            for (Message msg : chatHistory) {
                prompt.append("\n사용자: ").append(msg.getUser());
                prompt.append("\n당신: ").append(msg.getAi());
            }
        }

        prompt.append("\n사용자: ").append(userMessage);
        prompt.append("\n당신: ");

        return prompt.toString();
    }

    private String buildReplyPrompt(int diaryId) {
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

        return builder.append("\n일기 내용: ").append(content).toString();
    }
}
