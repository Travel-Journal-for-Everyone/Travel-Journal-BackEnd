package com.traveljournal.domain.member.dto;

import lombok.Builder;

@Builder
public record ReissueResonse(
	Long memberId,
	String email,
	String refreshToken,
	String deviceId
) {
	public static ReissueResonse of(MemberTokenResponse memberTokenResponse) {
		return ReissueResonse.builder()
			.memberId(memberTokenResponse.memberId())
			.email(memberTokenResponse.email())
			.refreshToken(memberTokenResponse.tokenInfo().refreshToken())
			.deviceId(memberTokenResponse.tokenInfo().deviceId())
			.build();
	}
}
