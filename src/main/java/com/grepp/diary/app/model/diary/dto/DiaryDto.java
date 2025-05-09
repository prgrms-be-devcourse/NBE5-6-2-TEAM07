package com.grepp.diary.app.model.diary.dto;

import com.grepp.diary.app.model.diary.code.Emotion;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.reply.entity.Reply;
import java.time.LocalDateTime;

public class DiaryDto {

    private Integer diaryId;
    private Member member;
    private Reply reply;
    private String content;
    private Emotion emotion;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Boolean isUse;

}
