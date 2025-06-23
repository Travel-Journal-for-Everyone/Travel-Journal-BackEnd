package com.traveljournal.domain.member.dto;

import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.member.entity.AccountScope;
import com.traveljournal.domain.member.entity.Member;
import com.traveljournal.domain.statistics.entity.MemberStatistics;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberProfileResponse (
	@Schema(example = "도요새")
	String nickname,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/default/profile/default1.png")
	String profileImageUrl,
	@Schema(example = "PUBLIC")
	AccountScope accountScope,
	@Schema(example = "1")
	Long followingCount,
	@Schema(example = "12")
	Long followerCount,
	@Schema(example = "53")
	Long travelDiaryCount,
	@Schema(example = "0")
	Long placesCount,
	@Schema(example = "false")
	Boolean isFirstLogin,
	@Schema(allowableValues = {"NONE", "BLOCKED_BY_ME", "BLOCKED_ME", "MUTUAL_BLOCK"})
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