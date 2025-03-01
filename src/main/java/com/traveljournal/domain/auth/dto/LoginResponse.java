package com.traveljournal.domain.auth.dto;

import java.time.LocalDateTime;

import com.traveljournal.domain.member.dto.TokenInfo;
import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.member.entity.SocialProvider;

import lombok.Builder;

@Builder
public record LoginResponse(
	Long memberId,
	String email,
	String nickname,
	String profileImageUrl,
	LocalDateTime birthdate,
	AccountScope accountScope,
	String phoneNumber,
	SocialProvider socialProvider,
	boolean isFirstLogin,
	String refreshToken,
	String deviceId
) {
	public static LoginResponse from(Member member, TokenInfo tokenInfo) {
		return LoginResponse.builder()
			.memberId(member.getId())
			.email(member.getEmail())
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.birthdate(member.getBirthdate())
			.accountScope(member.getAccountScope())
			.phoneNumber(member.getPhoneNumber())
			.socialProvider(member.getSocialProvider())
			.isFirstLogin(member.getIsFirstLogin())
			.refreshToken(tokenInfo.refreshToken())
			.deviceId(tokenInfo.deviceId())
			.build();
	}
}