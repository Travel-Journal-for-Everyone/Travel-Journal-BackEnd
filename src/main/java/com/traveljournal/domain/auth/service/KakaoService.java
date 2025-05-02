package com.traveljournal.domain.auth.service;

import org.springframework.stereotype.Service;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.dto.kakao.KakaoIdTokenInfo;
import com.traveljournal.domain.auth.dto.kakao.KakaoMemberInfo;
import com.traveljournal.domain.auth.dto.kakao.KakaoTokenResponse;
import com.traveljournal.domain.auth.util.KakaoClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
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
	public LoginCombinedResponse processKakaoLoginWithCode(String code, String deviceId,
		SocialProvider socialProvider) {

		// 카카오 토큰 획득
		KakaoTokenResponse kakaoTokenResponse = kakaoClient.getKakaoToken(code);

		return processKakaoLoginWithIdToken(kakaoTokenResponse.id_token(), deviceId, socialProvider);
	}

	public LoginCombinedResponse processKakaoLoginWithIdToken(String idToken, String deviceId,
		SocialProvider socialProvider) {

		// ID Token으로 카카오 사용자 정보 가져오기
		KakaoIdTokenInfo kakaoIdTokenInfo = kakaoClient.getKakaoMemberInfoFromIdToken(idToken);

		// 회원 찾기 또는 생성
		Member member = getMemberFromIdToken(kakaoIdTokenInfo, socialProvider);

		// JWT 토큰 생성 및 저장
		TokenInfo tokenInfo = createAndSaveTokens(member, deviceId);

		return createLoginResponse(member, tokenInfo);
	}

	/**
	 * 카카오 계정 연결 끊기 (회원탈퇴)
	 * 1. 카카오 API를 통해 연결 끊기 요청
	 * 2. 회원 정보 삭제
	 */
	public void unlinkKakaoAccount(Long memberId) {

		// 회원 정보 조회
		Member member = memberService.findById(memberId);

		// 카카오 API를 통해 연결 끊기
		kakaoClient.unlinkKakaoUser(member.getProviderId());

		// 회원 정보 삭제
		memberService.deleteMember(memberId);
	}

	private Member getMemberFromIdToken(KakaoIdTokenInfo kakaoIdTokenInfo, SocialProvider socialProvider) {
		return memberService.findOrCreateMember(
			KakaoMemberInfo.of(
				Long.parseLong(kakaoIdTokenInfo.sub()),
				kakaoIdTokenInfo.email(),
				kakaoIdTokenInfo.profile_image_url()
			), socialProvider
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
}
