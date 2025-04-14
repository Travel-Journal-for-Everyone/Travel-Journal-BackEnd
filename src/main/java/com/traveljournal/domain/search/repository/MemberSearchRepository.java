package com.traveljournal.domain.search.repository;

import com.traveljournal.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MemberSearchRepository extends JpaRepository<Member, Long> {
    Page<Member> findByNicknameContaining(String nickname, Pageable pageable);
}
