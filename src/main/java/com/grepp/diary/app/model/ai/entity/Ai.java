package com.grepp.diary.app.model.ai.entity;
import com.grepp.diary.app.model.custom.entity.Custom;
import com.grepp.diary.app.model.reply.entity.Reply;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Ai {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Integer aiId;

    @Column(length = 50)
    private String name;

    @Column(length = 500)
    private String prompt;

    @Column(name = "is_use")
    private boolean isUse;

    @OneToMany(mappedBy = "ai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Custom> customs = new ArrayList<>();

    @OneToMany(mappedBy = "ai", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

}
