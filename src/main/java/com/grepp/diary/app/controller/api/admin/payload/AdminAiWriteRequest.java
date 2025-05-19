package com.grepp.diary.app.controller.api.admin.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminAiWriteRequest {
    private Integer id;
    private String name;
    private String mbti;
    private String info;
    private String prompt;

    private List<MultipartFile> images;
}
