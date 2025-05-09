package com.grepp.diary.app.model.diary.entity;

import com.grepp.diary.app.model.diary.code.DiaryImgType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
public class DiaryImg {

    @Id
    @GeneratedValue
    private Integer imgId;
    private String savePath;
    @Enumerated(EnumType.STRING)
    private DiaryImgType type;
    private String originName;
    private String renamedName;
    private Boolean isUse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;
}
