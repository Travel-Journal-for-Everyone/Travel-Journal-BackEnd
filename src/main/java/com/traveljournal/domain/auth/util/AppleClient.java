package com.traveljournal.domain.auth.util;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.traveljournal.domain.auth.dto.apple.ApplePublicKeyResponse;
import com.traveljournal.domain.auth.dto.apple.AppleTokenResponse;

@FeignClient(name = "apple-client", url = "https://appleid.apple.com")
public interface AppleClient {
	// 애플 공개 키 가져오기
	@GetMapping("/auth/keys")
	ApplePublicKeyResponse getApplePublicKeys();

	// 토큰 요청
	@PostMapping(value = "/auth/token", consumes = "application/x-www-form-urlencoded")
	AppleTokenResponse appleAuth(
		@RequestParam("client_id") String clientId,
		@RequestParam("code") String code,
		@RequestParam("grant_type") String grantType,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("redirect_uri") String redirectUri
	);

	// 토큰 취소 (로그아웃)
	@PostMapping(value = "/auth/revoke", consumes = "application/x-www-form-urlencoded")
	void revokeToken(
		@RequestParam("client_id") String clientId,
		@RequestParam("client_secret") String clientSecret,
		@RequestParam("token") String token,
		@RequestParam("token_type_hint") String tokenTypeHint
	);
}