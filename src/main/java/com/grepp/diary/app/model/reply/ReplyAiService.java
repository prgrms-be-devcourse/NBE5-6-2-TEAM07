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

    @SystemMessage("당신은 일기를 읽고 그 사람의 하루에 대한 답장을 작성합니다.")
    String reply(String content);

    @SystemMessage("당신은 사용자가 작성한 일기와 그에 대해 당신이 작성했던 답장을 바탕으로 대화합니다."
        + "이미 답장을 작성했던 적이 있기 때문에 인사는 생략해주세요."
        + "이전 대화 내용이 있다면 대화의 문맥에 맞춰서 대답해주세요.")
    String chat(String content);

    @SystemMessage("당신은 사용자가 작성했던 일기와 그에 대해 당신이 작성했던 답장을 바탕으로 사용자와 대화를 나눴습니다."
        + "이제 대화의 내용을 요약해야 합니다. 사용자를 지칭할 때는 '당신', 그리고 당신 자신을 지칭할 때는 '저'라고 지칭해 주세요."
        + "요약이 완료되면 요약 내용과 당신이 작성했던 일기 답변의 원본을 함께 전달해 주세요."
        + "그리고 전달할 때 각 내용은 마크다운 기호로 구분하지 말고 이모지를 사용해서 구분해 주세요.")
    String memo(String prompt);
}
