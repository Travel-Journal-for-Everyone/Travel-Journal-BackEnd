package com.traveljournal.domain.photo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PhotoUploadResponse(
	@Schema(example = "journal_photo_20_1750242032088_99a07d36.jpeg")
	String uploadId,
	@Schema(example = "test4.jpeg")
	String uploadFilename
) {
	public static PhotoUploadResponse of(String uploadId, String uploadFilename) {
		return new PhotoUploadResponse(uploadId, uploadFilename);
	}
}