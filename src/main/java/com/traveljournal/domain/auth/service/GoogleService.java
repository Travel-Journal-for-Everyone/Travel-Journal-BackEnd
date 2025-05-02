package com.traveljournal.domain.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.dto.google.GoogleIdTokenInfo;
import com.traveljournal.domain.auth.dto.google.GoogleMemberInfo;
import com.traveljournal.domain.auth.dto.google.GoogleTokenResponse;
import com.traveljournal.domain.auth.util.GoogleClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.SocialTokenService;
import com.traveljournal.domain.member.service.TokenService;
import com.traveljournal.global.exception.BadRequestException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleService {

	private final TokenService tokenService;
	private final MemberService memberService;
	private final GoogleClient googleClient;
	private final SocialTokenService socialTokenService;

	public LoginCombinedResponse processGoogleLoginWithCode(String code, String deviceId,
		SocialProvider socialProvider, String platform) {
		if (code == null || code.isBlank()) {
			throw new BadRequestException("구글 인증 코드가 비어 있습니다.");
		}
		GoogleTokenResponse googleTokenResponse = googleClient.getGoogleToken(code);

		return processGoogleLoginWithIdToken(googleTokenResponse.id_token(), deviceId, socialProvider, platform, googleTokenResponse.refresh_token());
	}

	public LoginCombinedResponse processGoogleLoginWithIdToken(String idToken, String deviceId,
		SocialProvider socialProvider, String platform, String refreshToken) {
		GoogleIdTokenInfo googleIdTokenInfo = googleClient.getGoogleMemberInfoFromIdToken(idToken);

		Member member = getMemberFromGoogleIdToken(googleIdTokenInfo, socialProvider);

		TokenInfo tokenInfo = createAndSaveTokens(member, deviceId);

		if (refreshToken != null && !refreshToken.isEmpty()) {
			LocalDateTime expiryDate = LocalDateTime.now().plusMonths(6);
			socialTokenService.saveOrUpdateSocialToken(
				member.getId(),
				refreshToken,
				socialProvider,
				expiryDate,
				platform);
		}

		return createLoginResponse(member, tokenInfo);
	}

	private Member getMemberFromGoogleIdToken(GoogleIdTokenInfo googleIdTokenInfo, SocialProvider socialProvider) {
		return memberService.findOrCreateMember(
			new GoogleMemberInfo(
				googleIdTokenInfo.sub(),
				googleIdTokenInfo.email(),
				googleIdTokenInfo.profile_image_url()
			),
			socialProvider
		);
	}

	private TokenInfo createAndSaveTokens(Member member, String deviceId) {
		TokenInfo tokenInfo = tokenService.createTokens(member.getProviderId(), deviceId);
		tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());
		return tokenInfo;
	}

	private LoginCombinedResponse createLoginResponse(Member member, TokenInfo tokenInfo) {
		return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
	}

	public void unlinkGoogleAccount(Long memberId) {
		Member member = memberService.findById(memberId);
		String googleUserId = member.getProviderId();

		if (googleUserId == null || googleUserId.isEmpty()) {
			throw new BadRequestException("구글 계정 연결 정보가 없습니다.");
		}

		Optional<String> refreshTokenOpt = socialTokenService.getSocialRefreshToken(memberId, SocialProvider.GOOGLE);

		if (refreshTokenOpt.isEmpty()) {
			throw new BadRequestException("구글 계정 연결 해제 실패: 리프레시 토큰이 없습니다.");
		}

		String refreshToken = refreshTokenOpt.get();
		googleClient.revokeToken(refreshToken);

		memberService.deleteMember(memberId);
	}
}