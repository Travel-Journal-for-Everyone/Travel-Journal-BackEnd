package com.traveljournal.domain.auth.dto.google;

import com.traveljournal.domain.auth.dto.SocialMemberInfo;

import lombok.Builder;

// 구글 사용자 정보를 담을 DTO
@Builder
public record GoogleMemberInfo(
	// Google id 필드 : 문자열
	String id,
	String email,
	String profileImageUrl
) implements SocialMemberInfo {

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public String getEmail() {
		return this.email;
	}

	@Override
	public String getProfileImageUrl() {
		return this.profileImageUrl;
	}
}
