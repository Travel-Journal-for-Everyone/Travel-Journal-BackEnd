package com.traveljournal.domain.journal.dto;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.traveljournal.domain.hashtag.entity.HashTag;
import com.traveljournal.domain.journal.entity.Journal;
import com.traveljournal.domain.photo.dto.PhotoMetadataRequest;
import com.traveljournal.global.exception.BadRequestException;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record JournalUpdateRequest(
	@Schema(example = "2025.03.15")
	@NotBlank(message = "시작일은 필수입니다.")
	@Pattern(regexp = "\\d{4}\\.\\d{2}\\.\\d{2}", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM.DD)")
	String startDate,

	@Schema(example = "2025.03.18")
	@NotBlank(message = "종료일은 필수입니다.")
	@Pattern(regexp = "\\d{4}\\.\\d{2}\\.\\d{2}", message = "날짜 형식이 올바르지 않습니다. (YYYY.MM.DD)")
	String endDate,

	@Schema(example = "2")
	@NotNull(message = "박수는 필수입니다.")
	@Min(value = 0, message = "박수는 0 이상이어야 합니다.")
	@Max(value = 365, message = "박수는 365일을 초과할 수 없습니다.")
	Long nights,

	@Schema(example = "3")
	@NotNull(message = "일수는 필수입니다.")
	@Min(value = 1, message = "일수는 1 이상이어야 합니다.")
	@Max(value = 366, message = "일수는 366일을 초과할 수 없습니다.")
	Long days,

	@Schema(example = "서울특별시")
	@NotBlank(message = "지역 정보는 필수입니다.")
	@Size(max = 100, message = "지역명은 100자를 초과할 수 없습니다.")
	String region,

	@Schema(example = "서울 도심 속 힐링 명소 탐방기")
	@NotBlank(message = "여행일지 제목은 필수입니다.")
	@Size(max = 200, message = "제목은 200자를 초과할 수 없습니다.")
	String title,

	@Schema(example = "[\"서울\", \"여행\", \"도심\"]")
	@NotNull(message = "해시태그는 필수입니다.")
	@Size(max = 10, message = "해시태그는 최대 10개까지 가능합니다.")
	List<@NotBlank @Size(max = 20, message = "해시태그는 20자를 초과할 수 없습니다.") String> hashTag,

	@Schema(example = "설명")
	String description,

	@NotNull(message = "여행일차 정보는 필수입니다.")
	@Size(min = 1, max = 366, message = "여행일차는 1일 이상 366일 이하여야 합니다.")
	@Valid
	List<JournalDayRequest> journalDays,

	List<PhotoMetadataRequest> photoMetadataList,

	@Schema(description = "썸네일 업로드, 지정하지 않을 시 첫번째 사진으로 등록",
		example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String thumbnailUploadId
) {
	public static JournalUpdateRequest from(Journal journal) {
		return new JournalUpdateRequest(
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
				.map(PhotoMetadataRequest::from)
				.toList(),
			journal.getThumbnailPhoto() != null ?
				journal.getThumbnailPhoto().getImageInfo().getUploadId() : null
		);
	}

	public void validateBusinessRules() {
		if (days != nights + 1) {
			throw new BadRequestException("일수는 박수 + 1이어야 합니다.");
		}

		if (journalDays.size() != days.intValue()) {
			throw new BadRequestException("일차 정보 개수가 여행 일수와 일치하지 않습니다.");
		}

		Set<Integer> dayNumbers = journalDays.stream()
			.map(JournalDayRequest::dayNumber)
			.collect(Collectors.toSet());

		if (dayNumbers.size() != days.intValue()) {
			throw new BadRequestException("중복된 일차 번호가 있습니다.");
		}

		for (int i = 1; i <= days.intValue(); i++) {
			if (!dayNumbers.contains(i)) {
				throw new BadRequestException(i + "일차 정보가 누락되었습니다.");
			}
		}
	}
}