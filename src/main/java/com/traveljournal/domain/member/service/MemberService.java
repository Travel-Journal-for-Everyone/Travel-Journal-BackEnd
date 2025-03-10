package com.traveljournal.domain.member.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.auth.dto.FirstLoginRequest;
import com.traveljournal.domain.auth.dto.KakaoMemberInfo;
import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.repository.MemberRepository;
import com.traveljournal.global.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 이메일로 회원 조회
	 */
	@Transactional(readOnly = true)
	public Optional<Member> findByEmail(String email) {
		return memberRepository.findByEmail(email);
	}

	/**
	 * ID로 회원 조회
	 */
	@Transactional(readOnly = true)
	public Member findById(Long memberId) {
		return memberRepository.findById(memberId)
			.orElseThrow(() -> new ResourceNotFoundException("회원을 찾을 수 없습니다. ID: " + memberId));
	}

	/**
	 * 카카오 회원정보를 바탕으로 회원 조회 또는 생성
	 */
	@Transactional
	public Member findOrCreateMember(KakaoMemberInfo kakaoMemberInfo, SocialProvider socialProvider) {
		String email = kakaoMemberInfo.kakao_account().email();

		Optional<Member> existingMember = findByEmail(email);

		if (existingMember.isPresent()) {
			return existingMember.get();
		}

		Member newMember = createNewMember(kakaoMemberInfo, socialProvider);

		return memberRepository.save(newMember);
	}

	public Member createNewMember(KakaoMemberInfo kakaoMemberInfo, SocialProvider socialProvider) {
		return Member.builder()
			.email(kakaoMemberInfo.kakao_account().email())
			.nickname(kakaoMemberInfo.kakao_account().profile().nickname())
			.profileImageUrl(kakaoMemberInfo.kakao_account().profile().profile_image_url())
			.accountScope(AccountScope.PUBLIC)
			.socialProvider(socialProvider)
			.build();
	}

	/**
	 * 첫 로그인 완료 처리
	 */
	@Transactional
	public void completeFirstLogin(Long memberId, FirstLoginRequest firstLoginRequest) {
		Member member = findById(memberId);
		member.completeFirstLogin(firstLoginRequest.nickname(), firstLoginRequest.accountScope());
	}

	/**
	 * 회원 프로필 업데이트
	 */
	@Transactional
	public Member updateProfile(Long memberId, String nickname, String profileImageUrl,
		java.time.LocalDate birthdate, AccountScope accountScope,
		String phoneNumber) {
		Member member = findById(memberId);
		member.updateProfile(nickname, profileImageUrl, birthdate, accountScope, phoneNumber);
		return member;
	}

	@Transactional(readOnly = true)
	public boolean isDuplicate(String nickname) {
		return memberRepository.findByNickname(nickname) != null;
	}
}
