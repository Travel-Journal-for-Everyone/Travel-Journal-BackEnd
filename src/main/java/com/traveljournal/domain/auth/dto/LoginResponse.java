package com.traveljournal.domain.auth.dto;

import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record LoginResponse(
	@Schema(example = "1")
	Long memberId,
	@Schema(description = "사용자가 첫 로그인인 경우 true, 그렇지 않으면 false")
	boolean isFirstLogin,
	@Schema(example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJsdW5hcmlzMDIwNkBuYXZlci5jbODcsImV4cCI6MTc0MjE5MDk4N30.QloL4UToFQ4oBNb9motKxo17Uhn99w1O-l6lw0U88GA")
	String refreshToken,
	@Schema(example = "7d4a51f7-3522-4985-9431-5889a8822bfa")
	String deviceId
) {
	public static LoginResponse from(Member member, TokenInfo tokenInfo) {
		return LoginResponse.builder()
			.memberId(member.getId())
			.isFirstLogin(member.getIsFirstLogin())
			.refreshToken(tokenInfo.refreshToken())
			.deviceId(tokenInfo.deviceId())
			.build();
	}
}