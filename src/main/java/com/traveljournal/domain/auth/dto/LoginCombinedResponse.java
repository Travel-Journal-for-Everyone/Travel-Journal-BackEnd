package com.traveljournal.domain.auth.dto;

import lombok.Builder;

@Builder
public record LoginCombinedResponse (
	LoginResponse LoginResponse,
	String accessToken
) {
	public static LoginCombinedResponse of(LoginResponse loginResponse, String accessToken) {
		return LoginCombinedResponse.builder()
			.LoginResponse(loginResponse)
			.accessToken(accessToken)
			.build();
	}
}
