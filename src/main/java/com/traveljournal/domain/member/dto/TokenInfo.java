package com.traveljournal.domain.member.dto;

import lombok.Builder;

@Builder
public record TokenInfo(
	String accessToken,
	String refreshToken,
	String deviceId
) {
	public static TokenInfo of(String accessToken, String refreshToken, String deviceId) {
		return TokenInfo.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.deviceId(deviceId)
			.build();
	}
}