package com.traveljournal.domain.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.traveljournal.domain.auth.dto.LoginCombinedResponse;
import com.traveljournal.domain.member.entity.SocialProvider;
import com.traveljournal.domain.member.service.TokenService;
import com.traveljournal.global.security.jwt.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final TokenService tokenService;
	private final KakaoService kakaoService;
	private final JwtTokenProvider jwtTokenProvider;

	@Transactional
	public LoginCombinedResponse handleLoginWithCode(SocialProvider socialProvider, String code, String deviceId) {
		switch (socialProvider) {
			case KAKAO:
				return kakaoService.processKakaoLoginWithCode(code, deviceId, socialProvider);
			default:
				throw new UnsupportedOperationException("지원되지 않는 소셜 로그인 제공자입니다.");
		}
	}

	@Transactional
	public LoginCombinedResponse handleLoginWithIdToken(SocialProvider socialProvider, String authorizationHeader, String deviceId) {
		String idToken = jwtTokenProvider.resolveToken(authorizationHeader);

		switch (socialProvider) {
			case KAKAO:
				return kakaoService.processKakaoLoginWithIdToken(idToken, deviceId, socialProvider);
			default:
				throw new UnsupportedOperationException("지원되지 않는 소셜 로그인 제공자입니다.");
		}
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