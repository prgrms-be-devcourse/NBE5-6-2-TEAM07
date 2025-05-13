package com.grepp.diary.app.model.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.diary.code.DiaryImgType;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.diary.repository.DiaryImgRepository;
import com.grepp.diary.app.model.diary.repository.DiaryKeywordRepository;
import com.grepp.diary.app.model.diary.repository.DiaryRepository;
import com.grepp.diary.app.model.keyword.entity.DiaryKeyword;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.keyword.repository.KeywordRepository;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.app.model.member.repository.MemberRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final MemberRepository memberRepository;
    private final KeywordRepository keywordRepository;
    private final DiaryKeywordRepository diaryKeywordRepository;
    private final DiaryImgRepository diaryImgRepository;

    public Diary saveDiary(DiaryRequest form, String userId) {
        Member member = memberRepository.findById(userId)
                                        .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다: " + userId));

        Diary diary = new Diary();
        diary.setEmotion(form.getEmotion());
        diary.setContent(form.getContent());
        diary.setCreatedAt(LocalDateTime.now());
        diary.setModifiedAt(LocalDateTime.now());
        diary.setIsUse(true);
        diary.setMember(member);
        diaryRepository.save(diary);

        // 키워드를 선택했을 경우 키워드 저장
        if (form.getKeywords() != null && !form.getKeywords().isEmpty()) {
            List<DiaryKeyword> keywordList = form.getKeywords().stream()
                                                 .distinct()
                                                 .map(name -> {
                                                     Keyword keywordEntity = keywordRepository.findByName(name)
                                                                                              .orElseThrow(() -> new IllegalArgumentException("키워드 없음: " + name));

                                                     DiaryKeyword dk = new DiaryKeyword();
                                                     dk.setDiaryId(diary);
                                                     dk.setKeywordId(keywordEntity);
                                                     return dk;
                                                 }).collect(Collectors.toList());

            diaryKeywordRepository.saveAll(keywordList);
        }

        // 사진을 업로드 했을 경우 사진 저장
        if (form.getImages() != null && !form.getImages().isEmpty()) {
            List<DiaryImg> imageList = form.getImages().stream()
                                           .filter(file -> !file.isEmpty())
                                           .map(file -> saveFileAndBuildEntity(file, diary))
                                           .collect(Collectors.toList());

            diaryImgRepository.saveAll(imageList);
        }

        return diary;

    }

    /** 시작일과 끝을 기준으로 해당 날짜 사이에 존재하는 일기들을 반환합니다. */
    public List<Diary> getDiariesDateBetween(String userId, LocalDate start, LocalDate end) {
        LocalDateTime startDateTime = start.atStartOfDay();
        LocalDateTime endDateTime = end.plusDays(1).atStartOfDay();
        return diaryRepository.findByMemberUserIdAndCreatedAtBetweenAndIsUseTrue(userId, startDateTime, endDateTime);
    }

    public Integer getMonthDiariesCount() {
        LocalDateTime startDateTime = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endDateTime = startDateTime.plusMonths(1);

        return diaryRepository.countByCreatedAtBetweenAndIsUseTrue(startDateTime, endDateTime);
    }



    /** 최근 14개의 작성된 일기를 가져옵니다 */
    public List<Diary> getRecentDiariesWithImages(String userId) {
        Pageable limit = PageRequest.of(0, 14);
        return diaryRepository.findRecentDiariesWithImages(userId, limit);
    }

//    public void saveDiaryImg(DiaryRequest form) {
//        List<MultipartFile> images = form.getImages();
//
//        List<DiaryImg> imageList = form.getImages().stream()
//                                       .filter(file -> !file.isEmpty())
//                                       .map(file -> saveFileAndBuildEntity(file, diary))
//                                       .collect(Collectors.toList());
//
////        for (MultipartFile image : images) {
////            String originalName = image.getOriginalFilename();
////            // 파일 저장
////            image.transferTo(new File("/upload/path/" + originalName));
////        }
//
//    }


    private DiaryImg saveFileAndBuildEntity(MultipartFile file, Diary diary) {
        //String uploadDir = "src/main/resources/photo";
        String diaryId = String.valueOf(diary.getDiaryId());
        String projectRoot = System.getProperty("user.dir");  //프로젝트 폴터 경로
        String uploadDir = projectRoot + File.separator + "photo" + File.separator + diaryId;

        String originalFilename = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String renamedName = uuid + "_" + originalFilename;
        Path targetPath = Paths
            .get(uploadDir).resolve(renamedName).normalize();

        try {
            Files.createDirectories(targetPath.getParent()); // 폴더 없으면 생성
            file.transferTo(targetPath.toFile()); // 실제 파일 저장
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + originalFilename, e);
        }

        String webPath = "/photo/" + diaryId + "/" + renamedName; //프론트에서 접근 가능하도록 DB에 저장할 웹 경로 생성

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
