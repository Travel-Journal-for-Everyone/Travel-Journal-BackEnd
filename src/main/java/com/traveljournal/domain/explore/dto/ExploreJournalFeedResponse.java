package com.traveljournal.domain.explore.dto;

import java.util.List;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.member.entity.Member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record ExploreJournalFeedResponse(
	@Schema(example = "1")
	Long journalId,
	@Schema(example = "춘천 맛집 투어와 남이섬 산책")
	String title,
	@Schema(example = "[\"강원도\", \"춘천\", \"닭갈비\"]")
	List<String> hashTag,
	@Schema(example = "강원도")
	String region,
	@Schema(example = "1")
	Long nights,
	@Schema(example = "2")
	Long days,
	@Schema(example = "2025.04.01")
	String startDate,
	@Schema(example = "2025.04.03")
	String endDate,
	@Schema(example = "https://image.dongascience.com/Photo/2020/03/5bddba7b6574b95d37b6079c199d7101.jpg")
	String 	thumbnailUrl,
	@Schema(example = "100")
	Long likeCount,
	@Schema(example = "972")
	Long commentCount,
	@Schema(example = "1")
	Long memberId,
	@Schema(example = "도요새")
	String nickname,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/default/profile/default1.png")
	String profileImageUrl
) {
	public static ExploreJournalFeedResponse of(Journal journal, Member member, ImageService imageService) {
		return ExploreJournalFeedResponse.builder()
			.journalId(journal.getId())
			.title(journal.getTitle())
			.hashTag(journal.getHashTags().stream().map(HashTag::getTagName).toList())
			.region(journal.getRegion())
			.nights(journal.getNights())
			.days(journal.getDays())
			.startDate(journal.getStartDate())
			.endDate(journal.getEndDate())
			.thumbnailUrl(journal.getThumbnailUrl(imageService))
			.likeCount(100L)
			.commentCount(972L)
			.memberId(member.getId())
			.nickname(member.getNickname())
			.profileImageUrl(member.getProfileImageUrl())
			.build();
	}
}
