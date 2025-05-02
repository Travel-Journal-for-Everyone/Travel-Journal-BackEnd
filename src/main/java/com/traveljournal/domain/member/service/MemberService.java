package com.traveljournal.domain.member.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.auth.dto.SocialMemberInfo;
import com.traveljournal.domain.member.dto.MemberProfileResponse;
import com.traveljournal.domain.member.dto.ProfileRequest;
import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.repository.MemberRepository;
import com.traveljournal.domain.member.repository.SocialTokenRepository;
import com.traveljournal.domain.member.repository.TokenRepository;
import com.traveljournal.global.exception.MemberDeleteException;
import com.traveljournal.global.exception.ResourceNotFoundException;

import io.jsonwebtoken.io.IOException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

	private final MemberRepository memberRepository;
	private final ImageService imageService;
	private final TokenRepository tokenRepository;
	private final SocialTokenRepository socialTokenRepository;

	@PersistenceContext
	private EntityManager entityManager;

	/**
	 * 소셜 고유 회원번호로 조회
	 */
	@Transactional(readOnly = true)
	public Optional<Member> findByProviderId(String providerId) {
		return memberRepository.findByProviderId(providerId);
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
		String providerId = socialMemberInfo.getId();

		Optional<Member> existingMember = findByProviderId(providerId);

		if (existingMember.isPresent()) {
			return existingMember.get();
		}

		Member newMember = createNewMember(socialMemberInfo, socialProvider);

		return memberRepository.save(newMember);
	}

	public Member createNewMember(SocialMemberInfo socialMemberInfo, SocialProvider socialProvider) {

		String randomNickname = generateRandomNickname();

		return Member.builder()
			.providerId(socialMemberInfo.getId())
			.nickname(randomNickname)
			.profileImageUrl(imageService.getDefaultProfileImageUrl())
			.accountScope(AccountScope.PUBLIC)
			.socialProvider(socialProvider)
			.build();
	}

	@Transactional
	public void updateProfile(Long memberId, ProfileRequest request, MultipartFile profileImage) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

		String profileImageUrl = member.getProfileImageUrl();

		// 프로필 이미지가 제공된 경우 업로드
		if (profileImage != null && !profileImage.isEmpty() && !request.isMemberDefaultImage()) {
			try {
				profileImageUrl = imageService.uploadProfileImage(profileImage, memberId);
			} catch (IOException e) {
				throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
			} catch (java.io.IOException e) {
				throw new RuntimeException(e);
			}
		} else if(request.isMemberDefaultImage()) {
			profileImageUrl = imageService.getDefaultProfileImageUrl();
		}

		// 회원 정보 업데이트
		member.updateProfile(
			request.getNickname(),
			request.getAccountScope(),
			profileImageUrl,
			request.isMemberDefaultImage()
		);

		memberRepository.save(member);
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
		for (int i = 1; i < length; i++) {
			sb.append(lowerAlphabet.charAt(random.nextInt(lowerAlphabet.length())));
		}

		// 숫자 추가 (예: 10-99)
		int randomNumber = random.nextInt(90) + 10;
		sb.append(randomNumber);

		return "User" + sb.toString();
	}

	@Transactional(readOnly = true)
	public MemberProfileResponse getMemberProfile(Long memberId) {

		Member member = findById(memberId);

		return MemberProfileResponse.of(member);
	}

	@Transactional
	public void deleteMember(Long memberId) {
		try {
			Member member = findById(memberId);


			tokenRepository.deleteAllByMemberId(memberId);
			socialTokenRepository.deleteByMemberId(memberId);
			memberRepository.delete(member);

			memberRepository.flush();
			entityManager.clear();

		} catch (Exception e) {
			throw new MemberDeleteException("회원 삭제 중 오류가 발생했습니다.", e);
		}
	}
}