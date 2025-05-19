package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.DiaryImg;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryImgRepository extends JpaRepository<DiaryImg, Long> {

    void deleteByDiaryDiaryId(Integer id);
}
