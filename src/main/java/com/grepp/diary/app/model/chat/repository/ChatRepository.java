package com.grepp.diary.app.model.chat.repository;

import com.grepp.diary.app.model.chat.entity.Chat;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<Chat, Integer> {

    Optional<Chat> findByReply_ReplyId(Integer replyId);
}
