package com.grepp.diary.infra.util.file;

import com.grepp.diary.app.model.diary.code.DiaryImgType;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {
    // 파일명만 인코딩하는 함수
    public static String encodeFilenameInPath(String path) {
        if (path == null || path.isEmpty()) return path;
        int idx = path.lastIndexOf("/");
        String dir = idx >= 0 ? path.substring(0, idx + 1) : "";
        String filename = idx >= 0 ? path.substring(idx + 1) : path;
        String encodedFilename = URLEncoder
            .encode(filename, StandardCharsets.UTF_8).replace("+", "%20");
        return dir + encodedFilename;
    }

    private DiaryImg saveFileAndBuildEntity(MultipartFile file, Diary diary) {
        //String uploadDir = "src/main/resources/photo";

        if (diary.getDiaryId() == null) {
            throw new IllegalArgumentException("Diary must be saved before saving image (diaryId is null)");
        }

        String diaryId = String.valueOf(diary.getDiaryId());
        String projectRoot = System.getProperty("user.dir");  //프로젝트 폴더 경로
        String uploadDir = projectRoot + File.separator + "photo" + File.separator + diaryId;

        String originalFilename = file.getOriginalFilename();
        String uuid = UUID
            .randomUUID().toString();
        String renamedName = uuid + "_" + originalFilename;
        Path targetPath = Paths
            .get(uploadDir).resolve(renamedName).normalize();

        try {
            Files.createDirectories(targetPath.getParent()); // 폴더 없으면 생성
            file.transferTo(targetPath.toFile()); // 실제 파일 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalFilename, e);
        }

        String webPath = "photo/" + diaryId + "/" + renamedName; //프론트에서 접근 가능하도록 DB에 저장할 웹 경로 생성

        DiaryImg img = new DiaryImg();
        img.setDiary(diary);
        img.setOriginName(originalFilename);
        img.setRenamedName(renamedName);
        img.setSavePath(webPath);
        img.setType(DiaryImgType.THUMBNAIL); // 기본 타입
        img.setIsUse(true);
        return img;
    }
}
