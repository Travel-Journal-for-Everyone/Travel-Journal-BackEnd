package com.traveljournal.domain.journal.dto;

import java.util.List;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.entity.Journal;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalListWebResponse(
	@Schema(example = "1")
	Long journalId,
	@Schema(example = "[\"서울\", \"여행\", \"도심\"]")
	List<String> hashTag,
	@Schema(example = "서울 도심 속 힐링 명소 탐방기")
	String title,
	@Schema(example = "2")
	Long nights,
	@Schema(example = "3")
	Long days,
	@Schema(example = "2025.03.15")
	String startDate,
	@Schema(example = "2025.03.18")
	String endDate,
	@Schema(example = "서울 종로구 사직로 161")
	String address,
	@Schema(example = "https://image.dongascience.com/Photo/2020/03/5bddba7b6574b95d37b6079c199d7101.jpg")
	String thumbnailUrl,
	@Schema(example = "3박 4일 동안 지루하지도 않게 진짜 제대로 놀다 왔다! 바다도 좋고, 먹을 것도 좋고, 고마운 일정과 날씨 덕분에 하루하루 행복했다 \uD83E\uDD79 아침 일찍 일어나 부지런히 돌아다녔고, 결과물도 만족스러워서 기분 좋은 여행이었다!")
	String description,
	List<JournalDayRequest> journalDays,
	@Schema(example = "100")
	Long likeCount,
	@Schema(example = "972")
	Long commentCount
) {
	public static JournalListWebResponse of(Journal journal, ImageService imageService) {
		return new JournalListWebResponse(
			journal.getId(),
			journal.getHashTags().stream()
				.map(HashTag::getTagName)
				.toList(),
			journal.getTitle(),
			journal.getNights(),
			journal.getDays(),
			journal.getStartDate(),
			journal.getEndDate(),
			journal.getThumbnailAddress(),
			journal.getThumbnailUrl(imageService),
			journal.getDescription(),
			journal.getDaysDetail().stream()
				.map(JournalDayRequest::from)
				.toList(),
			100L,
			972L
		);
	}
}
