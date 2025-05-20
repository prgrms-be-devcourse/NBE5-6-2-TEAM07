package com.grepp.diary.app.controller.web.diary;

import com.grepp.diary.app.controller.web.diary.payload.DiaryRequest;
import com.grepp.diary.app.model.ai.entity.Ai;
import com.grepp.diary.app.model.ai.entity.AiImg;
import com.grepp.diary.app.model.diary.DiaryService;
import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.DiaryImg;
import com.grepp.diary.app.model.keyword.KeywordService;
import com.grepp.diary.app.model.keyword.entity.Keyword;
import com.grepp.diary.app.model.member.MemberService;
import com.grepp.diary.app.model.member.entity.Member;
import com.grepp.diary.infra.error.exceptions.CommonException;
import com.grepp.diary.infra.util.file.FileUtil;
import com.grepp.diary.infra.util.xss.XssProtectionUtils;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("diary")
public class DiaryController {

    private final DiaryService diaryService;
    private final KeywordService keywordService;
    private final MemberService memberService;
    private final XssProtectionUtils xssUtils;

    @GetMapping("/writing")
    public String showDiaryWritePage(
        @RequestParam(value = "date", required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
        Model model
    ) {
        LocalDate targetDate = (date != null) ? date : LocalDate.now();
        model.addAttribute("diaryDate", targetDate);
        model.addAttribute("diaryRequest", new DiaryRequest());
        List<Keyword> allKeywords = keywordService.findAllKeywordEntities();
        Map<String, List<Keyword>> grouped = allKeywords.stream()
                                                        .collect(Collectors.groupingBy(k -> k.getType().name()));
        model.addAttribute("keywordGroups", grouped);

        return "diary/write-diary";
    }

    @PostMapping
    public String writeAndSaveDiary(@ModelAttribute("diaryRequest") DiaryRequest form,
        Model model,
        @AuthenticationPrincipal UserDetails user
    ) {
        String userId = user.getUsername();

        try {
            log.info("form : {}", form);
            diaryService.saveDiary(form.getImages(), form, userId);
            return "redirect:/app";
        } catch (CommonException e) {
            model.addAttribute("error", e.getMessage());
            return "app/home"; // 동일한 페이지로 돌아가되 에러 메시지 표시
        }
    }

    @GetMapping("/record")
    public String showDiaryRecordPage(
        Model model,
        @RequestParam("date")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) // 문자열을 날짜 타입으로 변환 "yyyy-MM-dd" 형식(예: 2025-05-15)만 허용
        LocalDate targetDate,
        @AuthenticationPrincipal UserDetails user
    ) {
        String userId = user.getUsername();
        log.info("user : {}", user.getUsername());
        Member member = memberService.getMemberByUserId(userId);
        Optional<Diary> diaryExist = diaryService.findDiaryByUserIdAndDate(userId, targetDate);
        if (diaryExist.isPresent()) {
            //사진 파일 encoding
            List<DiaryImg> encodedImages = diaryExist.get().getImages().stream()
                                                     .map(img -> {
                                                    String encodedPath = FileUtil.encodeFilenameInPath(img.getSavePath());
                                                    DiaryImg copy = new DiaryImg();
                                                    copy.setSavePath(encodedPath);
                                                    copy.setOriginName(img.getOriginName());
                                                    // 필요한 다른 필드도 복사
                                                    return copy;
                                                }).collect(Collectors.toList());

            // ai 관련 정보 전달
            Ai ai = member.getCustom().getAi();
            AiImg aiImg = ai.getImages().getFirst();
            model.addAttribute("aiName", ai.getName());
            model.addAttribute("imgSavePath", aiImg.getSavePath());
            model.addAttribute("imgRenamedName", aiImg.getRenamedName());
            // 일기 답장 전달
            String content = diaryExist.get().getReply().getContent();
            model.addAttribute("replyContent", xssUtils.escapeHtmlWithLineBreaks(content));
            // 일기 사진 전달
            model.addAttribute("encodedImages", encodedImages);
            model.addAttribute("diary", diaryExist.get());
        } else {
            log.info("Diary not found");
            model.addAttribute("diary", new Diary()); // 빈 객체를 넘겨서 프론트에서 처리
        }
        return "diary/record";
    }

    @GetMapping("/edit/{id}")
    public String showDiaryEditPage(@PathVariable Integer id, Model model) {
        Diary diary = diaryService.findById(id);
        model.addAttribute("diary", diary);

        // 선택했던 키워드들
        List<String> keywordNames = diary.getKeywords().stream()
                                         .filter(k -> k.getKeywordId() != null)
                                         .map(k -> k.getKeywordId().getName())
                                         .collect(Collectors.toList());
        model.addAttribute("keywordNames", keywordNames);

        List<Keyword> allKeywords = keywordService.findAllKeywordEntities();
        Map<String, List<Keyword>> grouped = allKeywords.stream()
                                                        .collect(Collectors.groupingBy(k -> k.getType().name()));
        model.addAttribute("keywordGroups", grouped);
        return "diary/edit";
    }
}
