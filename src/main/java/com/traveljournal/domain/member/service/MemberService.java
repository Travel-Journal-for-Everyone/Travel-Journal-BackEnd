package com.traveljournal.domain.member.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.auth.dto.FirstLoginRequest;
import com.traveljournal.domain.auth.dto.SocialMemberInfo;
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
	public Member findOrCreateMember(SocialMemberInfo socialMemberInfo, SocialProvider socialProvider) {
		String email = socialMemberInfo.getEmail();

		Optional<Member> existingMember = findByEmail(email);

		if (existingMember.isPresent()) {
			return existingMember.get();
		}

		Member newMember = createNewMember(socialMemberInfo, socialProvider);

		return memberRepository.save(newMember);
	}

	public Member createNewMember(SocialMemberInfo socialMemberInfo, SocialProvider socialProvider) {

		String randomNickname = generateRandomNickname();
		
		return Member.builder()
			.email(socialMemberInfo.getEmail())
			.nickname(randomNickname)
			.profileImageUrl(socialMemberInfo.getProfileImageUrl())
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

	private String generateRandomNickname() {
		// 알파벳 문자열 정의
		String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		String lowerAlphabet = alphabet.toLowerCase();

		// 랜덤 객체 생성
		Random random = new Random();

		// StringBuilder 생성
		StringBuilder sb = new StringBuilder();

		// 랜덤 닉네임 길이 (예: 8자)
		int length = 8;

		// 첫 글자는 대문자로
		sb.append(alphabet.charAt(random.nextInt(alphabet.length())));

		// 나머지 글자 생성
		for(int i = 1; i < length; i++) {
			sb.append(lowerAlphabet.charAt(random.nextInt(lowerAlphabet.length())));
		}

		// 숫자 추가 (예: 10-99)
		int randomNumber = random.nextInt(90) + 10;
		sb.append(randomNumber);

		return "User" + sb.toString();
	}
}
