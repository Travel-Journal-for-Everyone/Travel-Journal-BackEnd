package com.traveljournal.domain.member.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.entity.SocialToken;
import com.traveljournal.domain.member.repository.SocialTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SocialTokenService {

	private final MemberService memberService;
	private final SocialTokenRepository socialTokenRepository;
	private final TokenService tokenService;

	@Transactional
	public void saveOrUpdateSocialToken(Long memberId, String refreshToken,
		SocialProvider provider, LocalDateTime expiryDate) {
		Member member = memberService.findById(memberId);

		// 기존 토큰이 있는지 확인 후 업데이트 또는 생성
		Optional<SocialToken> existingToken =
			socialTokenRepository.findByMemberIdAndProvider(memberId, provider);

		if (existingToken.isPresent()) {
			// 기존 토큰 업데이트
			SocialToken token = existingToken.get();
			token.updateRefreshToken(refreshToken, expiryDate);
		} else {
			// 새 토큰 생성
			SocialToken token = SocialToken.builder()
				.member(member)
				.refreshToken(refreshToken)
				.provider(provider)
				.expiryDate(expiryDate)
				.build();
			socialTokenRepository.save(token);
		}
	}

	/**
	 * 소셜 리프레시 토큰 조회
	 */
	@Transactional(readOnly = true)
	public Optional<String> getSocialRefreshToken(Long memberId, SocialProvider provider) {
		return socialTokenRepository.findByMemberIdAndProvider(memberId, provider)
			.filter(token -> !token.isExpired())
			.map(SocialToken::getRefreshToken);
	}
}