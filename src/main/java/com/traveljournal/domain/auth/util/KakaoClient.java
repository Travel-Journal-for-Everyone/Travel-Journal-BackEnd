package com.traveljournal.domain.auth.util;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.traveljournal.domain.auth.dto.KakaoIdTokenInfo;
import com.traveljournal.domain.auth.dto.KakaoMemberInfo;
import com.traveljournal.domain.auth.dto.KakaoTokenResponse;
import com.traveljournal.global.config.KakaoOAuthConfig;
import com.traveljournal.global.exception.ExternalApiException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoClient {

	private final RestTemplate restTemplate;
	private final KakaoOAuthConfig kakaoOAuthConfig;

	/**
	 * 카카오 인증 코드로 액세스 토큰 얻기
	 */
	public KakaoTokenResponse getKakaoToken(String code) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("grant_type", "authorization_code");
			params.add("client_id", kakaoOAuthConfig.getClientId());
			params.add("client_secret", kakaoOAuthConfig.getClientSecret());
			params.add("code", code);
			params.add("redirect_uri", kakaoOAuthConfig.getRedirectUri());

			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

			KakaoTokenResponse response = restTemplate.postForObject(
				kakaoOAuthConfig.getTokenUri(),
				request,
				KakaoTokenResponse.class
			);

			if (response == null) {
				throw new ExternalApiException("카카오 토큰 응답이 null입니다.");
			}

			log.info("카카오 토큰 발급 성공");
			return response;

		} catch (Exception e) {
			log.error("카카오 토큰 발급 실패: {}", e.getMessage());
			throw new ExternalApiException("카카오 토큰 발급에 실패했습니다: " + e.getMessage());
		}
	}

	/**
	 * 카카오 액세스 토큰으로 사용자 정보 가져오기
	 */
	public KakaoMemberInfo getKakaoMemberInfo(String accessToken) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			HttpEntity<Void> request = new HttpEntity<>(headers);

			KakaoMemberInfo response = restTemplate.exchange(
				kakaoOAuthConfig.getUserInfoUri(),
				HttpMethod.GET,
				request,
				KakaoMemberInfo.class
			).getBody();

			if (response == null) {
				throw new ExternalApiException("카카오 사용자 정보 응답이 null입니다.");
			}

			log.info("카카오 사용자 정보 조회 성공");
			return response;

		} catch (Exception e) {
			log.error("카카오 사용자 정보 조회 실패: {}", e.getMessage());
			throw new ExternalApiException("카카오 사용자 정보 조회에 실패했습니다: " + e.getMessage());
		}
	}

	public KakaoIdTokenInfo getKakaoMemberInfoFromIdToken(String idToken) {
		try {
			// ID Token 디코딩
			SignedJWT signedJWT = SignedJWT.parse(idToken);
			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

			String sub = claims.getSubject(); // 카카오 회원번호
			String email = (String) claims.getClaim("email");
			String nickname = (String) claims.getClaim("nickname");
			String picture = (String) claims.getClaim("picture"); // 프로필 이미지

			log.info("카카오 ID Token 정보: sub={}, email={}, nickname={}, picture={}", sub, email, nickname, picture);
			return new KakaoIdTokenInfo(sub, email, nickname, picture);
		} catch (Exception e) {
			log.error("ID Token 파싱 실패: {}", e.getMessage());
			throw new ExternalApiException("ID Token 파싱에 실패했습니다: " + e.getMessage());
		}
	}
}