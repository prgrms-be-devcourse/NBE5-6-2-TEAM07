package com.grepp.diary.app.model.member.repository;

import org.springframework.data.jpa.repository.Query;

public interface MemberRepositoryCustom {
    int updateEmail(String userId, String email);
    int updatePassword(String userId, String password);
}
