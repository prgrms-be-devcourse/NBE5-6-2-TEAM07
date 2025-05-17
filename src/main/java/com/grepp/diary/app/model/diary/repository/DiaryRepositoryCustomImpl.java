package com.grepp.diary.app.model.diary.repository;

import com.grepp.diary.app.model.diary.entity.Diary;
import com.grepp.diary.app.model.diary.entity.QDiary;
import com.grepp.diary.app.model.diary.entity.QDiaryImg;
import com.grepp.diary.app.model.diary.repository.DiaryRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    QDiary diary = QDiary.diary;
    QDiaryImg diaryImg = QDiaryImg.diaryImg;

    @Override
    public List<Diary> findRecentDiariesWithImages(String userId, Pageable pageable) {
        return queryFactory
            .selectFrom(diary)
            .leftJoin(diary.images, diaryImg).fetchJoin() // Diary.images → DiaryImg에서 mappedBy되어야 함
            .where(
                diary.member.userId.eq(userId),
                diary.isUse.isTrue(),
                diaryImg.isUse.isTrue().or(diaryImg.isUse.isNull()) // 이미지 사용 중이거나 없는 경우 허용
            )
            .orderBy(diary.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .distinct() // 중복 Diary 제거 (fetchJoin 시 유용)
            .fetch();
    }

    @Override
    public List<Object[]> findDateAndEmotionByUserIdAndYear(String userId, int year) {
        return queryFactory
            .select(diary.date, diary.emotion)
            .from(diary)
            .where(
                diary.member.userId.eq(userId),
                diary.isUse.isTrue(),
                diary.date.year().eq(year)
            )
            .fetch()
            .stream()
            .map(tuple -> new Object[] {
                tuple.get(diary.date),
                tuple.get(diary.emotion)
            })
            .toList();
    }
}
