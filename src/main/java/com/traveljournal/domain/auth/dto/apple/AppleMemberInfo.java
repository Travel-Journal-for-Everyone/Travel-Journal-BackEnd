package com.traveljournal.domain.auth.dto.apple;

import com.traveljournal.domain.auth.dto.SocialMemberInfo;

public record AppleMemberInfo(
	String id,
	String email,
	String profileImageUrl
) implements SocialMemberInfo {
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getEmail() {
		return email;
	}

	@Override
	public String getProfileImageUrl() {
		return profileImageUrl;
	}
}