package com.traveljournal.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.entity.SocialToken;

@Repository
public interface SocialTokenRepository extends JpaRepository<SocialToken, Long> {
	Optional<SocialToken> findByMemberIdAndProvider(Long memberId, SocialProvider provider);

	void deleteByMemberId(Long memberId);
}
