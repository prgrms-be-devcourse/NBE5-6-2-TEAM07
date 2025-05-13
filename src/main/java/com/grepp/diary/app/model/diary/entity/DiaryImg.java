package com.grepp.diary.app.model.diary.entity;

import com.grepp.diary.app.model.diary.code.DiaryImgType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter @Setter @ToString
@NoArgsConstructor
@Table(name="IMAGE")
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

    public String getRenamedPath(){
        return "/download/" + savePath + renamedName;
    }
}
