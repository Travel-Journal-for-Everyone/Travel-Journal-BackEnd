package com.traveljournal.domain.journal.dto;

import java.util.List;

import com.traveljournal.domain.photo.dto.PhotoMetadataRequest;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record JournalCreateRequest(
	@Schema(example = "2025.03.15")
	@NotBlank(message = "시작일은 필수입니다.")
	String startDate,

	@Schema(example = "2025.03.18")
	@NotBlank(message = "종료일은 필수입니다.")
	String endDate,

	@Schema(example = "2")
	@NotNull(message = "박수는 필수입니다.")
	@Min(value = 0, message = "박수는 0 이상이어야 합니다.")
	Long nights,

	@Schema(example = "3")
	@NotNull(message = "일수는 필수입니다.")
	@Min(value = 1, message = "일수는 1 이상이어야 합니다.")
	Long days,

	@Schema(example = "서울특별시")
	@NotBlank(message = "지역 정보는 필수입니다.")
	String region,

	@Schema(example = "서울 도심 속 힐링 명소 탐방기")
	@NotBlank(message = "여행일지 제목은 필수입니다.")
	String title,

	@Schema(example = "[\"서울\", \"여행\", \"도심\"]")
	@NotNull(message = "해시태그는 필수입니다.")
	List<String> hashTag,

	@Schema(example = "설명")
	String description,

	@NotNull(message = "여행일차 정보는 필수입니다.")
	@Valid
	List<JournalDayRequest> journalDays,

	List<PhotoMetadataRequest> photoMetadataList,

	@Schema(description = "썸네일 업로드, 지정하지 않을 시 첫번째 사진으로 등록",
		example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String thumbnailUploadId
) {
}