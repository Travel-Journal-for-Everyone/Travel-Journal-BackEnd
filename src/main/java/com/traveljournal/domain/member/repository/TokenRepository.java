package com.traveljournal.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.traveljournal.domain.member.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByDeviceId(String deviceId);

	Optional<Token> findByDeviceIdAndMemberId(String deviceId, Long memberId);

	@Modifying
	@Query("DELETE FROM Token t WHERE t.member.id = :memberId")
	void deleteAllByMemberId(@Param("memberId") Long memberId);
}