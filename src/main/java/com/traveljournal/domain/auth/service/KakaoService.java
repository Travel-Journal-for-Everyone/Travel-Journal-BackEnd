package com.traveljournal.domain.auth.service;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.auth.dto.KakaoIdTokenInfo;
import com.traveljournal.domain.auth.dto.KakaoMemberInfo;
import com.traveljournal.domain.auth.dto.KakaoTokenResponse;
import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.util.KakaoClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KakaoService {

	private final TokenService tokenService;
	private final KakaoClient kakaoClient;
	private final MemberService memberService;
	/**
	 * 카카오 로그인 처리
	 * 1. 카카오 인증 코드로 액세스 토큰 요청
	 * 2. 카카오 액세스 토큰으로 사용자 정보 요청
	 * 3. 이메일로 회원 조회 또는 생성
	 * 4. JWT 토큰 생성 및 저장
	 * 5. 로그인 응답 생성
	 */
	public LoginCombinedResponse processKakaoLoginWithCode(String code, String deviceId, SocialProvider socialProvider) {

		// 카카오 토큰 획득
		KakaoTokenResponse kakaoTokenResponse = kakaoClient.getKakaoToken(code);

		return processKakaoLoginWithIdToken(kakaoTokenResponse.id_token(), deviceId, socialProvider);
	}

	public LoginCombinedResponse processKakaoLoginWithIdToken(String idToken, String deviceId, SocialProvider socialProvider) {

		// ID Token으로 카카오 사용자 정보 가져오기
		KakaoIdTokenInfo kakaoIdTokenInfo = kakaoClient.getKakaoMemberInfoFromIdToken(idToken);

		// 회원 찾기 또는 생성
		Member member = getMemberFromIdToken(kakaoIdTokenInfo, socialProvider);

		// JWT 토큰 생성 및 저장
		TokenInfo tokenInfo = createAndSaveTokens(member, deviceId);

		return createLoginResponse(member, tokenInfo);
	}

	private Member getMemberFromIdToken(KakaoIdTokenInfo kakaoIdTokenInfo, SocialProvider socialProvider) {
		return memberService.findOrCreateMember(
			KakaoMemberInfo.of(
				Long.parseLong(kakaoIdTokenInfo.sub()),
				kakaoIdTokenInfo.email(),
				kakaoIdTokenInfo.nickname(),
				kakaoIdTokenInfo.profile_image_url()
			), socialProvider
		);
	}

	private TokenInfo createAndSaveTokens(Member member, String deviceId) {
		// JWT 토큰 생성
		TokenInfo tokenInfo = tokenService.createTokens(member.getEmail(), deviceId);

		// 토큰 저장
		tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());

		return tokenInfo;
	}

	private LoginCombinedResponse createLoginResponse(Member member, TokenInfo tokenInfo) {
		return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
	}
}
