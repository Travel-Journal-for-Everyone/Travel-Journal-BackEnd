package com.traveljournal.domain.auth.dto.kakao;

import com.traveljournal.domain.auth.dto.SocialMemberInfo;

import lombok.Builder;

@Builder
public record KakaoMemberInfo(
	Long id,
	KakaoAccount kakao_account
) implements SocialMemberInfo {
	@Builder
	public record KakaoAccount(
		String email,
		Profile profile
	) {}

	@Builder
	public record Profile(
		String profile_image_url
	) {}

	// of 메서드 추가
	public static KakaoMemberInfo of(Long id, String email, String profileImageUrl) {
		return KakaoMemberInfo.builder()
			.id(id)
			.kakao_account(KakaoAccount.builder()
				.email(email)
				.profile(Profile.builder()
					.profile_image_url(profileImageUrl)
					.build()
				)
				.build()
			)
			.build();
	}

	@Override
	public String getId() {
		return id.toString();
	}

	@Override
	public String getEmail() {
		return kakao_account.email();
	}

	@Override
	public String getProfileImageUrl() {
		return kakao_account.profile().profile_image_url();
	}
}