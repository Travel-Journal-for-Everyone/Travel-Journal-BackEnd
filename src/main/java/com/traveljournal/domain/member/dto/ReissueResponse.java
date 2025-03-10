package com.traveljournal.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReissueResponse(
	@Schema(example = "1")
	Long memberId,
	@Schema(example = "true")
	String email,
	@Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdW5hcmlzMDIwNkBuYXZlci5jb20iLCJpYXE3NDMsImV4cCI6MTc0MjIwMDAzM30.nf5sYgC3nQ-ysA3yRdHBpTXjzSdyIsIGQWxdYeT1tt8")
	String refreshToken,
	@Schema(example = "8bd0476d-e790-4133-bbfe-3a90f6757537")
	String deviceId
) {
	public static ReissueResponse of(MemberTokenResponse memberTokenResponse) {
		return ReissueResponse.builder()
			.memberId(memberTokenResponse.memberId())
			.email(memberTokenResponse.email())
			.refreshToken(memberTokenResponse.tokenInfo().refreshToken())
			.deviceId(memberTokenResponse.tokenInfo().deviceId())
			.build();
	}
}
