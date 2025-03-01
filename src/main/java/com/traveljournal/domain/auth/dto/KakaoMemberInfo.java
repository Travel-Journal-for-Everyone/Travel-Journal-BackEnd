package com.traveljournal.domain.auth.dto;

import lombok.Builder;

@Builder
public record KakaoMemberInfo(
	Long id,
	KakaoAccount kakao_account
) {
	@Builder
	public record KakaoAccount(
		String email,
		Profile profile
	) {}

	@Builder
	public record Profile(
		String nickname,
		String profile_image_url
	) {}

	// of 메서드 추가
	public static KakaoMemberInfo of(Long id, String email, String nickname, String profileImageUrl) {
		return KakaoMemberInfo.builder()
			.id(id)
			.kakao_account(KakaoAccount.builder()
				.email(email)
				.profile(Profile.builder()
					.nickname(nickname)
					.profile_image_url(profileImageUrl)
					.build()
				)
				.build()
			)
			.build();
	}
}