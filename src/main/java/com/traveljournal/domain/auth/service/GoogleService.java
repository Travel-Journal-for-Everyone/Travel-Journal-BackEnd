package com.traveljournal.domain.auth.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GoogleService {

	private final TokenService tokenService;
	private final MemberService memberService;
	private final GoogleClient googleClient;
	private final SocialTokenService socialTokenService;
	private final RestTemplate restTemplate;


	public LoginCombinedResponse processGoogleLoginWithCode(String code, String deviceId,
		SocialProvider socialProvider, String platform) {
		// 구글 토큰 획득
		GoogleTokenResponse googleTokenResponse = googleClient.getGoogleToken(code);

		return processGoogleLoginWithIdToken(googleTokenResponse.id_token(), deviceId, socialProvider, googleTokenResponse.refresh_token(), platform);
	}

	public LoginCombinedResponse processGoogleLoginWithIdToken(String idToken, String deviceId,
		SocialProvider socialProvider, String refreshToken, String platform) {
		// ID Token 으로 구글 사용자 정보 가져오기
		GoogleIdTokenInfo googleIdTokenInfo = googleClient.getGoogleMemberInfoFromIdToken(idToken);

		// 회원 찾기 또는 생성
		Member member = getMemberFromGoogleIdToken(googleIdTokenInfo, socialProvider);

		// JWT 토큰 생성 및 저장
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

		// 로그인 응답 생성
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
		// JWT 토큰 생성
		TokenInfo tokenInfo = tokenService.createTokens(member.getProviderId(), deviceId);

		// 토큰 저장
		tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());

		return tokenInfo;
	}

	private LoginCombinedResponse createLoginResponse(Member member, TokenInfo tokenInfo) {
		return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
	}


	public void unlinkGoogleAccount(Long memberId) {
		// 회원 정보 조회
		Member member = memberService.findById(memberId);
		// 회원의 구글 식별자(sub) 가져오기
		String googleUserId = member.getProviderId();

		if (googleUserId == null || googleUserId.isEmpty()) {
			throw new RuntimeException("구글 계정 연결 정보가 없습니다.");
		}

		try {
			// 리프레시 토큰 조회 및 처리
			Optional<String> refreshTokenOpt = socialTokenService.getSocialRefreshToken(memberId, SocialProvider.GOOGLE);

			if (refreshTokenOpt.isPresent()) {
				String refreshToken = refreshTokenOpt.get();

				googleClient.revokeToken(refreshToken);

				memberService.deleteMember(memberId);
			} else {
					throw new RuntimeException("구글 계정 연결 해제 실패");
				}

		} catch (Exception e) {
			throw new RuntimeException("구글 계정 연결 해제 실패: " + e.getMessage(), e);
		}
	}
}