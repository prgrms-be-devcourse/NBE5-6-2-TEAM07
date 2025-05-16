package com.grepp.diary.app.controller.api.ai;

import com.grepp.diary.app.controller.api.ai.AiRequestQueue.AiRequestTask;
import com.grepp.diary.app.controller.api.ai.payload.ChatRequest;
import com.grepp.diary.app.controller.api.ai.payload.Message;
import com.grepp.diary.app.model.ai.AiReplyScheduler;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.chat.ChatService;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.ai.AiChatService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class AiApiController {

    private final AiChatService aiChatService;
    private final DiaryService diaryService;
    private final AiReplyScheduler aiReplyScheduler;
    private final AiRequestQueue aiRequestQueue;
    private final ChatService chatService;

    @GetMapping("reply")
    @ResponseBody
    public String singleReply(@RequestParam int diaryId){
        String prompt = aiReplyScheduler.buildReplyPrompt(diaryId);
        String replyContent = aiChatService.reply(prompt);

        log.info("prompt : {}", prompt);
        log.info("reply: {}", replyContent);
        diaryService.registReply(diaryId, replyContent);
        return replyContent;
    }

    @GetMapping("retry-failed-replies")
    @ResponseBody
    public String retryFailedReplies() {
        List<DiaryDto> failedDiaries = diaryService.getNoReplyDtos();
        if (failedDiaries.isEmpty()) {
            return "No failed replies to process";
        }
        log.info("Start retry for {} failed replies", failedDiaries.size());
        aiReplyScheduler.schedulingBatchProcess(failedDiaries, 0);
        return "Complete retry for " + failedDiaries.size() + " failed replies";
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
    public CompletableFuture<String> chatWithAi(@RequestBody ChatRequest chatRequest) {
        Diary diary = diaryService.getDiaryById(chatRequest.getDiaryId());
        String prompt = buildChatPrompt(diary, chatRequest.getChatHistory(), chatRequest.getUserMessage());
        log.info("prompt : {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestTask(() -> aiChatService.chat(prompt), future)
        );

        return future;
    }

    @PostMapping("chat/memo")
    @ResponseBody
    public CompletableFuture<String> chatMemo(@RequestBody ChatRequest chatRequest) {
        int diaryId = chatRequest.getDiaryId();
        int chatCount = chatRequest.getChatHistory().size();
        log.info("chatCount : {}", chatCount);
        Diary diary = diaryService.getDiaryById(diaryId);
        String userId = diary.getMember().getUserId();
        Integer replyId = diary.getReply().getReplyId();
        String prompt = buildMemoPrompt(diary, chatRequest.getChatHistory());
        log.info("prompt : {}", prompt);

        CompletableFuture<String> future = new CompletableFuture<>();
        aiRequestQueue.addRequest(
            new AiRequestTask(() -> {
                String memo = aiChatService.memo(prompt);
                log.info("diary id: {}", diaryId);
                log.info("memo content: {}", memo);
                diaryService.registReply(diaryId, memo);
                chatService.registCount(userId, replyId, chatCount);
                return memo;
            }, future)
        );
        return future;
    }

    @PostMapping("chat/end")
    @ResponseBody
    public String chatEnd(@RequestBody ChatRequest chatRequest) {
        int diaryId = chatRequest.getDiaryId();
        int chatCount = chatRequest.getChatHistory().size();
        Diary diary = diaryService.getDiaryById(diaryId);
        String userId = diary.getMember().getUserId();
        Integer replyId = diary.getReply().getReplyId();

        chatService.registCount(userId, replyId, chatCount);
        log.info("Updated chat count {} for diaryId {}", chatCount, diaryId);

        return "Updated chat count";
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
            prompt.append(" 사용자에게는 존댓말로 정중하게 말해주세요. 따뜻하고 배려 있는 어투를 사용해 주세요.");
        } else {
            prompt.append(" 사용자에게는 반말로, 친구처럼 다정하고 편안한 말투로 이야기해 주세요.");
        }

        if (custom.isLong()) {
            prompt.append(" 답변은 1000자 이내로, 감정이나 상황을 충분히 설명할 수 있도록 길고 풍부하게 작성해 주세요.");
        } else {
            prompt.append(" 답변은 500자 분량으로, 감정과 핵심 메시지를 적절히 전달할 수 있도록 간결하게 작성해 주세요.");
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

}
