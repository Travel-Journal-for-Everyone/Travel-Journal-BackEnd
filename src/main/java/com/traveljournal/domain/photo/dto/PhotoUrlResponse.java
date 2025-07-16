package com.traveljournal.domain.photo.dto;

import com.traveljournal.domain.photo.entity.Photo;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoUrlResponse(
	@Schema(example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String uploadId,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/source/journal_photo_20_1750242032088_99a07d36.jpeg")
	String photoUrl,
	@Schema(example = "1")
	Integer photoOrder,
	@Schema(example = "1")
	Integer daySpotOrder,
	@Schema(example = "1", description = "몇 일차 사진인지")
	Integer dayNumber
) {
	public static PhotoUrlResponse from(Photo photo, String photoUrl) {
		return new PhotoUrlResponse(
			photo.getImageInfo().getUploadId(),
			photoUrl,
			photo.getPhotoOrder(),
			photo.getDaySpotOrder(),
			photo.getJournalDay().getDayNumber()
			);
	}
}