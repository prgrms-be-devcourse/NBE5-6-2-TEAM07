package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.model.diary.entity.Diary;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {

    List<Diary> findByReplyIsNull();
}
