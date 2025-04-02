package com.traveljournal.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberTokenResponse(
	Long memberId,
	TokenInfo tokenInfo
) {

	public static MemberTokenResponse of(Long memberId, String accessToken, String refreshToken, String deviceId) {
		return MemberTokenResponse.builder()
			.memberId(memberId)
			.tokenInfo(TokenInfo.builder()
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.deviceId(deviceId)
				.build()
			)
			.build();

	}
}
