package com.traveljournal.domain.journal.dto;

import java.util.List;

import com.traveljournal.domain.Image.service.ImageService;
import com.traveljournal.domain.block.dto.BlockRelationType;
import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.photo.dto.PhotoUrlResponse;

import io.swagger.v3.oas.annotations.media.Schema;

public record JournalDetailResponse(
	@Schema(example = "2025.03.15")
	String startDate,
	@Schema(example = "2025.03.18")
	String endDate,
	@Schema(example = "2")
	Long nights,
	@Schema(example = "3")
	Long days,
	@Schema(example = "서울특별시")
	String region,
	@Schema(example = "서울 도심 속 힐링 명소 탐방기")
	String title,
	@Schema(example = "[\"서울\", \"여행\", \"도심\"]")
	List<String> hashTag,
	@Schema(example = "설명")
	String description,
	List<JournalDayRequest> journalDays,
	List<PhotoUrlResponse> photoList,
	@Schema(allowableValues = {"NONE", "BLOCKED_BY_ME", "BLOCKED_ME", "MUTUAL_BLOCK"})
	BlockRelationType blockRelationType
) {
	public static JournalDetailResponse of(Journal journal, BlockRelationType blockRelationType, ImageService imageService) {
		return new JournalDetailResponse(
			journal.getStartDate(),
			journal.getEndDate(),
			journal.getNights(),
			journal.getDays(),
			journal.getRegion(),
			journal.getTitle(),
			journal.getHashTags().stream()
				.map(HashTag::getTagName)
				.toList(),
			journal.getDescription(),
			journal.getDaysDetail().stream()
				.map(JournalDayRequest::from)
				.toList(),
			journal.getDaysDetail().stream()
				.flatMap(day -> day.getPhotos().stream())
				.map(photo -> PhotoUrlResponse.from(
					photo,
					imageService.getImageUrl(photo.getImageInfo().getUploadId())
				))
				.toList(),
			blockRelationType
			);
	}
}
