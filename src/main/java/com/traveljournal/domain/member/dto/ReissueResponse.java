package com.traveljournal.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ReissueResponse(
	@Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdW5hcmlzMDIwNkBuYXZlci5jb20iLCJpYXE3NDMsImV4cCI6MTc0MjIwMDAzM30.nf5sYgC3nQ-ysA3yRdHBpTXjzSdyIsIGQWxdYeT1tt8")
	String refreshToken
) {
	public static ReissueResponse of(MemberTokenResponse memberTokenResponse) {
		return ReissueResponse.builder()
			.refreshToken(memberTokenResponse.tokenInfo().refreshToken())
			.build();
	}
}
