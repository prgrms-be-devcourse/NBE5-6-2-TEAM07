package com.grepp.diary.app.model.member.repository;

import com.grepp.diary.app.model.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface MemberRepository extends JpaRepository<Member, String> {

    Integer countByEnabledTrue();
}
