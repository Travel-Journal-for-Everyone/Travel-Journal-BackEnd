package com.traveljournal.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.auth.dto.KakaoIdTokenInfo;
import com.traveljournal.domain.auth.dto.KakaoMemberInfo;
import com.traveljournal.domain.auth.dto.KakaoTokenResponse;
import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.auth.dto.LoginResponse;
import com.traveljournal.domain.auth.util.KakaoClient;
import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.service.MemberService;
import com.traveljournal.domain.member.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final KakaoClient kakaoClient;
	private final MemberService memberService;
	private final TokenService tokenService;

	/**
	 * 카카오 로그인 처리
	 * 1. 카카오 인증 코드로 액세스 토큰 요청
	 * 2. 카카오 액세스 토큰으로 사용자 정보 요청
	 * 3. 이메일로 회원 조회 또는 생성
	 * 4. JWT 토큰 생성 및 저장
	 * 5. 로그인 응답 생성
	 */
	@Transactional
	public LoginCombinedResponse processKakaoLoginWithCode(String code, String deviceId) {

		// 카카오 토큰 획득
		KakaoTokenResponse kakaoTokenResponse = kakaoClient.getKakaoToken(code);

		return processKakaoLoginWithIdToken(kakaoTokenResponse.id_token(), deviceId);
	}

	@Transactional
	public LoginCombinedResponse processKakaoLoginWithIdToken(String idToken, String deviceId) {

		// ID Token으로 카카오 사용자 정보 가져오기
		KakaoIdTokenInfo kakaoIdTokenInfo = kakaoClient.getKakaoMemberInfoFromIdToken(idToken);

		// 회원 찾기 또는 생성
		Member member = memberService.findOrCreateMember(
			KakaoMemberInfo.of(
				Long.parseLong(kakaoIdTokenInfo.sub()),
				kakaoIdTokenInfo.email(),
				kakaoIdTokenInfo.nickname(),
				kakaoIdTokenInfo.profile_image_url()
			)
		);

		// JWT 토큰 생성
		TokenInfo tokenInfo = tokenService.createTokens(member.getEmail(), deviceId);

		// 토큰 저장
		tokenService.saveOrUpdateToken(member, tokenInfo.deviceId(), tokenInfo.refreshToken());

		// 회원 정보와 토큰 정보를 포함한 응답 생성
		return LoginCombinedResponse.of(LoginResponse.from(member, tokenInfo), tokenInfo.accessToken());
	}

	/**
	 * 로그아웃 처리
	 * 특정 장치의 토큰 삭제
	 */
	@Transactional
	public void logout(Long memberId, String deviceId) {
		tokenService.deleteToken(memberId, deviceId);
	}
}