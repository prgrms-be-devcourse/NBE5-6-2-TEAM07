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
    String test(String message);

    @SystemMessage("당신은 일기를 읽고 그 사람의 하루에 대한 답장을 작성하는 상담사입니다.")
    String reply(String content);

    @SystemMessage("당신은 사용자가 작성한 일기와 그에 대해 당신이 작성했던 답장을 바탕으로 대화하는 상담사입니다.")
    String chat(String content);

    @SystemMessage("당신은 사용자가 작성한 일기와 그에 대해 당신이 작성했던 답장을 바탕으로 사용자와 대화를 나눴습니다."
        + "대화 내용을 요약하여 당신이 작성했던 답장에 추가해 주세요.")
    String memo(String prompt);
}
