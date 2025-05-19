package com.grepp.diary.app.model.member.repository;

import com.grepp.diary.app.model.member.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String>, MemberRepositoryCustom {

    Integer countByEnabledTrue();

    Optional<Member> findByEmail(String email);

    boolean existsByUserIdAndEmail(String userId, String email);

    Optional<Member> findByUserIdAndEmail(String userId, String email);

    Optional<Member> findByUserId(String userId);
}
