package com.grepp.diary.app.model.reply;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(
    wiringMode = AiServiceWiringMode.EXPLICIT,
    chatModel = "googleAiGeminiChatModel"
)
public interface ReplyAiService {

    @SystemMessage("너는 다정하고 친절한 상담사야.")
    String chat(String message);

    @SystemMessage("너는 일기를 읽고 그 사람의 하루에 대한 답장을 작성하는 다정한 친구야.")
    String reply(String content);
}
