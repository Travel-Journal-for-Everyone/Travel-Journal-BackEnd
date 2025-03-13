package com.traveljournal.domain.auth.dto.kakao;

public record KakaoTokenResponse(
	String access_token,
	String token_type,
	String refresh_token,
	String id_token,
	int expires_in,
	String scope,
	int refresh_token_expires_in
) {}