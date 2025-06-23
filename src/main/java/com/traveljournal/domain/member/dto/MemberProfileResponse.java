package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.statistics.entity.MemberStatistics;

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
	Boolean isFirstLogin,
	BlockRelationType blockRelationType
) {

	public static MemberProfileResponse of(Member member, MemberStatistics memberStatistics) {
		return MemberProfileResponse.builder()
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.accountScope(member.getAccountScope())
			.followerCount(memberStatistics.getFollowerCount())
			.followingCount(memberStatistics.getFollowingCount())
			.travelDiaryCount(memberStatistics.getTravelDiaryCount())
			.placesCount(memberStatistics.getPlacesCount())
			.isFirstLogin(member.getIsFirstLogin())
			.build();
	}

	public static MemberProfileResponse of(Member member, MemberStatistics memberStatistics, BlockRelationType blockRelationType) {
		return MemberProfileResponse.builder()
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.accountScope(member.getAccountScope())
			.followerCount(memberStatistics.getFollowerCount())
			.followingCount(memberStatistics.getFollowingCount())
			.travelDiaryCount(memberStatistics.getTravelDiaryCount())
			.placesCount(memberStatistics.getPlacesCount())
			.isFirstLogin(member.getIsFirstLogin())
			.blockRelationType(blockRelationType)
			.build();
	}
}