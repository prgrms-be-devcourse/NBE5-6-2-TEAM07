package com.grepp.diary.app.controller.api.reply;

import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.dto.DiaryDto;
import com.grepp.diary.app.model.reply.ReplyAiService;
import com.grepp.diary.app.model.reply.ReplyService;
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
    private final ReplyService replyService;
    private final DiaryService diaryService;

    @GetMapping("test")
    public String chat(@RequestParam(required = false) String message){
        String chat = replyAiService.chat(message);
        System.out.println(chat);
        return chat;
    }

    @GetMapping("reply")
    public String singleReply(@RequestParam int diaryId){
        DiaryDto dto = diaryService.getDiaryDto(diaryId);
        String content = dto.getContent();
        String reply = replyAiService.reply(content);
        System.out.println(reply);
        return reply;
    }


}
