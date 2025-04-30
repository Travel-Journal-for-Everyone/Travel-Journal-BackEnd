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
import com.traveljournal.domain.auth.dto.kakao.KakaoIdTokenInfo;
import com.traveljournal.domain.auth.dto.kakao.KakaoTokenResponse;
import com.traveljournal.global.config.KakaoOAuthConfig;
import com.traveljournal.global.exception.BadRequestException;
import com.traveljournal.global.exception.ExternalApiException;
import com.traveljournal.global.exception.UnauthorizedException;

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
			return response;
		} catch (BadRequestException | ExternalApiException e) {
			throw e;
		} catch (Exception e) {
			throw new ExternalApiException("카카오 토큰 발급에 실패했습니다: " + e.getMessage());
		}
	}

	public KakaoIdTokenInfo getKakaoMemberInfoFromIdToken(String idToken) {
		try {
			if (idToken == null || idToken.isBlank()) {
				throw new UnauthorizedException("id_token이 비어있거나 null입니다. : " + idToken);
			}

			// ID Token 디코딩
			SignedJWT signedJWT = SignedJWT.parse(idToken);
			JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

			String sub = claims.getSubject(); // 카카오 회원번호
			if (sub == null || sub.isBlank()) {
				throw new UnauthorizedException("id_token 정보 중 회원번호(sub)가 없습니다.");
			}

			String email = (String)claims.getClaim("email");
			String nickname = (String)claims.getClaim("nickname");
			String picture = (String)claims.getClaim("picture"); // 프로필 이미지

			return new KakaoIdTokenInfo(sub, email, nickname, picture);
		} catch (UnauthorizedException | ExternalApiException e) {
			throw e;
		} catch (Exception e) {
			throw new ExternalApiException("id_token 파싱에 실패했습니다: " + e.getMessage());
		}
	}

	public void unlinkKakaoUser(String kakaoUserId) {
		try {
			if (kakaoUserId == null || kakaoUserId.isBlank()) {
				throw new BadRequestException("카카오 회원번호가 비어 있습니다.");
			}

			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "KakaoAK " + kakaoOAuthConfig.getAdminKey());

			MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
			params.add("target_id_type", "user_id");
			params.add("target_id", kakaoUserId);

			HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

			restTemplate.exchange(
				"https://kapi.kakao.com/v1/user/unlink",
				HttpMethod.POST,
				entity,
				Void.class
			);
		} catch (BadRequestException | ExternalApiException e) {
			throw e;
		} catch (Exception e) {
			throw new ExternalApiException("카카오 연결 끊기에 실패했습니다: " + e.getMessage());
		}
	}
}