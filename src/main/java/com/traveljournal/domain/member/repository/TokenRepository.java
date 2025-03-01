package com.traveljournal.domain.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.traveljournal.domain.member.entity.Token;

public interface TokenRepository extends JpaRepository<Token, Long> {

	Optional<Token> findByDeviceId(String deviceId);

	Optional<Token> findByDeviceIdAndMemberId(String deviceId, Long memberId);

}