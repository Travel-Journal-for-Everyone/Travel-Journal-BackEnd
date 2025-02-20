package com.traveljournal.domain.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	@Transactional(readOnly = true)
	public Optional<Member> findBySocialLoginId(String socialLoginId) {
		return memberRepository.findBySocialLoginId(socialLoginId);
	}
}

