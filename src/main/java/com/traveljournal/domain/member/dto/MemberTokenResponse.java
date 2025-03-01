package com.traveljournal.domain.member.dto;

import lombok.Builder;

@Builder
public record MemberTokenResponse(
	Long memberId,
	String email,
	TokenInfo tokenInfo
) {
}
