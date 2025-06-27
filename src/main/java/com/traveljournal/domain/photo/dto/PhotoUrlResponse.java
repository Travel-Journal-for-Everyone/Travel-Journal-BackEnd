package com.traveljournal.domain.photo.dto;

import com.traveljournal.domain.Image.entity.ImageInfo;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoUrlResponse(
	@Schema(example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String uploadId,
	@Schema(example = "https://travel-journal-s3.s3.amazonaws.com/source/journal_photo_20_1750242032088_99a07d36.jpeg")
	String photoUrl,
	@Schema(example = "1")
	Integer photoOrder
) {
	public static PhotoUrlResponse from(ImageInfo imageInfo, String photoUrl, Integer photoOrder) {
		return new PhotoUrlResponse(
			imageInfo.getFilename(),
			photoUrl,
			photoOrder
		);
	}
}