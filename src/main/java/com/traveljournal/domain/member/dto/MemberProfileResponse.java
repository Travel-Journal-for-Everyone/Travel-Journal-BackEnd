package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;

import lombok.Builder;

@Builder
public record MemberProfileResponse (
	String nickname,
	String profileImageUrl,
	AccountScope accountScope,
	Long followingCount,
	Long followerCount,
	Long travelDiaryCount,
	Long placesCount,
	Boolean isFirstLogin
) {

	public static MemberProfileResponse of(Member member) {
		return MemberProfileResponse.builder()
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.accountScope(member.getAccountScope())
			.followerCount(363L)
			.followingCount(180L)
			.travelDiaryCount(36L)
			.placesCount(88L)
			.isFirstLogin(member.getIsFirstLogin())
			.build();
	}// 임시로 멤버만 들어가 있습니다. 팔로워, 여행일지, 지역시 추가해야합니다
}