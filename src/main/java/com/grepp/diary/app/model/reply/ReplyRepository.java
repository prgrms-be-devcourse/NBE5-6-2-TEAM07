package com.grepp.diary.app.model.reply;

import com.grepp.diary.app.model.reply.entity.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Integer> {

}
